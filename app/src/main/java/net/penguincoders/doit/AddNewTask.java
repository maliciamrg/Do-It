package net.penguincoders.doit;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import net.penguincoders.doit.Model.ToDoModel;
import net.penguincoders.doit.Utils.DatabaseHandler;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static android.app.Activity.RESULT_OK;
import static net.penguincoders.doit.Model.ToDoModel.parentListToString;

public class AddNewTask extends BottomSheetDialogFragment {

    public static final String TAG = "ActionBottomDialog";
    private EditText newTaskText;
    private TextView textViewParent;
    private Button newTaskSaveButton;

    private DatabaseHandler db;
    private ToDoModel item;
    private List<ToDoModel> listParent;


    public static AddNewTask newInstance() {
        return new AddNewTask();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(STYLE_NORMAL, R.style.DialogStyle);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.new_task, container, false);
        getDialog().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        newTaskText = Objects.requireNonNull(getView()).findViewById(R.id.newTaskText);
        newTaskSaveButton = getView().findViewById(R.id.newTaskButton);
        textViewParent = getView().findViewById(R.id.textViewParent);

        boolean isUpdate = false;

        final Bundle bundle = getArguments();
        if (bundle != null) {
            isUpdate = true;
            item = (ToDoModel) bundle.getSerializable("taskClass");

            String task = item.getTask();
            newTaskText.setText(task);
            assert task != null;
            if (task.length() > 0)
                newTaskSaveButton.setTextColor(ContextCompat.getColor(Objects.requireNonNull(getContext()),
                        R.color.colorPrimaryDark));
        }

        db = new DatabaseHandler(getActivity());
        db.openDatabase();

        newTaskText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.toString().equals("")) {
                    textViewParent.setEnabled(false);
                    newTaskSaveButton.setEnabled(false);
                    newTaskSaveButton.setTextColor(Color.GRAY);
                } else {
                    textViewParent.setEnabled(true);
                    newTaskSaveButton.setEnabled(true);
                    newTaskSaveButton.setTextColor(ContextCompat.getColor(Objects.requireNonNull(getContext()), R.color.colorPrimaryDark));
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        final boolean finalIsUpdate = isUpdate;
        newTaskSaveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String text = newTaskText.getText().toString();
                //todo get parent
                List<Integer> parent = new ArrayList<Integer>();
                for (ToDoModel element : listParent) {
                    parent.add(element.getId());
                }
                Integer[] parentArray = parent.toArray(new Integer[0]);
                if (finalIsUpdate) {
                    db.updateTask(item.getId(), text, parentArray);
                } else {
                    db.insertTask(text, parentArray);
                }
                dismiss();
            }
        });

        listParent = isUpdate ? item.getParentList() : new ArrayList<ToDoModel>();
        textViewParent.setText(ToDoModel.parentListToString(listParent));
        textViewParent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (newTaskText.getText().toString().compareTo("")!=0) {
                    Intent messageIntent = new Intent(v.getContext(), ParentTask.class);
                    messageIntent.putExtra(ParentTask.EXTRA_ID, finalIsUpdate ? bundle.getInt("id") : 0);
                    messageIntent.putExtra(ParentTask.EXTRA_TEXT, newTaskText.getText());
                    startActivityForResult(messageIntent, ParentTask.TEXT_REQUEST);
                }
            }
        });

    }

    @Override
    public void onDismiss(@NonNull DialogInterface dialog) {
        Activity activity = getActivity();
        if (activity instanceof DialogCloseListener)
            ((DialogCloseListener) activity).handleDialogClose(dialog);
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == ParentTask.TEXT_REQUEST) {
            if (resultCode == RESULT_OK) {
                List<ToDoModel> parentList = (List<ToDoModel>) data.getSerializableExtra(ParentTask.DATA_SERIALIZABLE_EXTRA);
                listParent = parentList.size() > 0 ? parentList : new ArrayList<ToDoModel>();
                textViewParent.setText(parentListToString(listParent));
            }
        }
    }
}
