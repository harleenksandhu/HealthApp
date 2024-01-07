package com.example.seg_2105_project.Frontend.PatientActivities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.view.View;
import android.widget.Button;
import android.widget.RatingBar;
import android.widget.TextView;

import com.example.seg_2105_project.Backend.Appointment;
import com.example.seg_2105_project.Backend.Doctor;
import com.example.seg_2105_project.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class PastAppointmentDisplay extends AppCompatActivity {

    RatingBar rating;
    Doctor doctor;
    Appointment appt;
    Button submitRating;
    TextView textView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_patient_past_appointment_display);

        appt = (Appointment) getIntent().getSerializableExtra("Appointment");
        //Get doctor to update rating if needed
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Doctors");
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                doctor = Doctor.getDoctor(appt.getDoctor().getEmail(), appt.getDoctor().getPassword(), snapshot);

                //setting the text with the appointment information
                TextView apptInfo = findViewById(R.id.appointmentInformation);
                String information = appt.getDateAndTime() + "\nDoctor: " + doctor.getLastName() + ", " + doctor.getFirstName();
                apptInfo.setText(information);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {}
        });

        rating = findViewById(R.id.ratingBar);
        submitRating = findViewById(R.id.submitRating);
        textView = findViewById(R.id.rateDoctorText);
        if(appt.isRated()){
            submitRating.setVisibility(View.INVISIBLE);
            rating.setVisibility(View.INVISIBLE);
            textView.setText("You have already rated this doctor");
        }

    }

    public void onClickSubmitRating(View view){
        Float ratingNumber = rating.getRating();
        appt.rateDoctor();
        doctor.updateRating(ratingNumber);

        submitRating.setVisibility(View.INVISIBLE);
        rating.setVisibility(View.INVISIBLE);
        textView.setText("You have rated this doctor!");
    }

    public void onClickBackButton(View view) {
        Intent intent = new Intent(getApplicationContext(), PastAppointments.class);
        intent.putExtra("Patient", getIntent().getSerializableExtra("Patient"));
        startActivity(intent);
    }

}