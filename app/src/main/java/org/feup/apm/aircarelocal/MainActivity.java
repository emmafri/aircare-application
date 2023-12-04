package org.feup.apm.aircarelocal;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Bundle;
import android.os.Handler;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;


public class MainActivity extends AppCompatActivity {
    private DatabaseHelper databaseHelper;
    private SQLiteDatabase db = null;
    private TextView timeTextView;
    private TextView temperatureTextView;
    private TextView humidityTextView;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        databaseHelper = new DatabaseHelper(this);

        databaseHelper.copyDatabase();

        db = (databaseHelper).getWritableDatabase();

        timeTextView = findViewById(R.id.Time);
        temperatureTextView = findViewById(R.id.temperature_value);
        humidityTextView = findViewById(R.id.humidity_value);
        updateLatestReading();


        ActionBar actionbar = getSupportActionBar();
        if (actionbar != null) {
            actionbar.setLogo(R.drawable.aircare_logo);
            actionbar.setDisplayUseLogoEnabled(true);
            actionbar.setDisplayShowHomeEnabled(true);
        }

        View detailedButton = findViewById(R.id.BottomLayout);
        Handler handler = new Handler();
        View historyButton = findViewById(R.id.HistoryButton);

        detailedButton.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        break;

                    case MotionEvent.ACTION_UP:
                        startPopUpAnimation(v);
                        break;
                }
                return false;
            }
        });

        historyButton.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        break;

                    case MotionEvent.ACTION_UP:
                        startPopUpAnimation(v);
                        break;
                }
                return false;
            }
        });


        // Set OnClickListener for "test" layout
        detailedButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        // Create an Intent to start ParameterInfoActivity
                        Intent intent = new Intent(MainActivity.this, DetailedReadingActivity.class);
                        // Start the new activity
                        startActivity(intent);

                    }
                },300);

            }
        });


        historyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        // Create an Intent to start ParameterInfoActivity
                        Intent intent = new Intent(MainActivity.this, HistoryActivity.class);
                        // Start the new activity
                        startActivity(intent);

                    }
                },300);

            }
        });
    }

    private void updateLatestReading() {

        // Define the columns you want to retrieve
        String[] projection = {"Timestamp", "Temperature", "Humidity"};

        // Query the database to get the latest timestamp
        Cursor cursor = db.query(
                "sensor_data",
                projection,
                null,
                null,
                null,
                null,
                "Timestamp DESC",  // Order by timestamp in descending order to get the latest
                "1"  // Limit to 1 result to get only the latest timestamp
        );

        // Check if there is data in the cursor
        if (cursor.moveToFirst()) {
            // Retrieve the timestamp from the cursor
            String timestampString = cursor.getString(cursor.getColumnIndexOrThrow("Timestamp"));

            // Convert timestamp to a human-readable format
            String formattedTime = formatTimestamp(timestampString);

            // Update the TextView with the formatted time
            timeTextView.setText(formattedTime);

            float temperature = cursor.getFloat(cursor.getColumnIndexOrThrow("Temperature"));
            temperatureTextView.setText(formatValue(temperature,1));

            float humidity = cursor.getFloat(cursor.getColumnIndexOrThrow("Humidity"));
            humidityTextView.setText(formatValue(humidity,1));

        }

        // Close the cursor and database
        cursor.close();
    }

    // Format value from database to only show desired number of decimals
    private String formatValue(float originalValue, int decimalPlacesToShow) {

        String formattedValue = String.format("%." + decimalPlacesToShow + "f", originalValue);

        return formattedValue;
    }


    private String formatTimestamp(String timestampString) {
        // Parse the timestamp string into a Date object
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSSSSS", Locale.getDefault());
        Date date;
        try {
            date = sdf.parse(timestampString);
        } catch (ParseException e) {
            // Handle the parse exception if needed
            e.printStackTrace();
            return ""; // Return an empty string in case of an error
        }

        // Format the Date object to the desired HH:mm format
        SimpleDateFormat outputFormat = new SimpleDateFormat("HH:mm:ss", Locale.getDefault());
        return outputFormat.format(date);
    }



    private void startPopUpAnimation(View view) {
        Animation popUpAnimation = AnimationUtils.loadAnimation(this, R.anim.pop_up);
        view.startAnimation(popUpAnimation);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Close the database when the activity is destroyed
        if (db != null && db.isOpen()) {
            db.close();
        }
    }
}

