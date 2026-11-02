package shelter.model;

// The shelter's beneficiaries.
public class Animal {
    public static final String[] STATUSES = {"Available", "Under treatment", "Adopted"};
    public static final String[] HEALTH = {"Healthy", "Sick", "Injured", "Recovering"};

    private final int id;
    private String name;
    private String species;
    private String breed;
    private int age;
    private String healthStatus;
    private String status;

    public Animal(int id, String name, String species, String breed, int age, String healthStatus) {
        this.id = id;
        this.name = name;
        this.species = species;
        this.breed = breed;
        this.age = age;
        this.healthStatus = healthStatus;
        this.status = "Available";
    }

    public int getId() { return id; }
    public String getName() { return name; }
    public String getSpecies() { return species; }
    public String getBreed() { return breed; }
    public int getAge() { return age; }
    public String getHealthStatus() { return healthStatus; }
    public String getStatus() { return status; }

    public void setName(String name) { this.name = name; }
    public void setSpecies(String species) { this.species = species; }
    public void setBreed(String breed) { this.breed = breed; }
    public void setAge(int age) { this.age = age; }
    public void setHealthStatus(String healthStatus) { this.healthStatus = healthStatus; }
    public void setStatus(String status) { this.status = status; }

    @Override
    public String toString() {
        return "#" + id + " | " + name + " | " + species + " (" + breed + ") | Age: " + age
                + " | Health: " + healthStatus + " | Status: " + status;
    }
}
