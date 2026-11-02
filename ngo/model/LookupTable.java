package ngo.model;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class LookupTable<T> {
    private final Map<String, T> values = new LinkedHashMap<>();

    public void add(String key, T value) {
        values.put(key, value);
    }

    public boolean update(String key, T value) {
        if (!values.containsKey(key)) {
            return false;
        }
        values.put(key, value);
        return true;
    }

    public boolean delete(String key) {
        return values.remove(key) != null;
    }

    public T get(String key) {
        return values.get(key);
    }

    public List<T> listValues() {
        return new ArrayList<>(values.values());
    }

    public Set<String> keys() {
        return values.keySet();
    }

    public boolean containsKey(String key) {
        return values.containsKey(key);
    }
}
