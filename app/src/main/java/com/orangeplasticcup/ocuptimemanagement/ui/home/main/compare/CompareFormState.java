package com.orangeplasticcup.ocuptimemanagement.ui.home.main.compare;

import androidx.annotation.Nullable;

import com.orangeplasticcup.ocuptimemanagement.ui.home.main.newEntry.NewEntryFormState;

public class CompareFormState extends NewEntryFormState {
    public CompareFormState(@Nullable Integer startTimeDateError, @Nullable Integer startTimeTimeError, @Nullable Integer endTimeDateError, @Nullable Integer endTimeTimeError) {
        super(startTimeDateError, startTimeTimeError, endTimeDateError, endTimeTimeError);
    }

    public CompareFormState(boolean isDataValid) {
        super(isDataValid);
    }
}
