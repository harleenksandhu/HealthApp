package com.example.seg_2105_project.Backend;

import java.io.Serializable;
import java.util.Calendar;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;

public class Shift implements Serializable {

    private Calendar start;
    private Calendar end;
    private int year;
    private int month;
    private int day;
    private int startHours;
    private int startMinutes;
    private int endHours;
    private int endMinutes;
    private Map<String, Boolean> timeSlots;

    // Nested class for comparing shifts
    private static class ShiftComparator<T> implements Comparator<T> {
        @Override
        public int compare(T s1, T s2) {
            Shift shift1 = (Shift) s1;
            Shift shift2 = (Shift) s2;
            Calendar shift1Start = shift1.retrieveStart();
            Calendar shift2Start = shift2.retrieveStart();
            return shift1Start.compareTo(shift2Start);
        }
    }

    public Shift(Calendar start, Calendar end) {
        this.end = end;
        this.year = start.get(Calendar.YEAR);
        this.month = start.get(Calendar.MONTH);
        this.day = start.get(Calendar.DAY_OF_MONTH);
        this.startHours = start.get(Calendar.HOUR_OF_DAY);
        this.startMinutes = start.get(Calendar.MINUTE);
        this.endHours = end.get(Calendar.HOUR_OF_DAY);
        this.endMinutes = end.get(Calendar.MINUTE);

        //Initialize time slots to all available
        this.timeSlots = new HashMap<>();
        while (start.before(end)) {
            timeSlots.put(convertCalendarToStringTime(start), true);

            //Increment
            if (start.get(Calendar.MINUTE) == 30) {
                int hour = start.get(Calendar.HOUR_OF_DAY);
                start.set(Calendar.HOUR_OF_DAY, hour+1);
                start.set(Calendar.MINUTE, 0);
            }
            else
                start.set(Calendar.MINUTE, 30);

        }
        this.start = retrieveStart();
    }

    public Shift() {}

    public int getYear() {
        return year;
    }

    public int getMonth() {
        return month;
    }

    public int getDay() {
        return day;
    }

    public int getStartHours() {
        return startHours;
    }

    public int getStartMinutes() {
        return startMinutes;
    }

    public int getEndHours() {
        return endHours;
    }

    public int getEndMinutes() {
        return endMinutes;
    }

    public Map<String, Boolean> getTimeSlots() { return timeSlots; }

    // Static methods
    // return a comparator for shifts
    public static ShiftComparator getShiftComparator(){ return new ShiftComparator(); }

    public Calendar retrieveStart() {
        start = Calendar.getInstance();
        start.set(Calendar.HOUR_OF_DAY, startHours);
        start.set(Calendar.MINUTE, startMinutes);
        start.set(Calendar.YEAR, year);
        start.set(Calendar.MONTH, month);
        start.set(Calendar.DAY_OF_MONTH, day);
        return start;
    }

    public Calendar retrieveEnd() {
        end = Calendar.getInstance();
        end.set(Calendar.HOUR_OF_DAY, endHours);
        end.set(Calendar.MINUTE, endMinutes);
        end.set(Calendar.YEAR, year);
        end.set(Calendar.MONTH, month);
        end.set(Calendar.DAY_OF_MONTH, day);
        return end;
    }

    public String toString() {
        String date = day + "/" + (month + 1) + "/" + year;
        String startTime = convertCalendarToStringTime(retrieveStart());
        String endTime = convertCalendarToStringTime(retrieveEnd());

        return date + " from " + startTime + " to " + endTime;
    }

    public static String convertCalendarToStringTime(Calendar time) {
        String timeStr = "";
        int hours = time.get(Calendar.HOUR_OF_DAY);
        int minutes = time.get(Calendar.MINUTE);
        if (hours < 10)
            timeStr += "0";
        timeStr += hours + ":";
        if (minutes < 10)
            timeStr += "0";
        timeStr += minutes;
        return timeStr;
    }

}
