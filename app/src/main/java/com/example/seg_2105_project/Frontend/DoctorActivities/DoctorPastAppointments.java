package com.example.seg_2105_project.Frontend.DoctorActivities;

import com.example.seg_2105_project.Backend.Appointment;
import com.example.seg_2105_project.Backend.Doctor;
import com.example.seg_2105_project.R;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class DoctorPastAppointments extends AppCompatActivity {

    ArrayList<Appointment> appointments = new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_doctor_past_appointments);

        //obtaining references to the database
        FirebaseDatabase db = FirebaseDatabase.getInstance();
        DatabaseReference appointmentRef = db.getReference("Appointments");

        //retrieve data for doctors
        Doctor doctor = (Doctor) getIntent().getSerializableExtra("Doctor");
        appointmentRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                appointments.addAll(doctor.getDoctorAppointments(snapshot, true, Calendar.getInstance()));
                loadListView();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error){}
        });

        //Get user that was clicked on
        ListView listView = findViewById(R.id.listViewPastAppointments);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView parent, View view, int position, long id) {
                Appointment selectedAppointment = (Appointment) listView.getItemAtPosition(position);

                Intent intent = new Intent(getApplicationContext(), com.example.seg_2105_project.Frontend.DoctorActivities.PastAppointmentDisplay.class);
                intent.putExtra("Appointment", selectedAppointment);
                intent.putExtra("Doctor", doctor);
                startActivity(intent);
            }
        });




    }

    /**
     * Redirects to the Doctor screen when the back button is clicked
     */
    public void onClickBackButton(View view){
        Intent intent = new Intent(getApplicationContext(), DoctorScreen.class);
        intent.putExtra("Doctor", getIntent().getSerializableExtra("Doctor"));
        startActivity(intent);
    }

    /**
     * Adds all the past appointments to listview layout
     */
    private void loadListView() {
        ListView listView = findViewById(R.id.listViewPastAppointments);
        ArrayAdapter<Appointment> arrayAdapter = new ArrayAdapter<Appointment>(getApplicationContext(),
                android.R.layout.simple_list_item_single_choice, appointments);
        listView.setChoiceMode(AbsListView.CHOICE_MODE_SINGLE);
        listView.setAdapter(arrayAdapter);
    }
}