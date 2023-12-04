package org.feup.apm.aircarelocal;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import androidx.appcompat.widget.Toolbar;
import androidx.appcompat.app.AppCompatActivity;

public class DetailedReadingActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.detailed_reading);

        Toolbar toolbar = findViewById(R.id.customToolbar); // Find your toolbar by its ID
        setSupportActionBar(toolbar); // Set the toolbar as the ActionBar

        ImageView backButton = findViewById(R.id.backButton);
        ImageView menuButton = findViewById(R.id.menuButton);

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
                        intent.putExtra("PARAM_NAME", "PM2.5");
                        intent.putExtra("PARAM_DESCR", "PM2.5 particles are tiny airborne particles with a diameter of 2.5 micrometers or smaller.\n" +
                                "\n" +
                                "These can accumulate from various sources like cooking, cleaning products, and even outdoor air infiltration.\n" +
                                "\n" +
                                "Due to their minuscule size, PM2.5 particles can penetrate deep into the lungs when inhaled, potentially causing respiratory and cardiovascular issues.");
                        intent.putExtra("GOOD_VALUES", "0-12µg/m³");
                        intent.putExtra("MEDIUM_VALUES", "13-35µg/m³");
                        intent.putExtra("BAD_VALUES", "36-50µg/m³");
                        intent.putExtra("VERYBAD_VALUES", ">50µg/m³");
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
                        intent.putExtra("PARAM_NAME", "PM10");
                        intent.putExtra("PARAM_DESCR", "Lorem ipsum dolor sit amet, consectetur adipiscing elit. Etiam eget ligula eu lectus lobortis condimentum. Aliquam nonummy auctor massa. Pellentesque habitant morbi tristique senectus et netus et malesuada fames ac turpis egestas. Nulla at risus. Quisque purus magna, auctor et, sagittis ac, posuere eu, lectus. Nam mattis, felis ut adipiscing.");
                        intent.putExtra("GOOD_VALUES", "0-54 µg/m³");
                        intent.putExtra("MEDIUM_VALUES", "55-254 µg/m³");
                        intent.putExtra("BAD_VALUES", "255-354 µg/m³");
                        intent.putExtra("VERYBAD_VALUES", ">354 µg/m³");
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
                        intent.putExtra("PARAM_NAME", "VOCs");
                        intent.putExtra("PARAM_DESCR", "Lorem ipsum dolor sit amet, consectetur adipiscing elit. Etiam eget ligula eu lectus lobortis condimentum. Aliquam nonummy auctor massa. Pellentesque habitant morbi tristique senectus et netus et malesuada fames ac turpis egestas. Nulla at risus. Quisque purus magna, auctor et, sagittis ac, posuere eu, lectus. Nam mattis, felis ut adipiscing.");
                        intent.putExtra("GOOD_VALUES", "0-0.4 ppm");
                        intent.putExtra("MEDIUM_VALUES", "0.5 - 2.2 ppm");
                        intent.putExtra("BAD_VALUES", "2.3 - 30 ppm");
                        intent.putExtra("VERYBAD_VALUES", ">30 ppm");
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
                        intent.putExtra("PARAM_NAME", "CO₂");
                        intent.putExtra("PARAM_DESCR", "Lorem ipsum dolor sit amet, consectetur adipiscing elit. Etiam eget ligula eu lectus lobortis condimentum. Aliquam nonummy auctor massa. Pellentesque habitant morbi tristique senectus et netus et malesuada fames ac turpis egestas. Nulla at risus. Quisque purus magna, auctor et, sagittis ac, posuere eu, lectus. Nam mattis, felis ut adipiscing.");
                        intent.putExtra("GOOD_VALUES", "0-800 ppm");
                        intent.putExtra("MEDIUM_VALUES", "801 - 1200 ppm");
                        intent.putExtra("BAD_VALUES", "1201 - 2000 ppm");
                        intent.putExtra("VERYBAD_VALUES", ">2000 ppm");
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


}
