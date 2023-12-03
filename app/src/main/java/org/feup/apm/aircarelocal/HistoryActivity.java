package org.feup.apm.aircarelocal;

import android.icu.text.SimpleDateFormat;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.appcompat.app.ActionBar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import androidx.appcompat.app.AppCompatActivity;

public class HistoryActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.history);

        ActionBar actionbar = getSupportActionBar();
        if (actionbar != null) {
            actionbar.setLogo(R.drawable.aircare_logo);
            actionbar.setDisplayUseLogoEnabled(true);
            actionbar.setDisplayShowHomeEnabled(true);
        }
        RecyclerView recyclerView = findViewById(R.id.historyList);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        List<HistoryScroll.Item> itemList = generateItemList();
        HistoryScroll adapter = new HistoryScroll(itemList);
        recyclerView.setAdapter(adapter);
    }
    private List<HistoryScroll.Item> generateItemList() {
        List<HistoryScroll.Item> itemList = new ArrayList<>();
        itemList.add(new HistoryScroll.Item("Entry 3", false, parseTimestamp("2023-11-27 10:15:30.987654")));
        itemList.add(new HistoryScroll.Item("Entry 2", false, parseTimestamp("2023-11-26 18:45:12.123456")));
        itemList.add(new HistoryScroll.Item("Entry 1", false, parseTimestamp("2023-11-26 16:31:47.307125")));

        // Add dividers based on date change
        for (int i = 0; i < itemList.size() - 1; i++) {
            HistoryScroll.Item current = itemList.get(i);
            HistoryScroll.Item next = itemList.get(i + 1);

            if (!current.isDivider() && !next.isDivider()) {
                Date currentDate = current.getTimestamp();
                Date nextDate = next.getTimestamp();

                if (!isSameDay(currentDate, nextDate)) {
                    itemList.add(i + 1, new HistoryScroll.Item("", true, nextDate)); // Divider with default time
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
}
