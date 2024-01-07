package com.example.seg_2105_project.Frontend.DoctorActivities;

import com.example.seg_2105_project.Backend.Appointment;
import com.example.seg_2105_project.Backend.Status;
import com.example.seg_2105_project.R;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class UpcomingAppointmentDisplay extends AppCompatActivity {

    Appointment selectedAppt;
    TextView patientInfo;
    DataSnapshot dataSnapshot;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upcoming_appointment_display);

        selectedAppt = (Appointment) getIntent().getSerializableExtra("Appointment");
        patientInfo = (TextView) findViewById(R.id.patientInformation);
        patientInfo.setText(selectedAppt.getPatient().display());

        //Set visibility of buttons
        if (selectedAppt.getStatus() == Status.APPROVED) {
            Button acceptButton = findViewById(R.id.acceptAppointment);
            acceptButton.setVisibility(View.INVISIBLE);
        }

    }

    public void onClickAcceptAppointment(View view){
        selectedAppt.updateStatus(Status.APPROVED);

        //go back to upcoming appointments list
        Intent intent = new Intent(this, DoctorUpcomingAppointments.class);
        intent.putExtra("Doctor", getIntent().getSerializableExtra("Doctor"));
        startActivity(intent);
    }

    public void onClickRejectAppointment(View view){
        selectedAppt.updateStatus(Status.REJECTED);

        //go back to upcoming appointments list
        Intent intent = new Intent(this, DoctorUpcomingAppointments.class);
        intent.putExtra("Doctor", getIntent().getSerializableExtra("Doctor"));
        startActivity(intent);
    }

    public void onClickBackButton(View view){
        Intent intent = new Intent(this, DoctorUpcomingAppointments.class);
        intent.putExtra("Doctor", getIntent().getSerializableExtra("Doctor"));
        startActivity(intent);
    }

}