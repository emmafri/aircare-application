package org.feup.apm.aircarelocal;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.Space;
import android.widget.TextView;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

public class ParameterInfoActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.parameter_info);

        // TOOLBAR
        Toolbar toolbar = findViewById(R.id.customToolbar);
        setSupportActionBar(toolbar);
        //Hide back button on main activity and show empty space instead; Show info button
        ImageView backButton = findViewById(R.id.backButton);
        backButton.setVisibility(View.VISIBLE);
        Space backButtonSpace = findViewById(R.id.backButtonSpace);
        backButtonSpace.setVisibility(View.GONE);
        ImageView infoButton = findViewById(R.id.infoButton);
        infoButton.setVisibility(View.VISIBLE);
        Space infoButtonSpace = findViewById(R.id.infoButtonSpace);
        infoButtonSpace.setVisibility(View.GONE);
        ImageView appLogo = findViewById(R.id.appLogo);

        appLogo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ParameterInfoActivity.this, MainActivity.class);
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

        infoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ParameterInfoActivity.this, InfoActivity.class);
                startActivity(intent);
            }
        });

        Intent intent = getIntent();
        if (intent != null) {
            String parameterName = getIntent().getStringExtra("PARAM_NAME");
            String parameterDescr = getIntent().getStringExtra("PARAM_DESCR");
            String goodValues = getIntent().getStringExtra("GOOD_VALUES");
            String mediumValues = getIntent().getStringExtra("MEDIUM_VALUES");
            String badValues = getIntent().getStringExtra("BAD_VALUES");
            String vrybadValues = getIntent().getStringExtra("VERYBAD_VALUES");
            String parameterAdvice = getIntent().getStringExtra("PARAM_ADVICE");
            String parameterMeasure = getIntent().getStringExtra("PARAM_MEASURE");

            TextView parameterTitle = findViewById(R.id.Parameter);
            parameterTitle.setText(parameterName);

            TextView parameterDescription = findViewById(R.id.ParameterDescription);
            parameterDescription.setText(parameterDescr);

            TextView parameterGV = findViewById(R.id.GoodValues);
            parameterGV.setText(goodValues);

            TextView parameterMV = findViewById(R.id.MediumValues);
            parameterMV.setText(mediumValues);

            TextView parameterB = findViewById(R.id.BadValues);
            parameterB.setText(badValues);

            TextView parameterVB = findViewById(R.id.VeryBadValues);
            parameterVB.setText(vrybadValues);

            TextView parameterAdv = findViewById(R.id.advice);
            parameterAdv.setText(parameterAdvice);

            TextView GoodMeasure =findViewById(R.id.GoodValuesMeasure);
            GoodMeasure.setText(parameterMeasure);
            TextView MediumMeasure =findViewById(R.id.MediumValuesMeasure);
            MediumMeasure.setText(parameterMeasure);
            TextView BadMeasure =findViewById(R.id.BadMeasure);
            BadMeasure.setText(parameterMeasure);
            TextView VryBadMeasure =findViewById(R.id.VryBadMeasure);
            VryBadMeasure.setText(parameterMeasure);

        }

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle the Up button click
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
