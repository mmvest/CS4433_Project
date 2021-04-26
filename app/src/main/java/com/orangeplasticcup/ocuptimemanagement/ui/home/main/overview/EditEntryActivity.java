package com.orangeplasticcup.ocuptimemanagement.ui.home.main.overview;

import android.app.Activity;
import android.os.Bundle;
import android.widget.Button;

import com.orangeplasticcup.ocuptimemanagement.R;
import com.orangeplasticcup.ocuptimemanagement.data.TimeEntry;

public class EditEntryActivity extends Activity {

    private static EditEntryActivity instance;
    private final EditEntryViewModel editEntryViewModel = new EditEntryViewModel();
    private static TimeEntry entryToEdit;

    public static void setEditableEntry(TimeEntry entry) {
        entryToEdit = entry;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(Bundle.EMPTY);
        setContentView(R.layout.fragment_new_entry_screen);
        instance = this;

        Button confirmChangeButton = findViewById(R.id.createEntryButton);
    }
}
