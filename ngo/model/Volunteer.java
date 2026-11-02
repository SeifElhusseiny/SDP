package ngo.model;

public class Volunteer {
    private final int id;
    private final String name;
    private final String skill;
    private final String availability;

    public Volunteer(int id, String name, String skill, String availability) {
        this.id = id;
        this.name = name;
        this.skill = skill;
        this.availability = availability;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getSkill() {
        return skill;
    }

    public String getAvailability() {
        return availability;
    }

    @Override
    public String toString() {
        return "Volunteer #" + id + " | " + name + " | Skill: " + skill + " | Available: " + availability;
    }
}
