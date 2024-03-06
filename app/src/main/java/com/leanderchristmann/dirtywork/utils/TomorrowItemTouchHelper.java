package com.leanderchristmann.dirtywork.utils;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Canvas;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

import com.leanderchristmann.dirtywork.R;
import com.leanderchristmann.dirtywork.adapters.TomorrowAdapter;
import com.leanderchristmann.dirtywork.db.TodayDbHelper;
import com.leanderchristmann.dirtywork.models.Task;

public class TomorrowItemTouchHelper extends ItemTouchHelper.SimpleCallback {

    private TomorrowAdapter tomorrowAdapter;

    public TomorrowItemTouchHelper(TomorrowAdapter tomorrowAdapter) {
        super(0, ItemTouchHelper.START | ItemTouchHelper.END);
        this.tomorrowAdapter = tomorrowAdapter;
    }

    @Override
    public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder source, @NonNull RecyclerView.ViewHolder target) {
        return false;
    }

    //left-to-right swipe: remove item (if-condition) | right-to-left swipe: move item to today's list (else block)
    @Override
    public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
        final int position = viewHolder.getAdapterPosition();
        if(direction == ItemTouchHelper.END){
            AlertDialog.Builder builder = new AlertDialog.Builder(tomorrowAdapter.getContext());       //for this method getContext in Adapter
            builder.setTitle("delete task");
            builder.setMessage("Are you sure you want to delete this task?");
            builder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int which) {
                    tomorrowAdapter.deleteItem(position);
                }
            });
            builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int which) {
                    tomorrowAdapter.notifyItemChanged(viewHolder.getAdapterPosition());
                }
            });
            AlertDialog dialog = builder.create();
            dialog.show();
        }
        else    //move task to Today's DB (remove here, put in today database)
        {
            Task taskToMove = tomorrowAdapter.getItem(position);
            tomorrowAdapter.deleteItem(position);
            TodayDbHelper db = new TodayDbHelper(viewHolder.itemView.getContext());
            db.openDatabase();
            db.insertTask(taskToMove);
        }
    }

    @Override
    public void onChildDraw(@NonNull Canvas c, @NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
        super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);

        Drawable icon;
        ColorDrawable background;

        View itemView = viewHolder.itemView;
        int backgroundCornerOffset = 16;

        if (dX > 0) {
            icon = ContextCompat.getDrawable(tomorrowAdapter.getContext(), R.drawable.ic_delete);
            background = new ColorDrawable(ContextCompat.getColor(tomorrowAdapter.getContext(), R.color.littleBitLighterBlack));
        } else {
            icon = ContextCompat.getDrawable(tomorrowAdapter.getContext(), R.drawable.ic_move_left);
            background = new ColorDrawable(ContextCompat.getColor(tomorrowAdapter.getContext(), R.color.littleBitLighterBlack));
        }

        int iconMargin = (itemView.getHeight() - icon.getIntrinsicHeight()) / 2;
        int iconTop = itemView.getTop() + (itemView.getHeight() - icon.getIntrinsicHeight()) /2;
        int iconBottom = iconTop + icon.getIntrinsicHeight();

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
    }
}
