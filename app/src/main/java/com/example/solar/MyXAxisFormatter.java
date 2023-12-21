package com.example.solar;

import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.formatter.ValueFormatter;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

public class MyXAxisFormatter extends ValueFormatter {
    final DateTimeFormatter formatter;
    public MyXAxisFormatter(boolean showingWeeks) {
        if (showingWeeks) {
            formatter = DateTimeFormatter.ofPattern("h a");
        } else{
            formatter = DateTimeFormatter.ofPattern("hh:mm");
        }
    }

    @Override
    public String getAxisLabel(float value, AxisBase axis) {

        Instant instant = Instant.ofEpochSecond((long) value);
        LocalDateTime localDateTime = LocalDateTime.ofInstant(instant, ZoneId.of("UTC"));

        return localDateTime.format(formatter);
    }
    public String StringFromFloatHighlighter(float value, boolean showingWeeks) {
        Instant instant = Instant.ofEpochSecond((long) value);
        LocalDateTime localDateTime = LocalDateTime.ofInstant(instant, ZoneId.of("UTC"));
        if (showingWeeks) {
            return localDateTime.format(DateTimeFormatter.ofPattern("E, h a"));
        } else{
            return localDateTime.format(DateTimeFormatter.ofPattern("h:mm a"));
        }
    }
}
