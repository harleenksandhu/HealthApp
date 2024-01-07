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


public class RegistrationsInbox extends AppCompatActivity {
    ArrayList<User> usersList = new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registrations_inbox);

        //obtaining references to the database
        FirebaseDatabase db = FirebaseDatabase.getInstance();
        DatabaseReference patientRef = db.getReference("Patients");
        DatabaseReference doctorRef = db.getReference("Doctors");

        //retrieve data for patients
        patientRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                //adds all patients with pending status to list
                usersList.addAll(Patient.getPatients(Status.PENDING, snapshot));
                loadListView();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {}
        });

        //retrieve data for doctors
        doctorRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                usersList.addAll(Doctor.getDoctors(Status.PENDING, snapshot));
                loadListView();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error){}
        });

        //Get user that was clicked on
        ListView listView = findViewById(R.id.listViewRegistrationRequests);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView parent, View view, int position, long id) {
                User selectedUser = (User) listView.getItemAtPosition(position);

                Intent intent = new Intent(getApplicationContext(), UserInfoDisplay.class);
                intent.putExtra("User", selectedUser);
                startActivity(intent);
            }
        });
    }

    /**
     * Adds all the pending users to the list view in the layout
     */
    private void loadListView(){
        ListView listView = findViewById(R.id.listViewRegistrationRequests);
        ArrayAdapter<User> arrayAdapterDoctor = new ArrayAdapter<User>(getApplicationContext(),
                android.R.layout.simple_list_item_single_choice, usersList);
        listView.setChoiceMode(AbsListView.CHOICE_MODE_SINGLE);
        listView.setAdapter(arrayAdapterDoctor);
    }

    /**
     * Redirects to the admin screen when the back button is clicked
     */
    public void onClickBackButton(View view){
        Intent intent = new Intent(getApplicationContext(), AdminScreen.class);
        startActivity(intent);
    }


}