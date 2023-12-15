package com.example.solar;

import android.content.Context;
import android.view.LayoutInflater;
import android.widget.LinearLayout;
import android.widget.TextView;

public class TemperatureLineLayout extends LinearLayout {

    private TextView txtTempName;
    private TextView txtTempValue;


    public TemperatureLineLayout(Context context) {
        super(context);
        init(context);
    }


    private void init(Context context) {
        LayoutInflater.from(context).inflate(R.layout.list_view_line, this, true);
        txtTempName = findViewById(R.id.txtTempName);
        txtTempValue = findViewById(R.id.txtTempValue);
    }

    public void setName(String name) {
        txtTempName.setText(name);
    }

    public void setValue(String value) {
        txtTempValue.setText(value);
    }
}