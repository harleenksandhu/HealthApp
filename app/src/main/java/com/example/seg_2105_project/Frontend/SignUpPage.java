package com.example.seg_2105_project.Frontend;

import com.example.seg_2105_project.Frontend.PatientActivities.*;
import com.example.seg_2105_project.R;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;


public class SignUpPage extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up_page);

        // Initialize buttons
        Button patientButton = findViewById(R.id.button1);
        Button doctorButton = findViewById(R.id.button2);

        // Set click listeners for buttons
        patientButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onClick_PatientSignUp(v);
            }
        });

        doctorButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onClick_DoctorSignUp(v);
            }
        });
    }

    // Method called when "Patient" button is clicked
    public void onClick_PatientSignUp(View view) {
        // Start the PatientSignUp
        Intent intent = new Intent(this, PatientSignUp.class);
        startActivity(intent);
    }

    // Method called when "Doctor" button is clicked
    public void onClick_DoctorSignUp(View view) {
        // Start the DoctorSignUp
        Intent intent = new Intent(this, com.example.seg_2105_project.Frontend.DoctorActivities.Doctor_SignUp.class);
        startActivity(intent);
    }

    public void onClickBackButton(View view) {
        Intent intent = new Intent(getApplicationContext(), WelcomeScreen.class);
        startActivity(intent);
    }
}
