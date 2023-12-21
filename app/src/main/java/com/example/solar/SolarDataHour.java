package com.example.solar;

import androidx.annotation.NonNull;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

public class SolarDataHour {

    private LocalDateTime date;
    private float glycolRoof;
    private float glycolIn;
    private float glycolOutTank;
    private float glycolOutHE;
    private float solarTankHigh;
    private float solarTankMid;
    private float solarTankLow;
    private float boilerTankMid;
    private float boilerTankOut;
    private float solarTankOut;

    public SolarDataHour() {
    }
    public SolarDataHour(String[] line) {
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

    public SolarDataHour(LocalDateTime date, float glycolRoof, float glycolIn, float glycolOutTank,
                         float glycolOutHE, float solarTankHigh, float solarTankMid, float solarTankLow,
                         float boilerTankMid, float boilerTankOut, float solarTankOut) {
        this.date = date;
        this.glycolRoof = glycolRoof;
        this.glycolIn = glycolIn;
        this.glycolOutTank = glycolOutTank;
        this.glycolOutHE = glycolOutHE;
        this.solarTankHigh = solarTankHigh;
        this.solarTankMid = solarTankMid;
        this.solarTankLow = solarTankLow;
        this.boilerTankMid = boilerTankMid;
        this.boilerTankOut = boilerTankOut;
        this.solarTankOut = solarTankOut;
    }

    public LocalDateTime getDate() {return date;}

    public void setDate(LocalDateTime date) {
        this.date = date;
    }

    public float getGlycolRoof() {
        return glycolRoof;
    }

    public void setGlycolRoof(float glycolRoof) {
        this.glycolRoof = glycolRoof;
    }

    public float getGlycolIn() {
        return glycolIn;
    }

    public void setGlycolIn(float glycolIn) {
        this.glycolIn = glycolIn;
    }

    public float getGlycolOutTank() {
        return glycolOutTank;
    }

    public void setGlycolOutTank(float glycolOutTank) {
        this.glycolOutTank = glycolOutTank;
    }

    public float getGlycolOutHE() {
        return glycolOutHE;
    }

    public void setGlycolOutHE(float glycolOutHE) {
        this.glycolOutHE = glycolOutHE;
    }

    public float getSolarTankHigh() {
        return solarTankHigh;
    }

    public void setSolarTankHigh(float solarTankHigh) {
        this.solarTankHigh = solarTankHigh;
    }

    public float getSolarTankMid() {
        return solarTankMid;
    }

    public void setSolarTankMid(float solarTankMid) {
        this.solarTankMid = solarTankMid;
    }

    public float getSolarTankLow() {
        return solarTankLow;
    }

    public void setSolarTankLow(float solarTankLow) {
        this.solarTankLow = solarTankLow;
    }

    public float getBoilerTankMid() {
        return boilerTankMid;
    }

    public void setBoilerTankMid(float boilerTankMid) {
        this.boilerTankMid = boilerTankMid;
    }

    public float getBoilerTankOut() {
        return boilerTankOut;
    }

    public void setBoilerTankOut(float boilerTankOut) {
        this.boilerTankOut = boilerTankOut;
    }

    public float getSolarTankOut() {
        return solarTankOut;
    }

    public void setSolarTankOut(float solarTankOut) {
        this.solarTankOut = solarTankOut;
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
