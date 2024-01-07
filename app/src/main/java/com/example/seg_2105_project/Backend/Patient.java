package com.example.seg_2105_project.Backend;

import com.google.firebase.database.DataSnapshot;

import java.util.ArrayList;
import java.util.Calendar;

public class Patient extends User {

    private long healthCardNumber;

    public Patient() {}
    public Patient(String firstName, String lastName, String email, String password, long phoneNumber, String address, long healthCardNumber) {
        super(firstName, lastName, email, password, phoneNumber, address);
        this.healthCardNumber = healthCardNumber;
    }

    /**GETTERS**/

    public long getHealthCardNumber() {
        return healthCardNumber;
    }

    /*
     * Gives a list of appointments that are either past or upcoming
     * @param  dataSnapshot                DataSnapshot of doctor information in firebase
     * @param  pastAppointment             boolean to indicate if it is a passed appointment or not
     * @param  currentDate                 Current Date
     * @return                             An ArrayList of appointments
     */
    public ArrayList<Appointment> getPatientAppointments(DataSnapshot snapshot, boolean pastAppointment, Calendar currentDate){

        ArrayList<Appointment> appointmentList = new ArrayList<>();

        for(DataSnapshot appointmentSnapshot : snapshot.getChildren()){
            Appointment appt = appointmentSnapshot.getValue(Appointment.class);
            if (appt.getPatient().getEmail().equals(this.getEmail())) {
                //searching for past past appointments
                if (pastAppointment) {
                    boolean passed = currentDate.after(appt.retrieveDateTime());

                    //add appointments to the list if they are approved and in the past
                    if (passed && appt.getStatus() == Status.APPROVED) {
                        appointmentList.add(appt);
                    }
                }
                //searching for upcoming appointments
                else {
                    boolean upcoming = currentDate.before(appt.retrieveDateTime());

                    //add appointments to the list if they are approved and upcoming
                    if (upcoming && appt.getStatus() == Status.APPROVED) {
                        appointmentList.add(appt);
                    }
                }
            }
        }

        return appointmentList;
    }

    /**SETTERS**/

    /*
    * Acts as a setter and changes the registration status in Firebase
     */
    public void updateRegistrationStatus(Status registrationStatus) {
        super.updateRegistrationStatus(registrationStatus);
        updateFirebase("Patients", "registrationStatus", registrationStatus, this);
    }

    /**OTHER METHODS**/

    /*
    Displays all the information of this patient
     */
    @Override
    public String display() {
        return super.display() +
                "\nHealth Card Number: " + healthCardNumber;
    }

    /**CLASS METHODS**/

    /*
    Reads through the database for patients and return Patient object if found
     */
    public static Patient getPatient(String email, String password, DataSnapshot dataSnapshot) {
        for (DataSnapshot patientSnapshot : dataSnapshot.getChildren()) {
            Patient patient = patientSnapshot.getValue(Patient.class);
            if (patient.getEmail().equals(email) && patient.getPassword().equals(password)) {
                return patient;
            }
        }
        return null;
    }

    /*
    Gives a list of patients with a certain registration status from firebase
    @param  status                      Registration status of patients
    @param  dataSnapshot                DataSnapshot of patient information in firebase
    @return                             An ArrayList of patients
    @throws IllegalArgumentException   if dataSnapshot is null or doesn't contain a snapshot for the patients
     */
    public static ArrayList<User> getPatients(Status status, DataSnapshot dataSnapshot) {

        ArrayList<User> patients = new ArrayList<User>();

        //Make sure dataSnapshot isn't null and contains the Patient path
        if (dataSnapshot.exists() && dataSnapshot.getRef().getKey().equals("Patients")) {
            for (DataSnapshot patient : dataSnapshot.getChildren()) {
                Patient p = patient.getValue(Patient.class);
                //Check registration status of patient and add it to list if it matches
                if (p.getRegistrationStatus() == status) {
                    patients.add(p);
                }
            }
        }
        else {
            throw new IllegalArgumentException("dataSnapshot should not be null and should be from the Patient path");
        }
        return patients;

    }

}
