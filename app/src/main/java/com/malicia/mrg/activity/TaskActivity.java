package com.malicia.mrg.activity;

import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.malicia.mrg.adapters.TaskAdapter;
import com.malicia.mrg.utils.BuildHierarchyTree;
import com.malicia.mrg.utils.HierarchyData;
import com.malicia.mrg.DialogCloseListener;
import com.malicia.mrg.Model.TaskModel;
import com.malicia.mrg.Model.ToDoModel;
import com.malicia.mrg.R;
import com.malicia.mrg.RecyclerItemTouchHelper;
import com.malicia.mrg.utils.DatabaseHandler;

import java.util.*;

public abstract class TaskActivity extends AppCompatActivity implements DialogCloseListener {

    protected DatabaseHandler db;
    protected HashMap<Integer, ToDoModel> todoList;
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
        setImgDetailVivsibility();
        fabUp1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tasksAdapter.swapDetailVisibility();
                setImgDetailVivsibility();
                refreshData(false);
            }
        });

        fabUp2 = findViewById(R.id.fabUp2);
        setImgHierarchicalView();
        fabUp2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tasksAdapter.swapHierarchicalView();
                setImgHierarchicalView();
                refreshData(false);
            }
        });

        fabUp3 = findViewById(R.id.fabUp3);
        setImgOnlyRootView();
        fabUp3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tasksAdapter.swapViewFilter();
                setImgOnlyRootView();
                refreshData(false);
            }
        });
    }

    private void setImgOnlyRootView() {
        if (tasksAdapter.isViewRoot()) {
            fabUp3.setImageResource(android.R.drawable.ic_media_rew);
        }
        if (tasksAdapter.isViewAll()) {
            fabUp3.setImageResource(android.R.drawable.ic_media_pause);
        }
        if (tasksAdapter.isViewFirst()) {
            fabUp3.setImageResource(android.R.drawable.ic_media_ff);
        }
    }

    private void setImgHierarchicalView() {
        if (tasksAdapter.isHierarchical()) {
            fabUp2.setImageResource(android.R.drawable.ic_menu_sort_by_size);
        }
        if (tasksAdapter.isAlphabet()) {
            fabUp2.setImageResource(android.R.drawable.ic_menu_sort_alphabetically);
        }
    }

    private void setImgDetailVivsibility() {
        if (tasksAdapter.isDetailVisible()) {
            fabUp1.setImageResource(android.R.drawable.arrow_down_float);
        } else {
            fabUp1.setImageResource(android.R.drawable.arrow_up_float);
        }
    }

    protected abstract void fabOnClick();


    protected abstract TaskAdapter getTasksAdapter();


    public void refreshData(Boolean refreskTaskList) {
        if (refreskTaskList) {
            todoList = db.getAllTasks();
            tasksAdapter.setTodo(todoList);
        }

        List<TaskModel> orderedTaskList = new ArrayList<>();
        if (tasksAdapter.isHierarchical()) {
            //tri hierarchically

            BuildHierarchyTree taskTree = new BuildHierarchyTree(db.getAllLinks());

            for (ToDoModel todoIn : todoList.values()) {

                //, "HierarchyTree \n empty project:"
                //, "HierarchyTree \n solo task:"
                //, "HierarchyTree \n project:"
                //, "HierarchyTree \n master task:"
                if (db.getAllParentTasks(todoIn.getId()).size() == 0 || todoIn.isProject()) {

                    taskTree.buildHierarchyTree(todoIn.getId());

                    List<HierarchyData> hierarchyTasks = taskTree.printHierarchyTree(todoIn.getId(), 0);

                    int rank = 0;
                    for (HierarchyData eleHierarchyTasks : hierarchyTasks) {

                        TaskModel ele = new TaskModel(todoList.get(eleHierarchyTasks.getTaskId()));
                        if (rank == 0) {
                            ele.setHierarchicalRootNbSubtask(hierarchyTasks.size() - 1);
                        }
                        ele.setHierarchicalRoot(todoIn.getId());
                        ele.setHierarchicalRank(rank);
                        ele.setHierarchicalLevel(eleHierarchyTasks.getLevel());
                        ele.setChildList(db.getAllChildTasks(eleHierarchyTasks.getTaskId()));
                        ele.setParentList(db.getAllParentTasks(eleHierarchyTasks.getTaskId()));
                        orderedTaskList.add(ele);
                        rank++;

                    }

                }
            }
//            Map<Integer, Map<Integer, TaskModel>> hHerar = new LinkedHashMap<>();
//            for (TaskModel todoEle : taskList) {
//                Map<Integer, TaskModel> hRank = new HashMap<>();
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
//            for (Map<Integer, TaskModel> hHerarEle : hHerar.values()) {
//                for (int i = 0; i < hHerarEle.size(); i++) {
//                    TaskModel toDoModel = hHerarEle.get(i);
//                    orderedTaskList.add(toDoModel);
//                }
//            }
        } else {
            for (ToDoModel todoIn : todoList.values()) {
                TaskModel taskOut = new TaskModel(todoIn);
                taskOut.setChildList(db.getAllChildTasks(todoIn.getId()));
                taskOut.setParentList(db.getAllParentTasks(todoIn.getId()));
                orderedTaskList.add(taskOut);
            }
//            orderedTaskList = new ArrayList<TaskModel>(todoList.values());
        }

        List<TaskModel> orderedAndFilteredTaskList = new ArrayList<>();


        for (int i = 0; i < orderedTaskList.size(); i++) {
            TaskModel taskEle = orderedTaskList.get(i);
            if (taskEle.isPostIt()) {
                if (taskEle.getHierarchicalRank() == 0) {
                    orderedTaskList.remove(i);
                } else {
                    i++;
                }
                TaskModel taskElePostItZone = new TaskModel(taskEle);
                taskElePostItZone.setInPostItZone(true);
                orderedTaskList.add(0, taskElePostItZone);
            }
        }

        switch (tasksAdapter.getViewFilter()) {
            case ROOT:
                for (TaskModel todoEle : orderedTaskList) {
                    if (todoEle.getParentList().size() == 0
                            || todoEle.isHierarchicalRoot(tasksAdapter.getExpandInOnlyRootView())
                            || (todoEle.isProject() && todoEle.getHierarchicalRank() == 0)
                            || (todoEle.isPostIt() && todoEle.isInPostItZone())) {
                        orderedAndFilteredTaskList.add(todoEle);
                    }
                }
                break;
            case ALL:
                tasksAdapter.setExpandInOnlyRootView(0);
                orderedAndFilteredTaskList = orderedTaskList;
                break;
            case FIRST:
                for (TaskModel todoEle : orderedTaskList) {
                    if (todoEle.getChildList().size() == 0
                            || (todoEle.isPostIt() && todoEle.isInPostItZone())) {
                        orderedAndFilteredTaskList.add(todoEle);
                    }
                }
                break;
            default:
                throw new IllegalStateException("Unexpected value: " + tasksAdapter.getViewFilter());
        }

        TextView TaskText = findViewById(R.id.tasksText);
        TaskText.setText("Tasks :" + todoList.size());

        tasksAdapter.setTasks(orderedAndFilteredTaskList);
        notifyDataSetChanged();
    }

    private void notifyDataSetChanged() {
        tasksRecyclerView.post(new Runnable() {
            @Override
            public void run() {
                tasksAdapter.notifyDataSetChanged();
            }
        });
    }

    @Override
    public void handleDialogClose(DialogInterface dialog) {
        refreshData(true);
        notifyDataSetChanged();
    }

    public int delAllChecked() {
        int n = 0;
        for (ToDoModel element : new ArrayList<ToDoModel>(todoList.values())) {
            if (!element.isProject() && element.isStatus()) {
                db.deleteTask(element.getId());
                n++;
            }
        }
        return n;
    }
}