    package com.antopina.schedulingappointmentplanner.utils;

    import android.content.ContentValues;
    import android.content.Context;
    import android.database.Cursor;
    import android.database.sqlite.SQLiteDatabase;
    import android.database.sqlite.SQLiteOpenHelper;
    import android.widget.Toast;

    import androidx.annotation.Nullable;

    public class EventDataBsaeHelper extends SQLiteOpenHelper {

        private Context context;

        private static final String DATABASE_NAME = "Schedue_db";
        private static final int DATABASE_VERSION = 1;

        private static final String TABLE_NAME = "event";
        private static final String COLUMN_ID = "id";
        private static final String COLUMN_TITLE = "event_title";
        private static final String COLUMN_DESCRIPTION = "event_description";

        // Task table
        private static final String TASK_TABLE_NAME = "task";
        public static final String TASK_COLUMN_ID = "id";
        public static final String TASK_COLUMN_NAME = "task_name";
        private static final String TASK_COLUMN_DESCRIPTION = "task_description";
        public static final String TASK_COLUMN_STATUS = "task_status";
        public static final String TASK_COLUMN_DUE_DATE = "task_due_date";


        public EventDataBsaeHelper(@Nullable Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
            this.context = context;
        }

        @Override
        public void onCreate(SQLiteDatabase db) {

            //create table for event
            String query = "CREATE TABLE " + TABLE_NAME +
                    " (" + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    COLUMN_TITLE + " TEXT, " +
                    COLUMN_DESCRIPTION + " TEXT );";
            db.execSQL(query);

            // Create the task table
            String createTaskTable = "CREATE TABLE " + TASK_TABLE_NAME +
                    " (" + TASK_COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    TASK_COLUMN_NAME + " TEXT, " +
                    TASK_COLUMN_STATUS + " INTEGER DEFAULT 0, " + // 0 for incomplete, 1 for complete
                    TASK_COLUMN_DUE_DATE + " TEXT );"; // Store due date as ISO 8601 (e.g., "2024-11-30")
            db.execSQL(createTaskTable);
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int i, int i1) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + TASK_TABLE_NAME);
        onCreate(db);
        }

        public void insertEvent(String title, String description) {
            SQLiteDatabase db = this.getWritableDatabase();
            ContentValues values = new ContentValues();

            values.put(COLUMN_TITLE, title);
            values.put(COLUMN_DESCRIPTION, description);
            long result =  db.insert(TABLE_NAME, null, values);
            if (result == -1) {
                Toast.makeText(context, "Failed to add event", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(context, "Event added successfully", Toast.LENGTH_SHORT).show();
            }
        }

        public Cursor readAllData() {
            String query = "SELECT * FROM " + TABLE_NAME;
            SQLiteDatabase db = this.getReadableDatabase();

            Cursor cursor = null;
            if(db != null) {
               cursor = db.rawQuery(query, null);
            }

            return cursor;
        }

        // Insert task      int status, String dueDate
        public void insertTask(String name, String description) {
            SQLiteDatabase db = this.getWritableDatabase();
            ContentValues values = new ContentValues();

            values.put(TASK_COLUMN_NAME, name);
            values.put(TASK_COLUMN_DESCRIPTION, description);
           // values.put(TASK_COLUMN_STATUS, status);
          //  values.put(TASK_COLUMN_DUE_DATE, dueDate);

            long result = db.insert(TASK_TABLE_NAME, null, values);
            if (result == -1) {
                Toast.makeText(context, "Failed to add task", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(context, "Task added successfully", Toast.LENGTH_SHORT).show();
            }
        }

        // Read all tasks
        public Cursor readAllTasks() {
            String query = "SELECT * FROM " + TASK_TABLE_NAME;
            SQLiteDatabase db = this.getReadableDatabase();

            Cursor cursor = null;
            if (db != null) {
                cursor = db.rawQuery(query, null);
            }

            return cursor;
        }
    }
