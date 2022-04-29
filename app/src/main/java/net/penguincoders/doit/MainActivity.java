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