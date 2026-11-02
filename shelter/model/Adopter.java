package shelter.model;

import java.util.List;

public class Adopter extends User {
    private String homeType = "-";

    public Adopter(int id, String name, String email, String phone, List<String> channels) {
        super(id, name, email, phone, channels);
    }

    @Override
    public String getRole() { return "Adopter"; }

    public String getHomeType() { return homeType; }
    public void setHomeType(String homeType) { this.homeType = homeType; }

    @Override
    public String getExtraDetails() {
        return " | Home: " + homeType;
    }
}
