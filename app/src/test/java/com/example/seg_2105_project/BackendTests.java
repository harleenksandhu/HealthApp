package com.example.seg_2105_project;


import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertFalse;

import com.example.seg_2105_project.Backend.Administrator;
import com.example.seg_2105_project.Backend.Appointment;
import com.example.seg_2105_project.Backend.Doctor;
import com.example.seg_2105_project.Backend.Patient;
import com.example.seg_2105_project.Backend.Shift;
import com.example.seg_2105_project.Backend.Status;
import com.example.seg_2105_project.Backend.User;

import org.junit.Test;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Calendar;

public class BackendTests {

    @Test
    public void testAdministratorCredentials() {
        assertEquals("anika.pathak@gmail.com", Administrator.email);
        assertEquals("password", Administrator.password);
    }

    @Test
    public void testAppointmentCreation() {
        Doctor doctor = new Doctor("Wulfwynn", "Mercer", "Wulfwynn.Mercer@example.com", "password", 6132134567L, "123 Street", 123, new ArrayList<>());
        Patient patient = new Patient("Gwydion", "Hearn", "Gwydion.h@example.com", "password123", 6134015012L, "321 Drive", 456789);

        Calendar appointmentDateTime = Calendar.getInstance();
        appointmentDateTime.add(Calendar.DAY_OF_MONTH, 1);

        Appointment appointment = new Appointment(appointmentDateTime, doctor, patient);

        assertNotNull(appointment);
        assertEquals(doctor, appointment.getDoctor());
        assertEquals(patient, appointment.getPatient());

        // Adjust the expected status based on doctor's auto approval
        if (doctor.getAutoApprove()) {
            assertEquals(Status.APPROVED, appointment.getStatus());
        } else {
            assertEquals(Status.PENDING, appointment.getStatus());
        }
    }

    @Test
    public void testDoctorCreation() {
        Doctor doctor = new Doctor("Wulfwynn", "Mercer", "Wulfwynn.Mercer@example.com", "password", 6132134567L, "123 Street", 123, new ArrayList<>());

        assertNotNull(doctor);
        assertEquals("Wulfwynn", doctor.getFirstName());
        assertEquals("Mercer", doctor.getLastName());
        assertEquals("Wulfwynn.Mercer@example.com", doctor.getEmail());
        assertEquals("password", doctor.getPassword());
        assertEquals(6132134567L, doctor.getPhoneNumber());
        assertEquals("123 Street", doctor.getAddress());
        assertEquals(Status.PENDING, doctor.getRegistrationStatus());
        assertFalse(doctor.getAutoApprove());
    }

    @Test
    public void testPatientCreation() {
        Patient patient = new Patient("Gwydion", "Hearn", "Gwydion.h@example.com", "password123", 6134015012L, "321 Drive", 456789);

        assertNotNull(patient);
        assertEquals("Gwydion", patient.getFirstName());
        assertEquals("Hearn", patient.getLastName());
        assertEquals("Gwydion.h@example.com", patient.getEmail());
        assertEquals("password123", patient.getPassword());
        assertEquals(6134015012L, patient.getPhoneNumber());
        assertEquals("321 Drive", patient.getAddress());
        assertEquals(456789, patient.getHealthCardNumber());
        assertEquals(Status.PENDING, patient.getRegistrationStatus());
    }

    @Test
    public void testShiftCreation() {
        Calendar start = Calendar.getInstance();
        Calendar end = (Calendar) start.clone();
        end.add(Calendar.HOUR_OF_DAY, 3);

        Shift shift = new Shift(start, end);

        assertNotNull(shift);

        // Compare the time using Instant to eliminate timezone differences
        Instant expectedStartInstant = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            expectedStartInstant = Instant.ofEpochMilli(start.getTimeInMillis());
        }
        Instant actualStartInstant = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            actualStartInstant = Instant.ofEpochMilli(shift.retrieveStart().getTimeInMillis());
        }
        assertEquals("Start time mismatch", expectedStartInstant, actualStartInstant);

        Instant expectedEndInstant = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            expectedEndInstant = Instant.ofEpochMilli(end.getTimeInMillis());
        }
        Instant actualEndInstant = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            actualEndInstant = Instant.ofEpochMilli(shift.retrieveEnd().getTimeInMillis());
        }
        assertEquals("End time mismatch", expectedEndInstant, actualEndInstant);
    }


    @Test
    public void testUserCreation() {
        User user = new User("Thancmar", "Wronski", "Thancmar.Wronski@example.com", "password321", 6132190875L, "567 Yep Road");

        assertNotNull(user);
        assertEquals("Thancmar", user.getFirstName());
        assertEquals("Wronski", user.getLastName());
        assertEquals("Thancmar.Wronski@example.com", user.getEmail());
        assertEquals("password321", user.getPassword());
        assertEquals(6132190875L, user.getPhoneNumber());
        assertEquals("567 Yep Road", user.getAddress());
        assertEquals(Status.PENDING, user.getRegistrationStatus());
    }
}
