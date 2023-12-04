package org.feup.apm.aircarelocal;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.TextView;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

public class ParameterInfoActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.parameter_info);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setLogo(R.drawable.aircare_logo);
            actionBar.setDisplayUseLogoEnabled(true);
            actionBar.setDisplayShowHomeEnabled(true);
        }

        Intent intent = getIntent();
        if (intent != null) {
            String parameterName = getIntent().getStringExtra("PARAM_NAME");
            String parameterDescr = getIntent().getStringExtra("PARAM_DESCR");
            String goodValues = getIntent().getStringExtra("GOOD_VALUES");
            String mediumValues = getIntent().getStringExtra("MEDIUM_VALUES");
            String badValues = getIntent().getStringExtra("BAD_VALUES");
            String vrybadValues = getIntent().getStringExtra("VERYBAD_VALUES");

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
