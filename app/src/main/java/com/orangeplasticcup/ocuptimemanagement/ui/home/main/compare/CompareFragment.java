package com.orangeplasticcup.ocuptimemanagement.ui.home.main.compare;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.orangeplasticcup.ocuptimemanagement.R;
import com.orangeplasticcup.ocuptimemanagement.ui.home.main.PageViewModel;

public class CompareFragment extends Fragment {
    private CompareViewModel pageViewModel;

    public static CompareFragment newInstance() {
        CompareFragment fragment = new CompareFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(Bundle.EMPTY);
        pageViewModel = new CompareViewModel();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_compare_screen, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {

        
    }
}
