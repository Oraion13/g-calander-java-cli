package utils;

import java.io.IOException;
import java.time.format.DateTimeFormatter;
import java.time.format.ResolverStyle;
import java.util.Locale;

public class ValidIOHandlers {
    /**
     * Get input choice
     * 
     * @param message message to display
     * @return an integer choice
     * @throws IOException
     */
    public static int getChoice(String message) throws IOException {
        while (true) {
            try {
                System.out.print(message);
                int choice = Integer.parseInt(System.console().readLine());

                // if correct input is given, return the input
                if (choice >= 0)
                    return choice;
            } catch (NumberFormatException e) {
                System.out.println("Enter a valid input [Number]!");
            }

        }
    }

    /**
     * Get valid y / n choice
     * 
     * @param message message to display
     * @return a boolean true / false
     */
    public static boolean getYorN(String message) {
        while (true) {
            System.out.print(message);
            String choice = System.console().readLine().toLowerCase();

            // y - true
            if (choice.equals("y")) {
                return true;
            }

            // n - false
            if (choice.equals("n")) {
                return false;
            }

            System.out.println("Enter a valid input [Y/n]!");
        }
    }

    /**
     * Get valid date and return
     * 
     * @param message message for user
     * @return a valid dateTime
     */
    public static String getDate(String message, int confirm) {
        while (true) {
            System.out.print(message);
            String dateStr = System.console().readLine();

            DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("uuuu-MM-dd", Locale.getDefault())
                    .withResolverStyle(ResolverStyle.STRICT);
            DateValidatorUsingDateTimeFormatter validator = new DateValidatorUsingDateTimeFormatter(dateFormatter);

            // is a valid date
            if (confirm == 1) {
                if (validator.isValid(dateStr)) {
                    return dateStr;
                }
            } else {
                if (validator.isValid(dateStr) || dateStr.equals("0")) {
                    return dateStr;
                }
            }

            System.out.println("Enter a valid date [YYYY-MM-DD]!");
        }
    }

    /**
     * Get a valid message
     * 
     * @param message message for user
     * @return
     */
    public static String getString(String message) {
        while (true) {
            System.out.print(message);
            String str = System.console().readLine();

            if (!str.equals(null)) {
                return str;
            }

            System.out.println("Enter a valid message!");
        }
    }

}
