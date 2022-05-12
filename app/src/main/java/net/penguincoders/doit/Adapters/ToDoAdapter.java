package net.penguincoders.doit.Adapters;

import com.malicia.mrg.adapters.TaskAdapter;
import net.penguincoders.doit.MainActivity;
import net.penguincoders.doit.Model.TaskModel;
import net.penguincoders.doit.Model.ToDoModel;
import net.penguincoders.doit.Utils.DatabaseHandler;

public class ToDoAdapter extends TaskAdapter {


    public ToDoAdapter(DatabaseHandler db, MainActivity activity) {
        super(db,activity);
    }

    @Override
    protected void afterOnBindViewHolder(ViewHolder holder, TaskModel item) {

    }

    @Override
    protected boolean isChecked(TaskModel item) {
        if(!item.isCheckable()) {
            db.updateStatus(item.getId(), false);
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
