package org.feup.apm.aircarelocal;

import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.preference.PreferenceManager;
import java.util.Date;
import java.util.Locale;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;

import android.util.Log;

public class DatabaseHelper extends SQLiteOpenHelper {
    private final Context context;
    private static final String DATABASE_NAME = "aircare.db";
    private static final int DATABASE_VERSION = 4;

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.context = context;

    }
    @Override
    public void onOpen(SQLiteDatabase db) {
        super.onOpen(db);

        // Check if the sensor_values table exists
        Cursor cursor = db.rawQuery("SELECT name FROM sqlite_master WHERE type='table' AND name='sensor_data'", null);

        if (cursor.getCount() == 0) {
            // The table doesn't exist, create it
            String createTableQuery = "CREATE TABLE sensor_data (" +
                    "_id INTEGER PRIMARY KEY AUTOINCREMENT," +
                    "timestamp DATETIME DEFAULT CURRENT_TIMESTAMP," +
                    "humidity FLOAT," +
                    "temperature FLOAT," +
                    "co2 FLOAT," +
                    "voc FLOAT," +
                    "pm10 FLOAT," +
                    "pm25 FLOAT);";
            db.execSQL(createTableQuery);
        }

        cursor.close();
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        /*String createTableQuery = "CREATE TABLE sensor_data (" +
                "_id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "timestamp DATETIME DEFAULT CURRENT_TIMESTAMP," +
                "humidity REAL," +
                "temperature REAL," +
                "co2 REAL," +
                "voc REAL," +
                "pm10 REAL," +
                "pm25 REAL);";
        db.execSQL(createTableQuery);*/
    }

    private boolean isDatabaseCopied() {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        return preferences.getBoolean("DATABASE_COPIED", false);
    }

    private void setDatabaseCopied() {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        preferences.edit().putBoolean("DATABASE_COPIED", true).apply();
    }

    public void copyDatabase() {
        if (!isDatabaseCopied()) {
            try {
                InputStream inputStream = context.getAssets().open(DATABASE_NAME);
                String outFileName = context.getDatabasePath(DATABASE_NAME).getPath();
                OutputStream outputStream = Files.newOutputStream(Paths.get(outFileName));

                byte[] buffer = new byte[1024];
                int length;
                while ((length = inputStream.read(buffer)) > 0) {
                    outputStream.write(buffer, 0, length);
                }

                outputStream.flush();
                outputStream.close();
                inputStream.close();

                Log.d("DatabaseHelper", "Database copied successfully");

                // Set the flag to indicate that the database has been copied
                setDatabaseCopied();

            } catch (IOException e) {
                e.printStackTrace();
                Log.e("DatabaseHelper", "Error copying database");
            }
        }
    }

    public void insertNewEntry(float temperature, float humidity, float co2, float voc, float pm10, float pm25) {
        SQLiteDatabase db = this.getWritableDatabase();

        try {
            ContentValues values = new ContentValues();
            values.put("Temperature", temperature);
            values.put("Humidity", humidity);
            values.put("CO2", co2);
            values.put("VOC", voc);
            values.put("PM10", pm10);
            values.put("PM25", pm25);

            // Get the current time in milliseconds
            long currentTimeMillis = System.currentTimeMillis();

            // Convert the time to a formatted date string
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS", Locale.getDefault());
            String formattedTime = dateFormat.format(new Date(currentTimeMillis));

            //values.put("Timestamp", formattedTime);
            values.put("Timestamp", "2024-03-14 23:47:37.189509"); //CHANGE ONCE WE REMOVED FAKE VALUES IN DB

            // Insert the new entry
            db.insert("sensor_data", null, values);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            // Close the database connection in a finally block to ensure it gets closed
            if (db != null && db.isOpen()) {
                db.close();
            }
        }
    }



    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        android.util.Log.w("AirQualityData", "Upgrading database, which will destroy all old data");
        db.execSQL("DROP TABLE IF EXISTS AirQualityData");
        onCreate(db);
    }
}
