package com.example.seg_2105_project.Frontend.PatientActivities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.example.seg_2105_project.Backend.Appointment;
import com.example.seg_2105_project.Backend.Patient;
import com.example.seg_2105_project.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Calendar;

public class PastAppointments extends AppCompatActivity {
    ArrayList<Appointment> pastAppointments;
    Patient patient;
    ListView listView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_patient_past_appointments);

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Appointments");

        listView = (ListView) findViewById(R.id.listViewPatientPastAppointments);
        pastAppointments = new ArrayList<>();
        patient = (Patient) getIntent().getSerializableExtra("Patient");

        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                pastAppointments.addAll(patient.getPatientAppointments(snapshot, true, Calendar.getInstance()));
                loadListView();
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {}
        });


        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Appointment selectedAppointment = (Appointment) listView.getItemAtPosition(i);
                Intent intent = new Intent(getApplicationContext(), PastAppointmentDisplay.class);
                intent.putExtra("Appointment", selectedAppointment);
                intent.putExtra("Patient", patient);
                startActivity(intent);
            }
        });
    }

    public void onClickBackButton(View view) {
        Intent intent = new Intent(getApplicationContext(), PatientScreen.class);
        intent.putExtra("Patient", patient);
        startActivity(intent);
    }

    private void loadListView() {
        ArrayAdapter<Appointment> arrayAdapterDoctor = new ArrayAdapter<Appointment>(getApplicationContext(),
                android.R.layout.simple_list_item_single_choice, pastAppointments);
        listView.setChoiceMode(AbsListView.CHOICE_MODE_SINGLE);
        listView.setAdapter(arrayAdapterDoctor);
    }

}