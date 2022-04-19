package net.penguincoders.doit;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import net.penguincoders.doit.Adapters.ParentAdapter;
import net.penguincoders.doit.Model.ToDoModel;
import net.penguincoders.doit.Utils.DatabaseHandler;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class ParentTask extends AppCompatActivity implements DialogCloseListener {

    public static final String EXTRA_ID = "EXTRA_ID";
    public static final String DATA_SERIALIZABLE_EXTRA = "RETURN_MESSAGE";
    public static final int TEXT_REQUEST = 1;
    public static final String EXTRA_TEXT = "EXTRA_TEXT" ;
    private DatabaseHandler db;
    private RecyclerView tasksRecyclerView;
    private SwipeRefreshLayout swipeRefreshLayout;
    private ParentAdapter tasksAdapter;
    private TextView TaskText;
    private FloatingActionButton fab;
    private List<ToDoModel> taskList;
    private FloatingActionButton fabUp1;
    private FloatingActionButton fabUp2;
    private FloatingActionButton fabUp3;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Intent intent = getIntent();

        db = new DatabaseHandler(this);
        db.openDatabase();

        TaskText = findViewById(R.id.tasksText);

        String stringExtra = intent.getExtras().get(EXTRA_TEXT).toString();
        String stringExtraMod = ToDoModel.stringMax(stringExtra);
        TaskText.setText("Parents of :\n" + stringExtraMod);


        tasksRecyclerView = findViewById(R.id.tasksRecyclerView);
        tasksRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        tasksAdapter = new ParentAdapter();
        tasksRecyclerView.setAdapter(tasksAdapter);

/*        ItemTouchHelper itemTouchHelper = new
                ItemTouchHelper(new RecyclerItemTouchHelper(tasksAdapter));
        itemTouchHelper.attachToRecyclerView(tasksRecyclerView);*/

        refreshData(intent);

        swipeRefreshLayout = ( SwipeRefreshLayout ) findViewById ( R.id.swiperefreshlayout ) ;
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {@Override
        public void onRefresh() {
            refreshData(intent);
            //setting Refreshing to false
            swipeRefreshLayout.setRefreshing(false);

        }
        });

        fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Map checkOutBox = tasksAdapter.getCheckBoxOut();

                List<ToDoModel> todoList = new ArrayList<ToDoModel>();
                for (ToDoModel element : taskList) {
                    Object o = checkOutBox.get(element.getId());
                    if (o!=null && (Boolean) o){
                        todoList.add(element);
                    }
                }

                Intent returnIntent = new Intent();
                returnIntent.putExtra(DATA_SERIALIZABLE_EXTRA, (Serializable) todoList);
                setResult(RESULT_OK, returnIntent);
                finish();
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
                refreshData(intent);
            }
        });

        fabUp2 = findViewById(R.id.fabUp2);
        fabUp3 = findViewById(R.id.fabUp3);

/*
        Serializable input = intent.getSerializableExtra(MainActivity.EXTRA_MESSAGE);

        text1 = findViewById(R.id.textView1);
        text2 = findViewById(R.id.textView2);
        text3 = findViewById(R.id.textView3);

        text1.setText(intent.getStringExtra(MainActivity.EXTRA_MESSAGE1));
        text2.setText(intent.getStringExtra(MainActivity.EXTRA_MESSAGE2));
        text3.setText(intent.getStringExtra(MainActivity.EXTRA_MESSAGE3));


        text1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent returnIntent = new Intent();
                returnIntent.putExtra(MainActivity.RETURN_MESSAGE, text1.getText());
                setResult(RESULT_OK,returnIntent);
                finish();
            }
        });
        text2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent returnIntent = new Intent();
                returnIntent.putExtra(MainActivity.RETURN_MESSAGE, text2.getText());
                setResult(RESULT_OK,returnIntent);
                finish();
            }
        });
        text3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent returnIntent = new Intent();
                returnIntent.putExtra(MainActivity.RETURN_MESSAGE, text3.getText());
                setResult(RESULT_OK,returnIntent);
                finish();
            }
        });*/
    }

    private void refreshData(Intent intent) {
        int intExtra = intent.getIntExtra(EXTRA_ID, 0);
        taskList = db.getAllTasks();
        Collections.reverse(taskList);
        tasksAdapter.setTasks(taskList, intExtra);
    }

    @Override
    public void handleDialogClose(DialogInterface dialog) {

    }
}