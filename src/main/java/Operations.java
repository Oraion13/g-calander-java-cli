import java.util.Scanner;

import com.google.api.services.calendar.Calendar;

import controllers.DeleteEvents;
import controllers.EventsManagement;
import controllers.GetEvents;
import controllers.PostEvents;

public class Operations {
    EventsManagement eventsManagement = null;
    Scanner sc = null;

    // main caller
    public Operations(Calendar service, Scanner sc) {
        this.eventsManagement = new EventsManagement(service, sc);
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
     */
    private void mainOptions() {
        System.out.print("\033[H\033[2J");
        System.out.println("\n------------------------------------------");
        System.out.println("Choices: ");
        System.out.println("0 - Exit");
        System.out.println("1 - Display events");
        System.out.println("2 - Create events");
        System.out.println("------------------------------------------\n");
    }

    /**
     * Get a main operation choice
     * 
     * 0 - Exit
     * 1 - Display events
     * 2 - Create events
     * 3 - Delete events
     * 
     * @return an intger value (main option)
     */
    private int mainOperations() {
        int choice = 0;
        try {
            choice = 3;

            switch (choice) {
                // display events
                case 1:
                    new GetEvents(eventsManagement);
                    break;

                // create events
                case 2:
                    new PostEvents(eventsManagement);
                    break;

                case 3:
                    new DeleteEvents(eventsManagement);
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
