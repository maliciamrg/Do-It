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
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import net.penguincoders.doit.Model.ToDoModel;
import net.penguincoders.doit.Utils.DatabaseHandler;
import yuku.ambilwarna.AmbilWarnaDialog;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static android.app.Activity.RESULT_OK;
import static net.penguincoders.doit.Model.ToDoModel.parentListToString;

public class AddNewTask extends BottomSheetDialogFragment {

    public static final String TAG = "ActionBottomDialog";
    private EditText newTaskText;
    private CheckBox checkBox;
    private TextView textViewParent;
    private Button newTaskSaveButton;
    private View mColorPreview;

    private DatabaseHandler db;
    private ToDoModel item;
    private List<ToDoModel> listParent;


    // this is the default color of the preview box
    private int mDefaultColor;

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
        checkBox = getView().findViewById(R.id.checkBox);
        mColorPreview = getView().findViewById(R.id.preview_selected_color);

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
//                    checkBox.setEnabled(false);
//                    textViewParent.setEnabled(false);
                    newTaskSaveButton.setEnabled(false);
                    newTaskSaveButton.setTextColor(Color.GRAY);
                } else {
  //                  checkBox.setEnabled(true);
  //                  textViewParent.setEnabled(true);
                    newTaskSaveButton.setEnabled(true);
                    newTaskSaveButton.setTextColor(ContextCompat.getColor(Objects.requireNonNull(getContext()), R.color.colorPrimaryDark));
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        final boolean finalIsUpdate = isUpdate;

        if (finalIsUpdate) {
            checkBox.setChecked(item.isProject());
        } else {
            checkBox.setChecked(false);
        }

        newTaskSaveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String text = newTaskText.getText().toString();

                List<Integer> parent = new ArrayList<Integer>();
                for (ToDoModel element : listParent) {
                    parent.add(element.getId());
                }
                Integer[] parentArray = parent.toArray(new Integer[0]);
                if (finalIsUpdate) {
                    db.updateTask(item.getId(), text, parentArray,checkBox.isChecked());
                } else {
                    db.insertTask(text, parentArray,checkBox.isChecked());
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

        // set the default color to 0 as it is black
        mDefaultColor = 0;

        // button open the AmbilWanra color picker dialog.
        mColorPreview.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // to make code look cleaner the color
                        // picker dialog functionality are
                        // handled in openColorPickerDialogue()
                        // function
                        openColorPickerDialogue(v);
                    }
                });

    }

    @Override
    public void onDismiss(@NonNull DialogInterface dialog) {
        Activity activity = getActivity();
        if (activity instanceof DialogCloseListener)
            ((DialogCloseListener) activity).handleDialogClose(dialog);
        dismiss();
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
    // the dialog functionality is handled separately
    // using openColorPickerDialog this is triggered as
    // soon as the user clicks on the Pick Color button And
    // the AmbilWarnaDialog has 2 methods to be overridden
    // those are onCancel and onOk which handle the "Cancel"
    // and "OK" button of color picker dialog
    public void openColorPickerDialogue(View v) {

        // the AmbilWarnaDialog callback needs 3 parameters
        // one is the context, second is default color,
        final AmbilWarnaDialog colorPickerDialogue = new AmbilWarnaDialog(v.getContext(), mDefaultColor,
                new AmbilWarnaDialog.OnAmbilWarnaListener() {
                    @Override
                    public void onCancel(AmbilWarnaDialog dialog) {
                        // leave this function body as
                        // blank, as the dialog
                        // automatically closes when
                        // clicked on cancel button
                    }

                    @Override
                    public void onOk(AmbilWarnaDialog dialog, int color) {
                        // change the mDefaultColor to
                        // change the GFG text color as
                        // it is returned when the OK
                        // button is clicked from the
                        // color picker dialog
                        mDefaultColor = color;

                        // now change the picked color
                        // preview box to mDefaultColor
                        mColorPreview.setBackgroundColor(mDefaultColor);
                    }
                });
        colorPickerDialogue.show();
    }
}
