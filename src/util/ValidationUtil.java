package util;

import java.util.regex.Pattern;

public class ValidationUtil {
    
    // Pattern untuk validasi email
    private static final String EMAIL_PATTERN = 
        "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$";
    
    // Pattern untuk validasi nomor telepon Indonesia
    private static final String PHONE_PATTERN = 
        "^(\\+62|62|0)8[1-9][0-9]{6,9}$";
    
    //Validasi email

    public static boolean isValidEmail(String email) {
        if (email == null || email.trim().isEmpty()) return false;
        return Pattern.compile(EMAIL_PATTERN).matcher(email).matches();
    }
    
    //Validasi nomor telepon Indonesia

    public static boolean isValidPhone(String phone) {
        if (phone == null || phone.trim().isEmpty()) return false;
        return Pattern.compile(PHONE_PATTERN).matcher(phone).matches();
    }
    
    //Validasi input tidak kosong

    public static boolean isNotEmpty(String input) {
        return input != null && !input.trim().isEmpty();
    }
    
    //Validasi angka positif

    public static boolean isPositiveNumber(String number) {
        if (number == null || number.trim().isEmpty()) return false;
        try {
            double value = Double.parseDouble(number);
            return value > 0;
        } catch (NumberFormatException ex) {
            return false;
        }
    }
    
    //Validasi integer positif

    public static boolean isPositiveInteger(String number) {
        if (number == null || number.trim().isEmpty()) return false;
        try {
            int value = Integer.parseInt(number);
            return value > 0;
        } catch (NumberFormatException ex) {
            return false;
        }
    }
    
    //Validasi nomor kamar (harus antara 100-999)

    public static boolean isValidRoomNumber(int roomNumber) {
        return roomNumber >= 100 && roomNumber <= 999;
    }
    
    //Validasi harga (harus >= 0)

    public static boolean isValidPrice(double price) {
        return price >= 0;
    }
}