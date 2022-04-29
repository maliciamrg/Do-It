package com.malicia.mrg.activity;

import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.malicia.mrg.adapters.TaskAdapter;
import net.penguincoders.doit.DialogCloseListener;
import net.penguincoders.doit.Model.ToDoModel;
import net.penguincoders.doit.R;
import net.penguincoders.doit.RecyclerItemTouchHelper;
import net.penguincoders.doit.Utils.DatabaseHandler;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;

public abstract class RootActivity extends AppCompatActivity implements DialogCloseListener {

    protected DatabaseHandler db;
    protected Map<Integer, ToDoModel> taskList;
    private RecyclerView tasksRecyclerView;
    private SwipeRefreshLayout swipeRefreshLayout;
    private TaskAdapter tasksAdapter;
    private FloatingActionButton fab;
    private FloatingActionButton fabUp1;
    private FloatingActionButton fabUp2;
    private FloatingActionButton fabUp3;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        Objects.requireNonNull(getSupportActionBar()).hide();

        db = new DatabaseHandler(this);
        db.openDatabase();

        tasksRecyclerView = findViewById(R.id.tasksRecyclerView);
        tasksRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        tasksAdapter = getTasksAdapter();

        tasksRecyclerView.setAdapter(tasksAdapter);

        ItemTouchHelper itemTouchHelper = new
                ItemTouchHelper(new RecyclerItemTouchHelper(tasksAdapter));
        itemTouchHelper.attachToRecyclerView(tasksRecyclerView);

        refreshData(true);

        swipeRefreshLayout = findViewById(R.id.swiperefreshlayout);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refreshData(true);
                //setting Refreshing to false
                swipeRefreshLayout.setRefreshing(false);

            }
        });

        fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fabOnClick();
            }
        });

        fabUp1 = findViewById(R.id.fabUp1);
        fabUp1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tasksAdapter.swapDetailVisibility();
                if (tasksAdapter.isDetailVisible()) {
                    fabUp1.setImageResource(android.R.drawable.arrow_down_float);
                } else {
                    fabUp1.setImageResource(android.R.drawable.arrow_up_float);
                }

                refreshData(false);
            }
        });

        fabUp2 = findViewById(R.id.fabUp2);
        fabUp2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tasksAdapter.swapHierarchicalView();
                if (tasksAdapter.isHierarchicalView()) {
                    fabUp2.setImageResource(android.R.drawable.ic_menu_sort_by_size);
                } else {
                    fabUp2.setImageResource(android.R.drawable.ic_menu_sort_alphabetically);
                }
                refreshData(false);
            }
        });

        fabUp3 = findViewById(R.id.fabUp3);
        fabUp3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tasksAdapter.swapOnlyRootView();
                if (tasksAdapter.isOnlyRootView()) {
                    fabUp3.setImageResource(android.R.drawable.ic_media_rew);
                } else {
                    fabUp3.setImageResource(android.R.drawable.ic_media_ff);
                }
                refreshData(false);
            }
        });
    }

    protected abstract void fabOnClick();


    protected abstract TaskAdapter getTasksAdapter();


    public void refreshData(Boolean refreskTaskList) {
        if (refreskTaskList) {
            taskList = db.getAllTasks();
        }

        Map<Integer, ToDoModel> orderedTaskList = new LinkedHashMap<>();
        if (tasksAdapter.isHierarchicalView()) {
            //tri hierarchically
            Map<Integer, Map<Integer, ToDoModel>> hHerar = new LinkedHashMap<>();
            for (ToDoModel todoEle : taskList.values()) {
                Map<Integer, ToDoModel> hRank = new HashMap<>();
                if (hHerar.containsKey(todoEle.getHierarchicalRoot())) {
                    hRank = hHerar.get(todoEle.getHierarchicalRoot());
                    hRank.put(todoEle.getHierarchicalRank(), todoEle);
                    hHerar.put(todoEle.getHierarchicalRoot(), hRank);
                } else {
                    hRank.put(todoEle.getHierarchicalRank(), todoEle);
                    hHerar.put(todoEle.getHierarchicalRoot(), hRank);
                }
            }
            //mise en forme
            for (Map<Integer, ToDoModel> hHerarEle : hHerar.values()) {
                for (int i = 0; i < hHerarEle.size(); i++) {
                    ToDoModel toDoModel = hHerarEle.get(i);
                    orderedTaskList.put(toDoModel.getId(), toDoModel);
                }
            }
        } else {
            orderedTaskList = taskList;
        }

        Map<Integer, ToDoModel> orderedAndFilteredTaskList = new LinkedHashMap<>();
        if (tasksAdapter.isOnlyRootView()) {
            for (ToDoModel todoEle : orderedTaskList.values()) {
                if(todoEle.getParentList().size()==0 || tasksAdapter.getExpandInOnlyRootView()==todoEle.getHierarchicalRoot()){
                    orderedAndFilteredTaskList.put(todoEle.getId(), todoEle);
                }
            }
        } else {
            tasksAdapter.setExpandInOnlyRootView(0);
            orderedAndFilteredTaskList = orderedTaskList;
        }


        tasksAdapter.setTasks(orderedAndFilteredTaskList);
    }

    @Override
    public void handleDialogClose(DialogInterface dialog) {
        refreshData(true);
        tasksAdapter.notifyDataSetChanged();
    }
}