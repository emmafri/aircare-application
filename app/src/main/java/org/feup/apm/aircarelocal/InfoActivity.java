package org.feup.apm.aircarelocal;

import android.content.Intent;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.Space;
import androidx.appcompat.app.AppCompatActivity;

public class InfoActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.info);

        // Set up UI elements and configure toolbar
        configureUI();

        // Set OnClickListeners for Parameter buttons
        setParameterButtonListeners();
    }

    // Configure UI elements, toolbar buttons, and back navigation
    private void configureUI() {
        ImageView backButton = findViewById(R.id.backButton);
        backButton.setVisibility(View.VISIBLE);
        Space backButtonSpace = findViewById(R.id.backButtonSpace);
        backButtonSpace.setVisibility(View.GONE);
        ImageView infoButton = findViewById(R.id.infoButton);
        infoButton.setVisibility(View.GONE);
        Space infoButtonSpace = findViewById(R.id.infoButtonSpace);
        infoButtonSpace.setVisibility(View.VISIBLE);
        ImageView appLogo = findViewById(R.id.appLogo);

        appLogo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(InfoActivity.this, MainActivity.class);
                intent.putExtra("source", "MainActivity");
                startActivity(intent);
            }
        });
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }

    private void setParameterButtonListeners() {
        View ParamButton_PM25 = findViewById(R.id.ParamButton_PM25);

        // Set OnClickListener for ParamButton_PM25
        ParamButton_PM25.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startPopUpAnimation(v);
                goToDetailedReadingActivity("PM2.5");
            }
        });

        // Set OnClickListener for ParamButton_PM10
        View ParamButton_PM10 = findViewById(R.id.ParamButton_PM10);
        ParamButton_PM10.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startPopUpAnimation(v);
                goToDetailedReadingActivity("PM10");
            }
        });

        // Set OnClickListener for ParamButton_CO
        View ParamButton_CO = findViewById(R.id.ParamButton_CO);
        ParamButton_CO.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startPopUpAnimation(v);
                goToDetailedReadingActivity("CO");
            }
        });

        // Set OnClickListener for ParamButton_VOC
        View ParamButton_VOC = findViewById(R.id.ParamButton_VOC);
        ParamButton_VOC.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startPopUpAnimation(v);
                goToDetailedReadingActivity("VOC");
            }
        });
    }

    // Start the pop-up animation for the given view
    private void startPopUpAnimation(View view) {
        Animation popUpAnimation = AnimationUtils.loadAnimation(this, R.anim.pop_up);
        view.startAnimation(popUpAnimation);
    }

    // Navigate to DetailedReadingActivity with information about the selected parameter
    private void goToDetailedReadingActivity(String paramName) {
        Intent intent = new Intent(InfoActivity.this, ParameterInfoActivity.class);

        // Set intent extras based on the selected parameter
        switch (paramName) {
            case "PM2.5":
                intent.putExtra("PARAM_NAME", getString(R.string.param_name_pm25));
                intent.putExtra("PARAM_DESCR", getString(R.string.param_descr_pm25));
                intent.putExtra("GOOD_VALUES", getString(R.string.good_values_pm25));
                intent.putExtra("MEDIUM_VALUES", getString(R.string.medium_values_pm25));
                intent.putExtra("BAD_VALUES", getString(R.string.bad_values_pm25));
                intent.putExtra("VERYBAD_VALUES", getString(R.string.very_bad_values_pm25));
                intent.putExtra("PARAM_ADVICE", getString(R.string.advice_pm25));
                intent.putExtra("PARAM_MEASURE", getString(R.string.pm_measure));
                break;

            case "PM10":
                intent.putExtra("PARAM_NAME", getString(R.string.param_name_pm10));
                intent.putExtra("PARAM_DESCR", getString(R.string.param_descr_pm10));
                intent.putExtra("GOOD_VALUES", getString(R.string.good_values_pm10));
                intent.putExtra("MEDIUM_VALUES", getString(R.string.medium_values_pm10));
                intent.putExtra("BAD_VALUES", getString(R.string.bad_values_pm10));
                intent.putExtra("VERYBAD_VALUES", getString(R.string.very_bad_values_pm10));
                intent.putExtra("PARAM_ADVICE",getString(R.string.advice_pm10));
                intent.putExtra("PARAM_MEASURE", getString(R.string.pm_measure));
                break;

            case "VOC":
                intent.putExtra("PARAM_NAME", getString(R.string.param_name_voc));
                intent.putExtra("PARAM_DESCR", getString(R.string.param_descr_voc));
                intent.putExtra("GOOD_VALUES", getString(R.string.good_values_voc));
                intent.putExtra("MEDIUM_VALUES", getString(R.string.medium_values_voc));
                intent.putExtra("BAD_VALUES", getString(R.string.bad_values_voc));
                intent.putExtra("VERYBAD_VALUES", getString(R.string.very_bad_values_voc));
                intent.putExtra("PARAM_ADVICE",getString(R.string.advice_voc));
                intent.putExtra("PARAM_MEASURE", getString(R.string.measure_VOC));
                break;

            case "CO":
                intent.putExtra("PARAM_NAME", getString(R.string.param_name_co));
                intent.putExtra("PARAM_DESCR", getString(R.string.param_descr_co));
                intent.putExtra("GOOD_VALUES", getString(R.string.good_values_co));
                intent.putExtra("MEDIUM_VALUES", getString(R.string.medium_values_co));
                intent.putExtra("BAD_VALUES", getString(R.string.bad_values_co));
                intent.putExtra("VERYBAD_VALUES", getString(R.string.very_bad_values_co));
                intent.putExtra("PARAM_ADVICE",getString(R.string.advice_co));
                intent.putExtra("PARAM_MEASURE", getString(R.string.measure_CO));
                break;
        }

        // Start DetailedReadingActivity with the prepared intent
        startActivity(intent);
    }
}

