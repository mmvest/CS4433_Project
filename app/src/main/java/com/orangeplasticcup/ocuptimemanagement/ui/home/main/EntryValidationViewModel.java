package com.orangeplasticcup.ocuptimemanagement.ui.home.main;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.View;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.orangeplasticcup.ocuptimemanagement.R;
import com.orangeplasticcup.ocuptimemanagement.data.Result;
import com.orangeplasticcup.ocuptimemanagement.data.model.EntryCategoryRepository;
import com.orangeplasticcup.ocuptimemanagement.ui.home.main.newEntry.NewEntryFormState;

import java.text.SimpleDateFormat;

public class EntryValidationViewModel extends ViewModel {
    protected static final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
    protected static final SimpleDateFormat timeFormat = new SimpleDateFormat("hh:mm:ss");
    protected final MutableLiveData<NewEntryFormState> entryFormState = new MutableLiveData<>();
    protected final MutableLiveData<Result<String>> createEntryResult = new MutableLiveData<>();

    public LiveData<NewEntryFormState> getEntryFormState() { return entryFormState; }
    public LiveData<Result<String>> getCreateEntryResult() { return createEntryResult; }

    public void entryDataChanged(Context context, String startDate, String startTime, String endDate, String endTime) {
        try{
            if(startDate == null || startDate.equals(context.getString(R.string.start_date)) || startDate.isEmpty()) {
                //System.out.println("Start Date Error");
                entryFormState.setValue(new NewEntryFormState(R.string.start_date_not_set, null, null, null));
            }
            else if (startTime == null || startTime.equals(context.getString(R.string.start_time)) || startTime.isEmpty()) {
                //System.out.println("Start Time Error");
                entryFormState.setValue(new NewEntryFormState(null, R.string.start_time_not_set, null, null));
            }
            else if (endDate == null || endDate.equals(context.getString(R.string.end_date)) || endDate.isEmpty()) {
                //System.out.println("End Date Error");
                entryFormState.setValue(new NewEntryFormState(null, null, R.string.end_date_not_set, null));
            }
            else if (endTime == null || endTime.equals(context.getString(R.string.end_time)) || endTime.isEmpty()) {
                //System.out.println("End Time Error");
                entryFormState.setValue(new NewEntryFormState(null, null, null, R.string.end_time_not_set));
            }
            // Start date is after end date
            else if(dateFormat.parse(startDate).compareTo(dateFormat.parse(endDate)) > 0) {
                entryFormState.setValue(new NewEntryFormState(null, null, R.string.end_date_error, null));
            }
            // Start time is after end time and the dates are the same
            else if (dateFormat.parse(startDate).compareTo(dateFormat.parse(endDate)) == 0 && timeFormat.parse(startTime).compareTo(timeFormat.parse(endTime)) > 0) {
                entryFormState.setValue(new NewEntryFormState(null, null, null, R.string.end_time_error));
            }
            else {
                entryFormState.setValue(new NewEntryFormState(true));
            }
        }
        catch(Exception ignored) {
            System.out.println("Exception: ");
            ignored.printStackTrace();
            entryFormState.setValue(new NewEntryFormState(false));
        }
    }
}
