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
    public static int getChoice(Scanner sc) throws IOException {
        System.out.print("Enter a choice: ");
        int choice = sc.nextInt();

        sc.close();
        return choice;

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
