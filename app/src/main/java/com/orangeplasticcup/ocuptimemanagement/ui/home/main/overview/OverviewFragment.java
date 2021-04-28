package com.orangeplasticcup.ocuptimemanagement.ui.home.main.overview;

import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListView;

import androidx.annotation.Nullable;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.orangeplasticcup.ocuptimemanagement.R;
import com.orangeplasticcup.ocuptimemanagement.data.TimeEntry;
import com.orangeplasticcup.ocuptimemanagement.data.model.EntryCategoryRepository;
import com.orangeplasticcup.ocuptimemanagement.data.model.GraphEntry;
import com.orangeplasticcup.ocuptimemanagement.data.model.LoggedInUser;
import com.orangeplasticcup.ocuptimemanagement.networking.NetworkManager;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import lecho.lib.hellocharts.model.PieChartData;
import lecho.lib.hellocharts.model.SliceValue;
import lecho.lib.hellocharts.view.PieChartView;

/**
 * A placeholder fragment containing a simple view.
 */
public class OverviewFragment extends Fragment {
    private static final String RETRIEVE_CATEGORY_URL = "http://66.103.121.23/api/retrieve_category.php";

    private OverviewFragment instance;
    private OverviewViewModel overviewViewModel;
    private ExpandableListAdapter listAdapter;

    public static OverviewFragment newInstance() {
        OverviewFragment fragment = new OverviewFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(Bundle.EMPTY);
        overviewViewModel = new OverviewViewModel();
        instance = this;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_overview_screen, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        StringRequest retrieveCategoriesRequest = new StringRequest(Request.Method.POST, RETRIEVE_CATEGORY_URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject responseObject = new JSONObject(response);
                    JSONArray entries = responseObject.getJSONArray("body");

                    int count = responseObject.getInt("entryCount");
                    String[] categories = new String[count];

                    for(int i = 0; i < count; i++) {
                        JSONObject entryObject = entries.getJSONObject(i);
                        String categoryName = entryObject.getString("name");
                        categories[i] = categoryName;
                    }

                    EntryCategoryRepository.bindCategories(categories);
                }
                catch(Exception ignored) {}
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                System.out.println("Server Response: " + error);
            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String,String> headers = new HashMap<>();
                headers.put("Cookie", LoggedInUser.getInstance().getSessionToken());
                return headers;
            }
        };
        NetworkManager.getInstance(view.getContext().getApplicationContext()).addToRequestQueue(retrieveCategoriesRequest);

        overviewViewModel.getGraphData().observe(getViewLifecycleOwner(), new Observer<List<GraphEntry>>() {
            @Override
            public void onChanged(List<GraphEntry> graphEntries) {
                PieChartView pieChartView = getView().findViewById(R.id.chart);
                List<SliceValue> pieData = new ArrayList<>();

                for(GraphEntry entry : graphEntries) {
                    int color = EntryCategoryRepository.getColor(entry.getCategory());
                    pieData.add(new SliceValue(entry.getPercentTime(), color).setLabel(entry.getCategory() + ": " + String.format("%.1f%%", entry.getPercentTime())));
                }

                PieChartData pieChartData = new PieChartData(pieData);
                pieChartData.setHasLabels(true).setValueLabelTextSize(14);
                pieChartData.setHasCenterCircle(true).setCenterText1("Time Distribution").setCenterText1FontSize(16).setCenterText1Color(Color.parseColor("#0097A7"));

                pieChartView.setPieChartData(pieChartData);
                pieChartView.setChartRotationEnabled(false);
                pieChartView.setCircleFillRatio(0.9f);
            }
        });
        overviewViewModel.updateOverviewGraph(getContext());

        overviewViewModel.getTimeEntryData().observe(getViewLifecycleOwner(), new Observer<List<TimeEntry>>() {
            @Override
            public void onChanged(List<TimeEntry> timeEntries) {
                if(timeEntries.size() == 0) return;

                Map<String, List<TimeEntry>> dayCollection = new HashMap<>();

                for(TimeEntry entry : timeEntries) {
                    if(dayCollection.get(entry.getStartDate()) == null) {
                        dayCollection.put(entry.getStartDate(), new ArrayList<>());
                    }
                    dayCollection.get(entry.getStartDate()).add(entry);
                }

                List<String> days = new ArrayList<>(dayCollection.keySet());
                Collections.sort(days, Collections.reverseOrder());

                ExpandableListView expandableListView = view.findViewById(R.id.entryExpandableList);
                listAdapter = new ExpandableListAdapter(instance.getContext(), days, dayCollection);
                expandableListView.setAdapter(listAdapter);
            }
        });
        overviewViewModel.updateUserEntries(getContext());
    }
}