import java.util.Scanner;

import com.google.api.services.calendar.Calendar;

import controllers.EventsManagement;
import controllers.GetEvents;

public class Operations {
    EventsManagement eventManagement = null;
    Scanner sc = null;

    // main caller
    public Operations(Calendar service, Scanner sc) {
        this.eventManagement = new EventsManagement(service, sc);
        this.sc = sc;
        while (true) {
            // print the options
            mainOptions();
            // int choice = mainOperations();
            mainOperations();

            break;

        }
    }

    // ----------------------------- Main Operations ---------------------------- //

    /**
     * Prints the main options
     * 0 - Exit
     * 1 - Display events
     */
    private void mainOptions() {
        System.out.print("\033[H\033[2J");
        System.out.println("\n------------------------------------------");
        System.out.println("Choices: ");
        System.out.println("0 - Exit");
        System.out.println("1 - Display events");
        System.out.println("------------------------------------------\n");
    }

    /**
     * Get a main operation choice
     * 
     * @return an intger value (main option)
     */
    private int mainOperations() {
        int choice = 0;
        try {
            choice = 1;

            switch (choice) {
                // display events
                case 1:
                    new GetEvents(eventManagement);
                    break;

                default:
                    return choice;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return choice;
    }
}
