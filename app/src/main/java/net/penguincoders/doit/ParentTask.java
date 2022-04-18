package net.penguincoders.doit;

import android.content.DialogInterface;
import android.content.Intent;
import android.view.View;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import net.penguincoders.doit.Adapters.ToDoAdapter;
import net.penguincoders.doit.Adapters.ToDoParentAdapter;
import net.penguincoders.doit.Model.ToDoModel;
import net.penguincoders.doit.Utils.DatabaseHandler;

import java.util.Collections;
import java.util.List;

public class ParentTask extends AppCompatActivity implements DialogCloseListener{

    public static final String EXTRA_ID = "EXTRA_ID";
    public static final String RETURN_EXTRA_ID = "RETURN_MESSAGE";
    private DatabaseHandler db;
    private RecyclerView tasksRecyclerView;
    private ToDoParentAdapter tasksAdapter;
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
        TaskText.setText("Parents Task");

        tasksRecyclerView = findViewById(R.id.tasksRecyclerView);
        tasksRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        tasksAdapter = new ToDoParentAdapter();
        tasksRecyclerView.setAdapter(tasksAdapter);

/*        ItemTouchHelper itemTouchHelper = new
                ItemTouchHelper(new RecyclerItemTouchHelper(tasksAdapter));
        itemTouchHelper.attachToRecyclerView(tasksRecyclerView);*/

        int intExtra = intent.getIntExtra(EXTRA_ID, 0);
        taskList = db.getPotentialParentTasks(intExtra);
        Collections.reverse(taskList);

        tasksAdapter.setTasks(taskList);
        
        fab = findViewById(R.id.fab);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent returnIntent = new Intent();
                returnIntent.putExtra(RETURN_EXTRA_ID, 1);
                setResult(RESULT_OK,returnIntent);
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