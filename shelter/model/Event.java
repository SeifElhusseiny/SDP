package shelter.model;

import java.util.ArrayList;
import java.util.List;

import shelter.patterns.observer.Observer;
import shelter.patterns.observer.Subject;

// An event is an OBSERVER Subject: users who register are attached,
// and they are notified when the event changes or is cancelled.
public class Event implements Subject {
    private final int id;
    private String title;
    private String category;
    private String date;
    private String location;
    private int capacity;
    private String requiredSkill = "Any";
    private final List<Observer> observers = new ArrayList<>();
    private final List<Observer> attended = new ArrayList<>();
    private final List<VolunteerAssignment> assignments = new ArrayList<>();

    public Event(int id, String title, String category, String date, String location, int capacity) {
        this.id = id;
        this.title = title;
        this.category = category;
        this.date = date;
        this.location = location;
        this.capacity = capacity;
    }

    @Override
    public void attach(Observer observer) {
        if (!observers.contains(observer)) {
            observers.add(observer);
        }
    }

    @Override
    public void detach(Observer observer) {
        observers.remove(observer);
    }

    @Override
    public void notifyObservers(String message) {
        for (Observer observer : observers) {
            observer.update(message);
        }
    }

    // Ticket number given to a registered user, e.g. EVT1-U3.
    public String getTicketNumber(User user) {
        return "EVT" + id + "-U" + user.getId();
    }

    public void markAttended(Observer observer) {
        if (observers.contains(observer) && !attended.contains(observer)) {
            attended.add(observer);
        }
    }

    public boolean hasAttended(Observer observer) { return attended.contains(observer); }
    public int getAttendedCount() { return attended.size(); }

    // Assigning a volunteer also attaches them, so they get event updates too.
    public void assignVolunteer(Volunteer volunteer, String task) {
        assignments.add(new VolunteerAssignment(volunteer, task));
        attach(volunteer);
    }

    public boolean isAssigned(Volunteer volunteer) {
        for (VolunteerAssignment assignment : assignments) {
            if (assignment.getVolunteer() == volunteer) {
                return true;
            }
        }
        return false;
    }

    public List<VolunteerAssignment> getAssignments() { return new ArrayList<>(assignments); }

    // Used when a user is deleted from the system.
    public void removeUser(User user) {
        observers.remove(user);
        attended.remove(user);
        assignments.removeIf(assignment -> assignment.getVolunteer() == user);
    }

    public boolean isFull() { return observers.size() >= capacity; }
    public boolean isRegistered(Observer observer) { return observers.contains(observer); }
    public List<Observer> getRegisteredUsers() { return new ArrayList<>(observers); }

    public int getId() { return id; }
    public String getTitle() { return title; }
    public String getCategory() { return category; }
    public String getDate() { return date; }
    public String getLocation() { return location; }
    public int getCapacity() { return capacity; }
    public String getRequiredSkill() { return requiredSkill; }
    public void setRequiredSkill(String requiredSkill) { this.requiredSkill = requiredSkill; }

    public void setTitle(String title) { this.title = title; }
    public void setCategory(String category) { this.category = category; }
    public void setDate(String date) { this.date = date; }
    public void setLocation(String location) { this.location = location; }
    public void setCapacity(int capacity) { this.capacity = capacity; }

    @Override
    public String toString() {
        return "#" + id + " | " + title + " | " + category + " | " + date + " | " + location
                + " | Needs volunteers for: " + requiredSkill
                + " | Registered: " + observers.size() + "/" + capacity + " | Attended: " + attended.size();
    }
}
