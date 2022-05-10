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
import com.malicia.mrg.utils.BuildHierarchyTree;
import net.penguincoders.doit.DialogCloseListener;
import net.penguincoders.doit.Model.ToDoModel;
import net.penguincoders.doit.R;
import net.penguincoders.doit.RecyclerItemTouchHelper;
import net.penguincoders.doit.Utils.DatabaseHandler;

import java.util.*;

public abstract class TaskActivity extends AppCompatActivity implements DialogCloseListener {

    protected DatabaseHandler db;
    protected HashMap<Integer,ToDoModel> taskList;
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

        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(new RecyclerItemTouchHelper(tasksAdapter));
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

        List<ToDoModel> orderedTaskList = new ArrayList<>();
        if (tasksAdapter.isHierarchicalView()) {
            //tri hierarchically


            BuildHierarchyTree taskTree = new BuildHierarchyTree(db.getAllLinks());
            for (ToDoModel task : taskList.values()) {

                List<ToDoModel> childList = db.getAllChildTasks(task.getId());
                task.setChildList(childList);

                List<ToDoModel> parentList = db.getAllParentTasks(task.getId());
                task.setParentList(parentList);

                //, "HierarchyTree \n empty project:"
                //, "HierarchyTree \n solo task:"
                //, "HierarchyTree \n project:"
                //, "HierarchyTree \n master task:"
                if (task.getParentList().size() == 0 ) {
                    taskTree.buildHierarchyTree(task.getId());
                    Map<Integer, Integer> hierarchyTasks = taskTree.printHierarchyTree(task.getId(), 0);
                    int rank = 0;
                    for (Integer subTaskId : hierarchyTasks.keySet()) {
                        ToDoModel ele = taskList.get(subTaskId);
                        if (rank==0) {
                            ele.setHierarchicalRootNbSubtask(hierarchyTasks.size()-1);
                        }
                        ele.setHierarchicalRoot(task.getId());
                        ele.setHierarchicalRank(rank);
                        ele.setHierarchicalLevel(hierarchyTasks.get(subTaskId));
                        orderedTaskList.add(ele);
                        rank++;
                    }
                }
            }
//            Map<Integer, Map<Integer, ToDoModel>> hHerar = new LinkedHashMap<>();
//            for (ToDoModel todoEle : taskList) {
//                Map<Integer, ToDoModel> hRank = new HashMap<>();
//                if (hHerar.containsKey(todoEle.getHierarchicalRoot())) {
//                    hRank = hHerar.get(todoEle.getHierarchicalRoot());
//                    hRank.put(todoEle.getHierarchicalRank(), todoEle);
//                    hHerar.put(todoEle.getHierarchicalRoot(), hRank);
//                } else {
//                    hRank.put(todoEle.getHierarchicalRank(), todoEle);
//                    hHerar.put(todoEle.getHierarchicalRoot(), hRank);
//                }
//            }
//            //mise en forme
//            for (Map<Integer, ToDoModel> hHerarEle : hHerar.values()) {
//                for (int i = 0; i < hHerarEle.size(); i++) {
//                    ToDoModel toDoModel = hHerarEle.get(i);
//                    orderedTaskList.add(toDoModel);
//                }
//            }
        } else {
            orderedTaskList = new ArrayList<ToDoModel>(taskList.values());
        }

        List<ToDoModel> orderedAndFilteredTaskList = new ArrayList<>();
        if (tasksAdapter.isOnlyRootView()) {
            for (ToDoModel todoEle : orderedTaskList) {
                if(todoEle.getParentList().size()==0 || todoEle.isHierarchicalRoot(tasksAdapter.getExpandInOnlyRootView())){
                    orderedAndFilteredTaskList.add(todoEle);
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

    public void delAllChecked() {
        for (ToDoModel element : new ArrayList<ToDoModel>(taskList.values())) {
            if (!element.isProject() && element.isStatus()){
                db.deleteTask(element.getId());
            }
        }
    }
}