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
    private final List<Observer> observers = new ArrayList<>();

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

    public boolean isFull() { return observers.size() >= capacity; }
    public boolean isRegistered(Observer observer) { return observers.contains(observer); }
    public List<Observer> getRegisteredUsers() { return new ArrayList<>(observers); }

    public int getId() { return id; }
    public String getTitle() { return title; }
    public String getCategory() { return category; }
    public String getDate() { return date; }
    public String getLocation() { return location; }
    public int getCapacity() { return capacity; }

    public void setTitle(String title) { this.title = title; }
    public void setCategory(String category) { this.category = category; }
    public void setDate(String date) { this.date = date; }
    public void setLocation(String location) { this.location = location; }
    public void setCapacity(int capacity) { this.capacity = capacity; }

    @Override
    public String toString() {
        return "#" + id + " | " + title + " | " + category + " | " + date + " | " + location
                + " | Registered: " + observers.size() + "/" + capacity;
    }
}
