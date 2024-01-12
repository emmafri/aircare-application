package org.feup.apm.aircarelocal;

import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.Space;
import android.widget.TextView;
import android.widget.Toast;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;


public class MainActivity extends AppCompatActivity implements BluetoothHelper.ConnectionListener {
    private static final int MY_BLUETOOTH_PERMISSION_REQUEST = 1;
    private DatabaseHelper databaseHelper;
    private SQLiteDatabase db = null;
    private TextView timeTextView, temperatureTextView, humidityTextView;
    private double pm25, pm10, co, voc;
    private BluetoothHelper bluetoothHelper;
    private BluetoothHelper.ConnectionListener connectionListener = this;
    private boolean isConnected = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        databaseHelper = new DatabaseHelper(this);
        databaseHelper.copyDatabase();
        db = (databaseHelper).getWritableDatabase();

        // Bluetooth setup
        bluetoothHelper = new BluetoothHelper(this, this);
        bluetoothHelper.initializeBluetooth();

        //Set OnClickListener for "New Reading" button
        View newReadingButton = findViewById(R.id.NewReadingButton);
        newReadingButton.setOnTouchListener((v, event) -> {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    break;

                case MotionEvent.ACTION_UP:
                    startPopUpAnimation(v);
                    break;
            }
            return false;
        });

        newReadingButton.setOnClickListener(view -> {

            bluetoothHelper.connectToSensor();
            /* LEAVE for testing purposes without sensor
            //Bluetooth data received (replace this with actual data)
            String sensorData = "&AMB21 30;0;1013.88;4;21.51;2541;7356;0;8;32;0;100|\n";

            String sensorData = bluetoothHelper.getLatestSensorData();

            // Parse sensor data to extract individual parameters
            float temperature = parseSensorData(sensorData, 4);
            float humidity = parseSensorData(sensorData, 3);
            float co = parseSensorData(sensorData, 1); //OBS its CO not CO2
            float voc = parseSensorData(sensorData, 10);
            float pm10 = parseSensorData(sensorData, 9);
            float pm25 = parseSensorData(sensorData, 8);

            // Insert a new entry in the database
            databaseHelper.insertNewEntry(temperature, humidity, co, voc, pm10, pm25);
            */

        });


        // TOOLBAR
        Toolbar toolbar = findViewById(R.id.customToolbar);
        setSupportActionBar(toolbar);
        //Hide back button on main activity and show empty space instead; Show info button
        ImageView backButton = findViewById(R.id.backButton);
        backButton.setVisibility(View.GONE);
        Space backButtonSpace = findViewById(R.id.backButtonSpace);
        backButtonSpace.setVisibility(View.VISIBLE);
        ImageView infoButton = findViewById(R.id.infoButton);
        infoButton.setVisibility(View.VISIBLE);
        Space infoButtonSpace = findViewById(R.id.infoButtonSpace);
        infoButtonSpace.setVisibility(View.GONE);
        ImageView appLogo = findViewById(R.id.appLogo);

        appLogo.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, MainActivity.class);
            intent.putExtra("source", "MainActivity");
            startActivity(intent);
        });

        infoButton.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, InfoActivity.class);
            intent.putExtra("source", "MainActivity");
            startActivity(intent);
        });

        timeTextView = findViewById(R.id.Time);
        temperatureTextView = findViewById(R.id.temperature_value);
        humidityTextView = findViewById(R.id.humidity_value);

        // Update UI elements with values from latest reading
        updateLatestReading();

        Handler handler = new Handler();
        View historyButton = findViewById(R.id.HistoryButton);
        View detailedButton = findViewById(R.id.BottomLayout);

        detailedButton.setOnTouchListener((v, event) -> {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    break;

                case MotionEvent.ACTION_UP:
                    startPopUpAnimation(v);
                    break;
            }
            return false;
        });

        historyButton.setOnTouchListener((v, event) -> {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    break;

                case MotionEvent.ACTION_UP:
                    startPopUpAnimation(v);
                    break;
            }
            return false;
        });


        // Set OnClickListener for "test" layout
        detailedButton.setOnClickListener(v -> handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                // Create an Intent to start ParameterInfoActivity
                Intent intent = new Intent(MainActivity.this, DetailedReadingActivity.class);
                intent.putExtra("source", "MainActivity");
                // Start the new activity
                startActivity(intent);

            }
        }, 300));


        historyButton.setOnClickListener(v -> handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                // Create an Intent to start ParameterInfoActivity
                Intent intent = new Intent(MainActivity.this, HistoryActivity.class);
                // Start the new activity
                startActivity(intent);

            }
        }, 300));

    }


    // Update the UI with the latest sensor reading from the local database.
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

                // Update temperature and humidity values in the UI
                float temperature = cursor.getFloat(cursor.getColumnIndexOrThrow("Temperature"));
                temperatureTextView.setText(formatValue(temperature, 1));

                float humidity = cursor.getFloat(cursor.getColumnIndexOrThrow("Humidity"));
                humidityTextView.setText(formatValue(humidity, 1));

                // Retrieve sensor data for air quality calculation
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

    // Format value from database to only show desired number of decimals
    private String formatValue(float originalValue, int decimalPlacesToShow) {
        String formattedValue = String.format("%." + decimalPlacesToShow + "f", originalValue);
        return formattedValue;
    }

    // Format a timestamp string from the database to a human-readable format.
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

    // Start a pop-up animation on a given view.
    private void startPopUpAnimation(View view) {
        Animation popUpAnimation = AnimationUtils.loadAnimation(this, R.anim.pop_up);
        view.startAnimation(popUpAnimation);
    }

    // Handle actions to be performed when the activity is being destroyed.
    @Override
    protected void onDestroy() {
        super.onDestroy();
        bluetoothHelper.closeBluetoothConnection();

        // Close the database when the activity is destroyed
        if (db != null && db.isOpen()) {
            db.close();
        }
    }

    // Handles UI updates and notifications based on Bluetooth connection result.
    @Override
    public void onConnectionResult(boolean isConnected) {
        if (isConnected) {
            // Connection successful
            if (!this.isConnected) {
                // If it was not previously connected, show "Connected" message
                Toast.makeText(MainActivity.this, "Connected to AmbiUnit", Toast.LENGTH_SHORT).show();
            }
            updateLatestReading();
        } else {
            // Connection failed
            if (this.isConnected) {
                // If it was previously connected, show "Disconnected" message
                Toast.makeText(MainActivity.this, "Disconnected from AmbiUnit", Toast.LENGTH_SHORT).show();
            }
        }
        this.isConnected = isConnected;
    }

    // Handle the result of Bluetooth activation request.
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == MY_BLUETOOTH_PERMISSION_REQUEST) {
            if (resultCode == Activity.RESULT_OK) {
                // Bluetooth is now enabled
                Log.d(TAG, "Bluetooth is now enabled");
                bluetoothHelper.connectToSensor();
            } else {
                // User declined to enable Bluetooth or an error occurred
                Log.e(TAG, "Bluetooth activation declined or an error occurred");
                // Handle accordingly, e.g., show a message to the user
            }
        }
    }
}

