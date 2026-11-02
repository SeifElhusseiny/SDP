package shelter.controller;

import java.util.ArrayList;
import java.util.List;

import shelter.model.Event;
import shelter.model.User;
import shelter.model.Volunteer;
import shelter.patterns.observer.Observer;
import shelter.view.ConsoleView;

// Events: CRUD + Observer (registered users are notified of changes).
public class EventController extends BaseController {

    public EventController(ConsoleView view) {
        super(view);
    }

    @Override
    public void run() {
        String[] options = {"Create event", "View all events", "View event by ID (with attendees)",
                "Update event", "Cancel (delete) event", "Register a user (get a ticket)",
                "Assign a volunteer (matched by skill)", "Mark attendance", "Send reminder to attendees"};
        while (true) {
            int choice = view.showMenu("EVENTS", options, "Back");
            switch (choice) {
                case 1: createEvent(); break;
                case 2: view.showList("All events", model.getAllEvents()); break;
                case 3: viewById(); break;
                case 4: updateEvent(); break;
                case 5: cancelEvent(); break;
                case 6: registerUser(); break;
                case 7: assignVolunteer(); break;
                case 8: markAttendance(); break;
                case 9: sendReminder(); break;
                default: return;
            }
            save();
        }
    }

    private void createEvent() {
        String title = view.readText("Title: ");
        String category = view.chooseFrom("Category", model.getEventCategories().getAll());
        String date = view.readText("Date (e.g. 2026-11-20): ");
        String location = view.readText("Location: ");
        int capacity = view.readInt("Capacity: ", 1, 10000);
        Event event = new Event(model.nextEventId(), title, category, date, location, capacity);
        event.setRequiredSkill(chooseSkill());
        model.addEvent(event);
        view.showMessage("Created: " + event);
    }

    private void viewById() {
        Event event = findEvent();
        if (event == null) {
            return;
        }
        view.showMessage(event.toString());
        view.showMessage("Registered (ticket | attended):");
        for (Observer observer : event.getRegisteredUsers()) {
            User user = (User) observer;
            view.showMessage("  " + event.getTicketNumber(user) + " | " + (event.hasAttended(user) ? "attended" : "not yet")
                    + " | " + user.getName() + " (" + user.getRole() + ")");
        }
        view.showList("Volunteer assignments", event.getAssignments());
    }

    private void updateEvent() {
        Event event = findEvent();
        if (event == null) {
            return;
        }
        event.setTitle(view.readOptional("Title", event.getTitle()));
        if (view.confirm("Change category?")) {
            event.setCategory(view.chooseFrom("Category", model.getEventCategories().getAll()));
        }
        event.setDate(view.readOptional("Date", event.getDate()));
        event.setLocation(view.readOptional("Location", event.getLocation()));
        if (view.confirm("Change capacity?")) {
            event.setCapacity(view.readInt("Capacity: ", 1, 10000));
        }
        if (view.confirm("Change the volunteer skill needed?")) {
            event.setRequiredSkill(chooseSkill());
        }
        view.showMessage("Updated: " + event);

        // OBSERVER: everyone registered is told about the change.
        view.showMessage("Notifications sent:");
        event.notifyObservers("Update for \"" + event.getTitle() + "\": now on " + event.getDate()
                + " at " + event.getLocation() + ".");
    }

    private void cancelEvent() {
        Event event = findEvent();
        if (event != null && view.confirm("Cancel \"" + event.getTitle() + "\"?")) {
            view.showMessage("Notifications sent:");
            event.notifyObservers("Sorry, \"" + event.getTitle() + "\" on " + event.getDate() + " was cancelled.");
            model.deleteEvent(event.getId());
            view.showMessage("Event cancelled.");
        }
    }

    private void registerUser() {
        Event event = findEvent();
        if (event == null) {
            return;
        }
        if (event.isFull()) {
            view.showMessage("Sorry, this event is full.");
            return;
        }
        view.showList("Users", model.getAllUsers());
        User user = model.getUser(view.readInt("User ID: "));
        if (user == null) {
            view.showMessage("No user with that ID.");
        } else if (event.isRegistered(user)) {
            view.showMessage(user.getName() + " is already registered.");
        } else {
            event.attach(user);
            String ticket = event.getTicketNumber(user);
            view.showMessage(user.getName() + " registered for " + event.getTitle() + ". Ticket: " + ticket);
            user.update("You are registered for \"" + event.getTitle() + "\" on " + event.getDate()
                    + ". Your ticket number is " + ticket + ".");
        }
    }

    // Volunteers whose skill matches the event are listed first as recommended.
    private void assignVolunteer() {
        Event event = findEvent();
        if (event == null) {
            return;
        }
        List<Volunteer> matching = new ArrayList<>();
        List<Volunteer> others = new ArrayList<>();
        for (User user : model.getUsersByRole("Volunteer")) {
            Volunteer volunteer = (Volunteer) user;
            if (event.getRequiredSkill().equals("Any") || volunteer.getSkill().equals(event.getRequiredSkill())) {
                matching.add(volunteer);
            } else {
                others.add(volunteer);
            }
        }
        if (matching.isEmpty() && others.isEmpty()) {
            view.showMessage("No volunteers yet. Add one from the User Control Panel first.");
            return;
        }
        view.showMessage("Skill needed: " + event.getRequiredSkill());
        view.showList("Recommended (matching skill)", matching);
        view.showList("Other volunteers", others);
        User user = model.getUser(view.readInt("Volunteer ID: "));
        if (!(user instanceof Volunteer)) {
            view.showMessage("That ID is not a volunteer.");
            return;
        }
        Volunteer volunteer = (Volunteer) user;
        if (event.isAssigned(volunteer)) {
            view.showMessage(volunteer.getName() + " is already assigned to this event.");
            return;
        }
        if (!matching.contains(volunteer) && !view.confirm(volunteer.getName() + "'s skill does not match. Assign anyway?")) {
            return;
        }
        String task = view.readText("Task (e.g. walk the dogs, registration desk): ");
        event.assignVolunteer(volunteer, task);
        view.showMessage("Assigned: " + volunteer.getName() + " -> " + task);
        volunteer.update("You are assigned to \"" + task + "\" at \"" + event.getTitle() + "\" on " + event.getDate() + ".");
    }

    private void markAttendance() {
        Event event = findEvent();
        if (event == null) {
            return;
        }
        List<Observer> registered = event.getRegisteredUsers();
        if (registered.isEmpty()) {
            view.showMessage("Nobody is registered for this event yet.");
            return;
        }
        for (Observer observer : registered) {
            User user = (User) observer;
            view.showMessage("  #" + user.getId() + " " + user.getName() + " | " + event.getTicketNumber(user)
                    + " | " + (event.hasAttended(user) ? "attended" : "not yet"));
        }
        User user = model.getUser(view.readInt("User ID who attended: "));
        if (user == null || !event.isRegistered(user)) {
            view.showMessage("That user is not registered for this event.");
            return;
        }
        event.markAttended(user);
        view.showMessage(user.getName() + " marked as attended. (" + event.getAttendedCount() + "/"
                + registered.size() + " attended)");
    }

    private void sendReminder() {
        Event event = findEvent();
        if (event == null) {
            return;
        }
        if (event.getRegisteredUsers().isEmpty()) {
            view.showMessage("Nobody is registered for this event yet.");
            return;
        }
        view.showMessage("Reminders sent:");
        event.notifyObservers("Reminder: \"" + event.getTitle() + "\" is on " + event.getDate()
                + " at " + event.getLocation() + ". See you there!");
    }

    private String chooseSkill() {
        List<String> skills = new ArrayList<>(model.getVolunteerSkills().getAll());
        skills.add(0, "Any");
        return view.chooseFrom("Volunteer skill needed for this event", skills);
    }

    private Event findEvent() {
        view.showList("Events", model.getAllEvents());
        Event event = model.getEvent(view.readInt("Event ID: "));
        if (event == null) {
            view.showMessage("No event with that ID.");
        }
        return event;
    }
}
