package ngo.model;

public class Event {
    private final int id;
    private final String title;
    private final String type;
    private final String date;
    private final int capacity;

    public Event(int id, String title, String type, String date, int capacity) {
        this.id = id;
        this.title = title;
        this.type = type;
        this.date = date;
        this.capacity = capacity;
    }

    public int getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getType() {
        return type;
    }

    public String getDate() {
        return date;
    }

    public int getCapacity() {
        return capacity;
    }

    @Override
    public String toString() {
        return "Event #" + id + " | " + title + " | Type: " + type + " | Date: " + date + " | Capacity: " + capacity;
    }
}
