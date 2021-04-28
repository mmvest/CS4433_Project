package com.orangeplasticcup.ocuptimemanagement.ui.home.main.overview;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.orangeplasticcup.ocuptimemanagement.R;
import com.orangeplasticcup.ocuptimemanagement.data.TimeEntry;
import com.orangeplasticcup.ocuptimemanagement.data.model.LoggedInUser;
import com.orangeplasticcup.ocuptimemanagement.networking.NetworkManager;
import com.orangeplasticcup.ocuptimemanagement.ui.home.main.overview.editEntry.EditEntryActivity;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;

public class ExpandableListAdapter extends BaseExpandableListAdapter {
    private final Context context;
    private final List<String> days;
    private final Map<String, List<TimeEntry>> dayCollections;

    public ExpandableListAdapter(Context context, List<String> days, Map<String, List<TimeEntry>> dayCollections) {
        this.context = context;
        this.days = days;
        this.dayCollections = dayCollections;
    }

    @Override
    public int getGroupCount() {
        return days.size();
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return dayCollections.get(days.get(groupPosition)).size();
    }

    @Override
    public Object getGroup(int groupPosition) {
        return days.get(groupPosition);
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        return dayCollections.get(days.get(groupPosition)).get(childPosition);
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
        String dayName = (String) getGroup(groupPosition);
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.group_item, null);
        }

        TextView day = convertView.findViewById(R.id.dayTag);
        day.setText(dayName);
        return convertView;
    }

    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
        final TimeEntry childEntry = (TimeEntry) getChild(groupPosition, childPosition);

        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE); // This was changed from tutorial
            convertView = inflater.inflate(R.layout.child_item, null);
        }

        TextView noteTextView = convertView.findViewById(R.id.note);
        TextView categoryTextView = convertView.findViewById(R.id.category);
        TextView startTimeTextView = convertView.findViewById(R.id.startTime);
        TextView endTimeTextView = convertView.findViewById(R.id.endTime);
        FloatingActionButton editButtonTextView = convertView.findViewById(R.id.edit);
        FloatingActionButton deleteButton = convertView.findViewById(R.id.delete);

        BiConsumer<TimeEntry, Integer> setField = (entry, val) -> {
            noteTextView.setText("Note: " + childEntry.getNote());
            categoryTextView.setText("Category: " + childEntry.getCategoryName());
            startTimeTextView.setText("Start time: " + childEntry.getStartTime());
            endTimeTextView.setText("End time: " + childEntry.getEndTime());
        };

        setField.accept(childEntry, null);

        View finalConvertView = convertView;
        editButtonTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TimeEntry entry = (TimeEntry) getChild(groupPosition, childPosition);
                EditEntryActivity.setEditableEntry(entry);
                Intent editActivity = new Intent(finalConvertView.getContext(), EditEntryActivity.class);
                finalConvertView.getContext().startActivity(editActivity);
            }
        });

        deleteButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setMessage("Do you want to remove?");
                builder.setCancelable(false);
                builder.setPositiveButton("Yes",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                TimeEntry entryToDelete = dayCollections.get(days.get(groupPosition)).get(childPosition);

                                JSONObject body = new JSONObject();
                                try {
                                    body.put("entry_id", entryToDelete.getEntryID());
                                }
                                catch(Exception ignored){}

                                final String DELETE_ENTRY_URL = "http://66.103.121.23/api/delete_entry.php";
                                StringRequest deletePOSTRequest = new StringRequest(Request.Method.POST, DELETE_ENTRY_URL, new Response.Listener<String>() {
                                    @Override
                                    public void onResponse(String response) {
                                        if(response.equals("Entry deleted.")) {
                                            OverviewViewModel.getInstance().updateUserEntries(context.getApplicationContext());
                                            OverviewViewModel.getInstance().updateOverviewGraph(context.getApplicationContext());
                                            Toast.makeText(context.getApplicationContext(), "Successfully deleted entry", Toast.LENGTH_LONG).show();
                                        }
                                        else {
                                            System.out.println("Delete Server Response: " + response);
                                            Toast.makeText(context.getApplicationContext(), "Error deleting entry. No changes made", Toast.LENGTH_LONG).show();
                                        }
                                    }
                                }, new Response.ErrorListener() {
                                    @Override
                                    public void onErrorResponse(VolleyError error) {
                                        System.out.println("Server Error: " + error);
                                        Toast.makeText(context.getApplicationContext(), "Server Error. Please try again", Toast.LENGTH_LONG).show();
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

                                NetworkManager.getInstance(context.getApplicationContext()).addToRequestQueue(deletePOSTRequest);
                            }
                        });
                builder.setNegativeButton("No",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        });
                AlertDialog alertDialog = builder.create();
                alertDialog.show();
            }
        });

        return convertView;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }
}
