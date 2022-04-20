package net.penguincoders.doit;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;

import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import net.penguincoders.doit.Adapters.ToDoAdapter;
import net.penguincoders.doit.Model.ToDoModel;
import net.penguincoders.doit.Utils.DatabaseHandler;

import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class MainActivity extends AppCompatActivity implements DialogCloseListener{

    public static final String LOG_TAG = "DoIt_App";
    private DatabaseHandler db;
    private RecyclerView tasksRecyclerView;
    private SwipeRefreshLayout swipeRefreshLayout;
    private ToDoAdapter tasksAdapter;
    private FloatingActionButton fab;
    private FloatingActionButton fabUp1;
    private FloatingActionButton fabUp2;
    private FloatingActionButton fabUp3;

    private List<ToDoModel> taskList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Objects.requireNonNull(getSupportActionBar()).hide();

        db = new DatabaseHandler(this);
        db.openDatabase();

        tasksRecyclerView = findViewById(R.id.tasksRecyclerView);
        tasksRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        tasksAdapter = new ToDoAdapter(db,MainActivity.this);
        tasksRecyclerView.setAdapter(tasksAdapter);

        ItemTouchHelper itemTouchHelper = new
                ItemTouchHelper(new RecyclerItemTouchHelper(tasksAdapter));
        itemTouchHelper.attachToRecyclerView(tasksRecyclerView);

        refreshData();

        swipeRefreshLayout = ( SwipeRefreshLayout ) findViewById ( R.id.swiperefreshlayout ) ;
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {@Override
        public void onRefresh() {
            refreshData();
            //setting Refreshing to false
            swipeRefreshLayout.setRefreshing(false);

        }
        });

        fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AddNewTask.newInstance().show(getSupportFragmentManager(), AddNewTask.TAG);
            }
        });

        fabUp1 = findViewById(R.id.fabUp1);
        fabUp1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tasksAdapter.swapDetailVisibility();
                if (tasksAdapter.isDetailVisible()){
                    fabUp1.setImageResource(android.R.drawable.btn_minus);
                } else {
                    fabUp1.setImageResource(android.R.drawable.btn_plus);
                }

                refreshData();
            }
        });

        fabUp2 = findViewById(R.id.fabUp2);
        fabUp3 = findViewById(R.id.fabUp3);
    }

    private void refreshData() {
        taskList = db.getAllTasks();
        Collections.reverse(taskList);
        tasksAdapter.setTasks(taskList);
    }

    @Override
    public void handleDialogClose(DialogInterface dialog){
        refreshData();
        tasksAdapter.notifyDataSetChanged();
    }
}