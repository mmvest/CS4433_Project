package com.orangeplasticcup.ocuptimemanagement.ui.home.main.overview;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.orangeplasticcup.ocuptimemanagement.R;
import com.orangeplasticcup.ocuptimemanagement.data.TimeEntry;

import java.util.List;
import java.util.Map;

public class ExpandableListAdapter extends BaseExpandableListAdapter {
    private Context context;
    private List<String> days;
    private Map<String, List<TimeEntry>> dayCollections;

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

        noteTextView.setText("Note: " + childEntry.getNote());
        categoryTextView.setText("Category: " + childEntry.getCategoryName());
        startTimeTextView.setText("Start time: " + childEntry.getStartTime());
        endTimeTextView.setText("End time: " + childEntry.getEndTime());

        editButtonTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

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
                                List<TimeEntry> child =
                                        dayCollections.get(days.get(groupPosition));
                                child.remove(childPosition);
                                notifyDataSetChanged();
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
