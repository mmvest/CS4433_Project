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
import com.orangeplasticcup.ocuptimemanagement.R;
import com.orangeplasticcup.ocuptimemanagement.data.model.GraphEntry;
import com.orangeplasticcup.ocuptimemanagement.data.model.LoggedInUser;
import com.orangeplasticcup.ocuptimemanagement.networking.NetworkManager;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CompareViewModel extends ViewModel {
    private static final String COMPARE_URL = "http://66.103.121.23/api/comparison.php";
    private static final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
    private static final SimpleDateFormat timeFormat = new SimpleDateFormat("hh:mm:ss");

    private final MutableLiveData<List<GraphEntry>> leftGraphData = new MutableLiveData<>();
    private final MutableLiveData<List<GraphEntry>> rightGraphData = new MutableLiveData<>();
    private final MutableLiveData<CompareFormState> leftCompareFormState = new MutableLiveData<>();
    private final MutableLiveData<CompareFormState> rightCompareFromState = new MutableLiveData<>();
    private boolean shouldUseGlobal = true;

    public LiveData<List<GraphEntry>> getLeftGraphData() { return leftGraphData; }
    public LiveData<List<GraphEntry>> getRightGraphData() { return rightGraphData; }
    public LiveData<CompareFormState> getLeftCompareFormState() { return leftCompareFormState; }
    public LiveData<CompareFormState> getRightCompareFormState() { return rightCompareFromState; }

    private String leftNote, rightNote;
    private String[] leftCategories, rightCategories;
    private String leftStartDate, rightStartDate;
    private String leftEndDate, rightEndDate;

    public void setShouldUseGlobal(boolean val) { shouldUseGlobal = val; }
    public boolean getShouldUseGlobal() { return shouldUseGlobal; }

    public boolean isDataValid() {
        if(leftCompareFormState.getValue() == null || rightCompareFromState.getValue() == null)
            return false;

        return leftCompareFormState.getValue().isDataValid() && rightCompareFromState.getValue().isDataValid();
    }

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

    public void updateLeftEntryData(String note, String[] categories, String startDate, String startTime, String endDate, String endTime) {
        try {
            if(note == null && categories == null && startDate == null && startTime == null && endDate == null && endTime == null) {
                leftCompareFormState.setValue(new CompareFormState(false));
            }
            leftNote = note;
            leftCategories = categories;
            leftCompareFormState.setValue(new CompareFormState(true));
            if (startTime != null && startDate != null && endDate == null && endTime == null) {
                leftStartDate = startDate + " " + startTime;
                leftCompareFormState.setValue(new CompareFormState(true));
                return;
            }
            if (startTime == null && startDate == null && endDate != null && endTime != null) {
                leftEndDate = endDate + " " + endTime;
                leftCompareFormState.setValue(new CompareFormState(true));
                return;
            }
            if (dateFormat.parse(startDate).compareTo(dateFormat.parse(endDate)) <= 0 && timeFormat.parse(startTime).compareTo(timeFormat.parse(endTime)) <= 0) {
                leftStartDate = startDate + " " + startTime;
                leftEndDate = endDate + " " + endTime;
                leftCompareFormState.setValue(new CompareFormState(true));
            }
            else if (dateFormat.parse(startDate).compareTo(dateFormat.parse(endDate)) > 0 && timeFormat.parse(startTime).compareTo(timeFormat.parse(endTime)) > 0) {
                leftCompareFormState.setValue(new CompareFormState(null, null, R.string.end_date_error, R.string.end_time_error));
            }
            else if (dateFormat.parse(startDate).compareTo(dateFormat.parse(endDate)) > 0) {
                leftCompareFormState.setValue(new CompareFormState(null, null, R.string.end_date_error, null));
            }
            else {
                leftCompareFormState.setValue(new CompareFormState(null, null, null, R.string.end_time_error));
            }
        }
        catch(Exception e){e.printStackTrace();}
    }

    public void updateRightEntryDate(String note, String[] categories, String startDate, String startTime, String endDate, String endTime) {
        try {
            if(note == null && categories == null && startDate == null && startTime == null && endDate == null && endTime == null) {
                rightCompareFromState.setValue(new CompareFormState(false));
            }

            rightNote = note;
            rightCategories = categories;
            rightCompareFromState.setValue(new CompareFormState(true));
            if (startTime != null && startDate != null && endDate == null && endTime == null) {
                rightStartDate = startDate + " " + startTime;
                rightCompareFromState.setValue(new CompareFormState(true));
                return;
            }

            if (startTime == null && startDate == null && endDate != null && endTime != null) {
                rightEndDate = endDate + " " + endTime;
                rightCompareFromState.setValue(new CompareFormState(true));
                return;
            }

            if (dateFormat.parse(startDate).compareTo(dateFormat.parse(endDate)) <= 0 && timeFormat.parse(startTime).compareTo(timeFormat.parse(endTime)) <= 0) {
                rightStartDate = startDate + " " + startTime;
                rightEndDate = endDate + " " + endTime;
                rightCompareFromState.setValue(new CompareFormState(true));
            }
            else if (dateFormat.parse(startDate).compareTo(dateFormat.parse(endDate)) > 0 && timeFormat.parse(startTime).compareTo(timeFormat.parse(endTime)) > 0) {
                rightCompareFromState.setValue(new CompareFormState(null, null, R.string.end_date_error, R.string.end_time_error));
            }
            else if (dateFormat.parse(startDate).compareTo(dateFormat.parse(endDate)) > 0) {
                rightCompareFromState.setValue(new CompareFormState(null, null, R.string.end_date_error, null));
            }
            else {
                rightCompareFromState.setValue(new CompareFormState(null, null, null, R.string.end_time_error));
            }
        }
        catch(Exception e){e.printStackTrace();}
    }

    private static boolean isEmptyOrNull(String str) {
        return str == null || str.isEmpty();
    }

    public void compare(Context context) {
        System.out.println("\n\n\n");
        try {
            JSONObject body = new JSONObject();
            if(leftNote != null && !leftNote.trim().equals(""))
                body.put("note", leftNote);
            if(leftCategories != null && leftCategories.length > 0)
                body.put("category_name", new JSONArray(leftCategories));
            if(leftStartDate != null)
                body.put("start_date_time", leftStartDate);
            if(leftEndDate != null)
                body.put("end_date_time", leftEndDate);

            StringRequest leftDataPOSTRequest = new StringRequest(Request.Method.POST, COMPARE_URL, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    System.out.println("LeftCompare Server Response: " + response);
                    if(response.equals("This user has no entries that meet this criteria.")) {
                        Toast.makeText(context.getApplicationContext(), "No data found for left comparison. You have no entries", Toast.LENGTH_LONG).show();
                        return;
                    }

                    try {
                        List<GraphEntry> leftUserGraph = new ArrayList<>();
                        List<GraphEntry> possibleGlobalRight = new ArrayList<>();
                        JSONObject responseObject = new JSONObject(response);
                        JSONArray entries = responseObject.getJSONArray("body");

                        int count = responseObject.getInt("entryCount");
                        for(int i = 0; i < count; i++) {
                            JSONObject entryObject = entries.getJSONObject(i);
                            String categoryName = entryObject.getString("category_name");
                            String categoryTime = entryObject.getString("category_time");
                            String categoryPercentTime = entryObject.getString("percent_time");
                            if(shouldUseGlobal) {
                                String globalCategoryTime = entryObject.getString("global_category_time");
                                String globalCategoryPercent = entryObject.getString("global_percent_time");
                                possibleGlobalRight.add(new GraphEntry(categoryName, Integer.parseInt(globalCategoryTime), Float.parseFloat(globalCategoryPercent)));
                            }

                            leftUserGraph.add(new GraphEntry(categoryName, Integer.parseInt(categoryTime), Float.parseFloat(categoryPercentTime)));
                        }
                        leftGraphData.setValue(leftUserGraph);
                        if(possibleGlobalRight.size() > 0)
                            rightGraphData.setValue(possibleGlobalRight);
                    }
                    catch(Exception ignored){}
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    System.out.println("LeftCompare Error Response: " + error);
                }
            }) {
                @Override
                public byte[] getBody() throws AuthFailureError {
                    return body.toString().getBytes();
                }

                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    Map<String, String> headers = new HashMap<>();
                    headers.put("Cookie", LoggedInUser.getInstance().getSessionToken());
                    return headers;
                }
            };

            NetworkManager.getInstance(context.getApplicationContext()).addToRequestQueue(leftDataPOSTRequest);
        }
        catch(Exception ex) {
            Toast.makeText(context.getApplicationContext(), "Error on compare", Toast.LENGTH_LONG).show();
            ex.printStackTrace();
            return;
        }

        if(!shouldUseGlobal) {
            try {
                JSONObject body = new JSONObject();
                if (rightNote != null && !rightNote.trim().equals(""))
                    body.put("note", rightNote);
                if (rightCategories != null && rightCategories.length > 0)
                    body.put("category_name", new JSONArray(rightCategories));
                if (rightStartDate != null)
                    body.put("start_date_time", rightStartDate);
                if (rightEndDate != null)
                    body.put("end_date_time", rightEndDate);

                StringRequest rightDataPOSTRequest = new StringRequest(Request.Method.POST, COMPARE_URL, new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        System.out.println("RightCompare Server Response: " + response);
                        if (response.equals("This user has no entries that meet this criteria.")) {
                            Toast.makeText(context.getApplicationContext(), "No data found for right comparison. You have no entries", Toast.LENGTH_LONG).show();
                            return;
                        }

                        try {
                            List<GraphEntry> rightUserGraph = new ArrayList<>();
                            JSONObject responseObject = new JSONObject(response);
                            JSONArray entries = responseObject.getJSONArray("body");

                            int count = responseObject.getInt("entryCount");
                            for (int i = 0; i < count; i++) {
                                JSONObject entryObject = entries.getJSONObject(i);
                                String categoryName = entryObject.getString("category_name");
                                String categoryTime = entryObject.getString("category_time");
                                String categoryPercentTime = entryObject.getString("percent_time");

                                rightUserGraph.add(new GraphEntry(categoryName, Integer.parseInt(categoryTime), Float.parseFloat(categoryPercentTime)));
                            }
                            rightGraphData.setValue(rightUserGraph);
                        } catch (Exception ignored) {
                        }
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        System.out.println("RightCompare Error Response: " + error);
                    }
                }) {
                    @Override
                    public byte[] getBody() throws AuthFailureError {
                        return body.toString().getBytes();
                    }

                    @Override
                    public Map<String, String> getHeaders() throws AuthFailureError {
                        Map<String, String> headers = new HashMap<>();
                        headers.put("Cookie", LoggedInUser.getInstance().getSessionToken());
                        return headers;
                    }
                };

                NetworkManager.getInstance(context.getApplicationContext()).addToRequestQueue(rightDataPOSTRequest);
            } catch (Exception ex) {
                Toast.makeText(context.getApplicationContext(), "Error on compare", Toast.LENGTH_LONG).show();
                ex.printStackTrace();
                return;
            }
        }
    }
}
