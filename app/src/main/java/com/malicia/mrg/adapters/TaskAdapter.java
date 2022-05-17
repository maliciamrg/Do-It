package com.malicia.mrg.adapters;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.malicia.mrg.activity.TaskActivity;
import net.penguincoders.doit.AddNewTask;
import net.penguincoders.doit.Model.TaskModel;
import net.penguincoders.doit.Model.ToDoModel;
import net.penguincoders.doit.R;
import net.penguincoders.doit.Utils.DatabaseHandler;

import java.util.HashMap;
import java.util.List;

public abstract class TaskAdapter extends RecyclerView.Adapter<TaskAdapter.ViewHolder> {

    public static final int MARGINGLEVEL = 50;
    protected final DatabaseHandler db;
    protected final TaskActivity activity;
    private Integer detailVisibility = View.GONE;
    private boolean hierarchicalView = true;
    private boolean onlyRootView = true;
    private List<TaskModel> taskList;
    private View itemView;
    private int expandInOnlyRootView = 0;
    private HashMap<Integer, ToDoModel> todoList;
    public TaskAdapter(DatabaseHandler db, TaskActivity activity) {
        this.db = db;
        this.activity = activity;
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
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.task_layout, parent, false);
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull final TaskAdapter.ViewHolder holder, int position) {
        db.openDatabase();
        int holderPosition = holder.getAdapterPosition();
        final TaskModel item = (TaskModel) taskList.toArray()[holderPosition];


        Integer hierarchicalRoot = taskList.get(position).getHierarchicalRoot();
        int id = taskList.get(position).getId();
        int backgroundColor = todoList.get(id).getBackgroundColor();
        if (hierarchicalRoot != null) {
            backgroundColor = todoList.get(hierarchicalRoot).getBackgroundColor();
        }
        holder.rl1.setBackgroundColor(backgroundColor);

        String task = item.getTask();
        holder.project.setText(task);
        if (item.isPostIt()){holder.project.setTextSize(20);}
        holder.task.setText(task);

        holder.project.setVisibility(item.isProject()||item.isPostIt() ? View.VISIBLE : View.GONE);
        holder.task.setVisibility(item.isProject()||item.isPostIt() ? View.GONE : View.VISIBLE);

        holder.task.setOnCheckedChangeListener(null);//evite le pb de refresh quand on ajoute une ligne
        boolean checked = isChecked(item);
        holder.task.setChecked(checked);

        LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) holder.task.getLayoutParams();
        int levelHierarchical = 0;
        if (isHierarchicalView()) {
            levelHierarchical = item.getHierarchicalLevel() * MARGINGLEVEL;
        }
        params.setMarginStart(levelHierarchical);
        holder.task.setLayoutParams(params);

        int nbSubTask = item.getHierarchicalRootNbSubtask();
        if (nbSubTask > 0 && isHierarchicalView()) {
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
        if (( (parentList != null && parentList.size() > 0) || (childList != null && childList.size() > 0) ) && !item.isPostIt()) {
            holder.childList.setVisibility(detailVisibility);
        } else {
            holder.childList.setVisibility(View.GONE);
        }
        if (parentList != null && parentList.size() > 0) {
            text = text + TaskModel.parentListToString(parentList);
        }
        if (childList != null && childList.size() > 0) {
            String sepa = text != "" ? "\n" : "";
            text = text + sepa + TaskModel.childListToString(childList);
        }
        holder.childList.setText(text);

        afterOnBindViewHolder(holder, item);
    }

    protected abstract void afterOnBindViewHolder(ViewHolder holder, TaskModel item);

    protected abstract boolean isChecked(TaskModel item);

    private boolean createNewWithParent(int holderPosition) {
        TaskModel item = (TaskModel) taskList.toArray()[holderPosition];
        Bundle bundle = new Bundle();
        bundle.putSerializable("parentClass", item);
        AddNewTask fragment = new AddNewTask();
        fragment.setArguments(bundle);
        fragment.show(activity.getSupportFragmentManager(), AddNewTask.TAG);
        return true;
    }


    private void switcheExtendTask(TaskModel item) {
        if (item.isRoot()) {
            if (isOnlyRootView() && expandInOnlyRootView != item.getId()) {
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

    public void deleteAllChecked(int position) {
        activity.delAllChecked();
        activity.refreshData(true);
        notifyItemRemoved(position);
        notifyDataSetChanged();
    }

    public void editItem(int position) {
        TaskModel item = (TaskModel) taskList.toArray()[position];
        Bundle bundle = new Bundle();
        bundle.putInt("id", item.getId());
        bundle.putString("task", item.getTask());
        bundle.putSerializable("taskClass", item);
        AddNewTask fragment = new AddNewTask();
        fragment.setArguments(bundle);
        fragment.show(activity.getSupportFragmentManager(), AddNewTask.TAG);
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
        hierarchicalView = !hierarchicalView;
    }

    public boolean isHierarchicalView() {
        return hierarchicalView;
    }

    public void swapOnlyRootView() {
        onlyRootView = !onlyRootView;
    }

    public boolean isOnlyRootView() {
        return onlyRootView;
    }

    protected abstract void checkedChanged(boolean isChecked, TaskModel item);

    public TaskModel getItem(int position) {
        return (TaskModel) taskList.toArray()[position];
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public CheckBox task;
        public TextView project;
        TextView childList;
        TextView nbSub;
        RelativeLayout rl1;
        LinearLayout ll1;
        CardView cv;

        ViewHolder(View view) {
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
