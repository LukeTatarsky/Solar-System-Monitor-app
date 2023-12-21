package com.example.solar;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.core.content.ContextCompat;

public class TemperatureLineMax extends ScrollView {
    private TextView txtTempName;
    private TextView txtTempCurrValue;
    private TextView txtTempMaxValue;
    private ProgressBar progressBar;
    public int max_temp;
    public final int min_temp =10;
    private int max_progress;
    private final int min_progress = min_temp;
    private final int color_hot = ContextCompat.getColor(getContext(), R.color.hot);
    private final int color_cold = ContextCompat.getColor(getContext(), R.color.cold);


    public TemperatureLineMax(Context context, int max_bar_temp) {
        super(context);
        init(context);
        max_temp = max_bar_temp;
        max_progress = max_bar_temp;
    }
    public TemperatureLineMax(Context context, String name, String curr, String max, String min) {
        super(context);
        init(context);
        txtTempName.setText(name);
        txtTempCurrValue.setText(curr);
        txtTempMaxValue.setText(max);
    }
    private void init(Context context) {
        LayoutInflater.from(context).inflate(R.layout.overview_line, this, true);
        txtTempName = findViewById(R.id.txtOverviewName);
        txtTempCurrValue = findViewById(R.id.txtOverviewCurrentTemp);
        txtTempMaxValue = findViewById(R.id.txtOverviewMaxTemp);
        progressBar = findViewById(R.id.prgsBar);
    }

    public void setProgress(Double tempDbl){
        int tempInt = Math.toIntExact(Math.round(tempDbl));
        progressBar.setProgress(tempInt);
        progressBar.setMax(max_progress);
        progressBar.setMin(min_progress);

        // Change the tint color based on value
        int color = colorRatio(color_cold, color_hot, tempDbl);
        ColorStateList colorStateList = ColorStateList.valueOf(color);
        progressBar.setProgressTintList(colorStateList);

    }
    public int colorRatio(int cold_color, int hot_color, Double temp) {

        if (temp < min_temp){
            return cold_color;
        } else if (temp > max_temp){
            return hot_color;
        } else {
            double ratio = (temp - min_temp) / (max_temp - min_temp);

            if (ratio > 1.0) {
                ratio = 1.0;
            } else if (ratio < 0.0) {
                ratio = 0.0;
            }
            // Extract
            int alpha1 = Color.alpha(cold_color);
            int red1 = Color.red(cold_color);
            int green1 = Color.green(cold_color);
            int blue1 = Color.blue(cold_color);


            int alpha2 = Color.alpha(hot_color);
            int red2 = Color.red(hot_color);
            int green2 = Color.green(hot_color);
            int blue2 = Color.blue(hot_color);

            // Blend
            int blendedAlpha = (int) (alpha1 * (1 - ratio) + alpha2 * ratio);
            int blendedRed = (int) (red1 * (1 - ratio) + red2 * ratio);
            int blendedGreen = (int) (green1 * (1 - ratio) + green2 * ratio);
            int blendedBlue = (int) (blue1 * (1 - ratio) + blue2 * ratio);
            // Combine

            return Color.argb(blendedAlpha, blendedRed, blendedGreen, blendedBlue);
        }
    }
    public void setName(String name) {
        txtTempName.setText(name);
    }

    public void setCurrValue(String value) {
        txtTempCurrValue.setText(value);
    }
    public void setMaxValue(String value) {
        txtTempMaxValue.setText(value);
    }
    public void setMaxBarTemp(int value) {
        progressBar.setMax(value);
    }



}
