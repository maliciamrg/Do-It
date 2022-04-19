package net.penguincoders.doit.Adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import net.penguincoders.doit.Model.ToDoModel;
import net.penguincoders.doit.R;

import java.util.List;

public class ParentAdapter extends RecyclerView.Adapter<ParentAdapter.ViewHolder> {

    private List<ToDoModel> todoList;
    private int intExtra;
    public ParentAdapter() {
    }

    public List<ToDoModel> getTodoList() {
        return todoList;
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

        holder.task.setText(item.getTask());

        boolean checked = item.isParent(intExtra);
        holder.task.setChecked(checked);

        List<ToDoModel> parentList = item.getParentList();
        if (parentList != null && parentList.size()>0) {
            holder.childList.setVisibility(View.VISIBLE);
            holder.childList.setText(ToDoModel.parentListToString(parentList));
        } else {
            holder.childList.setVisibility(View.GONE);
            holder.childList.setText("");
        }
        List<ToDoModel> childList = item.getChildList();
        if (childList != null && childList.size()>0) {
            holder.childList.setVisibility(View.VISIBLE);
            holder.childList.setText(holder.childList.getText() + ToDoModel.childListToString(childList));
        } else {
            holder.childList.setVisibility(View.GONE);
            holder.childList.setText("");
        }
    }

    @Override
    public int getItemCount() {
        return todoList.size();
    }

    public void setTasks(List<ToDoModel> todoList, int intExtra) {
        this.todoList = todoList;
        this.intExtra = intExtra;
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