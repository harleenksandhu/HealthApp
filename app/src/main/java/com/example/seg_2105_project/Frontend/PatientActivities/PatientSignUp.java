package com.example.seg_2105_project.Frontend.PatientActivities;

import com.example.seg_2105_project.Backend.Patient;
import com.example.seg_2105_project.Frontend.SignIn;
import com.example.seg_2105_project.Frontend.SignUpPage;
import com.example.seg_2105_project.R;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.view.*;
import android.widget.*;

import android.os.Bundle;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class PatientSignUp extends AppCompatActivity {

    private FirebaseAuth auth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_patient_sign_up);
        auth = FirebaseAuth.getInstance();

    }

    public void onClickSignUp(View view) {

        //Get inputs of text views
        EditText input = findViewById(R.id.editTextFirstName);
        String firstName = input.getText().toString();

        input = findViewById(R.id.editTextLastName);
        String lastName = input.getText().toString();

        input = findViewById(R.id.editTextEmailAddress);
        String email = input.getText().toString();

        input = findViewById(R.id.editTextPassword);
        String password = input.getText().toString();

        input = findViewById(R.id.editTextPostalAddress);
        String address = input.getText().toString();

        input = findViewById(R.id.editTextPhone);
        String phoneNumberStr = input.getText().toString();
        long phoneNumber;

        input = findViewById(R.id.editTextHealthNumber);
        String healthNumberStr = input.getText().toString();
        long healthNumber;

        //Check if numerical text views are empty
        phoneNumber =  !phoneNumberStr.equals("")  ? Long.parseLong(phoneNumberStr)  : -1;
        healthNumber = !healthNumberStr.equals("") ? Long.parseLong(healthNumberStr) : -1;

        //Create patient object
        Patient patient = new Patient(firstName, lastName, email, password, phoneNumber, address, healthNumber);

        //Validate input
        if (inputValid(view, patient)) {

            //Authenticate user
            userAuthentication(patient);

        }

    }

    public void onClickBackButton(View view) {
        Intent intent = new Intent(getApplicationContext(), SignUpPage.class);
        startActivity(intent);
    }

    /*
     * Validates user input
     * @param view View of current activity
     * @param user Patient object that contains user info that they entered
    */
    private boolean inputValid(View view, Patient user) {

        boolean isValid = true;

        //Check if each input is correct or empty
        if (user.getFirstName().equals("")) {
            //Set visibility of error message to visible
            TextView text = findViewById(R.id.textViewFirstNameError);
            text.setVisibility(view.VISIBLE);

            //Set visibility of label to invisible
            text = findViewById(R.id.textViewFirstName);
            text.setVisibility(view.INVISIBLE);

            isValid = false;
        }
        else {
            //Reset visibilities
            TextView text = findViewById(R.id.textViewFirstName);
            text.setVisibility(view.VISIBLE);

            text = findViewById(R.id.textViewFirstNameError);
            text.setVisibility(view.INVISIBLE);

        }
        if (user.getLastName().equals("")) {
            //Set visibility of error message to visible
            TextView text = findViewById(R.id.textViewLastNameError);
            text.setVisibility(view.VISIBLE);

            //Set visibility of label to invisible
            text = findViewById(R.id.textViewLastName);
            text.setVisibility(view.INVISIBLE);

            isValid = false;
        }
        else {
            //Reset visibilities
            TextView text = findViewById(R.id.textViewLastName);
            text.setVisibility(view.VISIBLE);

            text = findViewById(R.id.textViewLastNameError);
            text.setVisibility(view.INVISIBLE);

        }
        if (user.getEmail().equals("") || !user.getEmail().contains("@")) {
            //Set visibility of error message to visible
            TextView text = findViewById(R.id.textViewEmailError);
            text.setVisibility(view.VISIBLE);

            //Set visibility of label to invisible
            text = findViewById(R.id.textViewEmail);
            text.setVisibility(view.INVISIBLE);

            //Clear input
            EditText input = findViewById(R.id.editTextEmailAddress);
            input.setText("");

            isValid = false;
        }
        else {
            //Reset visibilities
            TextView text = findViewById(R.id.textViewEmail);
            text.setVisibility(view.VISIBLE);

            text = findViewById(R.id.textViewEmailError);
            text.setVisibility(view.INVISIBLE);
        }
        if (user.getPassword().equals("") || user.getPassword().length() < 6) {
            //Set visibility of error message to visible
            TextView text = findViewById(R.id.textViewPasswordError);
            text.setVisibility(view.VISIBLE);

            //Set visibility of label to invisible
            text = findViewById(R.id.textViewPassword);
            text.setVisibility(view.INVISIBLE);

            isValid = false;
        }
        else {
            //Reset visibilities
            TextView text = findViewById(R.id.textViewPassword);
            text.setVisibility(view.VISIBLE);

            text = findViewById(R.id.textViewPasswordError);
            text.setVisibility(view.INVISIBLE);
        }
        if (user.getPhoneNumber() == -1 || user.getPhoneNumber() < 1000000000
                || user.getPhoneNumber() > (Long.parseLong("9999999999"))) {
            //Set visibility of error message to visible
            TextView text = findViewById(R.id.textViewPhoneNumberError);
            text.setVisibility(view.VISIBLE);

            //Set visibility of label to invisible
            text = findViewById(R.id.textViewPhoneNumber);
            text.setVisibility(view.INVISIBLE);

            isValid = false;
        }
        else {
            //Reset visibilities
            TextView text = findViewById(R.id.textViewPhoneNumber);
            text.setVisibility(view.VISIBLE);

            text = findViewById(R.id.textViewPhoneNumberError);
            text.setVisibility(view.INVISIBLE);

        }
        if (user.getAddress().equals("")) {
            //Set visibility of error message to visible
            TextView text = findViewById(R.id.textViewAddressError);
            text.setVisibility(view.VISIBLE);

            //Set visibility of label to invisible
            text = findViewById(R.id.textViewAddress);
            text.setVisibility(view.INVISIBLE);

            isValid = false;
        }
        else {
            //Reset visibilities
            TextView text = findViewById(R.id.textViewAddress);
            text.setVisibility(view.VISIBLE);

            text = findViewById(R.id.textViewAddressError);
            text.setVisibility(view.INVISIBLE);
        }
        if (user.getHealthCardNumber() == -1 || user.getHealthCardNumber() < 1000000000
                || user.getHealthCardNumber() > Long.parseLong("9999999999")) {
            //Set visibility of error message to visible
            TextView text = findViewById(R.id.textViewHealthNumberError);
            text.setVisibility(view.VISIBLE);

            //Set visibility of label to invisible
            text = findViewById(R.id.textViewHealthNumber);
            text.setVisibility(view.INVISIBLE);

            isValid = false;
        }
        else {
            //Reset visibilities
            TextView text = findViewById(R.id.textViewHealthNumber);
            text.setVisibility(view.VISIBLE);

            text = findViewById(R.id.textViewHealthNumberError);
            text.setVisibility(view.INVISIBLE);
        }
        return isValid;

    }

    /*
    Authenticate user info
    @param patient Object that contains information on user for authentication
     */
    private void userAuthentication(Patient patient) {
        auth.createUserWithEmailAndPassword(patient.getEmail(), patient.getPassword())
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(Task<AuthResult> task) {
                        //Check if authentication was successful
                        if (task.isSuccessful()) {
                            verifyEmail(patient);

                        } else {
                            // If unsuccessful, display a message to the user.
                            Toast.makeText(PatientSignUp.this, "This email already has an account",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    /*
    Send email to user to verify their email
     */
    private void verifyEmail(Patient patient) {
        FirebaseUser user = auth.getCurrentUser();
        //Send email to user
        user.sendEmailVerification()
                .addOnCompleteListener(this, new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(Task<Void> task) {
                        //Send message to user that email has been sent and change screen
                        Toast.makeText(getApplicationContext(), "Verification email sent",
                                Toast.LENGTH_SHORT).show();

                        //Set user's name in their profile
                        if(user != null){
                            UserProfileChangeRequest profileUpdate = new UserProfileChangeRequest.Builder()
                                    .setDisplayName(patient.getFirstName()).build();

                            user.updateProfile(profileUpdate).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(Task<Void> task) { }
                            });
                        }

                        //Write to database
                        FirebaseDatabase database = FirebaseDatabase.getInstance();
                        DatabaseReference databaseRef = database.getReference("Patients");
                        databaseRef.push().setValue(patient);

                        Intent intent = new Intent(getApplicationContext(), SignIn.class);
                        startActivity(intent);

                    }
                });
    }

}