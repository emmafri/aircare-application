package org.feup.apm.aircarelocal;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.icu.text.SimpleDateFormat;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.Space;

import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Random;

import androidx.appcompat.app.AppCompatActivity;

public class HistoryActivity extends AppCompatActivity implements HistoryScroll.OnItemClickListener{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.history);

        // Configure toolbar and UI elements
        configureUI();

        // Set up RecyclerView and populate it with data from the database
        setupRecyclerView();
    }

    // Configure UI elements, toolbar buttons, and back navigation
    private void configureUI() {
        // Set up toolbar
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
                Intent intent = new Intent(HistoryActivity.this, MainActivity.class);
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
                Intent intent = new Intent(HistoryActivity.this, InfoActivity.class);
                startActivity(intent);
            }
        });
    }

    // Set up RecyclerView and populate it with data from the database
    private void setupRecyclerView() {
        RecyclerView recyclerView = findViewById(R.id.historyList);
        recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));

        // Fetch data from the database and create a list of HistoryScroll.Items
        List<HistoryScroll.Item> itemList = fetchItemFromDatabase();

        // Initialize HistoryScroll adapter with the item list and set click listener
        HistoryScroll adapter = new HistoryScroll(itemList);
        adapter.setOnItemClickListener(this);

        // Set the adapter for the RecyclerView
        recyclerView.setAdapter(adapter);
    }

    // Fetch items from the database and create a list of HistoryScroll.Items
    private List<HistoryScroll.Item> fetchItemFromDatabase() {
        List<HistoryScroll.Item> itemList = new ArrayList<>();
        DatabaseHelper databaseHelper = new DatabaseHelper(this);

        String[] time = {"Timestamp", "PM25", "PM10", "CO", "VOC"};

        SQLiteDatabase db = databaseHelper.getReadableDatabase();
        Cursor cursor = db.query("sensor_data", time, null, null, null, null, null);


        if (cursor.moveToFirst()) {
            do {
                String timestampStr = cursor.getString(cursor.getColumnIndexOrThrow("Timestamp"));

                Date timestamp = parseTimestamp(timestampStr);
                float pm25 = cursor.getFloat(cursor.getColumnIndexOrThrow("PM25"));

                float pm10 = cursor.getFloat(cursor.getColumnIndexOrThrow("PM10"));

                float co = cursor.getFloat(cursor.getColumnIndexOrThrow("CO"));

                float voc = cursor.getFloat(cursor.getColumnIndexOrThrow("VOC"));

                itemList.add(new HistoryScroll.Item(timestamp,pm25,pm10,co,voc, false));
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();

        Collections.reverse(itemList);
        Random random = new Random();
        float randomFloat = random.nextFloat();

        if (!itemList.isEmpty()){

            Date firstEntryDate = itemList.get(0).getTimestamp();
            itemList.add(0, new HistoryScroll.Item(firstEntryDate,randomFloat,randomFloat,randomFloat,randomFloat,true));

        }

        // Add dividers based on date change
        for (int i = 0; i < itemList.size() - 1; i++) {
            HistoryScroll.Item current = itemList.get(i);
            HistoryScroll.Item next = itemList.get(i + 1);

            if (!current.isDivider() && !next.isDivider()) {
                Date currentDate = current.getTimestamp();
                Date nextDate = next.getTimestamp();

                if (!isSameDay(currentDate, nextDate)) {
                    itemList.add(i + 1, new HistoryScroll.Item( nextDate, randomFloat,randomFloat,randomFloat,randomFloat,true));// Divider with default time
                }
            }
        }

        return itemList;
    }

    // Parse timestamp from a string to a Date object
    private Date parseTimestamp(String timestamp) {
        try {
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSSSSS", Locale.getDefault());
            return dateFormat.parse(timestamp);
        } catch (ParseException e) {
            e.printStackTrace();
            return new Date(); // Handle parsing error
        }
    }

    // Check if two dates are the same day
    private boolean isSameDay(Date date1, Date date2) {
        Calendar cal1 = Calendar.getInstance();
        cal1.setTime(date1);

        Calendar cal2 = Calendar.getInstance();
        cal2.setTime(date2);

        return cal1.get(Calendar.DAY_OF_YEAR) == cal2.get(Calendar.DAY_OF_YEAR) &&
                cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR);
    }

    // Handle options menu item selection (e.g., Up button)
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle the Up button click
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    // Handle item click in the RecyclerView
    @Override
    public void onItemClick(HistoryScroll.Item item) {
        // Handle the item click, e.g., start a new activity
        if (!item.isDivider()) {
            Intent intent = new Intent(this, DetailedReadingActivity.class);

            // Pass any data you need to the new activity
            intent.putExtra("timestamp", item.getTimestamp().getTime());
            intent.putExtra("source", "HistoryActivity");

            // Pass additional data
            intent.putExtra("pm25", item.getPM25());
            intent.putExtra("pm10", item.getPM10());
            intent.putExtra("co", item.getCO());
            intent.putExtra("voc", item.getVOC());

            startActivity(intent);
        }
    }

}
