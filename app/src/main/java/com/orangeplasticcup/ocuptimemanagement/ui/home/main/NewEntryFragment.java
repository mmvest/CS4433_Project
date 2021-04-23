package com.orangeplasticcup.ocuptimemanagement.ui.home.main;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.TimePicker;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.orangeplasticcup.ocuptimemanagement.R;

import java.util.Calendar;
import java.util.TimeZone;

public class NewEntryFragment extends Fragment {
    private static final String ARG_SECTION_NUMBER = "section_number";
    private static final String NEW_ENTRY_URL = "http://66.103.121.23/api/create_entry.php";
    private NewEntryViewModel pageViewModel;
    private TextView selectedTextView;

    private static NewEntryFragment instance;

    // This is horrible, this should instead be a network call to the database to get all the categories
    private static final String[] CATEGORY_NAMES = new String[] {
            "Cleaning",
            "Eating",
            "Family Time",
            "Getting Ready",
            "Hobby",
            "Homework",
            "Other",
            "Practice (Sports, Music, Etc...)",
            "Recreation",
            "School",
            "Streaming (Netflix, Hulu, Youtube, Etc...)",
            "Studying",
            "Watching TV",
            "With Friends",
            "Work"
    };

    public static NewEntryFragment newInstance(int index) {
        NewEntryFragment fragment = new NewEntryFragment();
        Bundle bundle = new Bundle();
        bundle.putInt(ARG_SECTION_NUMBER, index);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(Bundle.EMPTY);
        instance = this;
        pageViewModel = new NewEntryViewModel();

        /*int index = 1;
        if (getArguments() != null) {
            index = getArguments().getInt(ARG_SECTION_NUMBER);
        }
        pageViewModel.setIndex(index);*/
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_new_entry_screen, container, false);
        /*final TextView textView = root.findViewById(R.id.section_label);
        pageViewModel.getText().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                textView.setText(s);
            }
        });*/
        return root;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {

        TextView categoryTextView = view.findViewById(R.id.category);
        TextView startTimeDate = view.findViewById(R.id.startTimeDate);
        TextView startTimeTime = view.findViewById(R.id.startTimeTime);
        TextView endTimeDate = view.findViewById(R.id.endTimeDate);
        TextView endTimeTime = view.findViewById(R.id.endTimeTime);
        Button createEntryButton = view.findViewById(R.id.createEntryButton);

        categoryTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(instance.getContext());
                builder.setTitle("Select category");
                builder.setItems(CATEGORY_NAMES, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        categoryTextView.setText(CATEGORY_NAMES[which]);
                    }
                });

                builder.show();
            }
        });

        DatePickerDialog.OnDateSetListener dateSetListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                selectedTextView.setText(year + "-" + month + "-" + dayOfMonth);
            }
        };

        TimePickerDialog.OnTimeSetListener timeSetListener = new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                selectedTextView.setText(hourOfDay + ":" + minute + ":00");
            }
        };

        View.OnClickListener dateViewSelector = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectedTextView = (TextView) v;
                Calendar calendar = Calendar.getInstance(TimeZone.getDefault());

                DatePickerDialog pickerDialog = new DatePickerDialog(view.getContext(), dateSetListener,
                        calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));

                pickerDialog.show();
            }
        };

        View.OnClickListener timeViewSelector = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectedTextView = (TextView) v;
                Calendar calendar = Calendar.getInstance(TimeZone.getDefault());

                TimePickerDialog timePickerDialog = new TimePickerDialog(view.getContext(), timeSetListener,
                        calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), false);

                timePickerDialog.show();
            }
        };

        startTimeDate.setOnClickListener(dateViewSelector);
        endTimeDate.setOnClickListener(dateViewSelector);
        startTimeTime.setOnClickListener(timeViewSelector);
        endTimeTime.setOnClickListener(timeViewSelector);

        createEntryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                
            }
        });
    }
}
