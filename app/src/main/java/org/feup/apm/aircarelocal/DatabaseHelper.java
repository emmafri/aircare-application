package org.feup.apm.aircarelocal;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "AirQualityDatabase";
    private static final int DATABASE_VERSION = 1;
    /*public static final String TEMPERATURE = "temperature";
    public static final String HUMIDITY = "humidity";
    public static final String CO2 = "co2";
    public static final String VOC = "voc";
    public static final String PM10 = "pm10";
    public static final String PM25 = "pm25";*/


    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String createTableQuery = "CREATE TABLE AirQualityData (" +
                "_id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "timestamp DATETIME DEFAULT CURRENT_TIMESTAMP," +
                "temperature REAL," +
                "humidity REAL," +
                "co2 REAL," +
                "voc REAL," +
                "pm10 REAL," +
                "pm25 REAL);";
        db.execSQL(createTableQuery);
    }


    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        android.util.Log.w("AirQualityData", "Upgrading database, which will destroy all old data");
        db.execSQL("DROP TABLE IF EXISTS AirQualityData");
        onCreate(db);
    }
}
