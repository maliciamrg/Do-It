package com.malicia.mrg.adapters;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;
import com.malicia.mrg.AddNewTask;
import com.malicia.mrg.R;
import com.malicia.mrg.activity.TaskActivity;
import com.malicia.mrg.model.TaskModel;
import com.malicia.mrg.model.ToDoModel;
import com.malicia.mrg.utils.DatabaseHandler;
import com.malicia.mrg.utils.ViewFilter;
import com.malicia.mrg.utils.ViewOrder;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static com.malicia.mrg.utils.ViewFilter.*;
import static com.malicia.mrg.utils.ViewOrder.ALPHABET;
import static com.malicia.mrg.utils.ViewOrder.HIERARCHICAL;

public abstract class TaskAdapter extends RecyclerView.Adapter<TaskAdapter.TaskViewHolder> {

    public static final int MARGINGLEVEL = 50;
    protected final DatabaseHandler db;
    protected final TaskActivity activity;
    private Integer detailVisibility = View.GONE;
    private ViewOrder viewOrder = HIERARCHICAL;
    private ViewFilter viewFilter = ViewFilter.ALL;
    private List<TaskModel> taskList;
    private View itemView;
    private int expandInOnlyRootView = 0;
    private HashMap<Integer, ToDoModel> todoList;

    public TaskAdapter(DatabaseHandler db, TaskActivity activity) {
        this.db = db;
        this.activity = activity;
    }

    public ViewFilter getViewFilter() {
        return viewFilter;
    }

    public List<TaskModel> getTaskList() {
        return taskList;
    }

    public int getExpandInOnlyRootView() {
        return expandInOnlyRootView;
    }

    public void setExpandInOnlyRootView(int expandInOnlyRootView) {
        this.expandInOnlyRootView = expandInOnlyRootView;
    }

    @NonNull
    @Override
    public TaskViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.task_layout, parent, false);
        return new TaskViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull final TaskViewHolder holder, int position) {
        db.openDatabase();
        int holderPosition = holder.getAdapterPosition();
        final TaskModel item = (TaskModel) taskList.toArray()[holderPosition];

        boolean isInPostItZone = item.isPostIt() && item.isInPostItZone();
        boolean isHeadOfProject = item.isProject() && item.getHierarchicalRank() == 0;
        boolean isAFirst = item.getChildList().size() == 0;


        Integer hierarchicalRoot = taskList.get(position).getHierarchicalRoot();
        int id = taskList.get(position).getId();
        int backgroundColor = todoList.get(id).getBackgroundColor();
        if (hierarchicalRoot != null) {
            backgroundColor = todoList.get(hierarchicalRoot).getBackgroundColor();
        }
        if (taskList.get(position).isInPostItZone()) {
            backgroundColor = 0xFF8B8726;
        }
        holder.rl1.setBackgroundColor(backgroundColor);

        String task = item.getTask();
        if (((isViewFirst() && isAFirst && !isInPostItZone))) {
            holder.project.setText(todoList.get(hierarchicalRoot).getTask());
        } else {
            if (isInPostItZone) {
                holder.project.setText(todoList.get(hierarchicalRoot).getTask() + "\n" + "     " + task);
            } else {
                holder.project.setText(task);
            }
        }

        if (isInPostItZone) {
            holder.project.setTextSize(16);
        } else {
            holder.project.setTextSize(12);
        }
        holder.task.setText(task);


        holder.project.setVisibility(isHeadOfProject || isInPostItZone || (isViewFirst() && isAFirst) ? View.VISIBLE : View.GONE);
        holder.task.setVisibility(isHeadOfProject || isInPostItZone ? View.GONE : View.VISIBLE);

        holder.task.setOnCheckedChangeListener(null);//evite le pb de refresh quand on ajoute une ligne
        boolean checked = isChecked(item);
        holder.task.setChecked(checked);

        indentCheckBox(holder, item.getHierarchicalLevel());

        int nbSubTask = item.getHierarchicalRootNbSubtask();
        if (nbSubTask > 0 && isHierarchical()) {
            holder.nbSub.setText(String.valueOf(nbSubTask));
        } else {
            holder.nbSub.setText("");
        }


        holder.task.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                checkedChanged(isChecked, item);
                activity.refreshData(true);
            }
        });


        holder.task.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switcheExtendTask(item);
            }
        });
        holder.project.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switcheExtendTask(item);
            }
        });
        holder.nbSub.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switcheExtendTask(item);
            }
        });
        holder.childList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switcheExtendTask(item);
            }
        });

        holder.task.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                return createNewWithParent(holderPosition);
            }
        });
        holder.project.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                return createNewWithParent(holderPosition);
            }
        });
        holder.nbSub.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                return createNewWithParent(holderPosition);
            }
        });
        holder.childList.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                return createNewWithParent(holderPosition);
            }
        });


        String text = "";
        List<TaskModel> parentList = item.getParentList();
        List<TaskModel> childList = item.getChildList();
        if (((parentList != null && parentList.size() > 0) || (childList != null && childList.size() > 0))) {
            holder.childList.setVisibility(detailVisibility);
        } else {
            holder.childList.setVisibility(View.GONE);
        }
        if (isInPostItZone) {
            if (hierarchicalRoot != null) {
                String sepa = text != "" ? "\n" : "";
                text = text + sepa + TaskModel.rootToString(todoList.get(hierarchicalRoot));
            }
        } else {
            if (parentList != null && parentList.size() > 0) {
                String sepa = text != "" ? "\n" : "";
                text = text + sepa + TaskModel.parentListToString(parentList);
            }
            if (childList != null && childList.size() > 0) {
                String sepa = text != "" ? "\n" : "";
                text = text + sepa + TaskModel.childListToString(childList);
            }
        }
        holder.childList.setText(text);

        afterOnBindViewHolder(holder, item);
    }

    public void indentCheckBox(TaskViewHolder holder, int hierarchicalLevel) {
        LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) holder.task.getLayoutParams();
        int levelHierarchical = 0;
        if (isHierarchical()) {
            levelHierarchical = hierarchicalLevel * MARGINGLEVEL;
        }
        params.setMarginStart(levelHierarchical);
        holder.task.setLayoutParams(params);
    }

    protected abstract void afterOnBindViewHolder(TaskViewHolder holder, TaskModel item);

    protected abstract boolean isChecked(TaskModel item);

    private boolean createNewWithParent(int holderPosition) {
//        return false;
        TaskModel item = (TaskModel) taskList.toArray()[holderPosition];
        Bundle bundle = new Bundle();
        bundle.putSerializable("parentClass", item);
        AddNewTask.newInstance(bundle).show(activity.getSupportFragmentManager(), AddNewTask.TAG);
        return true;
    }


    private void switcheExtendTask(TaskModel item) {
        if (item.isRoot()) {
            if (isViewRoot() && expandInOnlyRootView != item.getId()) {
                expandInOnlyRootView = item.getId();
            } else {
                expandInOnlyRootView = 0;
            }
        }
        activity.refreshData(false);
    }

    @Override
    public int getItemCount() {
        return taskList.size();
    }

    public Context getContext() {
        return activity;
    }

    public void setTasks(List<TaskModel> taskList) {
        this.taskList = taskList;
    }

    public void setTodo(HashMap<Integer, ToDoModel> todoList) {
        this.todoList = todoList;
    }

    public void deleteItem(int position) {
        TaskModel item = (TaskModel) taskList.toArray()[position];
        db.deleteTask(item.getId());
        taskList.remove(position);
        activity.refreshData(true);
        notifyItemRemoved(position);
        notifyDataSetChanged();
    }

    public int deleteAllChecked(int position) {
        int ret = activity.delAllChecked();
        activity.refreshData(true);
        notifyItemRemoved(position);
        notifyDataSetChanged();
        return ret;
    }

    public void editItem(int position) {
        TaskModel item = (TaskModel) taskList.toArray()[position];
        Bundle bundle = new Bundle();
        bundle.putInt("id", item.getId());
        bundle.putString("task", item.getTask());
        bundle.putSerializable("taskClass", item);

        AddNewTask.newInstance(bundle).show(activity.getSupportFragmentManager(), AddNewTask.TAG);

//        notifyDataSetChanged();
    }


    public void swapDetailVisibility() {
        if (detailVisibility == View.VISIBLE) {
            detailVisibility = View.GONE;
        } else {
            detailVisibility = View.VISIBLE;
        }
    }

    public boolean isDetailVisible() {
        return detailVisibility == View.VISIBLE;
    }

    public void swapHierarchicalView() {
        switch (viewOrder) {
            case HIERARCHICAL:
                viewOrder = ALPHABET;
                break;
            case ALPHABET:
                viewOrder = HIERARCHICAL;
                break;
            default:
                viewOrder = HIERARCHICAL;
        }
    }

    public boolean isHierarchical() {
        return viewOrder == HIERARCHICAL;
    }

    public boolean isAlphabet() {
        return viewOrder == ALPHABET;
    }

    public void swapViewFilter() {
        switch (viewFilter) {
            case ROOT:
                viewFilter = ALL;
                break;
            case ALL:
                viewFilter = FIRST;
                break;
            case FIRST:
                viewFilter = ROOT;
                break;
            default:
                viewFilter = ROOT;
        }
    }

    public boolean isViewRoot() {
        return viewFilter == ROOT;
    }

    public boolean isViewAll() {
        return viewFilter == ALL;
    }

    public boolean isViewFirst() {
        return viewFilter == FIRST;
    }

    protected abstract void checkedChanged(boolean isChecked, TaskModel item);

    public TaskModel getItem(int position) {
        return (TaskModel) taskList.toArray()[position];
    }

    public void swapTaskList(int fromPosition, int toPosition) {
        TaskModel newParent = null;
        TaskModel oldParent = null;
        List<TaskModel> childsEmpty = new ArrayList<>();

        //before Collections.swap(adapter.getTaskList(), fromPosition, toPosition);
        // fromPosition element en mouvement
        TaskModel fromPositionTask = taskList.get(fromPosition);
        TaskModel toPositionTask = taskList.get(toPosition);
        do {
            fromPositionTask = taskList.get(fromPosition);
            toPositionTask = taskList.get(toPosition);
//            System.out.println("before" + "\n" + "fromPosition=" + fromPosition + " fromPositionTask=" + fromPositionTask.getTask() + "\n" + fromPositionTask.toString());
//            System.out.println("before" + "\n" + "toPosition=" + toPosition + " toPositionTask=" + toPositionTask.getTask() + "\n" + toPositionTask.toString());
        } while (fromPositionTask == null || toPositionTask == null);

        if (fromPositionTask.getHierarchicalLevel() == toPositionTask.getHierarchicalLevel() && fromPositionTask.getChildList().size() == 0 && toPositionTask.getChildList().size() == 0) {
            return;
        }

        if (fromPositionTask.getHierarchicalLevel() == toPositionTask.getHierarchicalLevel()) {

            TaskModel fromTask;
            TaskModel toTask;
            if (fromPositionTask.getChildList().size() > 0) {
                fromTask = fromPositionTask;
                toTask = toPositionTask;
            } else {
                fromTask = toPositionTask;
                toTask = fromPositionTask;
            }

            List<TaskModel> childs = db.getAllChildTasks(fromTask.getId());
            List<Integer> childID = new ArrayList<>();
            for (TaskModel child : childs) {
                childID.add(child.getId());
            }
            Integer[] childIDInt = new Integer[childID.size()];
            childIDInt = childID.toArray(childIDInt);
            db.deleteLinkParent(fromTask.getId());
            db.addlink(childIDInt, toTask.getId());

        } else {
            if (fromPosition < toPosition) {
                //swapDOWN
                if (fromPositionTask.getHierarchicalLevel() > toPositionTask.getHierarchicalLevel()) {
                    //super lien du old parent avec le swapper
                    int child = fromPositionTask.getId();
                    db.deleteLink(toPositionTask.getId(), child);
                    //add link
                    newParent = findNewParentInTaskList(fromPosition, fromPositionTask.getHierarchicalLevel());
                    db.addLink(newParent.getId(), child);
                } else {
                    //super lien du old parent avec le swapper
                    int child = toPositionTask.getId();
                    oldParent = findNewParentInTaskList(toPosition - 1, toPositionTask.getHierarchicalLevel());
                    db.deleteLink(oldParent.getId(), child);
                    //add link
                    db.addLink(fromPositionTask.getId(), child);
                }
            } else {
                //swapUP
                if (fromPositionTask.getHierarchicalLevel() > toPositionTask.getHierarchicalLevel()) {
                    //super lien de parent avec le swapper
                    int child = fromPositionTask.getId();
                    oldParent = findNewParentInTaskList(fromPosition - 1, fromPositionTask.getHierarchicalLevel());
                    db.deleteLink(oldParent.getId(), child);
                    //Recherche le new parent et add link
                    db.addLink(toPositionTask.getId(), child);
                } else {
                    //super lien de parent avec le swapper
                    int child = toPositionTask.getId();
                    db.deleteLink(fromPositionTask.getId(), child);
                    //Recherche le new parent et add link
                    newParent = findNewParentInTaskList(toPosition, toPositionTask.getHierarchicalLevel());
                    db.addLink(newParent.getId(), child);
                }
            }
        }

        //refresh maj child parent
        if (newParent != null) {
            newParent.setChildList(db.getAllChildTasks(fromPositionTask.getId()));
            newParent.setParentList(db.getAllParentTasks(fromPositionTask.getId()));
        }
        if (oldParent != null) {
            oldParent.setChildList(db.getAllChildTasks(fromPositionTask.getId()));
            oldParent.setParentList(db.getAllParentTasks(fromPositionTask.getId()));
        }
        fromPositionTask.setChildList(db.getAllChildTasks(fromPositionTask.getId()));
        fromPositionTask.setParentList(db.getAllParentTasks(fromPositionTask.getId()));
        toPositionTask.setChildList(db.getAllChildTasks(toPositionTask.getId()));
        toPositionTask.setParentList(db.getAllParentTasks(toPositionTask.getId()));

//        System.out.println("after" + "\n" + "fromPosition=" + fromPosition + " fromPositionTask=" + fromPositionTask.getTask() + "\n" + fromPositionTask.toString());
//        System.out.println("after" + "\n" + "toPosition=" + toPosition + " toPositionTask=" + toPositionTask.getTask() + "\n" + toPositionTask.toString());

    }

    public void editIdent(TaskViewHolder viewHolder, int viewHolderPos, Integer sens) {

        TaskModel oldParent = null;
        TaskModel newParent = null;

        TaskModel ret = taskList.get(viewHolderPos);

        oldParent = findOldParentInTaskList(viewHolderPos);

        //find new parent
        int newHLevel = ret.getHierarchicalLevel() + sens;
        if (newHLevel < 0) {
            newHLevel = 0;
        }
        newParent = findNewParentInTaskList(viewHolderPos, newHLevel);
        newHLevel = newParent == null ? newHLevel : newParent.getHierarchicalLevel() + 1;

        int child = ret.getId();
        if (newParent == null) {
            db.deleteLink(oldParent.getId(), child);
//            System.out.println("del: " + oldParent.getId() + "-" + child);
        } else {
            if (oldParent == null) {
                db.addLink(newParent.getId(), child);
//                System.out.println("add: " + newParent.getId() + "-" + child);
            } else {
                db.movelink(oldParent.getId(), newParent.getId(), child);
//                System.out.println(oldParent.getId() + "-" + child + "=>" + newParent.getId() + "-" + child);
            }
        }
        indentCheckBox(viewHolder, newHLevel);
    }

    private TaskModel findNewParentInTaskList(int viewHolderPos, int newHLevel) {
        TaskModel newParent = null;
        for (int p = viewHolderPos - 1; p >= 0; p--) {
            if (taskList.get(p).getHierarchicalLevel() < newHLevel) {
                newParent = taskList.get(p);
                break;
            }
        }
        return newParent;
    }

    private TaskModel findOldParentInTaskList(int viewHolderPos) {
        //find old parent
        TaskModel oldParent = null;
        int id = taskList.get(viewHolderPos).getId();
        for (int p = viewHolderPos - 1; p >= 0; p--) {
            if (taskList.get(p).isParent(id)) {
                oldParent = taskList.get(p);
                break;
            }
        }
        return oldParent;
    }

    public void refreshActivityData(boolean b) {
        activity.refreshData(true);
    }

    public Boolean removeFragmentByTag(String tag) {
        Context context = activity;
        int exitTansaction = 0;
        Fragment fragment = activity.getSupportFragmentManager().findFragmentByTag(tag);
        if (fragment != null) {
            fragment.onStop();
//            FragmentTransaction ft = ((Activity) context)
//                    .getFragmentManager().beginTransaction();
//            ft.setCustomAnimations(0, exitTansaction);
//            ft.remove(fragment);
//            ft.commit();
            return true;
        }
        return false;
    }

    public static class TaskViewHolder extends RecyclerView.ViewHolder {
        public CheckBox task;
        public TextView project;
        TextView childList;
        TextView nbSub;
        RelativeLayout rl1;
        LinearLayout ll1;
        CardView cv;

        TaskViewHolder(View view) {
            super(view);
            task = view.findViewById(R.id.todoCheckBox);
            project = view.findViewById(R.id.textView3);
            childList = view.findViewById(R.id.textView);
            nbSub = view.findViewById(R.id.nbSub);
            ll1 = view.findViewById(R.id.ll1);
            rl1 = view.findViewById(R.id.rl1);
            cv = view.findViewById(R.id.cv);
        }
    }
}
