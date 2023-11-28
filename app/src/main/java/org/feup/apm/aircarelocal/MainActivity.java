package org.feup.apm.aircarelocal;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Bundle;
import android.view.View;

public class MainActivity extends AppCompatActivity {

    private SQLiteDatabase db = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        DatabaseHelper databaseHelper = new DatabaseHelper(this);

        databaseHelper.copyDatabase();

        db = (databaseHelper).getWritableDatabase();

        //add query etc

        ActionBar actionbar = getSupportActionBar();
        if (actionbar != null) {
            actionbar.setLogo(R.drawable.aircare_logo);
            actionbar.setDisplayUseLogoEnabled(true);
            actionbar.setDisplayShowHomeEnabled(true);
        }

        // Find the LinearLayout with ID "test"
        View testLayout = findViewById(R.id.test);

        // Set OnClickListener for "test" layout
        testLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Create an Intent to start ParameterInfoActivity
                Intent intent = new Intent(MainActivity.this, ParameterInfoActivity.class);

                // Start the new activity
                startActivity(intent);
            }
        });
    }
}