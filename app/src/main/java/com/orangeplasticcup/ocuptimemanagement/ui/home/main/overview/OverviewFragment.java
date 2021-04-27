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

import com.orangeplasticcup.ocuptimemanagement.R;
import com.orangeplasticcup.ocuptimemanagement.data.TimeEntry;
import com.orangeplasticcup.ocuptimemanagement.data.model.GraphEntry;

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
        overviewViewModel.getGraphData().observe(getViewLifecycleOwner(), new Observer<List<GraphEntry>>() {
            @Override
            public void onChanged(List<GraphEntry> graphEntries) {
                PieChartView pieChartView = getView().findViewById(R.id.chart);
                List<SliceValue> pieData = new ArrayList<>();

                for(GraphEntry entry : graphEntries) {
                    pieData.add(new SliceValue(entry.getPercentTime(), ((int)(Math.random()*16777215)) | (0xFF << 24)).setLabel(entry.getCategory()));
                }

                PieChartData pieChartData = new PieChartData(pieData);
                pieChartData.setHasLabels(true).setValueLabelTextSize(14);
                pieChartData.setHasCenterCircle(true).setCenterText1("Time Distribution").setCenterText1FontSize(20).setCenterText1Color(Color.parseColor("#0097A7"));

                pieChartView.setPieChartData(pieChartData);
                pieChartView.setChartRotationEnabled(false);
            }
        });
        overviewViewModel.updateOverviewGraph(getContext());

        overviewViewModel.getTimeEntryData().observe(getViewLifecycleOwner(), new Observer<List<TimeEntry>>() {
            @Override
            public void onChanged(List<TimeEntry> timeEntries) {
                if(timeEntries.size() == 0) return;
                /*
                Collections.sort(timeEntries);

                List<String> uniqueDays = new ArrayList<>();
                {
                    List<String> allDays = new ArrayList<>();
                    for (TimeEntry entry : timeEntries) {
                        allDays.add(entry.getStartDate());
                    }

                    uniqueDays = new ArrayList<>(new HashSet<>(allDays));
                }*/
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