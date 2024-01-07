package com.example.seg_2105_project.Frontend.PatientActivities;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.example.seg_2105_project.Backend.Appointment;
import com.example.seg_2105_project.Backend.Status;
import com.example.seg_2105_project.R;

import java.util.ArrayList;
import java.util.Calendar;

public class customAppointmentAdapter extends BaseAdapter implements ListAdapter {
    private ArrayList<Appointment> list;
    private Context context;

    public customAppointmentAdapter(ArrayList<Appointment> list, Context context) {
        this.list = list;
        this.context = context;
    }

    @Override
    public int getCount(){
        return list.size();
    }

    @Override
    public Object getItem(int position){
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return list.get(position).getID();
    }

    @Override
    public View getView(int position, View view, ViewGroup viewGroup) {
        View viewConverted = view;
        if (viewConverted == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            viewConverted = inflater.inflate(R.layout.activity_upcoming_appointment, null);

        }

        // Handle TextView and display string from my list
        TextView listItemText = (TextView)viewConverted.findViewById(R.id.list_appointment_string);
        listItemText.setText((CharSequence) list.get(position).toString());

        // Handle button and add onClickListener

        Button deleteButton = (Button) viewConverted.findViewById(R.id.deleteButton);

        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar currentTime = Calendar.getInstance();
                Appointment chosenAppointment = list.get(position);

                long difference = chosenAppointment.retrieveDateTime().getTimeInMillis() - currentTime.getTimeInMillis();
                long cancellationThreshold = 60*60*1000;
                if (difference < cancellationThreshold) {
                    // Add a toast
                    Toast.makeText(context, "Cannot cancel appointment that is within the next hour", Toast.LENGTH_SHORT).show();
                } else {
                    // Delete appointment & update appointment COMPLETE
                    chosenAppointment.updateStatus(Status.REJECTED);
                    list.remove(position);
                    notifyDataSetChanged();
                }
            }
        });
        return viewConverted;
    }

}


