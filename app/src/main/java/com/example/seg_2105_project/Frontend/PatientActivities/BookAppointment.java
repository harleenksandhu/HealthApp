package com.example.seg_2105_project.Frontend.PatientActivities;

import com.example.seg_2105_project.Backend.Appointment;
import com.example.seg_2105_project.Backend.Doctor;
import com.example.seg_2105_project.Backend.Patient;
import com.example.seg_2105_project.Backend.Shift;
import com.example.seg_2105_project.R;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AbsListView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Map;

public class BookAppointment extends AppCompatActivity {

    Doctor doctor;
    Calendar date = Calendar.getInstance();
    String time;
    ListView listViewTimeSlots;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book_appointment);

        doctor = (Doctor) getIntent().getSerializableExtra("Doctor");

        //Initialize UI variables
        listViewTimeSlots = findViewById(R.id.listViewTimeSlots);
        displayCalendar();

        //Read date chosen
        CalendarView calendarView = findViewById(R.id.calendarViewShifts);
        calendarView.setOnDateChangeListener((calendarView1, year, month, day) -> {
            date.set(Calendar.YEAR, year);
            date.set(Calendar.MONTH, month);
            date.set(Calendar.DAY_OF_MONTH, day);
            ArrayList<String> timeSlots = getTimeSlots(date);

            //Check if date has available time slots
            if (!timeSlots.isEmpty()) {
                //Set UI
                TextView textView = findViewById(R.id.textViewSelectTimeSlot);
                textView.setVisibility(View.VISIBLE);
                listViewTimeSlots.setVisibility(View.VISIBLE);
                loadListView(timeSlots);

                TextView textViewUnavailable = findViewById(R.id.textViewSlotUnavailable);
                textViewUnavailable.setVisibility(View.INVISIBLE);

            }
            else {
                //Set UI
                TextView textViewUnavailable = findViewById(R.id.textViewSlotUnavailable);
                textViewUnavailable.setVisibility(View.VISIBLE);

                TextView textViewAvailable = findViewById(R.id.textViewSelectTimeSlot);
                textViewAvailable.setVisibility(View.INVISIBLE);
                listViewTimeSlots.setVisibility(View.INVISIBLE);

            }
            Button confirmButton = findViewById(R.id.buttonConfirmBooking);
            confirmButton.setVisibility(View.INVISIBLE);

        });

        //Read date chosen
        listViewTimeSlots.setOnItemClickListener((adapterView, view, i, l) -> {
            time = (String) listViewTimeSlots.getAdapter().getItem(i);

            //Add time to date of appointment
            date.set(Calendar.HOUR_OF_DAY, Integer.parseInt(time.substring(0,2)));
            date.set(Calendar.MINUTE, Integer.parseInt(time.substring(3,5)));

            //Allow user to confirm
            Button confirmButton = findViewById(R.id.buttonConfirmBooking);
            confirmButton.setVisibility(View.VISIBLE);
        });

    }

    public void onClickConfirmBooking(View view) {
        //Create appointment and book it
        Patient patient = (Patient) getIntent().getSerializableExtra("Patient");
        Appointment appointment = new Appointment(date, doctor, patient);
        appointment.bookAppointment();
        Toast.makeText(getApplicationContext(), "Appointment Booked", Toast.LENGTH_SHORT).show();

        Intent intent = new Intent(getApplicationContext(), PatientBookAppointment.class);
        intent.putExtra("Patient", patient);
        startActivity(intent);
    }

    public void onClickBack(View view) {
        Intent intent = new Intent(getApplicationContext(), PatientBookAppointment.class);
        Patient patient = (Patient) getIntent().getSerializableExtra("Patient");
        intent.putExtra("Patient", patient);
        startActivity(intent);
    }

    /*
    Set the min and max date of the calendar based on doctor shifts
     */
    private void displayCalendar() {
        CalendarView calendarView = findViewById(R.id.calendarViewShifts);

        //Find min and max date in doctor shifts
        ArrayList<Shift> shifts = doctor.getShifts();
        if (shifts == null) {
            //Doctor has no shifts
            calendarView.setVisibility(View.INVISIBLE);
            TextView textView = findViewById(R.id.textViewSelectDate);
            textView.setText("This doctor has no available shifts");
            return;
        }
        Calendar maxDate = shifts.get(shifts.size() - 1).retrieveStart();
        Calendar minDate = shifts.get(0).retrieveStart();

        //Check if past shifts are still in system
        if (minDate.before(Calendar.getInstance())) {
            minDate = Calendar.getInstance();
        }
        //All shifts have passed
        if (maxDate.before(Calendar.getInstance())) {
            calendarView.setVisibility(View.INVISIBLE);
            TextView textView = findViewById(R.id.textViewSelectDate);
            textView.setText("This doctor has no available shifts");
            return;
        }

        calendarView.setMinDate(minDate.getTimeInMillis());
        calendarView.setMaxDate(maxDate.getTimeInMillis());

    }

    /*
    Get available time slots for user to choose from
     */
    private ArrayList<String> getTimeSlots(Calendar date) {

        //Go through doctor shifts to get those on date
        ArrayList<Shift> shifts = doctor.getShifts();
        ArrayList<String> timeSlots = new ArrayList<>();
        for (Shift shift : shifts) {
            //Check date of shift
            if (shift.retrieveStart().get(Calendar.DAY_OF_MONTH) ==  date.get(Calendar.DAY_OF_MONTH) &&
                    shift.retrieveStart().get(Calendar.MONTH) ==  date.get(Calendar.MONTH) &&
                    shift.retrieveStart().get(Calendar.YEAR) ==  date.get(Calendar.YEAR)) {

                //Iterate through shift in 30 minute increments and check availability
                Map<String, Boolean> allTimeSlots = shift.getTimeSlots();
                Calendar time = shift.retrieveStart();
                while (time.before(shift.retrieveEnd())) {

                    if (allTimeSlots.containsKey(Shift.convertCalendarToStringTime(time)) &&
                            allTimeSlots.get(Shift.convertCalendarToStringTime(time))) { //if slot is available, add it
                        timeSlots.add(Shift.convertCalendarToStringTime(time));
                    }
                    //Increment
                    if (time.get(Calendar.MINUTE) == 30) {
                        int hour = time.get(Calendar.HOUR_OF_DAY);
                        time.set(Calendar.HOUR_OF_DAY, hour+1);
                        time.set(Calendar.MINUTE, 0);
                    }
                    else
                        time.set(Calendar.MINUTE, 30);

                }

            }
        }
        return timeSlots;

    }

    /*
    Loads the list view with open time slots
     */
    private void loadListView(ArrayList<String> timeSlots) {
        ArrayList<String> timeSlotsDisplay = new ArrayList<>();
        for (String slot : timeSlots) {
            //Add end time to list view
            int hour = Integer.parseInt(slot.substring(0, 2));
            int minute = Integer.parseInt(slot.substring(3));
            slot += " - ";

            if (minute == 30) {
                if (hour < 9)
                    slot += "0";
                slot += (hour + 1) + ":00";
            }
            else {
                slot += (slot.substring(0, 2) + ":30");
            }
            timeSlotsDisplay.add(slot);

        }

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_single_choice, timeSlotsDisplay);
        listViewTimeSlots.setChoiceMode(AbsListView.CHOICE_MODE_SINGLE);
        listViewTimeSlots.setAdapter(adapter);
    }

}