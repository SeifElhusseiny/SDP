package shelter.patterns.observer;

// OBSERVER pattern: anything that sends notifications implements this.
public interface Subject {
    void attach(Observer observer);
    void detach(Observer observer);
    void notifyObservers(String message);
}
