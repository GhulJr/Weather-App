package com.example.weatherapp.recycler_views;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.weatherapp.R;
import com.example.weatherapp.data.WeatherDay;

import java.util.List;

public class WeatherAdapter extends RecyclerView.Adapter<WeatherAdapter.WeatherViewHolder> {
    private List<WeatherDay> weatherDayList;
    final private ListItemClickListener onClickListener;

    /** Constructor */
    public WeatherAdapter(List<WeatherDay> weatherDayList, ListItemClickListener onClickListener) {
        this.weatherDayList = weatherDayList;
        this.onClickListener = onClickListener;
    }

    @NonNull
    @Override
    /** Create new ViewHolder. */
    public WeatherViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        Context context = viewGroup.getContext();
        int layoutIdForListItem = R.layout.daily_forecast_list_item;
        LayoutInflater inflater = LayoutInflater.from(context);

        View view = inflater.inflate(layoutIdForListItem, viewGroup, false);
        WeatherViewHolder weatherViewHolder = new WeatherViewHolder(view, onClickListener);

        return weatherViewHolder;
    }

    @Override
    /** Bind data to It's view. */
    public void onBindViewHolder(@NonNull WeatherViewHolder weatherViewHolder, int i) {
        // TODO: finish it when list item will be finished
        // Get necessary views form weather list item.
        TextView maxTemp = weatherViewHolder.listItemWeather.findViewById(R.id.max_temp);
        TextView minTemp = weatherViewHolder.listItemWeather.findViewById(R.id.min_temp);

        // Set data to corresponding views.
        maxTemp.setText(String.valueOf(weatherDayList.get(i).getMaxTemp()));
        minTemp.setText(String.valueOf(weatherDayList.get(i).getMinTemp()));
    }

    @Override
    /** Get number of items. */
    public int getItemCount()  {
        return weatherDayList.size();
    }

    /** Class responsible for keeping the view of single item. */
    public static class WeatherViewHolder extends RecyclerView.ViewHolder
                                        implements View.OnClickListener{
        private LinearLayout listItemWeather;
        private ListItemClickListener onClickListener;

        public WeatherViewHolder(@NonNull View itemView, ListItemClickListener onClickListener) {
            super(itemView);
            this.listItemWeather = (LinearLayout) itemView;
            this.onClickListener = onClickListener;
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            onClickListener.onListItemClick(getAdapterPosition());
        }
    }

    public interface ListItemClickListener {
        void onListItemClick(int clickedItemIndex);
    }

}
