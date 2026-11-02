package shelter.model;

import java.time.LocalDate;

// One resource or service given to an animal (food, vaccine, vet check-up, ...).
public class CareRecord {
    private final String item;
    private final int quantity;
    private final LocalDate date;

    public CareRecord(String item, int quantity) {
        this.item = item;
        this.quantity = quantity;
        this.date = LocalDate.now();
    }

    // Used when loading a saved record (keeps its original date).
    public CareRecord(String item, int quantity, LocalDate date) {
        this.item = item;
        this.quantity = quantity;
        this.date = date;
    }

    public String getItem() { return item; }
    public int getQuantity() { return quantity; }
    public LocalDate getDate() { return date; }

    @Override
    public String toString() {
        return date + " | " + item + " x" + quantity;
    }
}
