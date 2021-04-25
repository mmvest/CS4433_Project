package com.orangeplasticcup.ocuptimemanagement.ui.home.main.overview;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.orangeplasticcup.ocuptimemanagement.data.TimeEntry;
import com.orangeplasticcup.ocuptimemanagement.data.model.GraphEntry;

import java.util.ArrayList;
import java.util.List;

public class OverviewViewModel extends ViewModel {
    private static final String OVERVIEW_URL = "";

    private MutableLiveData<List<GraphEntry>> graphData = new MutableLiveData<>();

    public LiveData<List<GraphEntry>> getGraphData() { return graphData; }

    public void updateOverviewGraph() {

    }
}
