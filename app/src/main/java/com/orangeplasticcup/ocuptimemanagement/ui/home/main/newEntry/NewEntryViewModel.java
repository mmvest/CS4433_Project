package com.orangeplasticcup.ocuptimemanagement.ui.home.main.newEntry;

import android.content.Context;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.orangeplasticcup.ocuptimemanagement.R;
import com.orangeplasticcup.ocuptimemanagement.data.Result;
import com.orangeplasticcup.ocuptimemanagement.data.model.LoggedInUser;
import com.orangeplasticcup.ocuptimemanagement.networking.NetworkManager;

import org.json.JSONObject;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class NewEntryViewModel extends ViewModel {
    private MutableLiveData<NewEntryFormState> entryFormState = new MutableLiveData<>();
    private MutableLiveData<Result<String>> createEntryResult = new MutableLiveData<>();

    private static final String NEW_ENTRY_URL = "http://66.103.121.23/api/create_entry.php";
    private static final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
    private static final SimpleDateFormat timeFormat = new SimpleDateFormat("hh:mm:ss");

    LiveData<NewEntryFormState> getEntryFormState() { return entryFormState; }
    LiveData<Result<String>> getCreateEntryResult() { return createEntryResult; }

    public void createEntry(Context context, String note, String category, String startTime, String endTime) {
        final JSONObject body = new JSONObject();
        try {
            body.put("note", note);
            body.put("category_name", category);
            body.put("start_date_time", startTime);
            body.put("end_date_time", endTime);
        }
        catch(Exception ignored) {
            createEntryResult.setValue(new Result.Error(new Exception("")));
        }

        StringRequest entryPOSTRequest = new StringRequest(Request.Method.POST, NEW_ENTRY_URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                System.out.println("Server response: " + response);
                switch(response) {
                    case "Entry Created":
                        createEntryResult.setValue(new Result.Success<String>("Successfully created entry named: " + note));
                        break;
                    case "Entry Creation Failed":
                        createEntryResult.setValue(new Result.Error(new Exception(context.getString(R.string.create_entry_failed))));
                        break;
                    case "Please login.":
                        createEntryResult.setValue(new Result.Error(new Exception(context.getString(R.string.create_entry_login_error))));
                        break;
                    default:
                        createEntryResult.setValue(new Result.Error(new Exception(context.getString(R.string.register_unknown_error))));
                        break;
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                System.out.println("Error Response: " + error);
                createEntryResult.setValue(new Result.Error(new Exception(context.getString(R.string.create_entry_unknown_error))));
            }
        }) {
            @Override
            public byte[] getBody() throws AuthFailureError {
                return body.toString().getBytes();
            }

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String,String> headers = new HashMap<>();
                headers.put("Cookie", LoggedInUser.getInstance().getSessionToken());
                return headers;
            }
        };

        entryPOSTRequest.setRetryPolicy(new DefaultRetryPolicy(
                150,
                5,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
        ));

        NetworkManager.getInstance(context.getApplicationContext()).addToRequestQueue(entryPOSTRequest);
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
