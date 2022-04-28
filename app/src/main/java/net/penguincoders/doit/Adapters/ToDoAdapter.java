package net.penguincoders.doit.Adapters;

import com.malicia.mrg.adapters.TaskAdapter;
import net.penguincoders.doit.MainActivity;
import net.penguincoders.doit.Model.ToDoModel;
import net.penguincoders.doit.Utils.DatabaseHandler;

public class ToDoAdapter extends TaskAdapter {

    private DatabaseHandler db;
    private MainActivity activity;

    public ToDoAdapter(DatabaseHandler db, MainActivity activity) {
        super(db,activity);
        this.db = db;
        this.activity = activity;
    }

    @Override
    protected void checkedChanged(boolean isChecked, ToDoModel item) {
            db.updateStatus(item.getId(), isChecked);
    }
}
