package com.example.seg_2105_project.Frontend.PatientActivities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Filter;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.Toast;

import com.example.seg_2105_project.Backend.Appointment;
import com.example.seg_2105_project.Backend.Doctor;
import com.example.seg_2105_project.Frontend.DoctorActivities.UpcomingAppointmentDisplay;
import com.example.seg_2105_project.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class PatientBookAppointment extends AppCompatActivity {

    ListView listViewDoctors;
    ArrayList<Doctor> allDoctors = new ArrayList<>();
    ArrayAdapter<Doctor> arrayAdapterDoctor;
    boolean searchBySpecialty = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_patient_book_appointment2);

        listViewDoctors = findViewById(R.id.listViewDoctors);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //obtaining references to the database
        FirebaseDatabase db = FirebaseDatabase.getInstance();
        DatabaseReference doctorsRef = db.getReference("Doctors");

        arrayAdapterDoctor = new ArrayAdapter<Doctor>(this, android.R.layout.simple_list_item_single_choice, new ArrayList<Doctor>()) {
            @NonNull
            @Override
            public Filter getFilter() {
                return new Filter() {
                    @Override
                    protected FilterResults performFiltering(CharSequence constraint) {
                        String filterPattern = constraint.toString().toLowerCase().trim();
                        ArrayList<Doctor> filteredDoctors = new ArrayList<>();

                        if (searchBySpecialty) {
                            //Check specialties of doctors
                            for (Doctor doctor : allDoctors) {
                                if (doctor.getSpecialties() != null) {
                                    for (String specialty : doctor.getSpecialties()) {
                                        if (specialty.toLowerCase().contains(filterPattern)) {
                                            filteredDoctors.add(doctor);
                                            break;
                                        }
                                    }
                                }
                            }
                        }
                        else {
                            //Check doctor names
                            for (Doctor doctor : allDoctors) {
                                String name = doctor.getFirstName().toLowerCase() + doctor.getLastName().toLowerCase();
                                if (name.contains(filterPattern)) {
                                    filteredDoctors.add(doctor);
                                }
                            }
                        }

                        FilterResults results = new FilterResults();
                        results.values = filteredDoctors;
                        results.count = filteredDoctors.size();
                        return results;
                    }

                    @Override
                    protected void publishResults(CharSequence constraint, FilterResults results) {
                        clear();
                        addAll((ArrayList<Doctor>) results.values);
                        notifyDataSetChanged();
                    }
                };
            }
        };

        listViewDoctors.setChoiceMode(AbsListView.CHOICE_MODE_SINGLE);
        listViewDoctors.setAdapter(arrayAdapterDoctor);

        //Get doctors from firebase
        doctorsRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    allDoctors.clear();
                    for (DataSnapshot doctorSnapshot : snapshot.getChildren()) {
                        Doctor doctor = doctorSnapshot.getValue(Doctor.class);

                        if (doctor != null) {
                            //Adding all the doctor specialties to the list
                            if (doctor.getSpecialties() != null) {
                                // The doctor has the targeted specialty
                                allDoctors.add(doctor);
                            }
                        }
                    }
                    arrayAdapterDoctor.notifyDataSetChanged();
                } else {
                    // No doctors found in the database
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {}
        });

        //Get doctor that was clicked on
        listViewDoctors.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView parent, View view, int position, long id) {
                Doctor doctor = (Doctor) listViewDoctors.getItemAtPosition(position);
                //add intent for next class

                Intent intent = new Intent(getApplicationContext(), BookAppointment.class);
                intent.putExtra("Doctor", doctor);
                intent.putExtra("Patient", getIntent().getSerializableExtra("Patient"));
                startActivity(intent);
            }
        });
    }

    public void onClickBackButton(View view) {
        Intent intent = new Intent(getApplicationContext(), PatientScreen.class);
        intent.putExtra("Patient", getIntent().getSerializableExtra("Patient"));
        startActivity(intent);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);

        MenuItem menuItem = menu.findItem(R.id.action_search);
        SearchView searchView = (SearchView) menuItem.getActionView();
        searchView.setQueryHint("Search");

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                arrayAdapterDoctor.getFilter().filter(s);

                //Go directly to date selection if only one doctor appears for name search
                if (listViewDoctors.getCount() == 1 && !searchBySpecialty) {
                    Doctor doctor = (Doctor) listViewDoctors.getItemAtPosition(0);
                    Intent intent = new Intent(getApplicationContext(), BookAppointment.class);
                    intent.putExtra("Doctor", doctor);
                    intent.putExtra("Patient", getIntent().getSerializableExtra("Patient"));
                    startActivity(intent);
                }

                return false;

            }

            @Override
            public boolean onQueryTextChange(String s) {
                arrayAdapterDoctor.getFilter().filter(s);
                return false;
            }
        });

        return super.onCreateOptionsMenu(menu);

    }

    public void onClickChangeSearch(View view) {

        Button button = findViewById(R.id.buttonChangeSearch);

        //Change search filter
        if (searchBySpecialty) {
            button.setText("Search by Specialty");
            searchBySpecialty = false;
        }

        else {
            button.setText("Search by Name");
            searchBySpecialty = true;
        }

    }


}