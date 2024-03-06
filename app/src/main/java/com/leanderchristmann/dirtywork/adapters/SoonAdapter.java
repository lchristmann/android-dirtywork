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
import com.leanderchristmann.dirtywork.db.SoonDbHelper;
import com.leanderchristmann.dirtywork.models.Task;

import java.util.ArrayList;
import java.util.List;

public class SoonAdapter extends RecyclerView.Adapter<SoonAdapter.ViewHolder> {

    private List<Task> soonTodoList;
    private SoonDbHelper db;
    private Context context;

    public SoonAdapter(SoonDbHelper db, Context context) {
        this.db = db;
        this.context = context;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        CheckBox task;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            task = itemView.findViewById(R.id.todoCheckBox);
        }

        CheckBox getTask(){
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

        final Task item = soonTodoList.get(position);
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
        return soonTodoList.size();
    }

    private boolean toBoolean(int n){
        return n!=0;
    }

    public void setSoonTasks(List<Task> soonTodoList) {
        this.soonTodoList = soonTodoList;
        notifyDataSetChanged();
    }

    public void deleteItem(int position) {
        Task item = soonTodoList.get(position);
        db.deleteTask(item.getId());
        soonTodoList.remove(position);
        notifyItemRemoved(position);
    }

    public Task getItem(int position){
        return soonTodoList.get(position);
    }

    public Context getContext() {
        return context;
    }
}
