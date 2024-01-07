package com.example.seg_2105_project.Frontend;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.example.seg_2105_project.Backend.Administrator;
import com.example.seg_2105_project.Backend.Doctor;
import com.example.seg_2105_project.Backend.Patient;
import com.example.seg_2105_project.Backend.Status;
import com.example.seg_2105_project.Frontend.PatientActivities.PatientScreen;
import com.example.seg_2105_project.Frontend.AdminActivities.AdminScreen;
import com.example.seg_2105_project.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class SignIn extends AppCompatActivity {
    EditText email;
    EditText pswd;
    TextView signInError;
    Button signInButton;
    private boolean validLogin = true;
    FirebaseAuth auth;
    DataSnapshot patientDataSnapshot;
    DataSnapshot doctorDataSnapshot;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);
        email = findViewById(R.id.emailAddress);
        pswd = findViewById(R.id.password);
        signInButton = findViewById(R.id.signInButton);
        signInError = findViewById(R.id.signInError);

        auth = FirebaseAuth.getInstance();

        /*
        Get a reference to the database
         */
        final FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference patientRef = database.getReference("Patients");
        DatabaseReference doctorRef = database.getReference("Doctors");

        /*
        Read data from Firebase for Patients and Doctors
         */
        patientRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                /*
                Store the snapshot of patient data
                 */
                patientDataSnapshot = dataSnapshot;
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });

        doctorRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                /*
                Store the snapshot of doctor data
                 */
                doctorDataSnapshot = dataSnapshot;
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }

    /*
    Method is called when sign-in button is clicked. It verifies the information inputted by the user,
    outputs error messages accordingly, and redirects to the corresponding app homepage when the
    sign-in is successful, or displays a rejection message if registration is rejected.
    */
    public void onClickSignInButton(View view) {

        /*
        Hide any previous error message
         */
        signInError.setVisibility(View.GONE);

        /*
        Validate email and password input
         */
        if (email.getText().toString().isEmpty()) {
            validLogin = false;
            email.setError("Cannot be left blank");
        }
        if (pswd.getText().toString().isEmpty()) {
            validLogin = false;
            pswd.setError("Cannot be left blank");
        }

        if (validLogin) {

            /*
            Attempt to sign in using Firebase Authentication
             */
            auth.signInWithEmailAndPassword(email.getText().toString(), pswd.getText().toString())
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                                if (user != null  && user.isEmailVerified()) {

                                    /*
                                    Check if the user is an administrator
                                     */
                                    if (isAdmin(user.getEmail())) {

                                        /*
                                        Redirect to the AdminScreen
                                         */
                                        Intent intent = new Intent(getApplicationContext(), AdminScreen.class);
                                        startActivity(intent);
                                    } else {

                                        /*
                                        Retrieve Doctor and Patient data from Firebase
                                         */
                                        Doctor doctor = Doctor.getDoctor(user.getEmail(), pswd.getText().toString(), doctorDataSnapshot);
                                        Patient patient = Patient.getPatient(user.getEmail(), pswd.getText().toString(), patientDataSnapshot);

                                        if (doctor != null) {
                                            if (isApproved(doctor.getRegistrationStatus())) {

                                                /*
                                                Redirect to DoctorScreen when registration is approved
                                                 */
                                                Intent intent = new Intent(getApplicationContext(), com.example.seg_2105_project.Frontend.DoctorActivities.DoctorScreen.class);
                                                intent.putExtra("Doctor", doctor);
                                                startActivity(intent);
                                            } else if (isRejected(doctor.getRegistrationStatus())) {

                                                /*
                                                Display rejection message
                                                 */
                                                signInError.setText("Your registration was rejected. Contact the\nAdministrator at (613)-614-6123");
                                                signInError.setVisibility(View.VISIBLE);
                                            } else {

                                                /*
                                                Display message when registration is not approved
                                                 */
                                                signInError.setText("Your registration has not been approved yet.");
                                                signInError.setVisibility(View.VISIBLE);
                                            }
                                        } else if (patient != null) {
                                            if (isApproved(patient.getRegistrationStatus())) {

                                                /*
                                                Redirect to PatientScreen when registration is approved
                                                 */
                                                Intent intent = new Intent(getApplicationContext(), PatientScreen.class);
                                                intent.putExtra("Patient", patient);
                                                startActivity(intent);
                                            } else if (isRejected(patient.getRegistrationStatus())) {

                                                /*
                                                Display rejection message
                                                 */
                                                signInError.setText("Your registration was rejected. Contact the\nAdministrator at (613)-614-6123");
                                                signInError.setVisibility(View.VISIBLE);
                                            } else {

                                                /*
                                                Display message when registration is not approved
                                                 */
                                                signInError.setText("Your registration has not been approved yet.");
                                                signInError.setVisibility(View.VISIBLE);
                                            }
                                        } else {

                                            /*
                                            Display message when registration is not approved
                                             */
                                            signInError.setText("Your registration has not been approved yet.");
                                            signInError.setVisibility(View.VISIBLE);
                                        }
                                    }
                                }
                                else {
                                    /*
                                    Handle unverified user
                                    */
                                    signInError.setText("Your account hasn't been verified\nPlease check your email");
                                    signInError.setVisibility(View.VISIBLE);

                                }
                            } else {

                                /*
                                Handle invalid email or password
                                 */
                                signInError.setText("Invalid email or password");
                                signInError.setVisibility(View.VISIBLE);
                            }
                        }
                    });
        } else {
            validLogin = true;
        }
    }

    /*
    Check if the user is an administrator
     */
    private boolean isAdmin(String email) {
        return email.equals(Administrator.email);
    }

    /*
    Check if the registration status is "Approved"
     */
    private boolean isApproved(Status status) {
        return status == Status.APPROVED;
    }

    /*
    Check if the registration status is "Rejected"
    */
    private boolean isRejected(Status status) {
        return status == Status.REJECTED;
    }
}
