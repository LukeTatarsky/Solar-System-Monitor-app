package com.example.solar;

import android.content.Context;
import android.view.LayoutInflater;
import android.widget.ScrollView;
import android.widget.TextView;

import com.github.mikephil.charting.charts.LineChart;

public class SingleChart extends ScrollView {

    private TextView txtTempCurrValue;
    private TextView txtTempMaxValue;
    private TextView txtTempMinValue;

    public SingleChart(Context context) {
        super(context);
        init(context);
    }

    private void init(Context context) {
        LayoutInflater.from(context).inflate(R.layout.linechart_single, this, true);
        LineChart lineChart = findViewById(R.id.lineChart);
    }

}