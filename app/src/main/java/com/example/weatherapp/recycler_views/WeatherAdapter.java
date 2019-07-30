package com.example.weatherapp.recycler_views;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.weatherapp.R;
import com.example.weatherapp.models.WeatherData;
import com.example.weatherapp.utilities.SunshineWeatherUtils;

import java.util.List;

public class WeatherAdapter extends RecyclerView.Adapter<WeatherAdapter.WeatherViewHolder> {

    private         Context context;
    private         List<WeatherData> weatherData;
    final private   ListItemClickListener onClickListener;


    /** Constructors */

    public WeatherAdapter(Context context, ListItemClickListener onClickListener) {
        this.context = context;
        this.onClickListener = onClickListener;
    }


    /** Create new ViewHolder. */
    @Override @NonNull
    public WeatherViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        Context context = viewGroup.getContext();
        int layoutIdForListItem = R.layout.daily_forecast_list_item;
        LayoutInflater inflater = LayoutInflater.from(context);

        View view = inflater.inflate(layoutIdForListItem, viewGroup, false);
        WeatherViewHolder weatherViewHolder = new WeatherViewHolder(view, onClickListener);

        return weatherViewHolder;
    }

    /** Bind data to It's view. */
    @Override
    public void onBindViewHolder(@NonNull WeatherViewHolder weatherViewHolder, int i) {
        // Get necessary views form weather list item.
        TextView timeView = weatherViewHolder.listItemWeather.findViewById(R.id.hourly_date);
        ImageView imageView = weatherViewHolder.listItemWeather.findViewById(R.id.hourly_image);
        TextView temperatureView = weatherViewHolder.listItemWeather.findViewById(R.id.hourly_temp);

        // Used values.
        String time = (String) android.text.format.DateFormat
                .format("H:mm", weatherData.get(i).getDateInMillis());
        int conditionId = weatherData.get(i).getWeatherConditionID();
        int imageRes = SunshineWeatherUtils.getIconResourceForWeatherCondition(conditionId);
        String temperature = SunshineWeatherUtils
                .formatTemperature(context, weatherData.get(i).getCurrTemp());

        // Inflate layout.
        timeView.setText(time);
        imageView.setImageResource(imageRes);
        temperatureView.setText(temperature);
    }

    public void setWeatherData(List<WeatherData> weatherData) {
        this.weatherData = weatherData;
        notifyDataSetChanged();
    }

    /** Get number of items. */
    @Override
    public int getItemCount()  {
        if(weatherData != null)
            return weatherData.size();
        return -1;
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
