package com.example.todolist;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

public class DatabaseHelper extends SQLiteOpenHelper {

    public static final String DATABASE_NAME = "todolist.db";
    public static final String TABLE_NAME = "tasks";
    public static final String COLUMN_ID = "id";
    public static final String COLUMN_NAME = "name";
    public static final String COLUMN_DESCRIPTION = "description";
    public static final String COLUMN_IS_SHARED = "is_shared";
    public static final String COLUMN_IS_COMPLETED = "completed";
    public static final String COLUMN_DATE = "date";
    public static final int DATABASE_VERSION = 10;

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_TABLE = "CREATE TABLE IF NOT EXISTS " + TABLE_NAME + "("
                + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + COLUMN_NAME + " TEXT, "
                + COLUMN_DESCRIPTION + " TEXT, "
                + COLUMN_IS_SHARED + " INTEGER, "
                + COLUMN_IS_COMPLETED + " INTEGER, "
                + COLUMN_DATE + " TEXT" + ")";
        db.execSQL(CREATE_TABLE);
        Log.d("DATABASE", "Database created successfully");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        if (oldVersion < 2) {
            String ALTER_TABLE_DATE = "ALTER TABLE " + TABLE_NAME + " ADD COLUMN " + COLUMN_DATE + " TEXT";
            db.execSQL(ALTER_TABLE_DATE);
            Log.d("DATABASE", "Added COLUMN_DATE to tasks table.");
        }

        if (oldVersion < 10) {
            String TEMP_TABLE = TABLE_NAME + "_temp";
            String CREATE_TEMP_TABLE = "CREATE TABLE " + TEMP_TABLE + "("
                    + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                    + COLUMN_NAME + " TEXT, "
                    + COLUMN_DESCRIPTION + " TEXT, "
                    + COLUMN_IS_SHARED + " INTEGER, "
                    + COLUMN_IS_COMPLETED + " INTEGER DEFAULT 0, "
                    + COLUMN_DATE + " TEXT)";
            db.execSQL(CREATE_TEMP_TABLE);
            String COPY_DATA = "INSERT INTO " + TEMP_TABLE + " ("
                    + COLUMN_ID + ", " + COLUMN_NAME + ", " + COLUMN_DESCRIPTION + ", "
                    + COLUMN_IS_SHARED + ", " + COLUMN_DATE + ", " + COLUMN_IS_COMPLETED + ") "
                    + "SELECT "
                    + COLUMN_ID + ", " + COLUMN_NAME + ", " + COLUMN_DESCRIPTION + ", "
                    + COLUMN_IS_SHARED + ", " + COLUMN_DATE + ", 0 "
                    + "FROM " + TABLE_NAME;
            db.execSQL(COPY_DATA);
            db.execSQL("DROP TABLE " + TABLE_NAME);
            db.execSQL("ALTER TABLE " + TEMP_TABLE + " RENAME TO " + TABLE_NAME);
            Log.d("DATABASE", "Recreated tasks table with correct columns.");
        }
    }

    public long addTask(Task task) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_NAME, task.getName());
        values.put(COLUMN_DESCRIPTION, task.getDescription());
        values.put(COLUMN_IS_SHARED, task.isShared() ? 1 : 0); //NON UTILISE
        values.put(COLUMN_IS_COMPLETED, task.isCompleted() ? 1 : 0); //1 TRUE | 0 FALSE
        values.put(COLUMN_DATE, task.getDate());
        return db.insert(TABLE_NAME, null, values);
    }

    @SuppressLint("Range")
    public List<Task> getAllTasks() {
        List<Task> taskList = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_NAME, null);

        if (cursor.moveToFirst()) {
            do {
                Task task = new Task();
                task.setId(cursor.getInt(cursor.getColumnIndex(COLUMN_ID)));
                task.setName(cursor.getString(cursor.getColumnIndex(COLUMN_NAME)));
                task.setDescription(cursor.getString(cursor.getColumnIndex(COLUMN_DESCRIPTION)));
                task.setDate(cursor.getString(cursor.getColumnIndex(COLUMN_DATE)));
                task.setCompleted(cursor.getInt(cursor.getColumnIndex(COLUMN_IS_COMPLETED)) == 1);
                taskList.add(task);
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return taskList;
    }

    public void updateTaskCompletionStatus(Task task) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("completed", task.isCompleted() ? 1 : 0); //1 TRUE | 0 FALSE
        db.update(TABLE_NAME, values, "id = ?", new String[]{String.valueOf(task.getId())});
        db.close();
    }

    public void deleteTask(int taskId) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_NAME, COLUMN_ID + " = ?", new String[]{String.valueOf(taskId)});
        db.close();
    }

    public void updateTask(Task task) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_NAME, task.getName());
        values.put(COLUMN_DESCRIPTION, task.getDescription());
        values.put(COLUMN_DATE, task.getDate());
        values.put(COLUMN_IS_COMPLETED, task.isCompleted() ? 1 : 0); //1 TRUE | 0 FALSE
        db.update(TABLE_NAME, values, COLUMN_ID + " = ?", new String[]{String.valueOf(task.getId())});
        db.close();
    }

}
