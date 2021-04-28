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
import com.orangeplasticcup.ocuptimemanagement.ui.home.main.EntryValidationViewModel;

import org.json.JSONObject;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class NewEntryViewModel extends EntryValidationViewModel {
    private static final String NEW_ENTRY_URL = "http://66.103.121.23/api/create_entry.php";

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

        System.out.println(body.toString());

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
                createEntryResult.setValue(new Result.Error(new Exception("Server error. Please try again")));
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

        NetworkManager.getInstance(context.getApplicationContext()).addToRequestQueue(entryPOSTRequest);
    }
}
