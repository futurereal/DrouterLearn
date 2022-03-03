package com.viomi.waterpurifier.edison.entity;

public class SeekBarSetEntity {
    String name;
    int minValue;
    int maxValue;
    int stepVlue;
    int currentValue;

    public SeekBarSetEntity() {

    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getMinValue() {
        return minValue;
    }

    public void setMinValue(int minValue) {
        this.minValue = minValue;
    }

    public int getMaxValue() {
        return maxValue;
    }

    public void setMaxValue(int maxValue) {
        this.maxValue = maxValue;
    }

    public int getStepVlue() {
        return stepVlue;
    }

    public void setStepVlue(int stepVlue) {
        this.stepVlue = stepVlue;
    }

    public int getCurrentValue() {
        return currentValue;
    }

    public void setCurrentValue(int currentValue) {
        this.currentValue = currentValue;
    }

    @Override
    public String toString() {
        return "SeekBarSetEntity{" +
                "name='" + name + '\'' +
                ", minValue=" + minValue +
                ", maxValue=" + maxValue +
                ", stepVlue=" + stepVlue +
                ", currentValue=" + currentValue +
                '}';
    }
}
