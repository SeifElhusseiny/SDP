package shelter.model;

import java.util.List;

public class Admin extends User {
    public Admin(int id, String name, String email, String phone, List<String> channels) {
        super(id, name, email, phone, channels);
    }

    @Override
    public String getRole() { return "Admin"; }
}
