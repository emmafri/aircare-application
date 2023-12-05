package org.feup.apm.aircarelocal;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Handler;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import androidx.appcompat.widget.Toolbar;
import android.widget.TextView;


import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

public class DetailedReadingActivity extends AppCompatActivity {
    private DatabaseHelper dbHelper;
    private TextView pm25TextView;
    private TextView pm10TextView;
    private TextView co2TextView;
    private TextView vocTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.detailed_reading);

        Toolbar toolbar = findViewById(R.id.customToolbar); // Find your toolbar by its ID
        setSupportActionBar(toolbar); // Set the toolbar as the ActionBar

        ImageView backButton = findViewById(R.id.backButton);
        ImageView menuButton = findViewById(R.id.menuButton);
        dbHelper = new DatabaseHelper(this);

        // Set OnClickListener for the backButton
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        // Set OnClickListener for the menuButton
        menuButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Add  action when menuButton is clicked
            }
        });


        Handler handler = new Handler();
        View historyButton = findViewById(R.id.HistoryButton);
        View pm25Button = findViewById(R.id.pm25_block);
        View pm10Button = findViewById(R.id.pm10_block);
        View co2Button = findViewById(R.id.co2_block);
        View vocButton = findViewById(R.id.vocs_block);

        pm25TextView = findViewById(R.id.pm25_value);
        pm10TextView = findViewById(R.id.pm10_value);
        co2TextView = findViewById(R.id.co2_value);
        vocTextView = findViewById(R.id.vocs_value);
        updateLatestReading();


        //history button
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
        historyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        // Create an Intent to start ParameterInfoActivity
                        Intent intent = new Intent(DetailedReadingActivity.this, HistoryActivity.class);
                        // Start the new activity
                        startActivity(intent);
                    }
                }, 300);

            }
        });

        //pm2.5 button
        pm25Button.setOnTouchListener(new View.OnTouchListener() {
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
        pm25Button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        Intent intent = new Intent(DetailedReadingActivity.this, ParameterInfoActivity.class);
                        intent.putExtra("PARAM_NAME", getString(R.string.param_name_pm25));
                        intent.putExtra("PARAM_DESCR", getString(R.string.param_descr_pm25));
                        intent.putExtra("GOOD_VALUES", getString(R.string.good_values_pm25));
                        intent.putExtra("MEDIUM_VALUES", getString(R.string.medium_values_pm25));
                        intent.putExtra("BAD_VALUES", getString(R.string.bad_values_pm25));
                        intent.putExtra("VERYBAD_VALUES", getString(R.string.very_bad_values_pm25));
                        intent.putExtra("PARAM_ADVICE",getString(R.string.advice_pm25));
                        startActivity(intent);
                    }
                }, 300);
            }
        });


        //pm10 button
        pm10Button.setOnTouchListener(new View.OnTouchListener() {
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
        pm10Button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        Intent intent = new Intent(DetailedReadingActivity.this, ParameterInfoActivity.class);
                        intent.putExtra("PARAM_NAME", getString(R.string.param_name_pm10));
                        intent.putExtra("PARAM_DESCR", getString(R.string.param_descr_pm10));
                        intent.putExtra("GOOD_VALUES", getString(R.string.good_values_pm10));
                        intent.putExtra("MEDIUM_VALUES", getString(R.string.medium_values_pm10));
                        intent.putExtra("BAD_VALUES", getString(R.string.bad_values_pm10));
                        intent.putExtra("VERYBAD_VALUES", getString(R.string.very_bad_values_pm10));
                        intent.putExtra("PARAM_ADVICE",getString(R.string.advice_pm10));
                        startActivity(intent);
                    }
                }, 300);
            }
        });

        //voc button
        vocButton.setOnTouchListener(new View.OnTouchListener() {
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
        vocButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        Intent intent = new Intent(DetailedReadingActivity.this, ParameterInfoActivity.class);
                        intent.putExtra("PARAM_NAME", getString(R.string.param_name_voc));
                        intent.putExtra("PARAM_DESCR", getString(R.string.param_descr_voc));
                        intent.putExtra("GOOD_VALUES", getString(R.string.good_values_voc));
                        intent.putExtra("MEDIUM_VALUES", getString(R.string.medium_values_voc));
                        intent.putExtra("BAD_VALUES", getString(R.string.bad_values_voc));
                        intent.putExtra("VERYBAD_VALUES", getString(R.string.very_bad_values_voc));
                        intent.putExtra("PARAM_ADVICE",getString(R.string.advice_voc));
                        startActivity(intent);
                    }
                }, 300);
            }
        });

        //co2 button
        co2Button.setOnTouchListener(new View.OnTouchListener() {
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
        co2Button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        Intent intent = new Intent(DetailedReadingActivity.this, ParameterInfoActivity.class);
                        intent.putExtra("PARAM_NAME", getString(R.string.param_name_co2));
                        intent.putExtra("PARAM_DESCR", getString(R.string.param_descr_co2));
                        intent.putExtra("GOOD_VALUES", getString(R.string.good_values_co2));
                        intent.putExtra("MEDIUM_VALUES", getString(R.string.medium_values_co2));
                        intent.putExtra("BAD_VALUES", getString(R.string.bad_values_co2));
                        intent.putExtra("VERYBAD_VALUES", getString(R.string.very_bad_values_co2));
                        intent.putExtra("PARAM_ADVICE",getString(R.string.advice_co2));
                        startActivity(intent);
                    }
                }, 300);
            }
        });


    }

    private void startPopUpAnimation(View view) {
        Animation popUpAnimation = AnimationUtils.loadAnimation(this, R.anim.pop_up);
        view.startAnimation(popUpAnimation);
    }


    private void updateLatestReading() {
        SQLiteDatabase db = dbHelper.getReadableDatabase(); // or getWritableDatabase() depending on your needs
        String[] projection = {"Timestamp", "PM25", "PM10", "CO2", "VOC"};

        Cursor cursor = db.query(
                "sensor_data",
                projection,
                null,
                null,
                null,
                null,
                "Timestamp DESC",  //descending order
                "1"  // Limit to 1 result to get only the latest values
        );

        // Check if there is data in the cursor
        if (cursor.moveToFirst()) {
            /*String timestampString = cursor.getString(cursor.getColumnIndexOrThrow("Timestamp"));

            // Convert timestamp to a human-readable format
            String formattedTime = formatTimestamp(timestampString);

            // Update the TextView with the formatted time
            timeTextView.setText(formattedTime);*/

            float pm25 = cursor.getFloat(cursor.getColumnIndexOrThrow("PM25"));
            pm25TextView.setText(formatValue(pm25,2));

            float pm10 = cursor.getFloat(cursor.getColumnIndexOrThrow("PM10"));
            pm10TextView.setText(formatValue(pm10,2));

            float co2 = cursor.getFloat(cursor.getColumnIndexOrThrow("CO2"));
            co2TextView.setText(formatValue(co2,2));

            float voc = cursor.getFloat(cursor.getColumnIndexOrThrow("VOC"));
            vocTextView.setText(formatValue(voc,5));

        }

        // Close the cursor and database
        cursor.close();
    }

    // Format value from database to only show desired amount of decimals
    private String formatValue(float originalValue, int decimalPlacesToShow) {
        String formattedValue = String.format("%." + decimalPlacesToShow + "f", originalValue);
        return formattedValue;
    }
}
