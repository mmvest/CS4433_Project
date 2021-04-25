package com.orangeplasticcup.ocuptimemanagement.ui.home.main.newEntry;

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
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;

import com.orangeplasticcup.ocuptimemanagement.R;
import com.orangeplasticcup.ocuptimemanagement.data.Result;

import java.util.Calendar;
import java.util.TimeZone;

public class NewEntryFragment extends Fragment {
    private NewEntryViewModel newEntryViewModel;
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
            "Practice (Sports, Music, Etc....)",
            "Recreation",
            "School",
            "Streaming (Netflix, Hulu, Youtube, etc...)",
            "Studying",
            "Watching TV",
            "With Friends",
            "Work"
    };

    public static NewEntryFragment newInstance() {
        NewEntryFragment fragment = new NewEntryFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(Bundle.EMPTY);
        instance = this;
        newEntryViewModel = new NewEntryViewModel();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_new_entry_screen, container, false);
        return root;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {

        TextView noteTextView = view.findViewById(R.id.note);
        TextView categoryTextView = view.findViewById(R.id.category);
        TextView startTimeDate = view.findViewById(R.id.startTimeDate);
        TextView startTimeTime = view.findViewById(R.id.startTimeTime);
        TextView endTimeDate = view.findViewById(R.id.endTimeDate);
        TextView endTimeTime = view.findViewById(R.id.endTimeTime);
        Button createEntryButton = view.findViewById(R.id.createEntryButton);

        newEntryViewModel.getEntryFormState().observe(getViewLifecycleOwner(), new Observer<NewEntryFormState>() {
            @Override
            public void onChanged(NewEntryFormState newEntryFormState) {
                if(newEntryFormState == null) return;

                createEntryButton.setEnabled(newEntryFormState.isDataValid());
                if(newEntryFormState.getStartTimeDateError() != null) {
                    startTimeDate.setError(getString(newEntryFormState.getStartTimeDateError()));
                }
                else {
                    startTimeDate.setError(null);
                }

                if(newEntryFormState.getStartTimeTimeError() != null) {
                    startTimeTime.setError(getString(newEntryFormState.getStartTimeTimeError()));
                }
                else {
                    startTimeTime.setError(null);
                }

                if(newEntryFormState.getEndTimeDateError() != null) {
                    endTimeDate.setError(getString(newEntryFormState.getEndTimeDateError()));
                }
                else {
                    endTimeDate.setError(null);
                }

                if(newEntryFormState.getEndTimeTimeError() != null) {
                    endTimeTime.setError(getString(newEntryFormState.getEndTimeTimeError()));
                }
                else {
                    endTimeTime.setError(null);
                }
            }
        });

        newEntryViewModel.getCreateEntryResult().observe(getViewLifecycleOwner(), new Observer<Result<String>>() {
            @Override
            public void onChanged(Result<String> stringResult) {
                if (stringResult == null) return;

                if(stringResult instanceof Result.Error) {
                    Result.Error entryError = (Result.Error) stringResult;
                    Toast.makeText(getContext(), entryError.getError().getMessage(), Toast.LENGTH_LONG).show();
                }
                if(stringResult instanceof Result.Success) {
                    Result.Success<String> entrySuccess = (Result.Success<String>) stringResult;
                    Toast.makeText(getContext(), entrySuccess.getData(), Toast.LENGTH_LONG).show();
                }
            }
        });

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
                String monthString = String.valueOf(month);
                if(month < 10) {
                    monthString = "0" + month;
                }

                String dayString = String.valueOf(dayOfMonth);
                if (dayOfMonth < 10) {
                    dayString = "0" + dayOfMonth;
                }

                selectedTextView.setText(year + "-" + monthString + "-" + dayString);

                newEntryViewModel.entryDataChanged(
                        instance,
                        startTimeDate.getText().toString(),
                        startTimeTime.getText().toString(),
                        endTimeDate.getText().toString(),
                        endTimeTime.getText().toString());
            }
        };

        TimePickerDialog.OnTimeSetListener timeSetListener = new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                String hourString = String.valueOf(hourOfDay);
                if(hourOfDay < 10) {
                    hourString = "0" + hourOfDay;
                }

                String minuteString = String.valueOf(minute);
                if(minute < 10) {
                    minuteString = "0" + minute;
                }

                selectedTextView.setText(hourString + ":" + minuteString + ":00");

                newEntryViewModel.entryDataChanged(
                        instance,
                        startTimeDate.getText().toString(),
                        startTimeTime.getText().toString(),
                        endTimeDate.getText().toString(),
                        endTimeTime.getText().toString());
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
                newEntryViewModel.createEntry(
                        instance.getContext(),
                        noteTextView.getText().toString(),
                        categoryTextView.getText().toString(),
                        startTimeDate.getText().toString() + " " + startTimeTime.getText().toString(),
                        endTimeDate.getText().toString() + " " + endTimeTime.getText().toString());
            }
        });
    }
}
