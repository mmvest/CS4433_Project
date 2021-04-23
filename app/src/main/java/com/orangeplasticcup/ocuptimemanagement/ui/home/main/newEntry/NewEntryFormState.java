package com.orangeplasticcup.ocuptimemanagement.ui.home.main.newEntry;

import androidx.annotation.Nullable;

public class NewEntryFormState {
    @Nullable
    private final Integer startTimeDateError;
    @Nullable
    private final Integer startTimeTimeError;
    @Nullable
    private final Integer endTimeDateError;
    @Nullable
    private final Integer endTimeTimeError;
    private final boolean isDataValid;

    public NewEntryFormState(@Nullable Integer startTimeDateError, @Nullable Integer startTimeTimeError, @Nullable Integer endTimeDateError, @Nullable Integer endTimeTimeError) {
        this.startTimeDateError = startTimeDateError;
        this.startTimeTimeError = startTimeTimeError;
        this.endTimeDateError = endTimeDateError;
        this.endTimeTimeError = endTimeTimeError;
        isDataValid = false;
    }

    public NewEntryFormState(boolean isDataValid) {
        startTimeDateError = null;
        startTimeTimeError = null;
        endTimeDateError = null;
        endTimeTimeError = null;
        this.isDataValid = isDataValid;
    }

    @Nullable
    public Integer getStartTimeDateError() {
        return startTimeDateError;
    }

    @Nullable
    public Integer getStartTimeTimeError() {
        return startTimeTimeError;
    }

    @Nullable
    public Integer getEndTimeDateError() {
        return endTimeDateError;
    }

    @Nullable
    public Integer getEndTimeTimeError() {
        return endTimeTimeError;
    }

    public boolean isDataValid() {
        return isDataValid;
    }
}
