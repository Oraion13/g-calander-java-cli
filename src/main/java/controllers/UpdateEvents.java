package controllers;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.api.services.calendar.model.Event;

public class UpdateEvents extends PostEvents {
    public UpdateEvents(EventsManagement eventsManagement) {
        super(eventsManagement);
    }

    // main caller
    public void updateEvent() throws IOException {
        System.out.print("\033[H\033[2J");
        System.out.println("\n------------------------------------------");
        System.out.println("\t\tUpdate Events");
        System.out.println("------------------------------------------\n");
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

            System.out.println("\n!! If you press 'n' for every update, the values will not be changed !!\n");

            updateEvents(start, end, limit);
        }
    }

    // display events to be updated
    private void updateEvents(String start, String end, int limit) throws IOException {
        Map<Integer, Event> toUpdate = new HashMap<>();
        int counter = 0;

        // get a list of events
        List<Event> events = eventsManagement.getEventsBetween(start, end, 10);

        // put all events with an associated ID to update
        for (Event event : events) {
            toUpdate.put(++counter, event);
        }

        // display the events
        System.out.println("Update ID   :   Event\n-----------------------------------");
        for (Map.Entry<Integer, Event> event : toUpdate.entrySet()) {
            System.out.println(
                    event.getKey() + " : " + event.getValue().getSummary() + " || "
                            + event.getValue().getStart().getDateTime());
        }

        System.out.print("Enter a Update ID to update an event (Number / 0): ");
        int updateID = Integer.parseInt(System.console().readLine());

        if (updateID == 0)
            return;

        Event event = toUpdate.get(updateID);

        setDefaultValues(event);

        // add recurence rules?
        System.out.print("Is a resucrsive event? (y/n): ");
        if (System.console().readLine().toLowerCase().equals("y")) {
            setReurrenceRules(event);
        }

        // set attendees
        System.out.print("Invite attendees to event? (y/n): ");
        if (System.console().readLine().toLowerCase().equals("y")) {
            setAttendees(event);
        }

        // set remainders
        System.out.print("Set remainders(y) or use default(n)? (y/n): ");
        if (System.console().readLine().toLowerCase().equals("y")) {
            setRemainder(event);
        }

        eventsManagement.updateEvent(event);

        System.out.println("Event Updated Successfully!\n----------------------------------");
    }
}
