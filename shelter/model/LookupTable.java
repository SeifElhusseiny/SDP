package shelter.model;

import java.util.ArrayList;
import java.util.List;

// A small reference table (e.g. species) that the admin can fully CRUD.
public class LookupTable {
    private final String name;
    private final List<String> values = new ArrayList<>();

    public LookupTable(String name, String... initialValues) {
        this.name = name;
        for (String value : initialValues) {
            values.add(value);
        }
    }

    public String getName() { return name; }

    public boolean add(String value) {
        if (value.isEmpty() || values.contains(value)) {
            return false;
        }
        values.add(value);
        return true;
    }

    // Replaces all values (used when loading from the database).
    public void setValues(List<String> newValues) {
        values.clear();
        values.addAll(newValues);
    }

    public List<String> getAll() {
        return new ArrayList<>(values);
    }

    public boolean update(int index, String newValue) {
        if (index < 0 || index >= values.size() || newValue.isEmpty()) {
            return false;
        }
        values.set(index, newValue);
        return true;
    }

    public boolean delete(int index) {
        if (index < 0 || index >= values.size()) {
            return false;
        }
        values.remove(index);
        return true;
    }

    @Override
    public String toString() {
        return name;
    }
}
