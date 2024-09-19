package com.example.bloodlink.donorpage;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class Utils {

    private static final String DATE_FORMAT = "yyyy-MM-dd";

    public static String calculateAge(String dateOfBirth) {
        SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT, Locale.getDefault());
        try {
            Date birthDate = sdf.parse(dateOfBirth);
            Calendar dob = Calendar.getInstance();
            dob.setTime(birthDate);

            Calendar today = Calendar.getInstance();

            int age = today.get(Calendar.YEAR) - dob.get(Calendar.YEAR);

            // If the current date is before the birth date, subtract one year
            if (today.get(Calendar.DAY_OF_YEAR) < dob.get(Calendar.DAY_OF_YEAR)) {
                age--;
            }

            return String.valueOf(age);
        } catch (ParseException e) {
            e.printStackTrace();
            // Return a default value or handle the error as needed
            return "N/A";
        }
    }
}
