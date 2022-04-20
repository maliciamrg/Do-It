package net.penguincoders.doit.Adapters;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;

import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import net.penguincoders.doit.AddNewTask;
import net.penguincoders.doit.MainActivity;
import net.penguincoders.doit.Model.ToDoModel;
import net.penguincoders.doit.R;
import net.penguincoders.doit.Utils.DatabaseHandler;

import java.util.List;

public class ToDoAdapter extends RecyclerView.Adapter<ToDoAdapter.ViewHolder> {

    private Integer detailVisility = View.VISIBLE;
    private List<ToDoModel> todoList;
    private DatabaseHandler db;
    private MainActivity activity;

    public ToDoAdapter(DatabaseHandler db, MainActivity activity) {
        this.db = db;
        this.activity = activity;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.task_layout, parent, false);
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {
        db.openDatabase();

        final ToDoModel item = todoList.get(position);

//        StringBuilder str = new StringBuilder();
//        str.append("\n" );
//        for (ToDoModel element : todoList) {
//            str.append(element.toString());
//            str.append("\n" );
//        }
//        System.out.println(str.toString());

        holder.project.setText(item.getTask());
        holder.task.setText(item.getTask());

        holder.project.setVisibility(item.isProject()?View.VISIBLE:View.GONE);
        holder.task.setVisibility(item.isProject()?View.GONE:View.VISIBLE);

        holder.task.setOnCheckedChangeListener(null);//evite le pb de refresh quand on ajoute une ligne
        holder.task.setChecked(item.isStatus() && !item.isProject());
        holder.task.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    db.updateStatus(item.getId(), true);
                } else {
                    db.updateStatus(item.getId(), false);
                }
            }
        });


        String text = "";
        List<ToDoModel> parentList = item.getParentList();
        List<ToDoModel> childList = item.getChildList();
        if ((parentList != null && parentList.size()>0)||(childList != null && childList.size()>0)) {
            holder.childList.setVisibility(detailVisility);
        } else {
            holder.childList.setVisibility(View.GONE);
        }
        if (parentList != null && parentList.size()>0) {
            text = text + ToDoModel.parentListToString(parentList);
        }
        if (childList != null && childList.size()>0) {
            String sepa = text != "" ? "\n" : "";
            text = text + sepa + ToDoModel.childListToString(childList);
        }
        holder.childList.setText(text);
    }

    @Override
    public int getItemCount() {
        return todoList.size();
    }

    public Context getContext() {
        return activity;
    }

    public void setTasks(List<ToDoModel> todoList) {
        this.todoList = todoList;
        notifyDataSetChanged();
    }

    public void deleteItem(int position) {
        ToDoModel item = todoList.get(position);
        db.deleteTask(item.getId());
        todoList.remove(position);
//        notifyItemRemoved(position);
        notifyDataSetChanged();
    }

    public void editItem(int position) {
        ToDoModel item = todoList.get(position);
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
        if (detailVisility == View.VISIBLE) {
            detailVisility = View.GONE;
        } else {
            detailVisility = View.VISIBLE;
        }
    }
    public boolean isDetailVisible() {
        if (detailVisility == View.VISIBLE) {
            return true;
        }
        return false;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        CheckBox task;
        TextView childList;
        TextView project;
        ViewHolder(View view) {
            super(view);
            task = view.findViewById(R.id.todoCheckBox);
            project = view.findViewById(R.id.textView3);
            childList = view.findViewById(R.id.textView);
        }
    }
}
