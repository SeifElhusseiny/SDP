package shelter.patterns.factory;

import java.util.List;

import shelter.model.Adopter;
import shelter.model.Admin;
import shelter.model.Donor;
import shelter.model.User;
import shelter.model.Volunteer;

// FACTORY pattern: creates the right kind of user from a type name.
public class UserFactory {
    public static final String[] USER_TYPES = {"Donor", "Volunteer", "Adopter", "Admin"};

    public static User createUser(String type, int id, String name, String email, String phone, List<String> channels) {
        switch (type) {
            case "Donor":
                return new Donor(id, name, email, phone, channels);
            case "Volunteer":
                return new Volunteer(id, name, email, phone, channels);
            case "Adopter":
                return new Adopter(id, name, email, phone, channels);
            case "Admin":
                return new Admin(id, name, email, phone, channels);
            default:
                throw new IllegalArgumentException("Unknown user type: " + type);
        }
    }
}
