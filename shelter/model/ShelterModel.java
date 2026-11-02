package shelter.model;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

// SINGLETON pattern: the one shared data store of the whole system.
// The constructor is private, so the only way to get it is getInstance().
public class ShelterModel {
    private static ShelterModel instance;

    private final Map<Integer, User> users = new LinkedHashMap<>();
    private final Map<Integer, Animal> animals = new LinkedHashMap<>();
    private final Map<Integer, Donation> donations = new LinkedHashMap<>();
    private final Map<Integer, Event> events = new LinkedHashMap<>();
    private final Map<Integer, AdoptionRequest> adoptions = new LinkedHashMap<>();

    private final LookupTable species = new LookupTable("Species", "Dog", "Cat", "Rabbit", "Bird");
    private final LookupTable volunteerSkills = new LookupTable("Volunteer skills",
            "Dog walking", "Cleaning", "Vet assistance", "Fostering");
    private final LookupTable eventCategories = new LookupTable("Event categories",
            "Adoption day", "Vaccination campaign", "Fundraiser");
    private final LookupTable donationTypes = new LookupTable("Donation types",
            "Money", "Pet food", "Medicine", "Blankets");
    private final LookupTable careTypes = new LookupTable("Care and resource types",
            "Food", "Vaccine", "Medicine", "Vet check-up", "Grooming");

    private int nextUserId = 1;
    private int nextAnimalId = 1;
    private int nextDonationId = 1;
    private int nextEventId = 1;
    private int nextAdoptionId = 1;

    private ShelterModel() {
    }

    public static ShelterModel getInstance() {
        if (instance == null) {
            instance = new ShelterModel();
        }
        return instance;
    }

    // ---------- ID generators ----------
    public int nextUserId() { return nextUserId++; }
    public int nextAnimalId() { return nextAnimalId++; }
    public int nextDonationId() { return nextDonationId++; }
    public int nextEventId() { return nextEventId++; }
    public int nextAdoptionId() { return nextAdoptionId++; }

    // After loading saved data, new IDs continue after the highest saved ID.
    public void recalculateIds() {
        nextUserId = maxKey(users) + 1;
        nextAnimalId = maxKey(animals) + 1;
        nextDonationId = maxKey(donations) + 1;
        nextEventId = maxKey(events) + 1;
        nextAdoptionId = maxKey(adoptions) + 1;
    }

    private int maxKey(Map<Integer, ?> map) {
        int max = 0;
        for (int key : map.keySet()) {
            max = Math.max(max, key);
        }
        return max;
    }

    // ---------- Users ----------
    public void addUser(User user) { users.put(user.getId(), user); }
    public User getUser(int id) { return users.get(id); }
    public List<User> getAllUsers() { return new ArrayList<>(users.values()); }
    public boolean deleteUser(int id) { return users.remove(id) != null; }

    public List<User> getUsersByRole(String role) {
        List<User> result = new ArrayList<>();
        for (User user : users.values()) {
            if (user.getRole().equals(role)) {
                result.add(user);
            }
        }
        return result;
    }

    // ---------- Animals ----------
    public void addAnimal(Animal animal) { animals.put(animal.getId(), animal); }
    public Animal getAnimal(int id) { return animals.get(id); }
    public List<Animal> getAllAnimals() { return new ArrayList<>(animals.values()); }
    public boolean deleteAnimal(int id) { return animals.remove(id) != null; }

    // ---------- Donations ----------
    public void addDonation(Donation donation) { donations.put(donation.getId(), donation); }
    public Donation getDonation(int id) { return donations.get(id); }
    public List<Donation> getAllDonations() { return new ArrayList<>(donations.values()); }
    public boolean deleteDonation(int id) { return donations.remove(id) != null; }

    // ---------- Events ----------
    public void addEvent(Event event) { events.put(event.getId(), event); }
    public Event getEvent(int id) { return events.get(id); }
    public List<Event> getAllEvents() { return new ArrayList<>(events.values()); }
    public boolean deleteEvent(int id) { return events.remove(id) != null; }

    // ---------- Adoptions ----------
    public void addAdoption(AdoptionRequest request) { adoptions.put(request.getId(), request); }
    public AdoptionRequest getAdoption(int id) { return adoptions.get(id); }
    public List<AdoptionRequest> getAllAdoptions() { return new ArrayList<>(adoptions.values()); }
    public boolean deleteAdoption(int id) { return adoptions.remove(id) != null; }

    // ---------- Lookup tables ----------
    public LookupTable getSpecies() { return species; }
    public LookupTable getVolunteerSkills() { return volunteerSkills; }
    public LookupTable getEventCategories() { return eventCategories; }
    public LookupTable getDonationTypes() { return donationTypes; }
    public LookupTable getCareTypes() { return careTypes; }

    public List<LookupTable> getAllLookupTables() {
        List<LookupTable> tables = new ArrayList<>();
        tables.add(species);
        tables.add(volunteerSkills);
        tables.add(eventCategories);
        tables.add(donationTypes);
        tables.add(careTypes);
        return tables;
    }
}
