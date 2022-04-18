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

public class ToDoParentAdapter extends RecyclerView.Adapter<ToDoParentAdapter.ViewHolder> {

    private List<ToDoModel> todoList;

    public ToDoParentAdapter() {
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
        final ToDoModel item = todoList.get(position);

        holder.task.setText(String.valueOf(position) + " - " +String.valueOf(item.getId()) + " - " + item.getTask());
        holder.task.setChecked(item.getStatus());

        List<ToDoModel> childList = item.getChildList();
        if (childList != null && childList.size()>0) {
            holder.childList.setVisibility(View.VISIBLE);
            holder.childList.setText(childListToString(childList));
        } else {
            holder.childList.setVisibility(View.GONE);
            holder.childList.setText("");
        }
    }

    private String childListToString(List<ToDoModel> childList) {
        StringBuilder str = new StringBuilder();
        for (ToDoModel element : childList) {
            str.append(element.getStatus()?"☒":"☐");
            str.append("-");
            str.append(element.getTask());
            str.append("\n" );
        }
        return str.toString();
    }


    @Override
    public int getItemCount() {
        return todoList.size();
    }

    public void setTasks(List<ToDoModel> todoList) {
        this.todoList = todoList;
        notifyDataSetChanged();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        CheckBox task;
        TextView childList;
        ViewHolder(View view) {
            super(view);
            task = view.findViewById(R.id.todoCheckBox);
            childList = view.findViewById(R.id.textView);
        }
    }
}
