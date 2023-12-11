package org.feup.apm.aircarelocal;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.media.Rating;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Space;
import android.widget.TextView;
import android.widget.Toast;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Set;


public class MainActivity extends AppCompatActivity implements BluetoothHelper.ConnectionListener {
    private DatabaseHelper databaseHelper;
    private SQLiteDatabase db = null;
    private TextView timeTextView, temperatureTextView, humidityTextView;
    private double pm25, pm10, co, voc;
    private BluetoothHelper bluetoothHelper;
    private BluetoothHelper.ConnectionListener connectionListener = this;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        databaseHelper = new DatabaseHelper(this);
        databaseHelper.copyDatabase();
        db = (databaseHelper).getWritableDatabase();


        // BLUETOOTH
        bluetoothHelper = new BluetoothHelper(this, connectionListener);
        bluetoothHelper.initializeBluetooth();

        //Set OnClickListener for "New Reading" button
        View newReadingButton = findViewById(R.id.NewReadingButton);
        newReadingButton.setOnTouchListener(new View.OnTouchListener() {
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

        newReadingButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                bluetoothHelper.connectToSensor();
                // Bluetooth data received (replace this with actual data)
                String sensorData = "&AMB21 30;0;1013.88;4;21.51;2541;7356;0;8;32;0;100|\n";

                //String sensorData = bluetoothHelper.getLatestSensorData();

                // Parse sensor data to extract individual parameters
                float temperature = parseSensorData(sensorData, 4);
                float humidity = parseSensorData(sensorData, 3);
                float co = parseSensorData(sensorData, 1); //OBS its CO not CO2
                float voc = parseSensorData(sensorData, 10);
                float pm10 = parseSensorData(sensorData, 9);
                float pm25 = parseSensorData(sensorData, 8);

                // Insert a new entry in the database
                databaseHelper.insertNewEntry(temperature, humidity, co, voc, pm10, pm25);

                //update ui elements
                updateLatestReading();
            }
        });


        // TOOLBAR
        Toolbar toolbar = findViewById(R.id.customToolbar);
        setSupportActionBar(toolbar);
        //Hide backbutton on main activity and show empty space instead
        ImageView backButton = findViewById(R.id.backButton);
        backButton.setVisibility(View.GONE);
        Space backButtonSpace = findViewById(R.id.backButtonSpace);
        backButtonSpace.setVisibility(View.VISIBLE);



        timeTextView = findViewById(R.id.Time);
        temperatureTextView = findViewById(R.id.temperature_value);
        humidityTextView = findViewById(R.id.humidity_value);
        updateLatestReading();


        Handler handler = new Handler();
        View historyButton = findViewById(R.id.HistoryButton);
        View detailedButton = findViewById(R.id.BottomLayout);

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
        SQLiteDatabase db = null;
        Cursor cursor = null;

        try {
            // Get a readable database
            db = databaseHelper.getReadableDatabase();

            // Define the columns you want to retrieve
            String[] projection = {"Timestamp", "Temperature", "Humidity", "CO", "VOC", "PM25", "PM10"};
            String[] measures = {};

            // Query the database to get the latest timestamp
            cursor = db.query(
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
                temperatureTextView.setText(formatValue(temperature, 1));

                float humidity = cursor.getFloat(cursor.getColumnIndexOrThrow("Humidity"));
                humidityTextView.setText(formatValue(humidity, 1));

                pm25 = cursor.getDouble(cursor.getColumnIndexOrThrow("PM25"));
                pm10 = cursor.getDouble(cursor.getColumnIndexOrThrow("PM10"));
                co = cursor.getDouble(cursor.getColumnIndexOrThrow("CO"));
                voc = cursor.getDouble(cursor.getColumnIndexOrThrow("VOC"));

                AirQualityCalculator.AirQualityResult airQualityResult = AirQualityCalculator.getAirQualityResult(pm25, pm10, co, voc);

                AirQualityCalculator.AirQualityCategory overallCategory = airQualityResult.getOverallCategory();

                // Updates air quality box based on the air quality category
                switch (overallCategory) {
                    case GOOD:
                        findViewById(R.id.BottomLayout).setBackgroundResource(R.drawable.green_box);
                        TextView good = findViewById(R.id.Rating);
                        good.setText(getString(R.string.good_rating));
                        break;
                    case MEDIUM:
                        findViewById(R.id.BottomLayout).setBackgroundResource(R.drawable.yellow_box);
                        TextView medium = findViewById(R.id.Rating);
                        medium.setText(getString(R.string.medium_rating));
                        break;
                    case BAD:
                        findViewById(R.id.BottomLayout).setBackgroundResource(R.drawable.red_box);
                        TextView bad = findViewById(R.id.Rating);
                        bad.setText(getString(R.string.bad_rating));
                        break;
                    case VERY_BAD:
                        findViewById(R.id.BottomLayout).setBackgroundResource(R.drawable.purple_box);
                        TextView vrybad = findViewById(R.id.Rating);
                        vrybad.setText(getString(R.string.vrybad_rating));
                        break;
                    default:
                        // Handle default case if needed
                        break;
                }

            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            // Close the cursor and database in a finally block to ensure they get closed
            if (cursor != null && !cursor.isClosed()) {
                cursor.close();
            }

            if (db != null && db.isOpen()) {
                db.close();
            }
        }
    }

    // Helper method to parse sensor data and extract a specific parameter
    private float parseSensorData(String sensorData, int index) {
        try {
            // Split the sensor data using ';' as the delimiter
            String[] dataParts = sensorData.split(";");

            // Extract the parameter at the specified index
            return Float.parseFloat(dataParts[index]);
        } catch (Exception e) {
            e.printStackTrace();
            return 0.0f;  // Default value if parsing fails
        }
    }

    // Format value from database to only show desired number of decimals
    private String formatValue(float originalValue, int decimalPlacesToShow) {
        String formattedValue = String.format("%." + decimalPlacesToShow + "f", originalValue);
        return formattedValue;
    }

    private String formatTimestamp(String timestampString) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS", Locale.getDefault());
        Date date;
        try {
            date = sdf.parse(timestampString);
        } catch (ParseException e) {
            e.printStackTrace();
            return ""; // Return an empty string in case of an error
        }

        // Format the Date object to the desired HH:mm:ss format
        SimpleDateFormat outputFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        return outputFormat.format(date);
    }

    private void startPopUpAnimation(View view) {
        Animation popUpAnimation = AnimationUtils.loadAnimation(this, R.anim.pop_up);
        view.startAnimation(popUpAnimation);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        bluetoothHelper.closeBluetoothConnection();

        // Close the database when the activity is destroyed
        if (db != null && db.isOpen()) {
            db.close();
        }
    }

    @Override
    public void onConnectionResult(boolean isConnected) {
        if (isConnected) {
            // Connection successful
            Toast.makeText(MainActivity.this, "Connected to AmbiUnit", Toast.LENGTH_SHORT).show();
        } else {
            // Connection failed
            Toast.makeText(MainActivity.this, "Failed to connect to AmbiUnit", Toast.LENGTH_SHORT).show();
        }
    }
}

