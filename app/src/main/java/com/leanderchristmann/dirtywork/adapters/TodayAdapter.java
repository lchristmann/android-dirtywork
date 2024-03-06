package com.leanderchristmann.dirtywork.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.leanderchristmann.dirtywork.R;
import com.leanderchristmann.dirtywork.db.TodayDbHelper;
import com.leanderchristmann.dirtywork.models.Task;

import java.util.ArrayList;
import java.util.List;

public class TodayAdapter extends RecyclerView.Adapter<TodayAdapter.ViewHolder> {

    private List<Task> todayTodoList;
    private TodayDbHelper db;
    private Context context;

    public TodayAdapter(TodayDbHelper db, Context context) {
        this.db = db;
        this.context = context;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        CheckBox task;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            task = itemView.findViewById(R.id.todoCheckBox);
        }

        CheckBox getTask() {
            return task;
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.task_layout, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        db.openDatabase();

        final Task item = todayTodoList.get(position);
        holder.getTask().setText(item.getText());
        holder.getTask().setChecked(toBoolean(item.getStatus()));
        holder.getTask().setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                if (isChecked)
                    db.updateStatus(item.getId(), 1);
                else
                    db.updateStatus(item.getId(), 0);
            }
        });
    }

    @Override
    public int getItemCount() {
        return todayTodoList.size();
    }

    private boolean toBoolean(int n) {
        return n != 0;
    }

    public void setTodayTasks(List<Task> todayTodoList) {
        this.todayTodoList = todayTodoList;
        notifyDataSetChanged();
    }

    public void deleteItem(int position) {
        Task item = todayTodoList.get(position);
        db.deleteTask(item.getId());
        todayTodoList.remove(position);
        notifyItemRemoved(position);
    }

    public Context getContext() {
        return context;
    }
}
