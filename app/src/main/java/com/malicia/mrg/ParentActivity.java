package com.malicia.mrg;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;
import com.malicia.mrg.activity.TaskActivity;
import com.malicia.mrg.adapters.TaskAdapter;
import com.malicia.mrg.adapters.ParentAdapter;
import com.malicia.mrg.Model.TaskModel;
import com.malicia.mrg.R;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ParentActivity extends TaskActivity {

    public static final String EXTRA_LIST_PARENT = "EXTRA_LIST_PARENT";
    public static final String DATA_SERIALIZABLE_EXTRA = "RETURN_MESSAGE";
    public static final int TEXT_REQUEST = 1;
    public static final String EXTRA_TEXT = "EXTRA_TEXT" ;
    private ParentAdapter parentAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent intent = getIntent();
        String stringExtra = intent.getExtras().get(EXTRA_TEXT).toString();
        ArrayList<TaskModel> listParents = (ArrayList<TaskModel>) intent.getSerializableExtra(EXTRA_LIST_PARENT);

        TextView TaskText = findViewById(R.id.tasksText);
        String stringExtraMod = TaskModel.stringMax(stringExtra);
        TaskText.setText("Parents of :\n" + stringExtraMod);

        parentAdapter.setlistParents(listParents);
    }

    @Override
    protected void fabOnClick() {
        Map checkOutBox = parentAdapter.getCheckBoxOut();

        List<TaskModel> taskList = new ArrayList<TaskModel>();
        for (TaskModel element : parentAdapter.getTaskList()) {
            Object o = checkOutBox.get(element.getId());
            if (o!=null && (Boolean) o){
                taskList.add(element);
            }
        }

        Intent returnIntent = new Intent();
        returnIntent.putExtra(DATA_SERIALIZABLE_EXTRA, (Serializable) taskList);
        setResult(RESULT_OK, returnIntent);
        finish();
    }

    @Override
    protected TaskAdapter getTasksAdapter() {
        parentAdapter = new ParentAdapter(db,ParentActivity.this);
        return parentAdapter;
    }

}