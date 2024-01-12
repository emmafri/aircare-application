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

import android.widget.Space;
import android.widget.TextView;


import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

public class DetailedReadingActivity extends AppCompatActivity {
    // DatabaseHelper instance for handling database operations
    private DatabaseHelper dbHelper;

    // TextViews to display air quality parameters
    private TextView pm25TextView;
    private TextView pm10TextView;
    private TextView coTextView;
    private TextView vocTextView;

    // Buttons to display air quality categories
    private View pm25Button;
    private View pm10Button;
    private View coButton;
    private View vocButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.detailed_reading);

        // Retrieve data source from the previous activity
        String source = getIntent().getStringExtra("source");
        dbHelper = new DatabaseHelper(this);

        // Toolbar setup for navigation and actions
        setupToolbar();

        // Set up UI components and event listeners
        initializeUI();

        // Depending on the source, update the UI with the latest reading or a selected reading
        if ("MainActivity".equals(source)) {
            updateLatestReading();
        }
        else{
            getSelectedReading();
        }
    }

    // Toolbar setup for navigation and actions
    private void setupToolbar() {
        Toolbar toolbar = findViewById(R.id.customToolbar);
        setSupportActionBar(toolbar);

        // Hide back button on main activity and show empty space instead; Show info button
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
            Intent intent = new Intent(DetailedReadingActivity.this, MainActivity.class);
            intent.putExtra("source", "MainActivity");
            startActivity(intent);
        });

        // Back button
        backButton.setOnClickListener(v -> onBackPressed());

        // Info button
        infoButton.setOnClickListener(v -> {
            Intent intent = new Intent(DetailedReadingActivity.this, InfoActivity.class);
            startActivity(intent);
        });
    }

    // Set up UI components and event listeners
    private void initializeUI() {
        Handler handler = new Handler();
        View historyButton = findViewById(R.id.HistoryButton);
        pm25Button = findViewById(R.id.pm25_block);
        pm10Button = findViewById(R.id.pm10_block);
        coButton = findViewById(R.id.co_block);
        vocButton = findViewById(R.id.vocs_block);

        pm25TextView = findViewById(R.id.pm25_value);
        pm10TextView = findViewById(R.id.pm10_value);
        coTextView = findViewById(R.id.co_value);
        vocTextView = findViewById(R.id.vocs_value);

        // History button
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
        historyButton.setOnClickListener(v -> handler.postDelayed(() -> {
            // Create an Intent to start ParameterInfoActivity
            Intent intent = new Intent(DetailedReadingActivity.this, HistoryActivity.class);
            // Start the new activity
            startActivity(intent);
        }, 300));

        //PM2.5 button
        pm25Button.setOnTouchListener((v, event) -> {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    break;

                case MotionEvent.ACTION_UP:
                    startPopUpAnimation(v);
                    break;
            }
            return false;
        });
        pm25Button.setOnClickListener(v -> handler.postDelayed(() -> {
            Intent intent = new Intent(DetailedReadingActivity.this, ParameterInfoActivity.class);
            intent.putExtra("PARAM_NAME", getString(R.string.param_name_pm25));
            intent.putExtra("PARAM_DESCR", getString(R.string.param_descr_pm25));
            intent.putExtra("GOOD_VALUES", getString(R.string.good_values_pm25));
            intent.putExtra("MEDIUM_VALUES", getString(R.string.medium_values_pm25));
            intent.putExtra("BAD_VALUES", getString(R.string.bad_values_pm25));
            intent.putExtra("VERYBAD_VALUES", getString(R.string.very_bad_values_pm25));
            intent.putExtra("PARAM_ADVICE",getString(R.string.advice_pm25));
            intent.putExtra("PARAM_MEASURE", getString(R.string.pm_measure));
            startActivity(intent);
        }, 300));


        //PM10 button
        pm10Button.setOnTouchListener((v, event) -> {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    break;

                case MotionEvent.ACTION_UP:
                    startPopUpAnimation(v);
                    break;
            }
            return false;
        });
        pm10Button.setOnClickListener(v -> handler.postDelayed(() -> {
            Intent intent = new Intent(DetailedReadingActivity.this, ParameterInfoActivity.class);
            intent.putExtra("PARAM_NAME", getString(R.string.param_name_pm10));
            intent.putExtra("PARAM_DESCR", getString(R.string.param_descr_pm10));
            intent.putExtra("GOOD_VALUES", getString(R.string.good_values_pm10));
            intent.putExtra("MEDIUM_VALUES", getString(R.string.medium_values_pm10));
            intent.putExtra("BAD_VALUES", getString(R.string.bad_values_pm10));
            intent.putExtra("VERYBAD_VALUES", getString(R.string.very_bad_values_pm10));
            intent.putExtra("PARAM_ADVICE",getString(R.string.advice_pm10));
            intent.putExtra("PARAM_MEASURE", getString(R.string.pm_measure));
            startActivity(intent);
        }, 300));

        //VOC button
        vocButton.setOnTouchListener((v, event) -> {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    break;

                case MotionEvent.ACTION_UP:
                    startPopUpAnimation(v);
                    break;
            }
            return false;
        });
        vocButton.setOnClickListener(v -> handler.postDelayed(() -> {
            Intent intent = new Intent(DetailedReadingActivity.this, ParameterInfoActivity.class);
            intent.putExtra("PARAM_NAME", getString(R.string.param_name_voc));
            intent.putExtra("PARAM_DESCR", getString(R.string.param_descr_voc));
            intent.putExtra("GOOD_VALUES", getString(R.string.good_values_voc));
            intent.putExtra("MEDIUM_VALUES", getString(R.string.medium_values_voc));
            intent.putExtra("BAD_VALUES", getString(R.string.bad_values_voc));
            intent.putExtra("VERYBAD_VALUES", getString(R.string.very_bad_values_voc));
            intent.putExtra("PARAM_ADVICE",getString(R.string.advice_voc));
            intent.putExtra("PARAM_MEASURE", getString(R.string.measure_VOC));
            startActivity(intent);
        }, 300));

        //CO button
        coButton.setOnTouchListener((v, event) -> {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    break;

                case MotionEvent.ACTION_UP:
                    startPopUpAnimation(v);
                    break;
            }
            return false;
        });
        coButton.setOnClickListener(v -> handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent(DetailedReadingActivity.this, ParameterInfoActivity.class);
                intent.putExtra("PARAM_NAME", getString(R.string.param_name_co));
                intent.putExtra("PARAM_DESCR", getString(R.string.param_descr_co));
                intent.putExtra("GOOD_VALUES", getString(R.string.good_values_co));
                intent.putExtra("MEDIUM_VALUES", getString(R.string.medium_values_co));
                intent.putExtra("BAD_VALUES", getString(R.string.bad_values_co));
                intent.putExtra("VERYBAD_VALUES", getString(R.string.very_bad_values_co));
                intent.putExtra("PARAM_ADVICE",getString(R.string.advice_co));
                intent.putExtra("PARAM_MEASURE", getString(R.string.measure_CO));
                startActivity(intent);
            }
        }, 300));
    }

    // Start the pop-up animation for the given view
    private void startPopUpAnimation(View view) {
        Animation popUpAnimation = AnimationUtils.loadAnimation(this, R.anim.pop_up);
        view.startAnimation(popUpAnimation);
    }

    // Update UI with the latest reading from the database
    private void updateLatestReading() {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        String[] projection = {"Timestamp", "PM25", "PM10", "CO", "VOC"};

        Cursor cursor = db.query(
                "sensor_data",
                projection,
                null,
                null,
                null,
                null,
                "Timestamp DESC",  // Descending order
                "1"  // Limit to 1 result to get only the latest values
        );

        // Retrieve and display air quality parameters
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

            float co = cursor.getFloat(cursor.getColumnIndexOrThrow("CO"));
            coTextView.setText(formatValue(co,2));

            float voc = cursor.getFloat(cursor.getColumnIndexOrThrow("VOC"));
            vocTextView.setText(formatValue(voc,2));

            AirQualityCalculator.AirQualityResult airQualityResult = AirQualityCalculator.getAirQualityResult(pm25, pm10,co,voc);

            AirQualityCalculator.AirQualityCategory overallCategory = airQualityResult.getOverallCategory();
            AirQualityCalculator.AirQualityCategory pm25Category = airQualityResult.getPm25Category();
            AirQualityCalculator.AirQualityCategory pm10Category = airQualityResult.getPm10Category();
            AirQualityCalculator.AirQualityCategory coCategory = airQualityResult.getCoCategory();
            AirQualityCalculator.AirQualityCategory vocCategory = airQualityResult.getVocCategory();

            // Updates air quality box based on the air quality category
            switch (overallCategory) {
                case GOOD:
                    findViewById(R.id.Rating).setBackgroundResource(R.drawable.green_box);
                    TextView good = findViewById(R.id.rating_text);
                    good.setText(getString(R.string.good_rating));
                    break;
                case MEDIUM:
                    findViewById(R.id.Rating).setBackgroundResource(R.drawable.yellow_box);
                    TextView medium = findViewById(R.id.rating_text);
                    medium.setText(getString(R.string.medium_rating));
                    break;
                case BAD:
                    findViewById(R.id.Rating).setBackgroundResource(R.drawable.red_box);
                    TextView bad = findViewById(R.id.rating_text);
                    bad.setText(getString(R.string.bad_rating));
                    break;
                case VERY_BAD:
                    findViewById(R.id.Rating).setBackgroundResource(R.drawable.purple_box);
                    TextView vrybad = findViewById(R.id.rating_text);
                    vrybad.setText(getString(R.string.vrybad_rating));
                    break;
                default:
                    // Handle default case if needed
                    break;
            }

            if(!pm25Category.equals(overallCategory)) {
                switch (pm25Category) {
                    case GOOD:
                        pm25Button.setBackgroundResource(R.drawable.green_box);
                        break;
                    case MEDIUM:
                        pm25Button.setBackgroundResource(R.drawable.yellow_box);
                        break;
                    case BAD:
                        pm25Button.setBackgroundResource(R.drawable.red_box);
                        break;
                    case VERY_BAD:
                        pm25Button.setBackgroundResource(R.drawable.purple_box);
                        break;
                    default:
                        // Handle default case if needed
                        break;
                }
            }

            if(!pm10Category.equals(overallCategory)) {
                switch (pm10Category) {
                    case GOOD:
                        pm10Button.setBackgroundResource(R.drawable.green_box);
                        break;
                    case MEDIUM:
                        pm10Button.setBackgroundResource(R.drawable.yellow_box);
                        break;
                    case BAD:
                        pm10Button.setBackgroundResource(R.drawable.red_box);
                        break;
                    case VERY_BAD:
                        pm10Button.setBackgroundResource(R.drawable.purple_box);
                        break;
                    default:
                        // Handle default case if needed
                        break;
                }
            }

            if(!coCategory.equals(overallCategory)) {
                switch (coCategory) {
                    case GOOD:
                        coButton.setBackgroundResource(R.drawable.green_box);
                        break;
                    case MEDIUM:
                        coButton.setBackgroundResource(R.drawable.yellow_box);
                        break;
                    case BAD:
                        coButton.setBackgroundResource(R.drawable.red_box);
                        break;
                    case VERY_BAD:
                        coButton.setBackgroundResource(R.drawable.purple_box);
                        break;
                    default:
                        // Handle default case if needed
                        break;
                }
            }

            if(!vocCategory.equals(overallCategory)) {
                switch (vocCategory) {
                    case GOOD:
                        vocButton.setBackgroundResource(R.drawable.green_box);
                        break;
                    case MEDIUM:
                        vocButton.setBackgroundResource(R.drawable.yellow_box);
                        break;
                    case BAD:
                        vocButton.setBackgroundResource(R.drawable.red_box);
                        break;
                    case VERY_BAD:
                        vocButton.setBackgroundResource(R.drawable.purple_box);
                        break;
                    default:
                        // Handle default case if needed
                        break;
                }
            }

        }

        // Close the cursor and database
        cursor.close();
    }

    // Get and display a selected reading passed from another activity
    private void getSelectedReading(){
        long timestamp = getIntent().getLongExtra("timestamp", 0L); // Replace with the default value or appropriate handling
        float pm25 = getIntent().getFloatExtra("pm25", 0.0f);
        float pm10 = getIntent().getFloatExtra("pm10", 0.0f);
        float co = getIntent().getFloatExtra("co", 0.0f);
        float voc = getIntent().getFloatExtra("voc", 0.0f);

        pm25TextView.setText(formatValue(pm25,2));
        pm10TextView.setText(formatValue(pm10,2));
        coTextView.setText(formatValue(co,2));
        vocTextView.setText(formatValue(voc,2));
        AirQualityCalculator.AirQualityResult airQualityResult = AirQualityCalculator.getAirQualityResult(pm25, pm10,co,voc);

        AirQualityCalculator.AirQualityCategory overallCategory = airQualityResult.getOverallCategory();
        AirQualityCalculator.AirQualityCategory pm25Category = airQualityResult.getPm25Category();
        AirQualityCalculator.AirQualityCategory pm10Category = airQualityResult.getPm10Category();
        AirQualityCalculator.AirQualityCategory coCategory = airQualityResult.getCoCategory();
        AirQualityCalculator.AirQualityCategory vocCategory = airQualityResult.getVocCategory();

        // Updates air quality box based on the air quality category
        switch (overallCategory) {
            case GOOD:
                findViewById(R.id.Rating).setBackgroundResource(R.drawable.green_box);
                TextView good = findViewById(R.id.rating_text);
                good.setText(getString(R.string.good_rating));
                break;
            case MEDIUM:
                findViewById(R.id.Rating).setBackgroundResource(R.drawable.yellow_box);
                TextView medium = findViewById(R.id.rating_text);
                medium.setText(getString(R.string.medium_rating));
                break;
            case BAD:
                findViewById(R.id.Rating).setBackgroundResource(R.drawable.red_box);
                TextView bad = findViewById(R.id.rating_text);
                bad.setText(getString(R.string.bad_rating));
                break;
            case VERY_BAD:
                findViewById(R.id.Rating).setBackgroundResource(R.drawable.purple_box);
                TextView vrybad = findViewById(R.id.rating_text);
                vrybad.setText(getString(R.string.vrybad_rating));
                break;
            default:
                // Handle default case if needed
                break;
        }

        if(!pm25Category.equals(overallCategory)) {
            switch (pm25Category) {
                case GOOD:
                    pm25Button.setBackgroundResource(R.drawable.green_box);
                    break;
                case MEDIUM:
                    pm25Button.setBackgroundResource(R.drawable.yellow_box);
                    break;
                case BAD:
                    pm25Button.setBackgroundResource(R.drawable.red_box);
                    break;
                case VERY_BAD:
                    pm25Button.setBackgroundResource(R.drawable.purple_box);
                    break;
                default:
                    // Handle default case if needed
                    break;
            }
        }

        if(!pm10Category.equals(overallCategory)) {
            switch (pm10Category) {
                case GOOD:
                    pm10Button.setBackgroundResource(R.drawable.green_box);
                    break;
                case MEDIUM:
                    pm10Button.setBackgroundResource(R.drawable.yellow_box);
                    break;
                case BAD:
                    pm10Button.setBackgroundResource(R.drawable.red_box);
                    break;
                case VERY_BAD:
                    pm10Button.setBackgroundResource(R.drawable.purple_box);
                    break;
                default:
                    // Handle default case if needed
                    break;
            }
        }

        if(!coCategory.equals(overallCategory)) {
            switch (coCategory) {
                case GOOD:
                    coButton.setBackgroundResource(R.drawable.green_box);
                    break;
                case MEDIUM:
                    coButton.setBackgroundResource(R.drawable.yellow_box);
                    break;
                case BAD:
                    coButton.setBackgroundResource(R.drawable.red_box);
                    break;
                case VERY_BAD:
                    coButton.setBackgroundResource(R.drawable.purple_box);
                    break;
                default:
                    // Handle default case if needed
                    break;
            }
        }

        if(!vocCategory.equals(overallCategory)) {
            switch (vocCategory) {
                case GOOD:
                    vocButton.setBackgroundResource(R.drawable.green_box);
                    break;
                case MEDIUM:
                    vocButton.setBackgroundResource(R.drawable.yellow_box);
                    break;
                case BAD:
                    vocButton.setBackgroundResource(R.drawable.red_box);
                    break;
                case VERY_BAD:
                    vocButton.setBackgroundResource(R.drawable.purple_box);
                    break;
                default:
                    // Handle default case if needed
                    break;
            }
        }

    }

    // Format a float value to display a specific number of decimal places
    // returns formatted value as a String
    private String formatValue(float originalValue, int decimalPlacesToShow) {
        String formattedValue = String.format("%." + decimalPlacesToShow + "f", originalValue);
        return formattedValue;
    }
}
