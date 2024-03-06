package com.leanderchristmann.dirtywork.ui;

import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.NavigationUI;

import android.os.Bundle;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.leanderchristmann.dirtywork.R;
import com.leanderchristmann.dirtywork.db.SoonDbHelper;
import com.leanderchristmann.dirtywork.db.TodayDbHelper;
import com.leanderchristmann.dirtywork.db.TomorrowDbHelper;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        TodayDbHelper todayDB = new TodayDbHelper(this);
        todayDB.openDatabase();
        todayDB.clearDB();

        TomorrowDbHelper tomorrowDB = new TomorrowDbHelper(this);
        tomorrowDB.openDatabase();
        todayDB.clearDB();

        SoonDbHelper soonDB = new SoonDbHelper(this);
        soonDB.openDatabase();
        soonDB.clearDB();

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigationView);
        bottomNavigationView.setItemIconTintList(null);
        NavController navController = Navigation.findNavController(this, R.id.fragment);
        NavigationUI.setupWithNavController(bottomNavigationView, navController);
    }
}