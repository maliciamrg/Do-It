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

import com.malicia.mrg.activity.RootActivity;
import net.penguincoders.doit.AddNewTask;
import net.penguincoders.doit.Model.ToDoModel;
import net.penguincoders.doit.R;
import net.penguincoders.doit.Utils.DatabaseHandler;

import java.util.List;
import java.util.Map;

public abstract class TaskAdapter extends RecyclerView.Adapter<TaskAdapter.ViewHolder> {

    public static final int MARGINGLEVEL = 50;
    protected final DatabaseHandler db;
    private final RootActivity activity;
    private Integer detailVisibility = View.VISIBLE;
    private boolean hierarchicalView = true;
    private boolean onlyRootView = false;
    private Map<Integer, ToDoModel> todoList;
    private View itemView;
    private int expandInOnlyRootView = 0;

    public TaskAdapter(DatabaseHandler db, RootActivity activity) {
        this.db = db;
        this.activity = activity;
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
    public void onBindViewHolder(@NonNull final TaskAdapter.ViewHolder holder,int position) {
        db.openDatabase();
        int holderPosition = holder.getAdapterPosition();
        final ToDoModel item = (ToDoModel) todoList.values().toArray()[holderPosition];

        holder.rl1.setBackgroundColor(todoList.get(item.getHierarchicalRoot()).getBackgroundColor());

        String task = item.getTask();
        holder.project.setText(task);
        holder.task.setText(task);

        holder.project.setVisibility(item.isProject() ? View.VISIBLE : View.GONE);
        holder.task.setVisibility(item.isProject() ? View.GONE : View.VISIBLE);

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
                checkedChanged(isChecked, item );
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
        List<ToDoModel> parentList = item.getParentList();
        List<ToDoModel> childList = item.getChildList();
        if ((parentList != null && parentList.size() > 0) || (childList != null && childList.size() > 0)) {
            holder.childList.setVisibility(detailVisibility);
        } else {
            holder.childList.setVisibility(View.GONE);
        }
        if (parentList != null && parentList.size() > 0) {
            text = text + ToDoModel.parentListToString(parentList);
        }
        if (childList != null && childList.size() > 0) {
            String sepa = text != "" ? "\n" : "";
            text = text + sepa + ToDoModel.childListToString(childList);
        }
        holder.childList.setText(text);

        afterOnBindViewHolder(holder,item);
    }

    protected abstract void afterOnBindViewHolder(ViewHolder holder, ToDoModel item);

    protected abstract boolean isChecked(ToDoModel item);

    private boolean createNewWithParent(int holderPosition) {
        ToDoModel item = (ToDoModel) todoList.values().toArray()[holderPosition];
        Bundle bundle = new Bundle();
        bundle.putSerializable("parentClass", item);
        AddNewTask fragment = new AddNewTask();
        fragment.setArguments(bundle);
        fragment.show(activity.getSupportFragmentManager(), AddNewTask.TAG);
        return true;
    }


    private void switcheExtendTask(ToDoModel item) {
        if (isOnlyRootView() && expandInOnlyRootView != item.getId()) {
            expandInOnlyRootView = item.getId();
        } else {
            expandInOnlyRootView = 0;
        }
        activity.refreshData(false);
    }

    @Override
    public int getItemCount() {
        return todoList.size();
    }

    public Context getContext() {
        return activity;
    }

    public void setTasks(Map<Integer, ToDoModel> todoList) {
        this.todoList = todoList;
        notifyDataSetChanged();
    }

    public void deleteItem(int position) {
        ToDoModel item = (ToDoModel) todoList.values().toArray()[position];
        db.deleteTask(item.getId());
        todoList.remove(item.getId());
        activity.refreshData(true);
        notifyItemRemoved(position);
        notifyDataSetChanged();
    }

    public void deleteAllChecked(int position) {
        for (ToDoModel element : todoList.values()) {
            if (!element.isProject() && element.isStatus()){
                db.deleteTask(element.getId());
            }
        }
        activity.refreshData(true);
        notifyItemRemoved(position);
        notifyDataSetChanged();
    }

    public void editItem(int position) {
        ToDoModel item = (ToDoModel) todoList.values().toArray()[position];
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

    protected abstract void checkedChanged(boolean isChecked, ToDoModel item);

    public ToDoModel getItem(int position) {
        return (ToDoModel) todoList.values().toArray()[position];
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public CheckBox task;
        TextView childList;
        public TextView project;
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
