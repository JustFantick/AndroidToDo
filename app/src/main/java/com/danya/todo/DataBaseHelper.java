package com.danya.todo;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.widget.Toast;

import androidx.annotation.Nullable;

import java.text.SimpleDateFormat;
import java.util.Date;

public class DataBaseHelper extends SQLiteOpenHelper {
    private Context context;
    private static final String DATABASE_NAME = "ToDo.db";
    private static final int DATABASE_VERSION = 1;

    private static final String TABLE_NAME = "notes";
    private static final String COLUMN_ID = "_id";
    private static final String COLUMN_TITLE = "note_title";
    private static final String COLUMN_TEXT = "note_text";
    private static final String COLUMN_LAST_EDIT = "last_edit";

    private static final String NOTES_TABLE_NAME = "tasks";
    private static final String NOTES_COLUMN_STATUS = "task_status";
    private static final String NOTES_COLUMN_TEXT = "task_text";

    private static final String NOTEBOOK_TABLE_NAME = "notebook";
    private static final String NOTEBOOK_COLUMN_TEXT = "notebook_text";


    DataBaseHelper(@Nullable Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.context = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String createNotesTable = "CREATE TABLE " + TABLE_NAME +
                        " (" + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                        COLUMN_TITLE + " TEXT, " +
                        COLUMN_TEXT + " TEXT, " +
                        COLUMN_LAST_EDIT + " TEXT);";
        db.execSQL(createNotesTable);

        String createTasksTable = "CREATE TABLE " + NOTES_TABLE_NAME +
                " (" + NOTES_COLUMN_STATUS + " INTEGER, " +
                NOTES_COLUMN_TEXT+ " TEXT);";
        db.execSQL(createTasksTable);

        String createNotebookTable = "CREATE TABLE " + NOTEBOOK_TABLE_NAME +
                " (" + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                NOTEBOOK_COLUMN_TEXT + " TEXT);";
        db.execSQL(createNotebookTable);

        //Create the only cell in Notebook table,dont need more
        ContentValues cv = new ContentValues();
        cv.put(NOTEBOOK_COLUMN_TEXT, "");
        db.insert(NOTEBOOK_TABLE_NAME, null, cv);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int i, int i1) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + NOTES_TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + NOTEBOOK_TABLE_NAME);
        onCreate(db);
    }

    void addNoteToDb(String title, String text) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();

        //Get the time when note was added
        Date date = new Date();
        SimpleDateFormat formatter = new SimpleDateFormat("HH:mm");
        String editTime = formatter.format(date);

        //Write info into BD
        cv.put(COLUMN_TITLE, title);
        cv.put(COLUMN_TEXT, text);
        cv.put(COLUMN_LAST_EDIT, editTime);
        long result = db.insert(TABLE_NAME, null, cv);
        if(result == -1) {
            Toast.makeText(context, "Failed db.insert", Toast.LENGTH_SHORT).show();
        } else  {
            Toast.makeText(context, "Insert did successfully", Toast.LENGTH_SHORT).show();
        }
    }

    Cursor readAllData() {
        String query = "SELECT * FROM " + TABLE_NAME;
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = null;
        if (db != null) {
            cursor = db.rawQuery(query, null);
        }
        return cursor;
    }

    void updateData(String row_id, String title, String text, String editTime) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(COLUMN_TITLE, title);
        cv.put(COLUMN_TEXT, text);
        cv.put(COLUMN_LAST_EDIT, editTime);

        long result = db.update(TABLE_NAME, cv, "_id=?", new String[]{row_id});
        if(result == -1) {
            Toast.makeText(context, "Failed to update", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(context, "Successfully updated", Toast.LENGTH_SHORT).show();
        }
    }

    void deleteOneRow(String row_id) {
        SQLiteDatabase db = this.getWritableDatabase();
        long result = db.delete(TABLE_NAME, "_id=?", new String[]{row_id});
        if(result == -1) {
            Toast.makeText(context, "Failed to delete", Toast.LENGTH_SHORT).show();
        } else  {
            Toast.makeText(context, "Successfully deleted", Toast.LENGTH_SHORT).show();
        }
    }

    void saveTaskToDb(int status, String text) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();

        cv.put(NOTES_COLUMN_STATUS, status);
        cv.put(NOTES_COLUMN_TEXT, text);

        //insert data into table
        db.insert(NOTES_TABLE_NAME, null, cv);
    }

    void updateTaskInDb(int status, String text) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();

        cv.put(NOTES_COLUMN_STATUS, status);
        cv.put(NOTES_COLUMN_TEXT, text);

        //insert data into table
        long result = db.update(NOTES_TABLE_NAME, cv, "task_text=?", new String[]{text});
    }

    void deleteTaskRow(String text) {
        SQLiteDatabase db = this.getWritableDatabase();
        long result = db.delete(NOTES_TABLE_NAME, "task_text=?", new String[]{text});
        if(result == -1) {
            Toast.makeText(context, "Failed to delete", Toast.LENGTH_SHORT).show();
        } else  {
            Toast.makeText(context, "Successfully deleted", Toast.LENGTH_SHORT).show();
        }
    }

    void saveNotebooksTextToDb(String text) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();

        cv.put(NOTEBOOK_COLUMN_TEXT, text);

        //insert data into table
        long result = db.update(NOTEBOOK_TABLE_NAME, cv, "_id=?", new String[]{"1"});
    }

    Cursor readTasksData() {
        String query = "SELECT * FROM " + NOTES_TABLE_NAME;
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = null;
        if (db != null) { cursor = db.rawQuery(query, null); }
        return cursor;
    }

    Cursor readNotebooksData() {
        String query = "SELECT * FROM " + NOTEBOOK_TABLE_NAME;
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = null;
        if (db != null) {
            cursor = db.rawQuery(query, null);
        }
        return cursor;
    }

}
