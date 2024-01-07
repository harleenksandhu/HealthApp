package com.example.seg_2105_project.Frontend.DoctorActivities;

import com.example.seg_2105_project.Backend.Doctor;
import com.example.seg_2105_project.Frontend.SignIn;
import com.example.seg_2105_project.Frontend.SignUpPage;
import com.example.seg_2105_project.R;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

public class Doctor_SignUp extends AppCompatActivity {

    private FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_doctor_sign_up);
        auth = FirebaseAuth.getInstance();
    }

    public void onClickSignUp(View view) {

        //Get inputs of text views
        EditText input = findViewById(R.id.first_name_enter);
        String firstName = input.getText().toString();

        input = findViewById(R.id.last_name_enter);
        String lastName = input.getText().toString();

        input = findViewById(R.id.email_address_enter);
        String email = input.getText().toString();

        input = findViewById(R.id.account_password_enter);
        String password = input.getText().toString();

        input = findViewById(R.id.address_enter);
        String address = input.getText().toString();

        input = findViewById(R.id.phone_number_enter);
        String phoneNumberStr = input.getText().toString();
        long phoneNumber;

        input = findViewById(R.id.employee_num_enter);
        String employeeNumberStr = input.getText().toString();
        int employeeNumber;

        input = findViewById(R.id.specialty_enter);
        String[] specialtiesArr = input.getText().toString().split(",");
        ArrayList<String> specialties = new ArrayList<String>();

        //Add specialties from array to list
        for (int i = 0; i < specialtiesArr.length; i ++) {
            specialties.add(specialtiesArr[i]);
        }

        //Check if numerical text views are empty
        phoneNumber =  !phoneNumberStr.equals("")  ? Long.parseLong(phoneNumberStr)  : -1;
        employeeNumber = !employeeNumberStr.equals("")  && Long.parseLong(employeeNumberStr) < 999999999
                        ? Integer.parseInt(employeeNumberStr)  : -1;

        //Create patient object
        Doctor doctor = new Doctor(firstName, lastName, email, password, phoneNumber, address, employeeNumber, specialties);

        //Validate input
        if (inputValid(view, doctor)) {

            //Authenticate user
            userAuthentication(doctor);

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
    private boolean inputValid(View view, Doctor user) {

        boolean isValid = true;

        //Check if each input is correct or empty
        if (user.getFirstName().equals("")) {
            //Set visibility of error message to visible
            TextView text = findViewById(R.id.textViewFirstNameError);
            text.setVisibility(view.VISIBLE);

            //Set visibility of label to invisible
            text = findViewById(R.id.first_name);
            text.setVisibility(view.INVISIBLE);

            isValid = false;
        }
        else {
            //Reset visibilities
            TextView text = findViewById(R.id.first_name);
            text.setVisibility(view.VISIBLE);

            text = findViewById(R.id.textViewFirstNameError);
            text.setVisibility(view.INVISIBLE);

        }
        if (user.getLastName().equals("")) {
            //Set visibility of error message to visible
            TextView text = findViewById(R.id.textViewLastNameError);
            text.setVisibility(view.VISIBLE);

            //Set visibility of label to invisible
            text = findViewById(R.id.last_name);
            text.setVisibility(view.INVISIBLE);

            isValid = false;
        }
        else {
            //Reset visibilities
            TextView text = findViewById(R.id.last_name);
            text.setVisibility(view.VISIBLE);

            text = findViewById(R.id.textViewLastNameError);
            text.setVisibility(view.INVISIBLE);

        }
        if (user.getEmail().equals("") || !user.getEmail().contains("@")) {
            //Set visibility of error message to visible
            TextView text = findViewById(R.id.textViewEmailError);
            text.setVisibility(view.VISIBLE);

            //Set visibility of label to invisible
            text = findViewById(R.id.email_address);
            text.setVisibility(view.INVISIBLE);

            //Clear input
            EditText input = findViewById(R.id.email_address_enter);
            input.setText("");

            isValid = false;
        }
        else {
            //Reset visibilities
            TextView text = findViewById(R.id.email_address);
            text.setVisibility(view.VISIBLE);

            text = findViewById(R.id.textViewEmailError);
            text.setVisibility(view.INVISIBLE);
        }
        if (user.getPassword().equals("") || user.getPassword().length() < 6) {
            //Set visibility of error message to visible
            TextView text = findViewById(R.id.textViewPasswordError);
            text.setVisibility(view.VISIBLE);

            //Set visibility of label to invisible
            text = findViewById(R.id.account_pass);
            text.setVisibility(view.INVISIBLE);

            isValid = false;
        }
        else {
            //Reset visibilities
            TextView text = findViewById(R.id.account_pass);
            text.setVisibility(view.VISIBLE);

            text = findViewById(R.id.textViewPasswordError);
            text.setVisibility(view.INVISIBLE);
        }
        if (user.getPhoneNumber() == -1 || user.getPhoneNumber() < 1000000000
                || user.getPhoneNumber() > Long.parseLong("9999999999")) {
            //Set visibility of error message to visible
            TextView text = findViewById(R.id.textViewPhoneNumberError);
            text.setVisibility(view.VISIBLE);

            //Set visibility of label to invisible
            text = findViewById(R.id.phone_number);
            text.setVisibility(view.INVISIBLE);

            isValid = false;
        }
        else {
            //Reset visibilities
            TextView text = findViewById(R.id.phone_number);
            text.setVisibility(view.VISIBLE);

            text = findViewById(R.id.textViewPhoneNumberError);
            text.setVisibility(view.INVISIBLE);

        }
        if (user.getAddress().equals("")) {
            //Set visibility of error message to visible
            TextView text = findViewById(R.id.textViewAddressError);
            text.setVisibility(view.VISIBLE);

            //Set visibility of label to invisible
            text = findViewById(R.id.address);
            text.setVisibility(view.INVISIBLE);

            isValid = false;
        }
        else {
            //Reset visibilities
            TextView text = findViewById(R.id.address);
            text.setVisibility(view.VISIBLE);

            text = findViewById(R.id.textViewAddressError);
            text.setVisibility(view.INVISIBLE);
        }
        if (user.getEmployeeNumber()== -1 || user.getEmployeeNumber() < 100000000
                || user.getEmployeeNumber() > 999999999) {
            //Set visibility of error message to visible
            TextView text = findViewById(R.id.textViewEmployeeNumberError);
            text.setVisibility(view.VISIBLE);

            //Set visibility of label to invisible
            text = findViewById(R.id.employee_num);
            text.setVisibility(view.INVISIBLE);

            isValid = false;
        }
        else {
            //Reset visibilities
            TextView text = findViewById(R.id.employee_num);
            text.setVisibility(view.VISIBLE);

            text = findViewById(R.id.textViewEmployeeNumberError);
            text.setVisibility(view.INVISIBLE);
        }
        if (user.getSpecialties().get(0).equals("")) {
            //Set visibility of error message to visible
            TextView text = findViewById(R.id.textViewSpecialtyError);
            text.setVisibility(view.VISIBLE);

            //Set visibility of label to invisible
            text = findViewById(R.id.specialty);
            text.setVisibility(view.INVISIBLE);

            isValid = false;
        }
        else {
            //Reset visibilities
            TextView text = findViewById(R.id.specialty);
            text.setVisibility(view.VISIBLE);

            text = findViewById(R.id.textViewSpecialtyError);
            text.setVisibility(view.INVISIBLE);
        }
        return isValid;

    }

    /*
    Authenticate user info
     */
    private void userAuthentication(Doctor doctor) {
        auth.createUserWithEmailAndPassword(doctor.getEmail(), doctor.getPassword())
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(Task<AuthResult> task) {
                        //Check if authentication was successful
                        if (task.isSuccessful()) {
                            verifyEmail(doctor);

                        } else {
                            // If unsuccessful, display a message to the user.
                            Toast.makeText(Doctor_SignUp.this, "This email already has an account",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    /*
    Send email to user to verify their email
     */
    private void verifyEmail(Doctor doctor) {
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
                                    .setDisplayName(doctor.getFirstName()).build();

                            user.updateProfile(profileUpdate).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(Task<Void> task) { }
                            });
                        }

                        //Write to database
                        FirebaseDatabase database = FirebaseDatabase.getInstance();
                        DatabaseReference databaseRef = database.getReference("Doctors");
                        databaseRef.push().setValue(doctor);

                        Intent intent = new Intent(getApplicationContext(), SignIn.class);
                        startActivity(intent);

                    }
                });
    }

}
