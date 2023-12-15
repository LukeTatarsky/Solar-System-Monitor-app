package com.example.solar;

import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.formatter.ValueFormatter;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;

public class XAxisFormatter extends ValueFormatter {

    @Override
    public String getAxisLabel(float value, AxisBase axis) {

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("hh:mm");

        Instant instant = Instant.ofEpochSecond((long) value);
        LocalDateTime localDateTime = LocalDateTime.ofInstant(instant, ZoneId.of("UTC"));

        return localDateTime.format(formatter);
    }
    public String StringFromFloat(float value) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("hh:mm");
        Instant instant = Instant.ofEpochSecond((long) value);
        LocalDateTime localDateTime = LocalDateTime.ofInstant(instant, ZoneId.of("UTC"));
        return localDateTime.format(formatter);
    }
}
