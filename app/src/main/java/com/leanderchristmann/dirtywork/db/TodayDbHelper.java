package com.leanderchristmann.dirtywork.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.leanderchristmann.dirtywork.models.Task;

import java.util.ArrayList;
import java.util.List;

public class TodayDbHelper extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "today.db";

    private static final String TABLE_NAME = "today_todos";
    private static final String COLUMN_ID = "id";
    private static final String COLUMN_TEXT = "todoText";
    private static final String COLUMN_STATUS = "status";
    private static final String CREATE_TODAY_TABLE = "CREATE TABLE " + TABLE_NAME
            + "(" + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
            COLUMN_TEXT + " TEXT, " + COLUMN_STATUS + " INTEGER);";

    SQLiteDatabase db;

    public TodayDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TODAY_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }

    public void openDatabase(){
        db = this.getWritableDatabase();
    }

    public void insertTask(Task task){
        ContentValues cv = new ContentValues();
        cv.put(COLUMN_TEXT, task.getText());
        cv.put(COLUMN_STATUS, 0);
        db.insert(TABLE_NAME, null, cv);
    }

    public List<Task> getAllTasks(){
        List<Task> todoList = new ArrayList<>();
        Cursor cursor = null;
        db.beginTransaction();
        try {
            cursor = db.query(TABLE_NAME, null, null, null, null, null, null);
            if(cursor != null){
                if(cursor.moveToFirst()){
                    do {
                        Task task = new Task();
                        task.setId(cursor.getInt(cursor.getColumnIndex(COLUMN_ID)));
                        task.setText(cursor.getString(cursor.getColumnIndex(COLUMN_TEXT)));
                        task.setStatus(cursor.getInt(cursor.getColumnIndex(COLUMN_STATUS)));
                        todoList.add(task);
                    } while (cursor.moveToNext());
                }
            }
        } finally {
            db.endTransaction();
            cursor.close();
        }
        return todoList;
    }

    public void updateStatus(int id, int status){
        ContentValues cv = new ContentValues();
        cv.put(COLUMN_STATUS, status);
        db.update(TABLE_NAME, cv, COLUMN_ID + "=?", new String[]{String.valueOf(id)});
    }

    public void deleteTask(int id){
        db.delete(TABLE_NAME, COLUMN_ID + "=?", new String[]{String.valueOf(id)});
    }

    public void clearDB(){
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }
}
