package net.penguincoders.doit.Adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.collection.ArraySet;
import androidx.recyclerview.widget.RecyclerView;
import net.penguincoders.doit.Model.ToDoModel;
import net.penguincoders.doit.R;
import org.json.JSONArray;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

public class ParentAdapter extends RecyclerView.Adapter<ParentAdapter.ViewHolder> {

    private List<ToDoModel> todoList;
    private int intExtra;
    private final Map checkBoxOut = new Hashtable();

    public ParentAdapter() {
    }

    public Map getCheckBoxOut() {
        return checkBoxOut;
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
        checkBoxOut.put(item.getId(), checked);
        holder.task.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                checkBoxOut.put(item.getId(), isChecked);
            }
        });

        String text = "";
        List<ToDoModel> parentList = item.getParentList();
        List<ToDoModel> childList = item.getChildList();
        if ((parentList != null && parentList.size()>0)||(childList != null && childList.size()>0)) {
            holder.childList.setVisibility(View.VISIBLE);
        } else {
            holder.childList.setVisibility(View.GONE);
        }
        if (parentList != null && parentList.size()>0) {
            text = text + ToDoModel.parentListToString(parentList);
        }
        if (childList != null && childList.size()>0) {
            text = text + ToDoModel.childListToString(childList);
        }
        holder.childList.setText(text);

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
