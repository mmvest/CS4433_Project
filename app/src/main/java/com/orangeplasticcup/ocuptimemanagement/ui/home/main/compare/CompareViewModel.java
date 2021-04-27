package com.orangeplasticcup.ocuptimemanagement.ui.home.main.compare;

import android.content.Context;
import android.widget.Toast;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.orangeplasticcup.ocuptimemanagement.data.Result;
import com.orangeplasticcup.ocuptimemanagement.data.model.GraphEntry;
import com.orangeplasticcup.ocuptimemanagement.data.model.LoggedInUser;
import com.orangeplasticcup.ocuptimemanagement.networking.NetworkManager;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CompareViewModel extends ViewModel {
    private static final String COMPARE_URL = "http://66.103.121.23/api/comparison.php";

    private final MutableLiveData<List<GraphEntry>> leftGraphData = new MutableLiveData<>();
    private final MutableLiveData<List<GraphEntry>> rightGraphData = new MutableLiveData<>();
    private final MutableLiveData<Result<String>> leftEntryResult = new MutableLiveData<>();
    private final MutableLiveData<Result<String>> rightEntryResult = new MutableLiveData<>();
    private boolean isRightGlobal = false;

    public LiveData<List<GraphEntry>> getLeftGraphData() { return leftGraphData; }
    public LiveData<List<GraphEntry>> getRightGraphData() { return rightGraphData; }
    public LiveData<Result<String>> getLeftEntryResult() { return leftEntryResult; }
    public LiveData<Result<String>> getRightEntryResult() { return rightEntryResult; }

    private String leftNote, rightNote;
    private String[] leftCategories, rightCategories;
    private String leftStartDate, rightStartDate;
    private String leftEndDate, rightEndDate;

    public boolean isRightGlobal() { return isRightGlobal; }

    public void getDefaultData(Context context) {
        StringRequest comparisonPOSTRequest = new StringRequest(Request.Method.POST, COMPARE_URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                if(response.equals("This user has no entries that meet this criteria.")) {
                    Toast.makeText(context.getApplicationContext(), "No data found. You have no entries", Toast.LENGTH_LONG).show();
                    return;
                }

                try {
                    List<GraphEntry> leftUserGraph = new ArrayList<>();
                    List<GraphEntry> rightGlobalGraph = new ArrayList<>();
                    JSONObject responseObject = new JSONObject(response);
                    JSONArray entries = responseObject.getJSONArray("body");

                    int count = responseObject.getInt("entryCount");
                    for(int i = 0; i < count; i++) {
                        JSONObject entryObject = entries.getJSONObject(i);
                        String categoryName = entryObject.getString("category_name");
                        String categoryTime = entryObject.getString("category_time");
                        String categoryPercentTime = entryObject.getString("percent_time");
                        String globalCategoryTime = entryObject.getString("global_category_time");
                        String globalCategoryPercent = entryObject.getString("global_percent_time");

                        leftUserGraph.add(new GraphEntry(categoryName, Integer.parseInt(categoryTime), Float.parseFloat(categoryPercentTime)));
                        rightGlobalGraph.add(new GraphEntry(categoryName, Integer.parseInt(globalCategoryTime), Float.parseFloat(globalCategoryPercent)));
                    }
                    isRightGlobal = true;
                    leftGraphData.setValue(leftUserGraph);
                    rightGraphData.setValue(rightGlobalGraph);
                }
                catch(Exception ignored){}
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                System.out.println("Server Error" + error);
            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String,String> headers = new HashMap<>();
                headers.put("Cookie", LoggedInUser.getInstance().getSessionToken());
                return headers;
            }
        };
        NetworkManager.getInstance(context.getApplicationContext()).addToRequestQueue(comparisonPOSTRequest);
    }

    public void updateLeftEntryData(String note, String[] categories, String startDate, String endDate) {

    }

    public void updateRightEntryDate(String note, String[] categories, String startDate, String endDate) {

    }

    public void compare() {


    }
}
