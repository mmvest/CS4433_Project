package com.orangeplasticcup.ocuptimemanagement.data.model;

public class GraphEntry {
    private final String category;
    private final int categoryTime;
    private final float percentTime;

    public GraphEntry(String category, int categoryTime, float percentTime) {
        this.category = category;
        this.categoryTime = categoryTime;
        this.percentTime = percentTime;
    }

    public String getCategory() { return category; }
    public int getGateforyTime() { return categoryTime; }
    public float getPercentTime() { return percentTime; }
}
