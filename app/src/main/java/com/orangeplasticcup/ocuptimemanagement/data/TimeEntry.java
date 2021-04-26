package com.orangeplasticcup.ocuptimemanagement.data;

public class TimeEntry {
    private final long entryID;
    private String startDate;
    private String startTime;
    private String endDate;
    private String endTime;
    private String note;
    private String categoryName;

    private TimeEntry(long entryID, String startDate, String startTime, String endDate, String endTime, String note, String username, String categoryName) {
        this.entryID = entryID;
        this.startDate = startDate;
        this.startTime = startTime;
        this.endDate = endDate;
        this.endTime = endTime;
        this.note = note;
        this.categoryName = categoryName;
    }

    public long getEntryID() { return entryID; }
    public String getStartDate() { return startDate; }
    public void setStartDate(String startDate) { this.startDate = startDate; }
    public String getStartTime() { return startTime; }
    public void setStartTime(String startTime) { this.startTime = startTime; }
    public String getEndDate() { return endDate; }
    public void setEndDate(String endDate) { this.endDate = endDate; }
    public String getEndTime() { return endTime; }
    public void setEndTime(String endTime) { this.endTime = endTime; }
    public String getNote() { return note; }
    public void setNote(String note) { this.note = note; }
    public String getCategoryName() { return categoryName; }
    public void setCategoryName(String categoryName) { this.categoryName = categoryName; }
}
