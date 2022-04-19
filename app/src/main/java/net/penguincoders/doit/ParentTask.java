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
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import net.penguincoders.doit.Adapters.ParentAdapter;
import net.penguincoders.doit.Model.ToDoModel;
import net.penguincoders.doit.Utils.DatabaseHandler;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ParentTask extends AppCompatActivity implements DialogCloseListener {

    public static final String EXTRA_ID = "EXTRA_ID";
    public static final String DATA_SERIALIZABLE_EXTRA = "RETURN_MESSAGE";
    public static final int TEXT_REQUEST = 1;
    public static final String EXTRA_TEXT = "EXTRA_TEXT" ;
    private DatabaseHandler db;
    private RecyclerView tasksRecyclerView;
    private ParentAdapter tasksAdapter;
    private TextView TaskText;
    private FloatingActionButton fab;
    private List<ToDoModel> taskList;

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
        TaskText.setText("Parents Task of :\n" + stringExtraMod);


        tasksRecyclerView = findViewById(R.id.tasksRecyclerView);
        tasksRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        tasksAdapter = new ParentAdapter();
        tasksRecyclerView.setAdapter(tasksAdapter);

/*        ItemTouchHelper itemTouchHelper = new
                ItemTouchHelper(new RecyclerItemTouchHelper(tasksAdapter));
        itemTouchHelper.attachToRecyclerView(tasksRecyclerView);*/

        int intExtra = intent.getIntExtra(EXTRA_ID, 0);
        taskList = db.getAllTasks();
        Collections.reverse(taskList);
        tasksAdapter.setTasks(taskList, intExtra);


        fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                List<ToDoModel> todoList = new ArrayList<ToDoModel>();
                for (int i = 0; i < tasksAdapter.getItemCount(); i++) {
                    View viewByPosition = tasksRecyclerView.getLayoutManager().findViewByPosition(i);
                    CheckBox check = viewByPosition.findViewById(R.id.todoCheckBox);
                    if (check.isChecked()) {
                        todoList.add(taskList.get(i));
                    }
                }

                Intent returnIntent = new Intent();
                returnIntent.putExtra(DATA_SERIALIZABLE_EXTRA, (Serializable) todoList);
                setResult(RESULT_OK, returnIntent);
                finish();
            }
        });
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

    @Override
    public void handleDialogClose(DialogInterface dialog) {

    }
}