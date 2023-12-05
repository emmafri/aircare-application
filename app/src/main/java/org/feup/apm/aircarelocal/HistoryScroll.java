package org.feup.apm.aircarelocal;
import android.icu.text.SimpleDateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class HistoryScroll extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int VIEW_TYPE_ENTRY = 0;
    private static final int VIEW_TYPE_DIVIDER = 1;

    private List<Item> itemList;

    public HistoryScroll(List<Item> itemList) {
        this.itemList = itemList;
    }

    @Override
    public int getItemViewType(int position) {
        Item item = itemList.get(position);
        return itemList.get(position).isDivider() ? VIEW_TYPE_DIVIDER : VIEW_TYPE_ENTRY;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());

        if (viewType == VIEW_TYPE_ENTRY) {
            View itemView = inflater.inflate(R.layout.history_item, parent, false);
            return new EntryViewHolder(itemView);
        } else {
            View dividerView = inflater.inflate(R.layout.history_divider, parent, false);
            return new DividerViewHolder(dividerView);
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        Item item = itemList.get(position);

        if (holder instanceof EntryViewHolder) {
            EntryViewHolder entryViewHolder = (EntryViewHolder) holder;
            SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm", Locale.getDefault());
            String formattedTime = timeFormat.format(item.getTimestamp());
            entryViewHolder.textView.setText(formattedTime);
        } else if (holder instanceof DividerViewHolder) {
            DividerViewHolder dividerViewHolder = (DividerViewHolder) holder;
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
            String formattedDate = sdf.format(item.getTimestamp());
            dividerViewHolder.textView.setText(formattedDate);
        }
    }

    @Override
    public int getItemCount() {
        return itemList.size();
    }

    public static class EntryViewHolder extends RecyclerView.ViewHolder {
        public TextView textView;

        public EntryViewHolder(View view) {
            super(view);
            textView = view.findViewById(R.id.textViewEntry);
        }
    }

    public static class DividerViewHolder extends RecyclerView.ViewHolder {
        public TextView textView;

        public DividerViewHolder(View view) {
            super(view);
            textView = view.findViewById(R.id.textViewDivider);
        }
    }

    public static class Item {
        private String data;
        private boolean isDivider;
        private Date timestamp;
        private int hour;
        private int minute;

        public Item(String data, boolean isDivider, Date timestamp) {
            this.data = data;
            this.isDivider = isDivider;
            this.timestamp = timestamp;
            this.hour = hour;
            this.minute = minute;
        }

        public String getData() {
            return data;
        }

        public boolean isDivider() {
            return isDivider;
        }

        public Date getTimestamp() {
            return timestamp;
        }

        public int getHour() {
            return hour;
        }

        public int getMinute() {
            return minute;
        }
    }
}
