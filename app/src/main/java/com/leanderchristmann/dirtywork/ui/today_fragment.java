package com.leanderchristmann.dirtywork.ui;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Typeface;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.res.ResourcesCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.Spannable;
import android.text.SpannableString;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.leanderchristmann.dirtywork.R;
import com.leanderchristmann.dirtywork.adapters.TodayAdapter;
import com.leanderchristmann.dirtywork.db.TodayDbHelper;
import com.leanderchristmann.dirtywork.models.CustomTFSpan;
import com.leanderchristmann.dirtywork.models.Task;
import com.leanderchristmann.dirtywork.utils.TodayItemTouchHelper;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class today_fragment extends Fragment {

    private RecyclerView todayRecyclerView;
    private FloatingActionButton addTodayButton;

    private TodayAdapter todayAdapter;

    private List<Task> todayTaskList;

    private TodayDbHelper db;

    public today_fragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_today_fragment, container, false);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        db = new TodayDbHelper(getContext());
        db.openDatabase();

        todayTaskList = new ArrayList<>();

        addTodayButton = view.findViewById(R.id.addTodayButton);

        //set up todayRecyclerView
        todayRecyclerView = view.findViewById(R.id.todayRecyclerView);
        todayRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        todayAdapter = new TodayAdapter(db, getContext());
        todayRecyclerView.setAdapter(todayAdapter);

        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(new TodayItemTouchHelper(todayAdapter));
        itemTouchHelper.attachToRecyclerView(todayRecyclerView);

        todayTaskList = db.getAllTasks();
        Collections.reverse(todayTaskList);

        todayAdapter.setTodayTasks(todayTaskList);

        addTodayButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showAddTodayTaskDialog();
            }
        });
    }

    private void showAddTodayTaskDialog(){
        //STEP 1: show Dialog with input & ok/cancel
        final AlertDialog.Builder builder = new AlertDialog.Builder(getContext());

        //set the title in montserrat font
        String alertDialogTitle = "New Task";
        Typeface tf = ResourcesCompat.getFont(getContext(), R.font.montserrat);
        CustomTFSpan tfSpan = new CustomTFSpan(tf);
        SpannableString spannableString = new SpannableString(alertDialogTitle);
        spannableString.setSpan(tfSpan, 0, spannableString.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        builder.setTitle(spannableString);

        //set positive and negative button in montserrat font
        String positiveButtonText = getContext().getResources().getString(R.string.ok);
        String negativeButtonText = getContext().getResources().getString(R.string.cancel);
        SpannableString spannableStringPositive = new SpannableString(positiveButtonText);
        SpannableString spannableStringNegative = new SpannableString(negativeButtonText);
        spannableStringPositive.setSpan(tfSpan, 0, spannableStringPositive.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        spannableStringNegative.setSpan(tfSpan, 0, spannableStringNegative.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

        //set the text input layout
        View viewInflated = LayoutInflater.from(getContext()).inflate(R.layout.new_task_dialog, (ViewGroup) getView(), false);

        builder.setView(viewInflated);

        //declare and init the editText
        EditText taskEditText = viewInflated.findViewById(R.id.newTaskEditText);

        //set up the buttons
        builder.setPositiveButton(spannableStringPositive, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                //STEP 2: add the book to toReadBooksList
                String taskText = taskEditText.getText().toString();

                if (taskText.isEmpty() || taskText.trim().length() == 0)
                    return;

                Task taskToAdd = new Task();
                taskToAdd.setText(taskText);
                taskToAdd.setStatus(0);
                todayTaskList.add(0, taskToAdd);
                todayAdapter.notifyItemInserted(0);

                TodayDbHelper db = new TodayDbHelper(getContext());
                db.openDatabase();
                db.insertTask(taskToAdd);

                dialogInterface.dismiss();
            }
        });
        builder.setNegativeButton(spannableStringNegative, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.cancel();
            }
        });

        builder.show();
    }
}