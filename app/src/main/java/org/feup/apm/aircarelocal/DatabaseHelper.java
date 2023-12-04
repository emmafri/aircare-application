package org.feup.apm.aircarelocal;

import android.content.Context;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.preference.PreferenceManager;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import android.util.Log;

public class DatabaseHelper extends SQLiteOpenHelper {
    private final Context context;
    private static final String DATABASE_NAME = "aircare.db";
    private static final int DATABASE_VERSION = 2;

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.context = context;

    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        /*String createTableQuery = "CREATE TABLE AirQualityData (" +
                "_id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "timestamp DATETIME DEFAULT CURRENT_TIMESTAMP," +
                "temperature REAL," +
                "humidity REAL," +
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

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        android.util.Log.w("AirQualityData", "Upgrading database, which will destroy all old data");
        db.execSQL("DROP TABLE IF EXISTS AirQualityData");
        onCreate(db);
    }
}
