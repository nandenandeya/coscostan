package coscostan.util;

import java.util.Date;

public class TestConnection {
    public static void main(String[] args) {
        // Test koneksi database
        System.out.println("Testing Database Connection...");
        DatabaseConnection.testConnection();
        
        // Test utility classes
        System.out.println("\nTesting Date Utility...");
        Date today = new Date();
        System.out.println("Today (DB format): " + DateUtil.dateToString(today));
        System.out.println("Today (Display): " + DateUtil.formatDisplay(today));
        
        System.out.println("\nTesting Validation Utility...");
        System.out.println("Valid email test@example.com: " + ValidationUtil.isValidEmail("test@example.com"));
        System.out.println("Valid phone 081234567890: " + ValidationUtil.isValidPhone("081234567890"));
        System.out.println("Positive number 1000: " + ValidationUtil.isPositiveNumber("1000"));
        
        // Tutup koneksi setelah test
        DatabaseConnection.closeConnection();
    }
}