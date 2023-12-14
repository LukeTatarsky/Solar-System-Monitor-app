package com.example.solar;

import java.time.LocalDateTime;

public class SolarData {

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

    public SolarData() {
    }

    public SolarData(LocalDateTime date, float glycolRoof, float glycolIn, float glycolOutTank, float glycolOutHE, float solarTankHigh, float solarTankMid, float solarTankLow, float boilerTankMid, float boilerTankOut, float solarTankOut) {
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
