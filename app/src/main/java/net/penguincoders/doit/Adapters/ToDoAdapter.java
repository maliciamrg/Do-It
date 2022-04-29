package net.penguincoders.doit.Adapters;

import com.malicia.mrg.adapters.TaskAdapter;
import net.penguincoders.doit.MainActivity;
import net.penguincoders.doit.Model.ToDoModel;
import net.penguincoders.doit.Utils.DatabaseHandler;

public class ToDoAdapter extends TaskAdapter {


    public ToDoAdapter(DatabaseHandler db, MainActivity activity) {
        super(db,activity);
    }

    @Override
    protected void afterOnBindViewHolder(ViewHolder holder, ToDoModel item) {

    }

    @Override
    protected boolean isChecked(ToDoModel item) {
        return item.isStatus() && !item.isProject();
    }

    @Override
    protected void checkedChanged(boolean isChecked, ToDoModel item) {
            db.updateStatus(item.getId(), isChecked);
    }
}
