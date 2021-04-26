package com.orangeplasticcup.ocuptimemanagement.data;

import java.util.Date;

public class TimeEntry {
    private long entryID;
    private Date startDate;
    private Date endDate;
    private String note;
    private String username;
    private String categoryName;

    private TimeEntry(long entryID, Date startDate, Date endDate, String note, String username, String categoryName) {
        this.entryID = entryID;
        this.startDate = startDate;
        this.endDate = endDate;
        this.note = note;
        this.username = username;
        this.categoryName = categoryName;
    }

    public long getEntryID() {
        return entryID;
    }
    public void setEntryID(long entryID) {
        this.entryID = entryID;
    }
    public Date getStartDate() {
        return startDate;
    }
    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }
    public Date getEndDate() {
        return endDate;
    }
    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }
    public String getNote() {
        return note;
    }
    public void setNote(String note) {
        this.note = note;
    }
    public String getUsername() {
        return username;
    }
    public void setUsername(String username) {
        this.username = username;
    }
    public String getCategoryName() {
        return categoryName;
    }
    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }
}
