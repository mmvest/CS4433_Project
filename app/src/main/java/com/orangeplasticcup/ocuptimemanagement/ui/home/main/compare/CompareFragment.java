package com.orangeplasticcup.ocuptimemanagement.ui.home.main.compare;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;

import com.orangeplasticcup.ocuptimemanagement.R;
import com.orangeplasticcup.ocuptimemanagement.data.model.EntryCategoryRepository;
import com.orangeplasticcup.ocuptimemanagement.data.model.GraphEntry;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;
import java.util.TimeZone;

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

        /*
            Left
         */
        EditText leftNote = view.findViewById(R.id.leftNote);
        TextView leftCategories = view.findViewById(R.id.leftCategories);
        final String[][] leftCategoyVals = new String[1][1];
        TextView leftStartDate = view.findViewById(R.id.leftStartDate);
        TextView leftStartTime = view.findViewById(R.id.leftStartTime);
        TextView leftEndDate = view.findViewById(R.id.leftEndDate);
        TextView leftEndTime = view.findViewById(R.id.leftEndTime);
        Button leftClearButton = view.findViewById(R.id.clearLeftButton);

        /*
            Right
         */
        EditText rightNote = view.findViewById(R.id.rightNote);
        TextView rightCategories = view.findViewById(R.id.rightCategories);
        final String[][] rightCategoryVals = new String[1][1];
        TextView rightStartDate = view.findViewById(R.id.rightStartDate);
        TextView rightStartTime = view.findViewById(R.id.rightStartTime);
        TextView rightEndDate = view.findViewById(R.id.rightEndDate);
        TextView rightEndTime = view.findViewById(R.id.rightEndTime);
        Button rightClearButton = view.findViewById(R.id.clearRightButton);

        /*
            Compare button
         */
        Button compareButton = view.findViewById(R.id.compareButton);

        compareViewModel.getLeftCompareFormState().observe(getViewLifecycleOwner(), new Observer<CompareFormState>() {
            @Override
            public void onChanged(CompareFormState compareFormState) {
                if(compareFormState == null) return;

                compareButton.setEnabled(compareFormState.isDataValid());
                if(compareFormState.getEndTimeDateError() != null) {
                    leftEndDate.setError(getString(compareFormState.getEndTimeDateError()));
                } else {
                    leftEndDate.setError(null);
                }

                if(compareFormState.getEndTimeTimeError() != null) {
                    leftEndTime.setError(getString(compareFormState.getEndTimeTimeError()));
                } else {
                    leftEndTime.setError(null);
                }
            }
        });
        compareViewModel.getRightCompareFormState().observe(getViewLifecycleOwner(), new Observer<CompareFormState>() {
            @Override
            public void onChanged(CompareFormState compareFormState) {
                if(compareFormState == null) return;

                compareButton.setEnabled(compareFormState.isDataValid());
                if(compareFormState.getEndTimeDateError() != null) {
                    rightEndDate.setError(getString(compareFormState.getEndTimeDateError()));
                } else {
                    rightEndDate.setError(null);
                }

                if(compareFormState.getEndTimeTimeError() != null) {
                    rightEndTime.setError(getString(compareFormState.getEndTimeTimeError()));
                } else {
                    rightEndTime.setError(null);
                }
            }
        });

        leftCategories.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String[] categoryNames = EntryCategoryRepository.getCategories();
                boolean[] boolValues = new boolean[categoryNames.length];
                for(int i = 0; i < categoryNames.length; i++) {
                    boolValues[i] = false;
                }

                AlertDialog.Builder builder = new AlertDialog.Builder(view.getContext());
                builder.setMultiChoiceItems(categoryNames, boolValues, new DialogInterface.OnMultiChoiceClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which, boolean isChecked) {

                    }
                });

                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        List<String> list = new ArrayList<String>();
                        String boxValue = "";
                        for(int i = 0; i < categoryNames.length; i++) {
                            if(boolValues[i]) {
                                list.add(categoryNames[i]);
                                boxValue += categoryNames[i] + ", ";
                            }
                        }
                        leftCategories.setText(boxValue);
                        final String[] vals = new String[list.size()];
                        for(int i = 0; i < list.size(); i++) {
                            vals[i] = list.get(i);
                        }
                        leftCategoyVals[0] = vals;
                        compareViewModel.updateLeftEntryData(
                                leftNote.getText().toString(),
                                vals,
                                leftStartDate.getText().toString(),
                                leftStartTime.getText().toString(),
                                leftEndDate.getText().toString(),
                                leftEndTime.getText().toString()
                        );
                    }
                });

                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });

                AlertDialog dialog = builder.create();
                dialog.show();
            }
        });
        rightCategories.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String[] categoryNames = EntryCategoryRepository.getCategories();
                boolean[] boolValues = new boolean[categoryNames.length];
                for(int i = 0; i < categoryNames.length; i++) {
                    boolValues[i] = false;
                }

                AlertDialog.Builder builder = new AlertDialog.Builder(view.getContext());
                builder.setMultiChoiceItems(categoryNames, boolValues, new DialogInterface.OnMultiChoiceClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which, boolean isChecked) { }
                });

                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        List<String> list = new ArrayList<>();
                        String boxValue = "";
                        for(int i = 0; i < categoryNames.length; i++) {
                            if(boolValues[i]) {
                                list.add(categoryNames[i]);
                                boxValue += categoryNames[i] + ", ";
                            }
                        }
                        rightCategories.setText(boxValue);
                        final String[] vals = new String[list.size()];
                        for(int i = 0; i < list.size(); i++) {
                            vals[i] = list.get(i);
                        }
                        rightCategoryVals[0] = vals;

                        compareViewModel.updateRightEntryDate(
                                rightNote.getText().toString(),
                                vals,
                                rightStartDate.getText().toString(),
                                rightStartTime.getText().toString(),
                                rightEndDate.getText().toString(),
                                rightEndTime.getText().toString()
                        );
                    }
                });

                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });

                AlertDialog dialog = builder.create();
                dialog.show();
            }
        });

        leftStartDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar calendar = Calendar.getInstance(TimeZone.getDefault());

                DatePickerDialog pickerDialog = new DatePickerDialog(view.getContext(), new DatePickerDialog.OnDateSetListener() {
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

                        leftStartDate.setText(year + "-" + monthString + "-" + dayString);

                        compareViewModel.updateRightEntryDate(
                                leftNote.getText().toString(),
                                leftCategoyVals[0],
                                leftStartDate.getText().toString(),
                                leftStartTime.getText().toString(),
                                leftEndDate.getText().toString(),
                                leftEndTime.getText().toString());
                    }
                }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));

                pickerDialog.show();
            }
        });
        leftEndDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar calendar = Calendar.getInstance(TimeZone.getDefault());

                DatePickerDialog pickerDialog = new DatePickerDialog(view.getContext(), new DatePickerDialog.OnDateSetListener() {
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

                        leftEndDate.setText(year + "-" + monthString + "-" + dayString);

                        compareViewModel.updateRightEntryDate(
                                leftNote.getText().toString(),
                                leftCategoyVals[0],
                                leftStartDate.getText().toString(),
                                leftStartTime.getText().toString(),
                                leftEndDate.getText().toString(),
                                leftEndTime.getText().toString());
                    }
                }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));

                pickerDialog.show();
            }
        });
        rightStartDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar calendar = Calendar.getInstance(TimeZone.getDefault());

                DatePickerDialog pickerDialog = new DatePickerDialog(view.getContext(), new DatePickerDialog.OnDateSetListener() {
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

                        rightStartDate.setText(year + "-" + monthString + "-" + dayString);

                        compareViewModel.updateRightEntryDate(
                                rightNote.getText().toString(),
                                rightCategoryVals[0],
                                rightStartDate.getText().toString(),
                                rightStartTime.getText().toString(),
                                rightEndDate.getText().toString(),
                                rightEndTime.getText().toString());
                    }
                }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));

                pickerDialog.show();
            }
        });
        rightEndDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar calendar = Calendar.getInstance(TimeZone.getDefault());

                DatePickerDialog pickerDialog = new DatePickerDialog(view.getContext(), new DatePickerDialog.OnDateSetListener() {
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

                        rightEndDate.setText(year + "-" + monthString + "-" + dayString);

                        compareViewModel.updateRightEntryDate(
                                rightNote.getText().toString(),
                                rightCategoryVals[0],
                                rightStartDate.getText().toString(),
                                rightStartTime.getText().toString(),
                                rightEndDate.getText().toString(),
                                rightEndTime.getText().toString());
                    }
                }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));

                pickerDialog.show();
            }
        });

        leftStartTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar calendar = Calendar.getInstance(TimeZone.getDefault());

                TimePickerDialog timePickerDialog = new TimePickerDialog(view.getContext(), new TimePickerDialog.OnTimeSetListener() {
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

                        leftStartTime.setText(hourString + ":" + minuteString + ":00");

                        compareViewModel.updateRightEntryDate(
                                leftNote.getText().toString(),
                                leftCategoyVals[0],
                                leftStartDate.getText().toString(),
                                leftStartTime.getText().toString(),
                                leftEndDate.getText().toString(),
                                leftEndTime.getText().toString());
                    }
                },calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), false);

                timePickerDialog.show();
            }
        });

        leftEndTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar calendar = Calendar.getInstance(TimeZone.getDefault());

                TimePickerDialog timePickerDialog = new TimePickerDialog(view.getContext(), new TimePickerDialog.OnTimeSetListener() {
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

                        leftEndTime.setText(hourString + ":" + minuteString + ":00");

                        compareViewModel.updateRightEntryDate(
                                leftNote.getText().toString(),
                                leftCategoyVals[0],
                                leftStartDate.getText().toString(),
                                leftStartTime.getText().toString(),
                                leftEndDate.getText().toString(),
                                leftEndTime.getText().toString());
                    }
                },calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), false);

                timePickerDialog.show();
            }
        });

        rightStartTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar calendar = Calendar.getInstance(TimeZone.getDefault());

                TimePickerDialog timePickerDialog = new TimePickerDialog(view.getContext(), new TimePickerDialog.OnTimeSetListener() {
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

                        rightStartTime.setText(hourString + ":" + minuteString + ":00");

                        compareViewModel.updateRightEntryDate(
                                rightNote.getText().toString(),
                                rightCategoryVals[0],
                                rightStartDate.getText().toString(),
                                rightStartTime.getText().toString(),
                                rightEndDate.getText().toString(),
                                rightEndTime.getText().toString());
                    }
                },calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), false);

                timePickerDialog.show();
            }
        });

        rightEndTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar calendar = Calendar.getInstance(TimeZone.getDefault());

                TimePickerDialog timePickerDialog = new TimePickerDialog(view.getContext(), new TimePickerDialog.OnTimeSetListener() {
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

                        rightEndTime.setText(hourString + ":" + minuteString + ":00");

                        compareViewModel.updateRightEntryDate(
                                rightNote.getText().toString(),
                                rightCategoryVals[0],
                                rightStartDate.getText().toString(),
                                rightStartTime.getText().toString(),
                                rightEndDate.getText().toString(),
                                rightEndTime.getText().toString());
                    }
                },calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), false);

                timePickerDialog.show();
            }
        });
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
