package net.penguincoders.doit;

import android.content.DialogInterface;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.view.View;

import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

import com.malicia.mrg.adapters.TaskAdapter;
import net.penguincoders.doit.Adapters.ToDoAdapter;

import java.util.Collections;

import static androidx.appcompat.graphics.drawable.DrawerArrowDrawable.ARROW_DIRECTION_END;

public class RecyclerItemTouchHelper extends ItemTouchHelper.SimpleCallback {

    private final TaskAdapter adapter;

    private Integer sens = 0;
    private boolean reloadReady;
    private boolean toRefresh;

    private long timeActive;

    public RecyclerItemTouchHelper(TaskAdapter adapter) {
        super(ItemTouchHelper.UP | ItemTouchHelper.DOWN | ItemTouchHelper.START | ItemTouchHelper.END, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT);
        this.adapter = adapter;
    }

    @Override
    public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
        if (adapter.isViewAll() && adapter.isHierarchical()) {
            int fromPosition = viewHolder.getAdapterPosition();
            int toPosition = target.getAdapterPosition();
            Collections.swap(adapter.getTaskList(), fromPosition, toPosition);
            adapter.swapTaskList(fromPosition, toPosition);
            recyclerView.getAdapter().notifyItemMoved(fromPosition, toPosition);
        }
        //adapter.refreshActivityData(true);
        return false;
    }

    @Override
    public void onSwiped(@NonNull final RecyclerView.ViewHolder viewHolder, int direction) {
        final int position = viewHolder.getAdapterPosition();
        if (direction == ItemTouchHelper.LEFT) {
            AlertDialog.Builder builder = new AlertDialog.Builder(adapter.getContext());
            builder.setTitle("Delete Task");
            builder.setMessage("Are you sure you want to delete this Task?");


            if (adapter.getItem(position).isStatus()) {
                builder.setNeutralButton("all Checked?",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                int nbDel = adapter.deleteAllChecked(position);
                                Toast.makeText(adapter.getContext(), nbDel + " todo deleted!", Toast.LENGTH_LONG).show();
                                adapter.notifyItemChanged(viewHolder.getAdapterPosition());
                            }
                        });
            }

            builder.setPositiveButton("Confirm",
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            adapter.deleteItem(position);
                            adapter.notifyItemChanged(viewHolder.getAdapterPosition());
                        }
                    });
            builder.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    adapter.notifyItemChanged(viewHolder.getAdapterPosition());
                }
            });
            AlertDialog dialog = builder.create();
            dialog.show();
        } else {
            adapter.editItem(position);
        }
    }

    @Override
    public void onChildDraw(@NonNull Canvas c, @NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
        super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);

//        System.out.println("actionState=" + actionState + " isCurrentlyActive=" + isCurrentlyActive);
        if (isCurrentlyActive) {
            if (timeActive == 0) {
                timeActive = System.currentTimeMillis();
            }
        }
        if (timeActive != 0 && System.currentTimeMillis() - timeActive > 1200) {
            //kill long press
            adapter.removeFragmentByTag(AddNewTask.TAG);
            timeActive = 0;
        }


        Drawable icon;
        ColorDrawable background;
        View itemView = viewHolder.itemView;
        int backgroundCornerOffset = 20;
        int iconMargin = 0;
        int iconTop = 0;
        int iconBottom = 0;

        switch (actionState) {
            case ItemTouchHelper.ACTION_STATE_SWIPE:

                if (dX > 0) {
                    icon = ContextCompat.getDrawable(adapter.getContext(), R.drawable.ic_baseline_edit);
                    background = new ColorDrawable(ContextCompat.getColor(adapter.getContext(), R.color.colorPrimaryDark));
                } else {
                    icon = ContextCompat.getDrawable(adapter.getContext(), R.drawable.ic_baseline_delete);
                    background = new ColorDrawable(Color.RED);
                }

                assert icon != null;
                iconMargin = (itemView.getHeight() - icon.getIntrinsicHeight()) / 2;
                iconTop = itemView.getTop() + (itemView.getHeight() - icon.getIntrinsicHeight()) / 2;
                iconBottom = iconTop + icon.getIntrinsicHeight();

                if (dX > 0) { // Swiping to the right
                    int iconLeft = itemView.getLeft() + iconMargin;
                    int iconRight = itemView.getLeft() + iconMargin + icon.getIntrinsicWidth();
                    icon.setBounds(iconLeft, iconTop, iconRight, iconBottom);

                    background.setBounds(itemView.getLeft(), itemView.getTop(),
                            itemView.getLeft() + ((int) dX) + backgroundCornerOffset, itemView.getBottom());
                } else if (dX < 0) { // Swiping to the left
                    int iconLeft = itemView.getRight() - iconMargin - icon.getIntrinsicWidth();
                    int iconRight = itemView.getRight() - iconMargin;
                    icon.setBounds(iconLeft, iconTop, iconRight, iconBottom);

                    background.setBounds(itemView.getRight() + ((int) dX) - backgroundCornerOffset,
                            itemView.getTop(), itemView.getRight(), itemView.getBottom());
                } else { // view is unSwiped
                    background.setBounds(0, 0, 0, 0);
                }

                background.draw(c);
                icon.draw(c);

                break;

            case ItemTouchHelper.ACTION_STATE_DRAG:

                if (adapter.isViewAll() && adapter.isHierarchical()) {
                    sens = 0;
                    int ecartBase = 150;
                    if (Math.abs(dX) > ecartBase) {

                        if (dX > 0) {
                            icon = ContextCompat.getDrawable(adapter.getContext(), R.drawable.ambilwarna_arrow_down);
                            background = new ColorDrawable(Color.GRAY);
                            sens = +1;
                        } else {
                            icon = ContextCompat.getDrawable(adapter.getContext(), R.drawable.ambilwarna_arrow_right);
                            background = new ColorDrawable(Color.GRAY);
                            sens = -1;
                        }

                        if (Math.abs(dX) > 1.5 * ecartBase) {
                            background = new ColorDrawable(Color.RED);
                            if (reloadReady) {
                                adapter.editIdent((TaskAdapter.TaskViewHolder) viewHolder, viewHolder.getAdapterPosition(), sens);
                                if (isCurrentlyActive) {
                                    toRefresh = true;
                                }
                                reloadReady = false;
                            }
                        } else {
                            reloadReady = true;
                        }

                        assert icon != null;
                        iconMargin = (itemView.getHeight() - icon.getIntrinsicHeight()) / 2;
                        iconTop = itemView.getTop() + (itemView.getHeight() - icon.getIntrinsicHeight()) / 2;
                        iconBottom = iconTop + icon.getIntrinsicHeight();

                        if (dX > 0) { // Swiping to the right
                            int iconLeft = itemView.getLeft() + iconMargin;
                            int iconRight = itemView.getLeft() + iconMargin + icon.getIntrinsicWidth();
                            icon.setBounds(iconLeft, iconTop, iconRight, iconBottom);

                            background.setBounds(itemView.getLeft(), itemView.getTop(),
                                    itemView.getLeft() + ((int) dX) + backgroundCornerOffset, itemView.getBottom());
                        } else if (dX < 0) { // Swiping to the left
                            int iconLeft = itemView.getRight() - iconMargin - icon.getIntrinsicWidth();
                            int iconRight = itemView.getRight() - iconMargin;
                            icon.setBounds(iconLeft, iconTop, iconRight, iconBottom);

                            background.setBounds(itemView.getRight() + ((int) dX) - backgroundCornerOffset,
                                    itemView.getTop(), itemView.getRight(), itemView.getBottom());
                        } else { // view is unSwiped
                            background.setBounds(0, 0, 0, 0);
                        }

                        background.draw(c);
                        icon.draw(c);

                        break;
                    }
                }
        }
        if (toRefresh && !isCurrentlyActive) {
            adapter.refreshActivityData(true);
            toRefresh = false;
        }
    }

}
