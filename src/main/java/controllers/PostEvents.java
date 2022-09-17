package controllers;

import com.google.api.client.util.DateTime;
import com.google.api.services.calendar.model.Event;
import com.google.api.services.calendar.model.EventAttendee;
import com.google.api.services.calendar.model.EventDateTime;
import com.google.api.services.calendar.model.EventReminder;

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

            setDefaultValues(event);

            // add recurence rules?
            System.out.print("Is a resucrsive event?(y/n): ");
            if (System.console().readLine().toLowerCase().equals("y")) {
                setReurrenceRules(event);
            }

            // set attendees
            System.out.print("Invite attendees to event?(y/n): ");
            if (System.console().readLine().toLowerCase().equals("y")) {
                setAttendees(event);
            }

            // set remainders
            System.out.print("Set remainders or use default?(y/n): ");
            if (System.console().readLine().toLowerCase().equals("y")) {
                setRemainder(event);
            }

            eventsManagement.postEvent(event);

            System.out.println("Event created!!");

            break;
        }
    }

    // get time for start and end
    private DateTime setStartAndEndTime(LocalDate ld) {
        System.out.print("Enter hour(HH)[0 - 23]: ");
        int hour = Integer.parseInt(System.console().readLine());
        System.out.print("Enter minute(MM)[0 - 59]: ");
        int minute = Integer.parseInt(System.console().readLine());

        return new DateTime(Date.from(ld.atTime(LocalTime.of(hour, minute, 00))
                .atZone(ZoneId.systemDefault()).toInstant()));

    }

    // default values for an event
    protected void setDefaultValues(Event event) {
        System.out.print("Event summary: ");
        String summary = System.console().readLine();

        System.out.print("Enter Description: ");
        String description = System.console().readLine();

        // Start time
        System.out.print("Enter Start date(YYYY-MM-DD): ");
        String start = System.console().readLine();
        LocalDate startTime = LocalDate.parse(start);

        boolean timeFlag = false;
        DateTime sTime = null;
        System.out.print("Add start and end time?(y/n): ");
        if (System.console().readLine().toLowerCase().equals("y")) {
            timeFlag = true;
            sTime = setStartAndEndTime(startTime);
        } else {
            sTime = new DateTime(Date.from(startTime.atStartOfDay(ZoneId.systemDefault()).toInstant()));
        }

        // End time
        System.out.print("Enter End date(YYYY-MM-DD): ");
        String end = System.console().readLine();
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

    // set RDATE and EXDATE
    private void setRdateEXdate(List<String> recurrence, String name) {
        StringBuilder forComp = new StringBuilder(name + "DATE;VALUE=DATE:");
        StringBuilder DATE = new StringBuilder(name + "DATE;VALUE=DATE:");
        System.out.println("Enter 0 to exit...");
        while (true) {
            System.out.print("Enter date (YYYY-MM-DD): ");
            String recur = System.console().readLine();

            if (recur.equals("0"))
                break;

            DATE.append(recur.replaceAll("-", "\u0000")).append(",");
        }

        if (DATE.compareTo(forComp) != 0) {
            recurrence.add(DATE.toString());
        }
    }

    // get RRULE, RDATE, EXDATE
    protected void setReurrenceRules(Event event) {
        List<String> recurrence = new ArrayList<>();
        StringBuilder forComp = new StringBuilder("RRULE:");
        StringBuilder RRULE = new StringBuilder("RRULE:");

        // set frequency
        Frequency.printFrequency();
        System.out.print("Enter Frequency value: ");
        int freqn = Integer.parseInt(System.console().readLine());
        Frequency freq = Frequency.getFrequency(freqn);
        RRULE.append("FREQ=").append(freq);

        // set interval
        System.out.print("Set Interval?(y/n): ");
        if (System.console().readLine().toLowerCase().equals("y")) {
            System.out.print("Enter Interval (Number): ");
            int interval = Integer.parseInt(System.console().readLine());
            RRULE.append(";").append("INTERVAL=").append(interval);
        }

        // set either count or until
        System.out.print("Set Count?(y/n): ");
        if (System.console().readLine().toLowerCase().equals("y")) {
            System.out.print("Enter Count (Number): ");
            int count = Integer.parseInt(System.console().readLine());
            RRULE.append(";").append("COUNT=").append(count);
        } else {
            // set until
            System.out.print("Set Until by day?(y/n): ");
            if (System.console().readLine().toLowerCase().equals("y")) {
                System.out.print("Enter Until (YYYY-MM-DD): ");
                String until = System.console().readLine();
                RRULE.append(";").append("UNTIL=").append(until.replaceAll("-", "\u0000"));
            }
        }

        // set day?
        System.out.print("Set by week days?(y/n): ");
        if (System.console().readLine().toLowerCase().equals("y")) {
            Days.printDays();
            System.out.print("Enter Days value: ");
            int dayn = Integer.parseInt(System.console().readLine());
            Days day = Days.getDays(dayn);
            RRULE.append(";").append("BYDAY=").append(day);
        }

        // set RRULE
        if (RRULE.compareTo(forComp) != 0) {
            recurrence.add(RRULE.toString());
        }

        // set RDATE
        System.out.print("Set Recurring Dates?(y/n): ");
        if (System.console().readLine().toLowerCase().equals("y")) {
            setRdateEXdate(recurrence, "R");
        }

        // set RDATE
        System.out.print("Set Exception Dates?(y/n): ");
        if (System.console().readLine().toLowerCase().equals("y")) {
            setRdateEXdate(recurrence, "EX");
        }

        event.setRecurrence(recurrence);
    }

    // invite attendees for event
    protected void setAttendees(Event event) {
        List<EventAttendee> attendees = new ArrayList<>();

        System.out.println("Enter 0 to exit...");
        while (true) {
            System.out.print("Enter an email to invite: ");
            String email = System.console().readLine().toLowerCase();
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

        if (attendees.size() > 0) {
            event.setAttendees(attendees);
        }
    }

    private void setRemainderEvents(List<EventReminder> reminders, String method) {
        System.out.println("Enter remainder time: ");

        System.out.print("In Hours?(y/n): ");
        if (System.console().readLine().toLowerCase().equals("y")) {
            System.out.print("Enter Hour before actual event: ");
            int hour = Integer.parseInt(System.console().readLine());
            reminders.add(new EventReminder().setMethod(method).setMinutes(hour * 60));
        } else {
            System.out.print("Enter Minutes before actual event: ");
            int minutes = Integer.parseInt(System.console().readLine());
            reminders.add(new EventReminder().setMethod(method).setMinutes(minutes));
        }
    }

    // set remainder
    protected void setRemainder(Event event) {
        List<EventReminder> reminders = new ArrayList<>();

        System.out.print("Set Email remainder?(y/n): ");
        if (System.console().readLine().toLowerCase().equals("y")) {
            setRemainderEvents(reminders, "email");
        } else {
            reminders.add(new EventReminder().setMethod("email").setMinutes(60));
        }

        System.out.print("Set Popup remainder?(y/n): ");
        if (System.console().readLine().toLowerCase().equals("y")) {
            setRemainderEvents(reminders, "popup");
        } else {
            reminders.add(new EventReminder().setMethod("popuo").setMinutes(10));
        }

        event.setReminders(new Event.Reminders().setUseDefault(false).setOverrides(reminders));
    }
}
