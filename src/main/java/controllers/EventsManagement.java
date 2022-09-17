package controllers;

import java.io.IOException;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;
import java.util.Scanner;

import com.google.api.client.util.DateTime;
import com.google.api.services.calendar.Calendar;
import com.google.api.services.calendar.model.Event;

public class EventsManagement {
    Calendar service = null;
    Scanner sc = null;

    // set the service credentials to access the API
    public EventsManagement(Calendar service, Scanner sc) {
        this.service = service;
        this.sc = sc;
    }

    // print events
    public void printEvents(List<Event> events) {
        for (Event event : events) {
            System.out.print(event.getSummary() + " || ");
            System.out.println(event.getStart().getDateTime());
        }
    }

    // first N events
    public List<Event> getFirstNEvents(int limit) throws IOException {
        List<Event> events = service.events().list("primary").setMaxResults(limit).execute().getItems();

        return events;
    }

    // prints events between two dates
    public List<Event> getEventsBetween(String from, String to, int limit) throws IOException {
        com.google.api.services.calendar.Calendar.Events.List events = service.events()
                .list("primary")
                .setTimeMin(new DateTime(
                        Date.from(LocalDate.parse(from).atStartOfDay(ZoneId.systemDefault()).toInstant())));

        if (to.equals(" ") == false) {
            events.setTimeMax(new DateTime(
                    Date.from(LocalDate.parse(to).atStartOfDay(ZoneId.systemDefault()).toInstant())));
        }

        return events.setMaxResults(limit).execute().getItems();
    }

    // create events
    public void postEvent(Event event) throws IOException {
        event = service.events().insert("primary", event).execute();
    }
}
