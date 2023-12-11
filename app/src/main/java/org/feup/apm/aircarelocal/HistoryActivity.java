package org.feup.apm.aircarelocal;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.icu.text.SimpleDateFormat;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.ActionBar;
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

public class HistoryActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.history);

        //TOOLBAR
        Toolbar toolbar = findViewById(R.id.customToolbar); // Find your toolbar by its ID
        setSupportActionBar(toolbar); // Set the toolbar as the ActionBar

        ImageView backButton = findViewById(R.id.backButton);
        ImageView menuButton = findViewById(R.id.menuButton);

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        menuButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Add  action when menuButton is clicked
                // For example, show a popup menu or perform some action
            }
        });




        RecyclerView recyclerView = findViewById(R.id.historyList);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        List<HistoryScroll.Item> itemList = fetchItemFromDatabase();
        HistoryScroll adapter = new HistoryScroll(itemList);
        recyclerView.setAdapter(adapter);
    }
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
    private Date parseTimestamp(String timestamp) {
        try {
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSSSSS", Locale.getDefault());
            return dateFormat.parse(timestamp);
        } catch (ParseException e) {
            e.printStackTrace();
            return new Date(); // Handle parsing error
        }
    }

    private boolean isSameDay(Date date1, Date date2) {
        Calendar cal1 = Calendar.getInstance();
        cal1.setTime(date1);

        Calendar cal2 = Calendar.getInstance();
        cal2.setTime(date2);

        return cal1.get(Calendar.DAY_OF_YEAR) == cal2.get(Calendar.DAY_OF_YEAR) &&
                cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR);
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
