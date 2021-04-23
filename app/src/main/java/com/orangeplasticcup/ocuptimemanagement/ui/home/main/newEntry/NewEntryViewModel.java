package com.orangeplasticcup.ocuptimemanagement.ui.home.main.newEntry;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.orangeplasticcup.ocuptimemanagement.R;
import com.orangeplasticcup.ocuptimemanagement.data.Result;

import org.json.JSONObject;

import java.text.SimpleDateFormat;

public class NewEntryViewModel extends ViewModel {
    private MutableLiveData<NewEntryFormState> entryFormState = new MutableLiveData<>();
    private MutableLiveData<Result<String>> createEntryResult = new MutableLiveData<>();

    private static final String NEW_ENTRY_URL = "http://66.103.121.23/api/create_entry.php";
    private static final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
    private static final SimpleDateFormat timeFormat = new SimpleDateFormat("hh:mm:ss");

    LiveData<NewEntryFormState> getEntryFormState() { return entryFormState; }
    LiveData<Result<String>> getCreateEntryResult() { return createEntryResult; }

    public void createEntry(String note, String category, String startTime, String endTime) {
        final JSONObject body = new JSONObject();
        try {
            body.put("note", note);
            body.put("category_name", category);
            body.put("start_date_time", startTime);
            body.put("end_date_time", endTime);
        }
        catch(Exception ignored) {}
    }

    public void entryDataChanged(Fragment dummyFragment, String startDate, String startTime, String endDate, String endTime) {
        try{
            if(startDate == null || startDate.equals(dummyFragment.getString(R.string.start_date))) {
                System.out.println("Start Date Error");
                entryFormState.setValue(new NewEntryFormState(R.string.start_date_not_set, null, null, null));
            }
            else if (startTime == null || startTime.equals(dummyFragment.getString(R.string.start_time))) {
                System.out.println("Start Time Error");
                entryFormState.setValue(new NewEntryFormState(null, R.string.start_time_not_set, null, null));
            }
            else if (endDate == null || endDate.equals(dummyFragment.getString(R.string.end_date))) {
                System.out.println("End Date Error");
                entryFormState.setValue(new NewEntryFormState(null, null, R.string.end_date_not_set, null));
            }
            else if (endTime == null || endTime.equals(dummyFragment.getString(R.string.end_time))) {
                System.out.println("End Time Error");
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
