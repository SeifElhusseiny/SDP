package shelter.patterns.observer;

// OBSERVER pattern: anything that wants to be notified implements this.
public interface Observer {
    void update(String message);
}
