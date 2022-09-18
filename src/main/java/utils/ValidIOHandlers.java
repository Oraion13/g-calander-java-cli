package utils;

import java.io.IOException;
import java.time.format.DateTimeFormatter;
import java.time.format.ResolverStyle;
import java.util.Locale;
import java.util.Scanner;

public class ValidIOHandlers {
    /**
     * Get input choice
     * 
     * @param sc Scanner object to get input
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
                System.out.println("Enter a valid input (Number)!");
            }

        }

    }

    /**
     * Get valid date and return
     * 
     * @param sc      Scanner object to get input
     * @param message Input message
     * @return a valid dateTime
     */
    public static String getDateTime(Scanner sc, String message) {
        while (true) {

            System.out.print(message);
            String dateStr = sc.next();

            DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("uuuu-MM-dd", Locale.US)
                    .withResolverStyle(ResolverStyle.STRICT);
            DateValidatorUsingDateTimeFormatter validator = new DateValidatorUsingDateTimeFormatter(dateFormatter);

            if (validator.isValid(dateStr))
                return dateStr;

        }
    }

}
