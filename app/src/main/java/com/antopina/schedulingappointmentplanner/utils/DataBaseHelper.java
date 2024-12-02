package com.antopina.schedulingappointmentplanner.utils;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

import com.antopina.schedulingappointmentplanner.model.TodoModel;

import java.util.ArrayList;
import java.util.List;

public class DataBaseHelper extends SQLiteOpenHelper {

    private SQLiteDatabase db;

    private static final String DATABASE_NAME = "SCHEDUE_DATABASE";
    private static final String TABLE_NAME = "TASK_TABLE";
    private static final String COL_1 = "ID";
    private static final String COL_2 = "TASK";
    private static final String COL_3 = "STATUS";

    public DataBaseHelper(@Nullable Context context) {
        super(context, DATABASE_NAME, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE IF NOT EXISTS " + TABLE_NAME + "(ID INTEGER PRIMARY KEY AUTOINCREMENT , TASK TEXT , STATUS INTEGER)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int i, int i1) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }

    public void insertTask(TodoModel model) {
        db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COL_2 , model.getTask());
        values.put(COL_3 , 0);
        db.insert(TABLE_NAME , null, values);
    }

    public void updateTask(int id, String task) {
        db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COL_2 , task);
        db.update(TABLE_NAME, values, "ID=?" , new String[]{String.valueOf(id)});
    }

    public void updateStatus(int id, String status) {
        db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COL_3 , status);
        db.update(TABLE_NAME, values, "ID=?" , new String[]{String.valueOf(id)});
    }

    public void deleteTask(int id) {
        db = this.getWritableDatabase();
        db.delete(TABLE_NAME, "ID=?" , new String[]{String.valueOf(id)});
    }

    public List<TodoModel> getAllTasks() {
        db = this.getWritableDatabase();
        Cursor cursor = null;
        List<TodoModel> modelList = new ArrayList<>();

        db.beginTransaction();
        try {
            cursor = db.query(TABLE_NAME, null, null, null, null, null, null);
            if (cursor != null && cursor.moveToFirst()) {
                int colIdIndex = cursor.getColumnIndex(COL_1);
                int colTaskIndex = cursor.getColumnIndex(COL_2);
                int colStatusIndex = cursor.getColumnIndex(COL_3);

                // Check for valid column indexes
                if (colIdIndex != -1 && colTaskIndex != -1 && colStatusIndex != -1) {
                    do {
                        TodoModel task = new TodoModel();
                        task.setId(cursor.getInt(colIdIndex));
                        task.setTask(cursor.getString(colTaskIndex));
                        task.setStatus(cursor.getInt(colStatusIndex));
                        modelList.add(task);
                    } while (cursor.moveToNext());
                } else {
                    // Handle invalid column indexes (e.g., log error)
                    throw new IllegalStateException("Invalid column index detected.");
                }
            }
        } finally {
            db.endTransaction();
            if (cursor != null) {
                cursor.close();
            }
        }

        return modelList;
    }

}
