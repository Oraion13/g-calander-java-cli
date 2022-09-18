package controllers;

import com.google.api.client.util.DateTime;
import com.google.api.services.calendar.model.Event;
import com.google.api.services.calendar.model.EventAttendee;
import com.google.api.services.calendar.model.EventDateTime;
import com.google.api.services.calendar.model.EventReminder;

import utils.ValidIOHandlers;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.EnumSet;
import java.util.List;
import java.util.TimeZone;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PostEvents {
    EventsManagement eventsManagement = null;
    Event event = null;

    // FREQ in RRULE
    enum Frequency {
        YEARLY, MONTHLY, WEEKLY, DAILY, HOURLY, MINUTELY, SECONDLY;

        // display frequency
        public static void printFrequency() {
            int count = 1;
            for (Frequency freq : EnumSet.allOf(Frequency.class)) {
                System.out.println(count++ + " : " + freq);
            }
        }

        // get frequency
        public static Frequency getFrequency(int n) {
            int count = 1;
            for (Frequency freq : EnumSet.allOf(Frequency.class)) {
                if (n == count++)
                    return freq;
            }

            return Frequency.YEARLY;
        }
    }

    // BYDAY in RRULE
    enum Days {
        MO, TU, WE, TH, FR, SA, SU;

        // display Days
        public static void printDays() {
            int count = 1;
            for (Days freq : EnumSet.allOf(Days.class)) {
                System.out.println(count++ + " : " + freq);
            }
        }

        // get Days
        public static Days getDays(int n) {
            int count = 1;
            for (Days freq : EnumSet.allOf(Days.class)) {
                if (n == count++)
                    return freq;
            }

            return Days.MO;
        }
    }

    // Post constructor
    public PostEvents(EventsManagement eventsManagement) {
        this.eventsManagement = eventsManagement;
    }

    // main caller
    public void postEvents() throws IOException {
        System.out.print("\033[H\033[2J");
        System.out.println("\n------------------------------------------");
        System.out.println("\t\tPost Events");
        System.out.println("------------------------------------------\n");
        while (true) {
            event = new Event();

            // set default values to create an event
            setDefaultValues(event);

            // add recurence rules?
            if (ValidIOHandlers.getYorN("\nIs a resucrsive event? [Y/n]: ")) {
                setReurrenceRules(event);
            }

            // set attendees
            if (ValidIOHandlers.getYorN("\nInvite attendees to event? [Y/n]: ")) {
                setAttendees(event);
            }

            // set remainders
            if (ValidIOHandlers.getYorN("\nSet remainders or use default? [Y/n]: ")) {
                setRemainder(event);
            }

            eventsManagement.postEvent(event);
            System.out.println("Event created!!");

            if (!ValidIOHandlers.getYorN("Continue adding events? [Y/n]: "))
                break;
        }
    }

    /**
     * Get time for start and end event
     * 
     * @param ld LocalDate - YYYY-MM-DD
     * @return a DateTime vale in RFC3339 format
     * @throws IOException
     */
    private DateTime setStartAndEndTime(LocalDate ld) throws IOException {
        int hour = ValidIOHandlers.getChoice("Enter hour(HH)[0 - 23]: ");
        int minute = ValidIOHandlers.getChoice("Enter minute(MM)[0 - 59]: ");

        return new DateTime(Date.from(ld.atTime(LocalTime.of(hour, minute, 00))
                .atZone(ZoneId.systemDefault()).toInstant()));
    }

    /**
     * Default values for an event
     * Summary
     * Description
     * Start and End DateTime values
     * 
     * @param event New event to set default values
     * @throws IOException
     */
    protected void setDefaultValues(Event event) throws IOException {
        // get summary
        String summary = ValidIOHandlers.getString("Event summary: ");

        // get description
        String description = ValidIOHandlers.getString("Enter Description: ");

        // Event starting time
        String start = ValidIOHandlers.getDate("Enter Start date [YYYY-MM-DD]: ", 1);
        LocalDate startTime = LocalDate.parse(start);

        // flag to check if both start and end timeformat are Date/DateTime
        boolean timeFlag = false;
        DateTime sTime = null;
        if (ValidIOHandlers.getYorN("Add start and end time? [Y/n]: ")) {
            timeFlag = true;
            sTime = setStartAndEndTime(startTime);
        } else {
            sTime = new DateTime(Date.from(startTime.atStartOfDay(ZoneId.systemDefault()).toInstant()));
        }

        // Event starting time
        String end = ValidIOHandlers.getDate("Enter End date [YYYY-MM-DD]: ", 1);
        LocalDate endTime = LocalDate.parse(end);
        DateTime eTime = null;
        if (timeFlag) {
            eTime = setStartAndEndTime(endTime);
        } else {
            eTime = new DateTime(Date.from(endTime.atStartOfDay(ZoneId.systemDefault()).toInstant()));
        }

        // set default values
        event.setSummary(summary).setDescription(description)
                .setStart(new EventDateTime().setDateTime(sTime).setTimeZone(TimeZone.getDefault().getID()))
                .setEnd(new EventDateTime().setDateTime(eTime).setTimeZone(TimeZone.getDefault().getID()));
    }

    /**
     * Set RDATE and EXDATE
     * 
     * @param recurrence list of String to store recurrence rule
     * @param name       can be R / EX DATE
     */
    private void setRdateEXdate(List<String> recurrence, String name) {
        // a temporary value to check if no changes occured
        StringBuilder forComp = new StringBuilder(name + "DATE;VALUE=DATE:");
        StringBuilder DATE = new StringBuilder(name + "DATE;VALUE=DATE:");
        System.out.println("Enter 0 to exit...");
        while (true) {
            String recur = ValidIOHandlers.getDate("Enter date [YYYY-MM-DD / 0]: ", 0);

            if (recur.equals("0"))
                break;

            DATE.append(recur.replaceAll("-", "\u0000")).append(",");
        }

        if (DATE.compareTo(forComp) != 0) {
            recurrence.add(DATE.toString());
        }
    }

    /**
     * Get RRULE, RDATE, EXDATE
     * 
     * @param event new event
     * @throws IOException
     */
    protected void setReurrenceRules(Event event) throws IOException {
        List<String> recurrence = new ArrayList<>();
        // a temporary value to check if no changes occured
        StringBuilder forComp = new StringBuilder("RRULE:");
        StringBuilder RRULE = new StringBuilder("RRULE:");

        // set frequency - DAILY, MONTHLY ...
        Frequency.printFrequency();
        int freqn = ValidIOHandlers.getChoice("Enter Frequency value(default = YEARLY) [1 - 7]: ");
        Frequency freq = Frequency.getFrequency(freqn);
        RRULE.append("FREQ=").append(freq);

        // set interval - 1, 2, 3 ...
        if (ValidIOHandlers.getYorN("Set Interval? [Y/n]: ")) {
            int interval = ValidIOHandlers.getChoice("Enter Interval [Number]: ");
            RRULE.append(";").append("INTERVAL=").append(interval);
        }

        // set either count or until - 1, 2, 3 ...
        if (ValidIOHandlers.getYorN("Set Count? [Y/n]: ")) {
            int count = ValidIOHandlers.getChoice("Enter Count [Number]: ");
            RRULE.append(";").append("COUNT=").append(count);
        } else {
            // set until
            if (ValidIOHandlers.getYorN("Set Until by day? [Y/n]: ")) {
                String until = ValidIOHandlers.getDate("Enter Until [YYYY-MM-DD]: ", 1);
                RRULE.append(";").append("UNTIL=").append(until.replaceAll("-", "\u0000"));
            }
        }

        // set day?
        if (ValidIOHandlers.getYorN("Set by week days? [Y/n]: ")) {
            Days.printDays();
            int dayn = ValidIOHandlers.getChoice("Enter Days value(default = MO) [Number]: ");
            Days day = Days.getDays(dayn);
            RRULE.append(";").append("BYDAY=").append(day);
        }

        // set RRULE
        if (RRULE.compareTo(forComp) != 0) {
            recurrence.add(RRULE.toString());
        }

        // set RDATE
        if (ValidIOHandlers.getYorN("Set Recurring Dates? [Y/n]: ")) {
            setRdateEXdate(recurrence, "R");
        }

        // set RDATE
        if (ValidIOHandlers.getYorN("Set Exception Dates? [Y/n]: ")) {
            setRdateEXdate(recurrence, "EX");
        }

        event.setRecurrence(recurrence);
    }

    /**
     * Invite attendees for event
     * 
     * @param event new event
     */
    protected void setAttendees(Event event) {
        List<EventAttendee> attendees = new ArrayList<>();

        System.out.println("Enter 0 to exit...");
        while (true) {
            String email = ValidIOHandlers.getString("Enter an email to invite: ");
            if (email.equals("0"))
                break;

            // check for valid email
            String regex = "^(.+)@(.+)$";
            Pattern pattern = Pattern.compile(regex);
            Matcher matcher = pattern.matcher(email);

            if (!matcher.matches()) {
                System.out.println("Enter a valid email!");
                continue;
            }

            attendees.add(new EventAttendee().setEmail(email));
        }

        // if an attendee is added
        if (attendees.size() > 0) {
            event.setAttendees(attendees);
        }
    }

    /**
     * Set email and popu remainder
     * get time or set default
     * 
     * @param reminders event remainder
     * @param method    email / popup
     */
    private void setRemainderEvents(List<EventReminder> reminders, String method) {
        System.out.println("Enter remainder time: \n");

        if (ValidIOHandlers.getYorN("In Hours? [Y/n]: ")) {
            System.out.print("Enter Hour before actual event: ");
            int hour = Integer.parseInt(System.console().readLine());
            reminders.add(new EventReminder().setMethod(method).setMinutes(hour * 60));
        } else {
            System.out.print("Enter Minutes before actual event: ");
            int minutes = Integer.parseInt(System.console().readLine());
            reminders.add(new EventReminder().setMethod(method).setMinutes(minutes));
        }
    }

    /**
     * Set email and popu remainder
     * 
     * @param event new event
     */
    protected void setRemainder(Event event) {
        List<EventReminder> reminders = new ArrayList<>();

        // Email remainder
        if (ValidIOHandlers.getYorN("Set Email remainder? [Y/n]: ")) {
            setRemainderEvents(reminders, "email");
        } else {
            reminders.add(new EventReminder().setMethod("email").setMinutes(60));
        }

        // Popup remainder
        if (ValidIOHandlers.getYorN("Set Popup remainder? [Y/n]: ")) {
            setRemainderEvents(reminders, "popup");
        } else {
            reminders.add(new EventReminder().setMethod("popup").setMinutes(10));
        }

        event.setReminders(new Event.Reminders().setUseDefault(false).setOverrides(reminders));
    }
}
