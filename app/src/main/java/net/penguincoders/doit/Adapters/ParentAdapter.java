package net.penguincoders.doit.Adapters;

import android.view.View;
import com.malicia.mrg.adapters.TaskAdapter;
import net.penguincoders.doit.Model.TaskModel;
import net.penguincoders.doit.Model.ToDoModel;
import net.penguincoders.doit.ParentActivity;
import net.penguincoders.doit.Utils.DatabaseHandler;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Map;

public class ParentAdapter extends TaskAdapter {

    private final Map checkBoxOut = new Hashtable();
    private final ParentActivity activity;
    private Integer detailVisility = View.VISIBLE;

    public ParentAdapter(DatabaseHandler db, ParentActivity activity) {
        super(db,activity);
        this.activity = activity;
    }

    @Override
    protected void afterOnBindViewHolder(ViewHolder holder, TaskModel item) {
        holder.project.setText("---Project---");
        holder.task.setVisibility(View.VISIBLE);
    }

    protected boolean isChecked(TaskModel item) {
        Object ret = checkBoxOut.get(item.getId());
        return ret==null ? false : (Boolean) ret;
    }

    @Override
    protected void checkedChanged(boolean isChecked, TaskModel item) {
        checkBoxOut.put(item.getId(), isChecked);
    }

    public void setlistParents(ArrayList<ToDoModel> listParents) {
        for (ToDoModel todoEle : listParents) {
            checkBoxOut.put(todoEle.getId(), true);
        }
    }

    public Map getCheckBoxOut() {
        return checkBoxOut;
    }
}
