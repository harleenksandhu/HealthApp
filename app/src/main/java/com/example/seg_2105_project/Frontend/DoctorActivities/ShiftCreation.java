package com.example.seg_2105_project.Frontend.DoctorActivities;

import com.example.seg_2105_project.Backend.Doctor;
import com.example.seg_2105_project.Backend.Shift;
import com.example.seg_2105_project.R;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.Spinner;
import android.widget.Toast;
import android.widget.ArrayAdapter;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

public class ShiftCreation extends AppCompatActivity {

    Calendar currentTime;

    Calendar calendar;
    CalendarView calendarView;
    Spinner timeStart;
    Spinner timeEnd;

    Button confirmDate;

    Doctor doctor;

    int yearCalendar, monthCalendar, dayCalendar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shift_creation);
        calendarView = findViewById(R.id.calendarView);
        currentTime = Calendar.getInstance();
        // Restrict dates to be selected
        calendarView.setMinDate(currentTime.getTimeInMillis());
        confirmDate = findViewById(R.id.confirmDate);
        calendar = Calendar.getInstance();
        timeStart = findViewById(R.id.timeStartInput);
        timeEnd = findViewById(R.id.timeEndInput);

        // Initialize
        yearCalendar = currentTime.get(Calendar.YEAR);
        monthCalendar = currentTime.get(Calendar.MONTH);
        dayCalendar = currentTime.get(Calendar.DAY_OF_MONTH);


        //Get doctor
        this.doctor = (Doctor) getIntent().getSerializableExtra("Doctor");

        calendarView.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(@NonNull CalendarView calendarView, int year, int month, int day) {
                Toast.makeText(ShiftCreation.this, day + "/" + (month + 1) + "/" + year, Toast.LENGTH_SHORT).show();
                yearCalendar = year;
                monthCalendar = month;
                dayCalendar = day;
            }
        });

        //Load times to time inputs
        ArrayList<String> times = new ArrayList<>();
        for (int i = 0; i <= 23; i ++) {
            times.add(i + ":00");
            times.add(i + ":30");
        }
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getApplicationContext(),
                android.R.layout.simple_spinner_dropdown_item, times);
        timeStart.setAdapter(adapter);
        timeEnd.setAdapter(adapter);
        
    }

    // Method to initialize calendar to a particular date
    public void setDate(int year, int month, int day) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.YEAR, year);
        calendar.set(Calendar.MONTH, month - 1);
        calendar.set(Calendar.DAY_OF_MONTH, day);

        long milliseconds = calendar.getTimeInMillis();

        calendarView.setDate(milliseconds);
    }


    public void onClickConfirmDate(View view) {
        calendar = Calendar.getInstance();
        calendar.set(Calendar.YEAR, yearCalendar);
        calendar.set(Calendar.MONTH, monthCalendar);
        calendar.set(Calendar.DAY_OF_MONTH, dayCalendar);
        boolean validShift = true;


        // Parse start time
        int hoursStart, minutesStart;
        String selectedItemStart = (String) timeStart.getSelectedItem();
        String[] timeValuesStart = selectedItemStart.split(":");
        hoursStart = Integer.parseInt(timeValuesStart[0]);
        minutesStart = Integer.parseInt(timeValuesStart[1]);

        // Parse end time
        int hoursEnd, minutesEnd;
        String selectedItemEnd = (String) timeEnd.getSelectedItem();
        String[] timeValuesEnd = selectedItemEnd.split(":");
        hoursEnd = Integer.parseInt(timeValuesEnd[0]);
        minutesEnd = Integer.parseInt(timeValuesEnd[1]);

        // Ensure that the end time is after start time
        if ((hoursEnd < hoursStart) || ((hoursEnd == hoursStart) && (minutesEnd <= minutesStart))){
            Toast.makeText(ShiftCreation.this, "Please select a different end time", Toast.LENGTH_SHORT).show();
            validShift = false;
        }

        // If the day chosen is the current day, ensure that the shift time is after the current time
        if ((calendar.get(Calendar.YEAR) == currentTime.get(Calendar.YEAR)) && (calendar.get(Calendar.MONTH) == currentTime.get(Calendar.MONTH))
                && (calendar.get(Calendar.DAY_OF_MONTH) == currentTime.get(Calendar.DAY_OF_MONTH))) {
            if (hoursStart < calendar.get(Calendar.HOUR_OF_DAY) || (hoursStart == calendar.get(Calendar.HOUR_OF_DAY) &&
                    minutesStart < calendar.get(Calendar.MINUTE))) {
                Toast.makeText(ShiftCreation.this, "Please select a different time", Toast.LENGTH_SHORT).show();
                validShift = false;
            }
        }

        Calendar shiftStart = Calendar.getInstance();
        Calendar shiftEnd = Calendar.getInstance();
        shiftStart.setTimeInMillis(calendar.getTimeInMillis());
        shiftEnd.setTimeInMillis(calendar.getTimeInMillis());
        shiftStart.set(Calendar.HOUR_OF_DAY, hoursStart);
        shiftStart.set(Calendar.MINUTE, minutesStart);
        shiftEnd.set(Calendar.HOUR_OF_DAY, hoursEnd);
        shiftEnd.set(Calendar.MINUTE, minutesEnd);

        //Check if shift conflict with another shift
        if (doctor.getShifts() != null && !doctor.getShifts().isEmpty()) {
            for (Shift shift: doctor.getShifts()) {

                if (shift.retrieveStart().get(Calendar.YEAR) == shiftStart.get(Calendar.YEAR) &&
                        shift.retrieveStart().get(Calendar.MONTH) == shiftStart.get(Calendar.MONTH) &&
                        shift.retrieveStart().get(Calendar.DAY_OF_MONTH) == shiftStart.get(Calendar.DAY_OF_MONTH) &&
                        ((shift.retrieveStart().before(shiftStart) && shift.retrieveEnd().after(shiftEnd)) ||
                                (shift.retrieveStart().before(shiftEnd) && shift.retrieveEnd().after(shiftEnd)) ||
                                (shift.retrieveStart().before(shiftStart) && shift.retrieveEnd().after(shiftStart))) ||
                        (shift.retrieveStart().after(shiftStart) && shift.retrieveEnd().before(shiftEnd))) {
                    Toast.makeText(ShiftCreation.this, "Please select a different time. This conflicts with another shift.", Toast.LENGTH_SHORT).show();
                    validShift = false;
                    break;
                }

            }
        }



        if (validShift) {
            // add shift
            Shift shiftToAdd = new Shift(shiftStart, shiftEnd);
            doctor.addShift(shiftToAdd);

            Intent intent = new Intent(getApplicationContext(), DoctorScreen.class);
            intent.putExtra("Doctor", doctor);
            startActivity(intent);

        }

    }

    public void onClickBackButton(View view){
        Intent intent = new Intent(this, DoctorShifts.class);
        intent.putExtra("Doctor", doctor);
        startActivity(intent);
    }

}