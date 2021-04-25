package com.orangeplasticcup.ocuptimemanagement.ui.home.main.overview;

import android.content.Context;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.orangeplasticcup.ocuptimemanagement.data.TimeEntry;
import com.orangeplasticcup.ocuptimemanagement.data.model.GraphEntry;
import com.orangeplasticcup.ocuptimemanagement.data.model.LoggedInUser;
import com.orangeplasticcup.ocuptimemanagement.networking.NetworkManager;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class OverviewViewModel extends ViewModel {
    private static final String OVERVIEW_URL = "http://66.103.121.23/api/overview.php";

    private MutableLiveData<List<GraphEntry>> graphData = new MutableLiveData<>();

    public LiveData<List<GraphEntry>> getGraphData() { return graphData; }

    public void updateOverviewGraph(Context context) {
        StringRequest overviewPOSTRequest = new StringRequest(Request.Method.POST, OVERVIEW_URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                System.out.println("Server response: " + response);
                try {
                    List<GraphEntry> newEntries = new ArrayList<>();
                    JSONObject responseObject = new JSONObject(response);
                    JSONArray entries = responseObject.getJSONArray("body");

                    int count = responseObject.getInt("entryCount");
                    for(int i = 0; i < count; i++) {
                        JSONObject entryObject = entries.getJSONObject(i);
                        String categoryName = entryObject.getString("category_name");
                    }
                }
                catch(Exception e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                System.out.println("Error Response: " + error);
            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String,String> headers = new HashMap<>();
                headers.put("Cookie", LoggedInUser.getInstance().getSessionToken());
                return headers;
            }
        };

        overviewPOSTRequest.setRetryPolicy(new DefaultRetryPolicy(
                150,
                5,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
        ));

        NetworkManager.getInstance(context.getApplicationContext()).addToRequestQueue(overviewPOSTRequest);
    }
}
