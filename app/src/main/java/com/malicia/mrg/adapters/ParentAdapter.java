package com.malicia.mrg.adapters;

import android.view.View;
import com.malicia.mrg.Model.TaskModel;
import com.malicia.mrg.ParentActivity;
import com.malicia.mrg.utils.DatabaseHandler;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Map;

public class ParentAdapter extends TaskAdapter {

    private final Map checkBoxOut = new Hashtable();
    private final ParentActivity activity;
    private Integer detailVisility = View.VISIBLE;

    public ParentAdapter(DatabaseHandler db, ParentActivity activity) {
        super(db,activity);
        this.activity = activity;
        do {
            swapViewFilter();
        } while (!isViewRoot());
    }

    @Override
    protected void afterOnBindViewHolder(TaskViewHolder holder, TaskModel item) {
        holder.project.setText("---Project---");
        holder.task.setVisibility(View.VISIBLE);
    }

    protected boolean isChecked(TaskModel item) {
        Object ret = checkBoxOut.get(item.getId());
        return ret==null ? false : (Boolean) ret;
    }

    @Override
    protected void checkedChanged(boolean isChecked, TaskModel item) {
        checkBoxOut.put(item.getId(), isChecked);
    }

    public void setlistParents(ArrayList<TaskModel> listParents) {
        for (TaskModel todoEle : listParents) {
            checkBoxOut.put(todoEle.getId(), true);
        }
    }

    public Map getCheckBoxOut() {
        return checkBoxOut;
    }
}
