    package com.antopina.schedulingappointmentplanner.utils;

    import static android.app.DownloadManager.COLUMN_DESCRIPTION;
    import static android.app.DownloadManager.COLUMN_TITLE;

    import android.content.ContentValues;
    import android.content.Context;
    import android.database.Cursor;
    import android.database.sqlite.SQLiteDatabase;
    import android.database.sqlite.SQLiteOpenHelper;
    import android.util.Log;
    import android.widget.Toast;

    import androidx.annotation.Nullable;

    import com.antopina.schedulingappointmentplanner.HomePage.calendar.Event;

    import java.text.SimpleDateFormat;
    import java.time.LocalDate;
    import java.time.LocalTime;
    import java.time.format.DateTimeFormatter;
    import java.util.ArrayList;
    import java.util.Date;

    public class EventDataBsaeHelper extends SQLiteOpenHelper {

        private Context context;

        private static final String DATABASE_NAME = "Schedue_db";
        private static final int DATABASE_VERSION = 2;

        private static final String EVENT_TABLE_NAME = "event";
        private static final String EVENT_COLUMN_ID = "event_id";
        public static final String EVENT_COLUMN_TITLE = "event_title";
        private static final String EVENT_COLUMN_DESCRIPTION = "event_description";
        private static final String EVENT_COLUMN_START_DATE = "event_start_date";
        private static final String EVENT_COLUMN_END_DATE = "event_end_date";
        private static final String EVENT_COLUMN_START_TIME = "event_start_time";
        private static final String EVENT_COLUMN_END_TIME = "event_end_time";


        // Task table
        private static final String TASK_TABLE_NAME = "task";
        private static final String TASK_COLUMN_ID = "task_id";
        public static final String TASK_COLUMN_NAME = "task_name";
        public static final String TASK_COLUMN_DESCRIPTION = "task_description";
        public static final String TASK_COLUMN_STATUS = "task_status";
        public static final String TASK_COLUMN_DUE_DATE = "task_due_date";
        public static final String TASK_COLUMN_DUE_TIME = "task_due_time";


        public EventDataBsaeHelper(@Nullable Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
            this.context = context;
        }

        @Override
        public void onCreate(SQLiteDatabase db) {

            // Create table for event
            String query = "CREATE TABLE " + EVENT_TABLE_NAME + " ("
                    + EVENT_COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                    + EVENT_COLUMN_TITLE + " TEXT, "
                    + EVENT_COLUMN_DESCRIPTION + " TEXT, "
                    + EVENT_COLUMN_START_DATE + " TEXT, " // Column for start date
                    + EVENT_COLUMN_END_DATE + " TEXT, "   // Column for end date
                    + EVENT_COLUMN_START_TIME + " TEXT, " // Column for start time
                    + EVENT_COLUMN_END_TIME + " TEXT);";  // Column for end time
            db.execSQL(query);


            // Create the task table
            String createTaskTable = "CREATE TABLE " + TASK_TABLE_NAME +
                    " (" + TASK_COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    TASK_COLUMN_NAME + " TEXT, " +
                    TASK_COLUMN_DESCRIPTION + " TEXT, " +
                    TASK_COLUMN_STATUS + " INTEGER DEFAULT 0, " + // 0 for incomplete, 1 for complete
                    TASK_COLUMN_DUE_DATE + " TEXT, " + // Store due date as ISO 8601
                    TASK_COLUMN_DUE_TIME + " TEXT);"; // Store due time in HH:mm format
            db.execSQL(createTaskTable);

        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int i, int i1) {
        db.execSQL("DROP TABLE IF EXISTS " + EVENT_TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + TASK_TABLE_NAME);
        onCreate(db);
        }

        public void insertEvent(String title, String description, String startDate, String endDate, String startTime, String endTime) {
            SQLiteDatabase db = this.getWritableDatabase();
            ContentValues values = new ContentValues();

            values.put(EVENT_COLUMN_TITLE, title);
            values.put(EVENT_COLUMN_DESCRIPTION, description);
            values.put(EVENT_COLUMN_START_DATE, startDate); // Add start date
            values.put(EVENT_COLUMN_END_DATE, endDate);     // Add end date
            values.put(EVENT_COLUMN_START_TIME, startTime); // Add start time
            values.put(EVENT_COLUMN_END_TIME, endTime);     // Add end time

            long result = db.insert(EVENT_TABLE_NAME, null, values);
            if (result == -1) {
                Toast.makeText(context, "Failed to add event", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(context, "Event added successfully", Toast.LENGTH_SHORT).show();
            }
        }

        public ArrayList<Event> getEventsForDate(String date) {
            SQLiteDatabase db = this.getReadableDatabase();
            ArrayList<Event> events = new ArrayList<>();
            Cursor cursor = null;

            try {
                // First, get events that overlap with the given date
                String selection = EVENT_COLUMN_START_DATE + " <= ? AND " + EVENT_COLUMN_END_DATE + " >= ?";
                String[] selectionArgs = {date, date};  // Pass 'date' for both the start and end condition

                cursor = db.query(EVENT_TABLE_NAME, null, selection, selectionArgs, null, null, null);

                if (cursor != null && cursor.moveToFirst()) {
                    do {
                        try {
                            String title = cursor.getString(cursor.getColumnIndexOrThrow(EVENT_COLUMN_TITLE));
                            String description = cursor.getString(cursor.getColumnIndexOrThrow(EVENT_COLUMN_DESCRIPTION));
                            String startDate = cursor.getString(cursor.getColumnIndexOrThrow(EVENT_COLUMN_START_DATE));
                            String startTime = cursor.getString(cursor.getColumnIndexOrThrow(EVENT_COLUMN_START_TIME));
                            String endDate = cursor.getString(cursor.getColumnIndexOrThrow(EVENT_COLUMN_END_DATE));
                            String endTime = cursor.getString(cursor.getColumnIndexOrThrow(EVENT_COLUMN_END_TIME));

                            // Assuming taskStatus is 0 by default for events (you can adjust based on your logic)
                            int taskStatus = 0;

                            // Create an Event object with all required parameters
                            events.add(new Event(
                                    title,
                                    description,
                                    LocalDate.parse(startDate),
                                    LocalTime.parse(startTime),
                                    false, // isTask set to false for events
                                    taskStatus,
                                    LocalDate.parse(endDate), // Assuming endDate is available
                                    LocalTime.parse(endTime)  // Assuming endTime is available
                            ));
                        } catch (IllegalArgumentException e) {
                            // Handle the case where column name is invalid
                            Log.e("DatabaseError", "Column not found: " + e.getMessage());
                        }
                    } while (cursor.moveToNext());
                }
            } finally {
                if (cursor != null) cursor.close();
            }

            return events;
        }

        public boolean deleteEvent(String eventId) {
            SQLiteDatabase db = this.getWritableDatabase();

            // Log the query to make sure it's correct
            String deleteQuery = "DELETE FROM " + EVENT_TABLE_NAME + " WHERE " + EVENT_COLUMN_ID + " = ?";
            Log.d("Database", "Executing query: " + deleteQuery + " with task_id: " + eventId);

            // Perform the deletion using db.delete()
            int rowsDeleted = db.delete(EVENT_TABLE_NAME, EVENT_COLUMN_ID + " = ?", new String[]{eventId});

            boolean result = rowsDeleted > 0; // true if task was deleted, false otherwise
            if (result) {
                Log.d("Database", "Event deleted successfully.");
            } else {
                Log.d("Database", "No event found with task_id: " + eventId);
            }

            db.close();
            return result;
        }



        public Cursor readAllData() {
            String query = "SELECT * FROM " + EVENT_TABLE_NAME;
            SQLiteDatabase db = this.getReadableDatabase();

            Cursor cursor = null;
            if(db != null) {
               cursor = db.rawQuery(query, null);
            }

            return cursor;
        }

        // Insert task      int status, String dueDate
        public long insertTask(String name, String description, String dueDate, String dueTime) {
            SQLiteDatabase db = this.getWritableDatabase();
            ContentValues values = new ContentValues();

            values.put(TASK_COLUMN_NAME, name);
            values.put(TASK_COLUMN_DESCRIPTION, description);
            values.put(TASK_COLUMN_DUE_DATE, dueDate);
            values.put(TASK_COLUMN_DUE_TIME, dueTime);

            long result = -1;
            try {
                result = db.insert(TASK_TABLE_NAME, null, values);
                if (result == -1) {
                    Log.e("DatabaseError", "Failed to insert task. Values: " + values.toString());
                    Toast.makeText(context, "Failed to add task", Toast.LENGTH_SHORT).show();
                } else {
                    Log.d("DatabaseSuccess", "Task added successfully. ID: " + result);
                    Toast.makeText(context, "Task added successfully", Toast.LENGTH_SHORT).show();
                }
            } catch (Exception e) {
                Log.e("DatabaseError", "Error inserting task: " + e.getMessage());
                Toast.makeText(context, "Error adding task", Toast.LENGTH_SHORT).show();
            }

            return result; // Return the inserted task's ID
        }


        public boolean updateTask(int id, String title, String description, String date, String time) {
            SQLiteDatabase db = this.getWritableDatabase();
            ContentValues values = new ContentValues();
            values.put(TASK_COLUMN_NAME, title);
            values.put(TASK_COLUMN_DESCRIPTION, description);
            values.put(TASK_COLUMN_DUE_DATE, date);
            values.put(TASK_COLUMN_DUE_TIME, time);
            return db.update(TASK_TABLE_NAME, values, "id = ?", new String[]{String.valueOf(id)}) > 0;
        }

        public boolean deleteTask(String taskId) {
            SQLiteDatabase db = this.getWritableDatabase();

            // Log the query to make sure it's correct
            String deleteQuery = "DELETE FROM " + TASK_TABLE_NAME + " WHERE " + TASK_COLUMN_ID + " = ?";
            Log.d("Database", "Executing query: " + deleteQuery + " with task_id: " + taskId);

            // Perform the deletion using db.delete()
            int rowsDeleted = db.delete(TASK_TABLE_NAME, TASK_COLUMN_ID + " = ?", new String[]{taskId});

            boolean result = rowsDeleted > 0; // true if task was deleted, false otherwise
            if (result) {
                Log.d("Database", "Task deleted successfully.");
            } else {
                Log.d("Database", "No task found with task_id: " + taskId);
            }

            db.close();
            return result;
        }


        public boolean updateTaskStatus(long taskId, int status) {
            SQLiteDatabase db = this.getWritableDatabase();
            ContentValues contentValues = new ContentValues();
            contentValues.put(TASK_COLUMN_STATUS, status);  // Set the status to the given value (0 or 1)

            int result = db.update(TASK_TABLE_NAME, contentValues, TASK_COLUMN_ID + " = ?", new String[]{String.valueOf(taskId)});
            return result > 0;  // Return true if at least one row is updated
        }

        public Cursor getTaskById(int taskId) {
            SQLiteDatabase db = this.getReadableDatabase();
            return db.rawQuery("SELECT * FROM tasks WHERE id = ?", new String[]{String.valueOf(taskId)});
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

        // Get tasks for a specific date
        public Cursor getTasksForDate(String date) {
            SQLiteDatabase db = this.getReadableDatabase();
            String[] projection = {TASK_COLUMN_NAME};  // Add more columns if ne    cessary
            String selection = TASK_COLUMN_DUE_DATE + " = ?";
            String[] selectionArgs = {date};

            return db.query(TASK_TABLE_NAME, projection, selection, selectionArgs, null, null, null);
        }

        // Method to get tasks for a specific date
        public Cursor getTasksForDueDate(String dueDate) {
            SQLiteDatabase db = this.getReadableDatabase();

            // Updated projection to select task details including the TASK_COLUMN_STATUS
            String[] projection = {TASK_COLUMN_NAME, TASK_COLUMN_DESCRIPTION, TASK_COLUMN_STATUS, TASK_COLUMN_DUE_DATE, TASK_COLUMN_DUE_TIME};

            // Selection query to find tasks that match the due date
            String selection = TASK_COLUMN_DUE_DATE + " = ?";
            String[] selectionArgs = {dueDate};

            return db.query(TASK_TABLE_NAME, projection, selection, selectionArgs, null, null, null);
        }

        public void deleteAllCompletedTasks() {
            SQLiteDatabase db = this.getWritableDatabase();
            db.delete("task", TASK_COLUMN_STATUS + "= ?", new String[]{"1"}); // Assuming '1' is the status for completed tasks
            db.close();
        }

    }
