package controllers;

import java.io.IOException;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.api.client.util.DateTime;
import com.google.api.services.calendar.Calendar;
import com.google.api.services.calendar.model.Event;

public class EventsManagement {
    Calendar service = null;
    String calanderId = null;

    // set the service credentials to access the API
    public EventsManagement(Calendar service, String calanderId) {
        this.service = service;
        this.calanderId = calanderId;
    }

    // ------------------------- Print Events ------------------------- //

    /**
     * Print events
     * 
     * @param events Events in a List<Event>
     */
    public void printEvents(List<Event> events) {
        for (Event event : events) {
            System.out.print(event.getSummary() + " || ");
            System.out.println(event.getStart().getDateTime());
        }
    }

    /**
     * Print events in map
     * 
     * @param events Events in a Map<Integer, Event>
     */
    public void printMapEvents(Map<Integer, Event> events) {
        for (Map.Entry<Integer, Event> event : events.entrySet()) {
            System.out.println(
                    event.getKey() + " : " + event.getValue().getSummary() + " || "
                            + event.getValue().getStart().getDateTime());
        }
    }

    // ----------------------------- Get Events ----------------------------- //

    /**
     * GET First N events
     * 
     * @param limit N - events
     * @return a list of events List<Event>
     * @throws IOException
     */
    public List<Event> getFirstNEvents(int limit) throws IOException {
        List<Event> events = service.events().list(calanderId).setMaxResults(limit).execute().getItems();

        return events;
    }

    /**
     * GET events between two dates
     * 
     * @param from  Events Starting date
     * @param to    Events Ending date
     * @param limit N - events
     * @return a list of events List<Event>
     * @throws IOException
     */
    public List<Event> getEventsBetween(String from, String to, int limit) throws IOException {
        com.google.api.services.calendar.Calendar.Events.List events = service.events()
                .list(calanderId)
                .setTimeMin(new DateTime(
                        Date.from(LocalDate.parse(from).atStartOfDay(ZoneId.systemDefault()).toInstant())));

        if (!to.equals("0")) {
            events.setTimeMax(new DateTime(
                    Date.from(LocalDate.parse(to).atStartOfDay(ZoneId.systemDefault()).toInstant())));
        }

        return events.setMaxResults(limit).execute().getItems();
    }

    /**
     * GET events between two dates
     * 
     * @param from  Events Starting date
     * @param to    Events Ending date
     * @param limit N - events
     * @return a list of events Map<Integer, Event>
     * @throws IOException
     */
    public Map<Integer, Event> getEventsInMap(String from, String to, int limit) throws IOException {
        Map<Integer, Event> map = new HashMap<>();
        int counter = 0;

        // get a list of events
        List<Event> events = getEventsBetween(from, to, 10);

        // put all events with an associated ID to delete
        for (Event event : events) {
            map.put(++counter, event);
        }

        return map;
    }

    /**
     * POST an event
     * 
     * @param event New Event to insert
     * @throws IOException
     */
    public void postEvent(Event event) throws IOException {
        event = service.events().insert(calanderId, event).execute();
    }

    /**
     * DELETE an event
     * 
     * @param eventID an Event ID
     * @throws IOException
     */
    public void deleteEvent(String eventID) throws IOException {
        service.events().delete(calanderId, eventID).execute();
    }

    /**
     * PUT (UPDATE) an event
     * 
     * @param event An Event to update
     * @throws IOException
     */
    public void updateEvent(Event event) throws IOException {
        service.events().update(calanderId, event.getId(), event).execute();
    }
}
