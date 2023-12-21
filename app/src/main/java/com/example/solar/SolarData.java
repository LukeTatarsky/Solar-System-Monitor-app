package com.example.solar;

import androidx.annotation.NonNull;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

public class SolarData {

    private final LocalDateTime date;
    private final float glycolRoof;
    private final float glycolIn;
    private final float glycolOutTank;
    private final float glycolOutHE;
    private final float solarTankHigh;
    private final float solarTankMid;
    private final float solarTankLow;
    private final float boilerTankMid;
    private final float boilerTankOut;
    private final float solarTankOut;

    public SolarData(String[] line) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        this.date = LocalDateTime.parse(line[0], formatter);
        this.glycolRoof = Float.parseFloat(line[1]);
        this.glycolIn = Float.parseFloat(line[2]);
        this.glycolOutTank = Float.parseFloat(line[3]);
        this.glycolOutHE = Float.parseFloat(line[4]);
        this.solarTankHigh = Float.parseFloat(line[5]);
        this.solarTankMid = Float.parseFloat(line[6]);
        this.solarTankLow = Float.parseFloat(line[7]);
        this.boilerTankMid = Float.parseFloat(line[8]);
        this.boilerTankOut = Float.parseFloat(line[9]);
        this.solarTankOut = Float.parseFloat(line[10]);
    }

    public SolarData(String[] line, int avg_max_min) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd H:mm:ss.SSSSSS");
        this.date = LocalDateTime.parse(line[1], formatter);
        this.glycolRoof = Float.parseFloat(line[2 + avg_max_min]);
        this.glycolIn = Float.parseFloat(line[5 + avg_max_min]);
        this.glycolOutTank = Float.parseFloat(line[8 + avg_max_min]);
        this.glycolOutHE = Float.parseFloat(line[11 + avg_max_min]);
        this.solarTankHigh = Float.parseFloat(line[14 + avg_max_min]);
        this.solarTankMid = Float.parseFloat(line[17 + avg_max_min]);
        this.solarTankLow = Float.parseFloat(line[20 + avg_max_min]);
        this.boilerTankMid = Float.parseFloat(line[23 + avg_max_min]);
        this.boilerTankOut = Float.parseFloat(line[26 + avg_max_min]);
        this.solarTankOut = Float.parseFloat(line[29 + avg_max_min]);
    }

    public LocalDateTime getDate() {return date;}


    public float getGlycolRoof() {
        return glycolRoof;
    }


    public float getGlycolIn() {
        return glycolIn;
    }


    public float getGlycolOutTank() {
        return glycolOutTank;
    }


    public float getGlycolOutHE() {
        return glycolOutHE;
    }


    public float getSolarTankHigh() {
        return solarTankHigh;
    }


    public float getSolarTankMid() {
        return solarTankMid;
    }


    public float getSolarTankLow() {
        return solarTankLow;
    }


    public float getBoilerTankMid() {
        return boilerTankMid;
    }


    public float getTempByIndex(int i) {
        ArrayList<Float> temps = new ArrayList<>();
        temps.add(glycolRoof);
        temps.add(glycolIn);
        temps.add(glycolOutTank);
        temps.add(glycolOutHE);
        temps.add(solarTankHigh);
        temps.add(solarTankMid);
        temps.add(solarTankLow);
        temps.add(boilerTankMid);
        temps.add(boilerTankOut);
        temps.add(solarTankOut);
        // -1 because no date
        return temps.get(i-1);
    }
    @NonNull
    @Override
    public String toString() {
        return "{" +
                "date=" + date +
                ", gRoof=" + glycolRoof +
                ", gIn=" + glycolIn +
                ", gOutTank=" + glycolOutTank +
                ", gOutHE=" + glycolOutHE +
                ", stHigh=" + solarTankHigh +
                ", stMid=" + solarTankMid +
                ", stLow=" + solarTankLow +
                ", btMid=" + boilerTankMid +
                ", btOut=" + boilerTankOut +
                ", stOut=" + solarTankOut +
                '}';
    }
}
