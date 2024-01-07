package com.example.seg_2105_project.Frontend.AdminActivities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.example.seg_2105_project.Frontend.WelcomeScreen;
import com.example.seg_2105_project.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class AdminScreen extends AppCompatActivity {

    TextView welcomeMessage;
    String name;///Admin name;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_screen);
        welcomeMessage = findViewById(R.id.welcomeMessage);
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        name = user.getDisplayName();
        welcomeMessage.setText("Welcome, you are signed in as Administrator!");
    }

    public void onClickSignOutButton(View view) {
        //Transition to home screen of app
        Intent intent = new Intent(getApplicationContext(), WelcomeScreen.class);
        startActivity(intent);

    }

    public void onClickInboxRegistrations(View view) {
        Intent intent = new Intent(getApplicationContext(), RegistrationsInbox.class);
        startActivity(intent);
    }

    public void onClickRejectedRegistrations(View view) {
        Intent intent = new Intent(getApplicationContext(), RejectedRegistrations.class);
        startActivity(intent);
    }
}