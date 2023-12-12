package org.feup.apm.aircarelocal;
import android.icu.text.SimpleDateFormat;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
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

    public interface OnItemClickListener {

        void onItemClick(Item item);
    }
    private OnItemClickListener onItemClickListener;

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.onItemClickListener = listener;
    }


    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        Item item = itemList.get(position);

        if (holder instanceof EntryViewHolder) {
            EntryViewHolder entryViewHolder = (EntryViewHolder) holder;
            SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm", Locale.getDefault());
            String formattedTime = timeFormat.format(item.getTimestamp());
            Float pm25 = item.getPM25();
            Float pm10 = item.getPM10();
            Float co = item.getCO();
            Float voc = item.getVOC();

            entryViewHolder.textView.setText(formattedTime);
            AirQualityCalculator.AirQualityResult airQualityResult = AirQualityCalculator.getAirQualityResult(pm25, pm10,co,voc);

            AirQualityCalculator.AirQualityCategory overallCategory = airQualityResult.getOverallCategory();

            // Updates air quality box based on the air quality category
            switch (overallCategory) {
                case GOOD:
                    entryViewHolder.textView.setBackgroundResource(R.drawable.green_box);
                    break;
                case MEDIUM:
                    entryViewHolder.textView.setBackgroundResource(R.drawable.yellow_box);
                    break;
                case BAD:
                    entryViewHolder.textView.setBackgroundResource(R.drawable.red_box);
                    break;
                case VERY_BAD:
                    entryViewHolder.textView.setBackgroundResource(R.drawable.purple_box);
                    break;
                default:
                    // Handle default case if needed
                    break;


            }

            entryViewHolder.itemView.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                    break;

                    case MotionEvent.ACTION_UP:
                    startPopUpAnimation(v);
                    break;
                }
                    return false; // Return true if the event is consumed and shouldn't propagate further
                }
            });

            entryViewHolder.itemView.setOnClickListener(v -> {
                if (onItemClickListener != null) {
                    onItemClickListener.onItemClick(item);
                }
            });

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
        private float pm25;
        private float pm10;
        private float co;
        private float voc;
        private int hour;
        private int minute;

        public Item( Date timestamp,Float pm25,Float pm10,Float co,Float voc, boolean isDivider) {
            this.data = data;
            this.isDivider = isDivider;
            this.timestamp = timestamp;
            this.pm25 = pm25;
            this.pm10 = pm10;
            this.co = co;
            this.voc = voc;
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
        public Float getPM25() {
            return pm25;
        }
        public Float getPM10() {
            return pm10;
        }
        public Float getCO() {
            return co;
        }
        public Float getVOC() {
            return voc;
        }

        public int getHour() {
            return hour;
        }

        public int getMinute() {
            return minute;
        }
    }

    private void startPopUpAnimation(View view) {
        Animation popUpAnimation = AnimationUtils.loadAnimation(view.getContext(), R.anim.pop_up);
        view.startAnimation(popUpAnimation);
    }


}
