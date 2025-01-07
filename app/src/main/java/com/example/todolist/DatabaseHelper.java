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
    public static final String COLUMN_IS_COMPLETED = "is_completed";
    public static final String COLUMN_DATE = "date";
    public static final int DATABASE_VERSION = 2;


    // Constructeur
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
            String ALTER_TABLE = "ALTER TABLE " + TABLE_NAME + " ADD COLUMN " + COLUMN_DATE + " TEXT";
            Log.d("DATABASE", "Adding COLUMN_DATE to tasks table.");
            db.execSQL(ALTER_TABLE);

            String UPDATE_DEFAULT_DATE = "UPDATE " + TABLE_NAME + " SET " + COLUMN_DATE + " = DATE('now')";
            Log.d("DATABASE", "Updating existing tasks with default date.");
            db.execSQL(UPDATE_DEFAULT_DATE);
        }
    }


    // Méthode pour ajouter une tâche
    public long addTask(Task task) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_NAME, task.getName());
        values.put(COLUMN_DESCRIPTION, task.getDescription());
        values.put(COLUMN_IS_SHARED, task.isShared() ? 1 : 0);
        values.put(COLUMN_IS_COMPLETED, task.isCompleted() ? 1 : 0);
        values.put(COLUMN_DATE, task.getDate());

        // Insertion dans la base de données
        return db.insert(TABLE_NAME, null, values);
    }

    // Méthode pour récupérer toutes les tâches
    @SuppressLint("Range")
    public List<Task> getAllTasks() {
        List<Task> taskList = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(TABLE_NAME, null, null, null, null, null, null);
        if (cursor != null && cursor.moveToFirst()) {
            do {
                int id = cursor.getInt(cursor.getColumnIndex(COLUMN_ID));
                String name = cursor.getString(cursor.getColumnIndex(COLUMN_NAME));
                String description = cursor.getString(cursor.getColumnIndex(COLUMN_DESCRIPTION));
                boolean isShared = cursor.getInt(cursor.getColumnIndex(COLUMN_IS_SHARED)) == 1;
                boolean isCompleted = cursor.getInt(cursor.getColumnIndex(COLUMN_IS_COMPLETED)) == 1;
                String date = cursor.getString(cursor.getColumnIndex(COLUMN_DATE));

                Task task = new Task(id, name, description, isShared, isCompleted, date);
                taskList.add(task);
            } while (cursor.moveToNext());
            cursor.close();
        }

        return taskList;
    }



    public int markAsCompleted(int id, boolean isCompleted) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_IS_COMPLETED, isCompleted ? 1 : 0);
        return db.update(TABLE_NAME, values, COLUMN_ID + " = ?", new String[]{String.valueOf(id)});
    }

    public void deleteTask(int taskId) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_NAME, COLUMN_ID + " = ?", new String[]{String.valueOf(taskId)});
        db.close();
    }

    public void updateTaskCompletionStatus(Task task) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_IS_COMPLETED, task.isCompleted() ? 1 : 0);

        db.update(TABLE_NAME, values, COLUMN_ID + " = ?", new String[]{String.valueOf(task.getId())});
        db.close();
    }

    public void updateTask(Task task) {
        SQLiteDatabase db = this.getWritableDatabase();

        // Créer un objet ContentValues pour stocker les nouvelles valeurs à mettre à jour
        ContentValues contentValues = new ContentValues();
        contentValues.put(COLUMN_NAME, task.getName());  // Nom de la tâche
        contentValues.put(COLUMN_DESCRIPTION, task.getDescription());  // Description de la tâche
        contentValues.put(COLUMN_DATE, task.getDate());  // Date de la tâche
        contentValues.put(COLUMN_IS_COMPLETED, task.isCompleted() ? 1 : 0);  // Statut de la tâche (complétée ou non)

        // Effectuer la mise à jour sur la base de données en spécifiant l'ID de la tâche à mettre à jour
        db.update(TABLE_NAME, contentValues, COLUMN_ID + " = ?", new String[]{String.valueOf(task.getId())});

        db.close();  // Fermer la base de données après l'opération
    }


}

