package com.orangeplasticcup.ocuptimemanagement.ui.home.main.compare;

import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;

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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import lecho.lib.hellocharts.model.PieChartData;
import lecho.lib.hellocharts.model.SliceValue;
import lecho.lib.hellocharts.view.PieChartView;

public class CompareFragment extends Fragment {
    private CompareViewModel compareViewModel;

    public static CompareFragment newInstance() {
        CompareFragment fragment = new CompareFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(Bundle.EMPTY);
        compareViewModel = new CompareViewModel();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_compare_screen, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        PieChartView leftChart = view.findViewById(R.id.leftChart);
        compareViewModel.getLeftGraphData().observe(getViewLifecycleOwner(), new Observer<List<GraphEntry>>() {
            @Override
            public void onChanged(List<GraphEntry> graphEntries) {
                if(compareViewModel.isRightGlobal())
                    setGraphData(leftChart, graphEntries, "User");
                else
                    setGraphData(leftChart, graphEntries, "Left");
            }
        });

        PieChartView rightChart = view.findViewById(R.id.rightChart);
        compareViewModel.getRightGraphData().observe(getViewLifecycleOwner(), new Observer<List<GraphEntry>>() {
            @Override
            public void onChanged(List<GraphEntry> graphEntries) {
                if(compareViewModel.isRightGlobal())
                    setGraphData(rightChart, graphEntries, "Global");
                else
                    setGraphData(rightChart, graphEntries, "Right");
            }
        });

        compareViewModel.getDefaultData(view.getContext());
    }

    private void setGraphData(PieChartView chart, List<GraphEntry> entryData, String text) {
        List<SliceValue> pieData = new ArrayList<>();
        for(GraphEntry entry : entryData) {
            pieData.add(new SliceValue(entry.getPercentTime(), ((int)(Math.random()*16777215)) | (0xFF << 24)).setLabel(entry.getCategory() + ": " + String.format("%.1f%%", entry.getPercentTime())));
        }

        PieChartData pieChartData = new PieChartData(pieData);
        pieChartData.setHasLabels(true).setValueLabelTextSize(11);
        pieChartData.setHasCenterCircle(true).setCenterText1(text).setCenterText1FontSize(20).setCenterText1Color(Color.parseColor("#0097A7"));

        chart.setPieChartData(pieChartData);
        chart.setChartRotationEnabled(false);
        chart.setCircleFillRatio(0.8f);
    }
}
