package util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class DateUtil {
    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd");
    private static final SimpleDateFormat DISPLAY_FORMAT = new SimpleDateFormat("dd-MM-yyyy");
    
    //Konversi Date ke String format database (yyyy-MM-dd)

    public static String dateToString(Date date) {
        if (date == null) return null;
        return DATE_FORMAT.format(date);
    }
    
    //Konversi String format database ke Date

    public static Date stringToDate(String dateString) {
        if (dateString == null || dateString.trim().isEmpty()) return null;
        try {
            return DATE_FORMAT.parse(dateString);
        } catch (ParseException ex) {
            System.err.println("Error parsing date: " + ex.getMessage());
            return null;
        }
    }
    

    // Format date untuk display (dd-MM-yyyy)
    public static String formatDisplay(Date date) {
        if (date == null) return "";
        return DISPLAY_FORMAT.format(date);
    }
    
    //Parse dari display format ke Date

    public static Date parseDisplay(String displayDate) {
        if (displayDate == null || displayDate.trim().isEmpty()) return null;
        try {
            return DISPLAY_FORMAT.parse(displayDate);
        } catch (ParseException ex) {
            System.err.println("Error parsing display date: " + ex.getMessage());
            return null;
        }
    }
    
    //Cek apakah string adalah format date yang valid

    public static boolean isValidDate(String dateString) {
        if (dateString == null || dateString.trim().isEmpty()) return false;
        try {
            DATE_FORMAT.parse(dateString);
            return true;
        } catch (ParseException ex) {
            return false;
        }
    }
}