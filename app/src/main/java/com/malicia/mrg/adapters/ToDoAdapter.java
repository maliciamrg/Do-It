package com.malicia.mrg.adapters;

import com.malicia.mrg.MainActivity;
import com.malicia.mrg.model.TaskModel;
import com.malicia.mrg.utils.DatabaseHandler;

public class ToDoAdapter extends TaskAdapter {


    public ToDoAdapter(DatabaseHandler db, MainActivity activity) {
        super(db,activity);
    }

    @Override
    protected void afterOnBindViewHolder(TaskViewHolder holder, TaskModel item) {

    }

    @Override
    protected boolean isChecked(TaskModel item) {
        if(!item.isCheckable()) {
            db.updateStatus(item.getId(), false);
            if (item.isStatus()){
                activity.refreshData(true);
            }
        }
        return item.isStatus() && !item.isProject() && item.isCheckable();
    }

    @Override
    protected void checkedChanged(boolean isChecked, TaskModel item) {
            if(item.isCheckable()) {
                db.updateStatus(item.getId(), isChecked);
            }
    }
}
