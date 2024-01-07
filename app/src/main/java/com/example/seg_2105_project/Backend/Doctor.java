package com.example.seg_2105_project.Backend;

import androidx.annotation.NonNull;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Map;

public class Doctor extends User {
    private int employeeNumber;
    private ArrayList<String> specialties;
    private ArrayList<Shift> shifts;
    private boolean autoApprove;
    private float rating;
    private int numOfRatings;

    public Doctor() {}
    public Doctor(String firstName, String lastName, String email, String password, long phoneNumber, String address, int employee_number, ArrayList<String> specialties) {
        super(firstName, lastName, email, password, phoneNumber, address);
        this.employeeNumber = employee_number;
        this.specialties = specialties;
        this.shifts = new ArrayList<>();
        this.autoApprove = false;
        numOfRatings = 0;
        rating = 0;
    }

    /**GETTERS**/
    public int getEmployeeNumber() { return employeeNumber; }
    public ArrayList<String> getSpecialties() { return specialties; }
    public ArrayList<Shift> getShifts() { return shifts; }
    public boolean getAutoApprove() { return autoApprove; }
    public float getRating(){
        return rating;
    }
    public float getNumOfRatings() { return numOfRatings; }

    /*
     * Gives a list of appointments that are either past or upcoming
     * @param  dataSnapshot                DataSnapshot of doctor information in firebase
     * @param  passed                      boolean to indicate if it is a passed appointment or not
     * @param  currentDate                 Current Date
     * @return                             An ArrayList of appointments
     */
    public ArrayList<Appointment> getDoctorAppointments(DataSnapshot dataSnapshot, boolean passed, Calendar currentDate) {
        ArrayList<Appointment> appointments = new ArrayList<>();

        //Add appointment this doctor is associated with
        for (DataSnapshot appointmentSnapshot : dataSnapshot.getChildren()) {
            Appointment appointment = appointmentSnapshot.getValue(Appointment.class);
            if (appointment.getDoctor().getEmail().equals(this.getEmail())) {
                if (!passed) {
                    boolean check = currentDate.before(appointment.retrieveDateTime());
                    // make sure appointment status is either pending or approved and the appointments time is greater than current time
                    if ((appointment.getStatus() == Status.PENDING || appointment.getStatus() == Status.APPROVED) && currentDate.before(appointment.retrieveDateTime())) {
                        appointments.add(appointment);
                    }
                }
                // add past appointments
                else {
                    // make sure appointment status is approved and the appointment time is less than current time
                    if (appointment.getStatus() == Status.APPROVED && currentDate.after(appointment.retrieveDateTime())) {
                        appointments.add(appointment);
                    }
                }
            }
        }

        return appointments;

    }

    /**SETTERS**/
    /*
    * Acts as a setter for autoApprove and updates firebase
    * @param  autoApprove  Boolean value to set autoApprove to
    * @param  snapshot     DataSnapshot of database to update firebase with
     */
    public void updateAutoApprove(boolean autoApprove) {
        this.autoApprove = autoApprove;
        updateFirebase("Doctors", "autoApprove", autoApprove, this);
    }

    /*
     * Acts as a setter and changes the registration status in Firebase
     * @param  registrationStatus   Status value to set registrationStatus to
     */
    public void updateRegistrationStatus(Status registrationStatus) {
        super.updateRegistrationStatus(registrationStatus);
        updateFirebase("Doctors", "registrationStatus", registrationStatus, this);
    }

    /*
    * Acts as a setter and changes the availability of the time slots in doctor's shifts
    * @param  date         Calendar date of shift to be updated
    * @param  isAvailable  Boolean value indicating availability of time slot
     */
    public void updateShiftAvailability(Calendar date, boolean isAvailable) {

        //Get shift at date
        for(Shift shift : this.shifts) {
            boolean sameStart = false, sameEnd = false;

            //Check if day is same
            if (shift.retrieveStart().get(Calendar.DAY_OF_MONTH) ==  date.get(Calendar.DAY_OF_MONTH) &&
                    shift.retrieveStart().get(Calendar.MONTH) ==  date.get(Calendar.MONTH) &&
                    shift.retrieveStart().get(Calendar.YEAR) ==  date.get(Calendar.YEAR)){

                //Check if start time is same
                if (shift.getStartHours() == date.get(Calendar.HOUR_OF_DAY) &&
                    shift.getStartMinutes() == date.get(Calendar.MINUTE)) {
                    sameStart = true;
                }

                //Check if end time is same
                int endHours = date.get(Calendar.MINUTE) == 0 ? date.get(Calendar.HOUR_OF_DAY) :
                                                                date.get(Calendar.HOUR_OF_DAY) + 1;
                int endMinutes = date.get(Calendar.MINUTE) == 0 ? 30 : 0;
                if (shift.getEndHours() == endHours && shift.getEndMinutes() == endMinutes) {
                    sameEnd = true;
                }

            }

            if ((shift.retrieveStart().before(date) || sameStart) && (shift.retrieveEnd().after(date) || sameEnd)) {
                //Change availability and update firebase
                shift.getTimeSlots().put(Shift.convertCalendarToStringTime(date), isAvailable);
                updateFirebase("Doctors", "shifts", shifts, this);
                break;

            }
        }

    }

    public void updateRating(float ratingToAdd){
        numOfRatings ++;
        rating = (rating*(numOfRatings-1) + ratingToAdd)/(numOfRatings);
        updateFirebase("Doctors", "rating", rating, this);
        updateFirebase("Doctors", "numOfRatings", numOfRatings, this);

    }

    /**OTHER METHODS**/

    /*
    * Displays all information of this doctor
     */
    @Override
    public String display() {
        String display = super.display() + "\nEmployee Number: " + getEmployeeNumber() + "\nSpecialties: ";
        if (getSpecialties() != null && !getSpecialties().isEmpty()) {
            for (int i = 0; i < getSpecialties().size(); i++) {
                display += getSpecialties().get(i);
            }
        }
        return display;

    }

    /*
    * Adds shift to list
     */
    public void addShift(Shift shift) {
        if (shifts == null) {
            this.shifts = new ArrayList<Shift>();
            this.shifts.add(shift);
        }
        else {
            this.shifts.add(shift);
        }
        Collections.sort(shifts, Shift.getShiftComparator());
        updateFirebase("Doctors", "shifts", shifts, this);

    }


    /*
    Deletes an existing shift from list
     */
    public void deleteShift(Shift shift){
        if(!(shift.getTimeSlots().containsValue(false))){
            this.shifts.remove(shift);
            updateFirebase("Doctors", "shifts", shifts, this);
        }
    }

    /**CLASS METHODS**/

    /*
    * Reads through the database for doctors and return Doctor object if found
     */
    public static Doctor getDoctor(String email, String password, DataSnapshot dataSnapshot) {
        for (DataSnapshot doctorSnapshot : dataSnapshot.getChildren()) {
            Doctor doctor = doctorSnapshot.getValue(Doctor.class);
            if (doctor.getEmail().equals(email) && doctor.getPassword().equals(password)) {
                return doctor;
            }
        }
        return null;
    }

    /*
     * Gives a list of doctors with a certain registration status from firebase
     * @param  status                      Registration status of doctors
     * @param  dataSnapshot                DataSnapshot of doctor information in firebase
     * @return                             An ArrayList of doctors
     * @throws IllegalArgumentException   if dataSnapshot is null or doesn't contain a snapshot for the doctors
     */
    public static ArrayList<User> getDoctors(Status status, DataSnapshot dataSnapshot) {

        ArrayList<User> doctors = new ArrayList<User>();

        //Make sure dataSnapshot isn't null and contains the Doctor path
        if (dataSnapshot.exists() && dataSnapshot.getRef().getKey().equals("Doctors")) {
            for (DataSnapshot doctor : dataSnapshot.getChildren()) {
                Doctor d = doctor.getValue(Doctor.class);
                //Check registration status of doctor and add it to list if it matches
                if (d.getRegistrationStatus() == status) {
                    doctors.add(d);
                }
            }
        }
        else {
            throw new IllegalArgumentException("dataSnapshot should not be null and should be from the Doctor path");
        }
        return doctors;

    }


}
