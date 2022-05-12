package net.penguincoders.doit;

import com.malicia.mrg.activity.TaskActivity;
import com.malicia.mrg.adapters.TaskAdapter;
import net.penguincoders.doit.Adapters.ToDoAdapter;

public class MainActivity extends TaskActivity {

    public static final String LOG_TAG = "DoIt_App";
    private ToDoAdapter toDoAdapter;


    @Override
    protected void fabOnClick() {
        AddNewTask.newInstance().show(getSupportFragmentManager(), AddNewTask.TAG);
    }

    @Override
    protected TaskAdapter getTasksAdapter() {
        toDoAdapter = new ToDoAdapter(db, MainActivity.this);
        return toDoAdapter;
    }

}