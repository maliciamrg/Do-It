package net.penguincoders.doit;

import android.os.Bundle;

import com.malicia.mrg.activity.RootActivity;
import com.malicia.mrg.adapters.TaskAdapter;
import net.penguincoders.doit.Adapters.ToDoAdapter;
import net.penguincoders.doit.Model.ToDoModel;
import net.penguincoders.doit.Utils.DatabaseHandler;

import java.util.Map;

public class MainActivity extends RootActivity {

    public static final String LOG_TAG = "DoIt_App";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void fabOnClick() {
        AddNewTask.newInstance().show(getSupportFragmentManager(), AddNewTask.TAG);
    }

    @Override
    protected TaskAdapter getTasksAdapter(DatabaseHandler db) {
        return new ToDoAdapter(db, MainActivity.this);
    }

    @Override
    protected Map<Integer, ToDoModel> getTaskList(DatabaseHandler db) {
        return db.getAllTasks();
    }

}