package com.orangeplasticcup.ocuptimemanagement.ui.home.main.overview.editEntry;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.orangeplasticcup.ocuptimemanagement.R;
import com.orangeplasticcup.ocuptimemanagement.data.Result;
import com.orangeplasticcup.ocuptimemanagement.data.TimeEntry;
import com.orangeplasticcup.ocuptimemanagement.data.model.EntryCategoryRepository;
import com.orangeplasticcup.ocuptimemanagement.data.model.LoggedInUser;
import com.orangeplasticcup.ocuptimemanagement.networking.NetworkManager;
import com.orangeplasticcup.ocuptimemanagement.ui.home.main.newEntry.NewEntryFormState;
import com.orangeplasticcup.ocuptimemanagement.ui.home.main.overview.OverviewViewModel;

import org.json.JSONObject;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.TimeZone;

public class EditEntryActivity extends AppCompatActivity {

    private static EditEntryActivity instance;
    private static TimeEntry entryToEdit;
    //private static boolean wasEntryChanged = false;
    private final EditEntryViewModel editEntryViewModel = new EditEntryViewModel();
    private TextView selectedTextView;

    public static void setEditableEntry(TimeEntry entry) {
        entryToEdit = entry;
    }
    //public static boolean wasEntryChanged() { return wasEntryChanged; }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(Bundle.EMPTY);
        setContentView(R.layout.fragment_new_entry_screen);
        instance = this;
        //wasEntryChanged = false;

        TextView noteTextView = findViewById(R.id.note);
        TextView categoryTextView = findViewById(R.id.category);
        TextView startTimeDate = findViewById(R.id.startTimeDate);
        TextView startTimeTime = findViewById(R.id.startTimeTime);
        TextView endTimeDate = findViewById(R.id.endTimeDate);
        TextView endTimeTime = findViewById(R.id.endTimeTime);
        Button editEntryButton = findViewById(R.id.createEntryButton);
        Button editCancelButton = findViewById(R.id.cancelEdit);

        noteTextView.setText(entryToEdit.getNote());
        categoryTextView.setText(entryToEdit.getCategoryName());
        startTimeDate.setText(entryToEdit.getStartDate());
        startTimeTime.setText(entryToEdit.getStartTime());
        endTimeDate.setText(entryToEdit.getEndDate());
        endTimeTime.setText(entryToEdit.getEndTime());
        editEntryButton.setText(R.string.action_edit_entry);
        editEntryButton.setEnabled(true);
        editCancelButton.setEnabled(true);
        editCancelButton.setVisibility(View.VISIBLE);

        editCancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        editEntryViewModel.getEntryFormState().observe(this, new Observer<NewEntryFormState>() {
            @Override
            public void onChanged(NewEntryFormState newEntryFormState) {
                if(newEntryFormState == null) return;

                editEntryButton.setEnabled(newEntryFormState.isDataValid());
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

        editEntryViewModel.getCreateEntryResult().observe(this, new Observer<Result<String>>() {
            @Override
            public void onChanged(Result<String> stringResult) {
                if (stringResult == null) return;

                if(stringResult instanceof Result.Error) {
                    Result.Error entryError = (Result.Error) stringResult;
                    Toast.makeText(instance, entryError.getError().getMessage(), Toast.LENGTH_LONG).show();
                }
                if(stringResult instanceof Result.Success) {
                    Result.Success<String> entrySuccess = (Result.Success<String>) stringResult;
                    Toast.makeText(instance, entrySuccess.getData(), Toast.LENGTH_LONG).show();
                    OverviewViewModel.getInstance().updateOverviewGraph(instance.getApplicationContext());
                }
            }
        });

        categoryTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String[] categoryNames = EntryCategoryRepository.getCategories();
                AlertDialog.Builder builder = new AlertDialog.Builder(instance);
                builder.setTitle("Select category");
                builder.setItems(categoryNames, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        categoryTextView.setText(categoryNames[which]);
                    }
                });

                builder.show();
            }
        });

        DatePickerDialog.OnDateSetListener dateSetListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                month += 1;

                String monthString = String.valueOf(month);
                if(month < 10) {
                    monthString = "0" + month;
                }

                String dayString = String.valueOf(dayOfMonth);
                if (dayOfMonth < 10) {
                    dayString = "0" + dayOfMonth;
                }

                selectedTextView.setText(year + "-" + monthString + "-" + dayString);

                editEntryViewModel.entryDataChanged(
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

                editEntryViewModel.entryDataChanged(
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

                DatePickerDialog pickerDialog = new DatePickerDialog(instance, dateSetListener,
                        calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));

                pickerDialog.show();
            }
        };

        View.OnClickListener timeViewSelector = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectedTextView = (TextView) v;
                Calendar calendar = Calendar.getInstance(TimeZone.getDefault());

                TimePickerDialog timePickerDialog = new TimePickerDialog(instance, timeSetListener,
                        calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), false);

                timePickerDialog.show();
            }
        };

        startTimeDate.setOnClickListener(dateViewSelector);
        endTimeDate.setOnClickListener(dateViewSelector);
        startTimeTime.setOnClickListener(timeViewSelector);
        endTimeTime.setOnClickListener(timeViewSelector);

        editEntryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                JSONObject body = new JSONObject();
                try{
                    body.put("entry_id", entryToEdit.getEntryID());
                    body.put("category_name", categoryTextView.getText().toString());
                    body.put("start_date_time", startTimeDate.getText().toString() + " " + startTimeTime.getText().toString());
                    body.put("end_date_time", endTimeDate.getText().toString() + " " + endTimeTime.getText().toString());
                    body.put("note", noteTextView.getText().toString());
                }
                catch(Exception ignored){}
                final String UPDATE_ENTRY_URL = "http://66.103.121.23/api/update_entry.php";

                StringRequest updatePOSTRequest = new StringRequest(Request.Method.POST, UPDATE_ENTRY_URL, new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        if (response.equals("Entry updated.")) {
                            OverviewViewModel.getInstance().updateUserEntries(getApplicationContext());
                            OverviewViewModel.getInstance().updateOverviewGraph(getApplicationContext());
                            Toast.makeText(getApplicationContext(), "Entry successfully updated", Toast.LENGTH_LONG).show();
                        }
                        else {
                            Toast.makeText(getApplicationContext(), "Error editing entry. No changes made", Toast.LENGTH_LONG).show();
                        }
                        finish();
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(getApplicationContext(), "Server Error", Toast.LENGTH_LONG).show();
                        System.out.println("Server Error: " + error);
                        finish();
                    }
                }) {
                    @Override
                    public byte[] getBody() throws AuthFailureError {
                        return body.toString().getBytes();
                    }

                    @Override
                    public Map<String, String> getHeaders() throws AuthFailureError {
                        Map<String,String> headers = new HashMap<>();
                        headers.put("Cookie", LoggedInUser.getInstance().getSessionToken());
                        return headers;
                    }
                };

                NetworkManager.getInstance(getApplicationContext()).addToRequestQueue(updatePOSTRequest);
            }
        });
    }
}
