package controllers;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.api.services.calendar.model.Event;

public class DeleteEvents {
    EventsManagement eventsManagement = null;

    public DeleteEvents(EventsManagement eventsManagement) throws IOException {
        this.eventsManagement = eventsManagement;

        while (true) {
            System.out.println("Get events from - to...\nEnter '0' to exit...");
            System.out.print("Enter the start date(YYYY-MM-DD / 0): ");

            String start = System.console().readLine();
            if (start.equals("0"))
                break;

            System.out.print("Enter the date 'To', '0' for no ending date(YYYY-MM-DD / 0): ");
            String end = System.console().readLine();

            // events limit

            System.out.print("Enter the number of events to get(Number): ");
            int limit = Integer.parseInt(System.console().readLine());

            deleteEvents(start, end, limit);
        }
    }

    // display events to be deleted
    private void deleteEvents(String start, String end, int limit) throws IOException {
        Map<Integer, Event> toDelete = new HashMap<>();
        int counter = 0;

        // get a list of events
        List<Event> events = eventsManagement.getEventsBetween(start, end, 10);

        // put all events with an associated ID to delete
        for (Event event : events) {
            toDelete.put(++counter, event);
        }

        // display the events
        System.out.println("Delete ID   :   Event\n-----------------------------------");
        for (Map.Entry<Integer, Event> event : toDelete.entrySet()) {
            System.out.println(
                    event.getKey() + " : " + event.getValue().getSummary() + " || "
                            + event.getValue().getStart().getDateTime());
        }

        System.out.print("Enter a Delete ID to delete an event (Number / 0): ");
        int deleteID = Integer.parseInt(System.console().readLine());

        if (deleteID == 0)
            return;

        eventsManagement.deleteEvent(toDelete.get(deleteID).getId());
        System.out.println("Event Deleted Successfully!\n----------------------------------");
    }
}
