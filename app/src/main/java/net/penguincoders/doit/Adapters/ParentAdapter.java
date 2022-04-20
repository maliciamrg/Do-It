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

    private Integer detailVisility = View.VISIBLE;
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

        holder.project.setText("--project--");
        holder.task.setText(item.getTask());

        holder.project.setVisibility(item.isProject()?View.VISIBLE:View.GONE);
        holder.task.setVisibility(View.VISIBLE);

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


    public void setTasks(List<ToDoModel> todoList, int intExtra) {
        this.todoList = todoList;
        this.intExtra = intExtra;
        notifyDataSetChanged();
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
