package com.example.seg_2105_project.Frontend.AdminActivities;

import com.example.seg_2105_project.Backend.Doctor;
import com.example.seg_2105_project.Backend.Patient;
import com.example.seg_2105_project.Backend.Status;
import com.example.seg_2105_project.Backend.User;
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

public class RejectedRegistrations extends AppCompatActivity {

    //Store users to display
    ArrayList<User> users = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rejected_registrations);

        //Get references to database
        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        DatabaseReference patientsRef = firebaseDatabase.getReference("Patients");
        DatabaseReference doctorsRef = firebaseDatabase.getReference("Doctors");

        //Read data
        patientsRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                users.addAll(Patient.getPatients(Status.REJECTED, snapshot));
                loadListView();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {}
        });
        doctorsRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                users.addAll(Doctor.getDoctors(Status.REJECTED, snapshot));
                loadListView();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {}
        });

        //Get user that was clicked on
        ListView listView = findViewById(R.id.listViewRejectedRegistrations);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView parent, View view, int position, long id) {
                User selectedUser = (User) listView.getItemAtPosition(position);
                //pass selectedUser to next screen
                Intent intent = new Intent(getApplicationContext(), RejectedUserInfoDisplay.class);
                intent.putExtra("User", selectedUser);
                startActivity(intent);
            }
        });
    }

    public void onClickBackButton(View view) {
        Intent intent = new Intent(getApplicationContext(), AdminScreen.class);
        startActivity(intent);
    }

    /*
    Populate list view with users
     */
    private void loadListView() {
        ListView listView = findViewById(R.id.listViewRejectedRegistrations);
        ArrayAdapter<User> arrayAdapterDoctor = new ArrayAdapter<User>(getApplicationContext(),
                android.R.layout.simple_list_item_single_choice, users);
        listView.setChoiceMode(AbsListView.CHOICE_MODE_SINGLE);
        listView.setAdapter(arrayAdapterDoctor);
    }

}