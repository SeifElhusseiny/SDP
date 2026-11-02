package shelter.database;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import shelter.model.Adopter;
import shelter.model.AdoptionRequest;
import shelter.model.Animal;
import shelter.model.CareRecord;
import shelter.model.Donation;
import shelter.model.Donor;
import shelter.model.Event;
import shelter.model.LookupTable;
import shelter.model.ShelterModel;
import shelter.model.User;
import shelter.model.Volunteer;
import shelter.model.VolunteerAssignment;
import shelter.patterns.factory.UserFactory;
import shelter.patterns.observer.Observer;

// SINGLETON pattern: the one object that reads and writes the JSON files.
// The files are created only once (the first time the program runs);
// after that they are loaded on start and saved after every change.
public class JsonDatabase {
    private static JsonDatabase instance;

    private static final String FOLDER = "data";
    private static final String USERS = "users.json";
    private static final String ANIMALS = "animals.json";
    private static final String DONATIONS = "donations.json";
    private static final String EVENTS = "events.json";
    private static final String ADOPTIONS = "adoptions.json";
    private static final String LOOKUPS = "lookups.json";
    private static final String[] FILES = {USERS, ANIMALS, DONATIONS, EVENTS, ADOPTIONS, LOOKUPS};

    private final boolean firstRun;

    private JsonDatabase() {
        File folder = new File(FOLDER);
        firstRun = !new File(folder, USERS).exists();
        folder.mkdirs();
        for (String name : FILES) {
            File file = new File(folder, name);
            if (!file.exists()) {
                writeFile(name, name.equals(LOOKUPS) ? "{}" : "[]");
            }
        }
    }

    public static JsonDatabase getInstance() {
        if (instance == null) {
            instance = new JsonDatabase();
        }
        return instance;
    }

    public boolean isFirstRun() {
        return firstRun;
    }

    public String getFolderPath() {
        return new File(FOLDER).getAbsolutePath();
    }

    // ============================== SAVE ==============================

    public void save(ShelterModel model) {
        List<Object> users = new ArrayList<>();
        for (User user : model.getAllUsers()) {
            users.add(userToMap(user));
        }
        writeFile(USERS, Json.write(users));

        List<Object> animals = new ArrayList<>();
        for (Animal animal : model.getAllAnimals()) {
            animals.add(animalToMap(animal));
        }
        writeFile(ANIMALS, Json.write(animals));

        List<Object> donations = new ArrayList<>();
        for (Donation donation : model.getAllDonations()) {
            Map<String, Object> map = new LinkedHashMap<>();
            map.put("id", donation.getId());
            map.put("donorId", donation.getDonor().getId());
            map.put("type", donation.getType());
            map.put("amount", donation.getAmount());
            map.put("paymentMethod", donation.getPaymentMethod());
            map.put("date", donation.getDate().toString());
            donations.add(map);
        }
        writeFile(DONATIONS, Json.write(donations));

        List<Object> events = new ArrayList<>();
        for (Event event : model.getAllEvents()) {
            events.add(eventToMap(event));
        }
        writeFile(EVENTS, Json.write(events));

        List<Object> adoptions = new ArrayList<>();
        for (AdoptionRequest request : model.getAllAdoptions()) {
            Map<String, Object> map = new LinkedHashMap<>();
            map.put("id", request.getId());
            map.put("adopterId", request.getAdopter().getId());
            map.put("animalId", request.getAnimal().getId());
            map.put("addOns", request.getAddOns());
            map.put("status", request.getStatus());
            adoptions.add(map);
        }
        writeFile(ADOPTIONS, Json.write(adoptions));

        Map<String, Object> lookups = new LinkedHashMap<>();
        for (LookupTable table : model.getAllLookupTables()) {
            lookups.put(table.getName(), table.getAll());
        }
        writeFile(LOOKUPS, Json.write(lookups));
    }

    private Map<String, Object> userToMap(User user) {
        Map<String, Object> map = new LinkedHashMap<>();
        map.put("id", user.getId());
        map.put("role", user.getRole());
        map.put("name", user.getName());
        map.put("email", user.getEmail());
        map.put("phone", user.getPhone());
        map.put("channels", user.getPreferredChannels());
        if (user instanceof Donor) {
            map.put("totalDonated", ((Donor) user).getTotalDonated());
        } else if (user instanceof Volunteer) {
            Volunteer volunteer = (Volunteer) user;
            map.put("skill", volunteer.getSkill());
            map.put("availability", volunteer.getAvailability());
            map.put("hoursWorked", volunteer.getHoursWorked());
        } else if (user instanceof Adopter) {
            map.put("homeType", ((Adopter) user).getHomeType());
        }
        return map;
    }

    private Map<String, Object> animalToMap(Animal animal) {
        Map<String, Object> map = new LinkedHashMap<>();
        map.put("id", animal.getId());
        map.put("name", animal.getName());
        map.put("species", animal.getSpecies());
        map.put("breed", animal.getBreed());
        map.put("age", animal.getAge());
        map.put("healthStatus", animal.getHealthStatus());
        map.put("status", animal.getStatus());
        map.put("needs", animal.getNeeds());
        List<Object> care = new ArrayList<>();
        for (CareRecord record : animal.getCareRecords()) {
            Map<String, Object> recordMap = new LinkedHashMap<>();
            recordMap.put("item", record.getItem());
            recordMap.put("quantity", record.getQuantity());
            recordMap.put("date", record.getDate().toString());
            care.add(recordMap);
        }
        map.put("careRecords", care);
        return map;
    }

    private Map<String, Object> eventToMap(Event event) {
        Map<String, Object> map = new LinkedHashMap<>();
        map.put("id", event.getId());
        map.put("title", event.getTitle());
        map.put("category", event.getCategory());
        map.put("date", event.getDate());
        map.put("location", event.getLocation());
        map.put("capacity", event.getCapacity());
        map.put("requiredSkill", event.getRequiredSkill());
        List<Object> registered = new ArrayList<>();
        List<Object> attended = new ArrayList<>();
        for (Observer observer : event.getRegisteredUsers()) {
            User user = (User) observer;
            registered.add(user.getId());
            if (event.hasAttended(user)) {
                attended.add(user.getId());
            }
        }
        map.put("registeredUserIds", registered);
        map.put("attendedUserIds", attended);
        List<Object> assignments = new ArrayList<>();
        for (VolunteerAssignment assignment : event.getAssignments()) {
            Map<String, Object> assignmentMap = new LinkedHashMap<>();
            assignmentMap.put("volunteerId", assignment.getVolunteer().getId());
            assignmentMap.put("task", assignment.getTask());
            assignments.add(assignmentMap);
        }
        map.put("assignments", assignments);
        return map;
    }

    // ============================== LOAD ==============================

    public void load(ShelterModel model) {
        // Users first, because donations, events and adoptions point to users.
        for (Map<String, Object> map : readList(USERS)) {
            User user = UserFactory.createUser(text(map, "role"), number(map, "id"), text(map, "name"),
                    text(map, "email"), text(map, "phone"), textList(map, "channels"));
            if (user instanceof Donor) {
                ((Donor) user).setTotalDonated(decimal(map, "totalDonated"));
            } else if (user instanceof Volunteer) {
                Volunteer volunteer = (Volunteer) user;
                volunteer.setSkill(text(map, "skill"));
                volunteer.setAvailability(text(map, "availability"));
                volunteer.setHoursWorked(decimal(map, "hoursWorked"));
            } else if (user instanceof Adopter) {
                ((Adopter) user).setHomeType(text(map, "homeType"));
            }
            model.addUser(user);
        }

        for (Map<String, Object> map : readList(ANIMALS)) {
            Animal animal = new Animal(number(map, "id"), text(map, "name"), text(map, "species"),
                    text(map, "breed"), number(map, "age"), text(map, "healthStatus"));
            animal.setStatus(text(map, "status"));
            animal.setNeeds(text(map, "needs"));
            for (Map<String, Object> record : mapList(map, "careRecords")) {
                animal.addCareRecord(new CareRecord(text(record, "item"), number(record, "quantity"),
                        LocalDate.parse(text(record, "date"))));
            }
            model.addAnimal(animal);
        }

        for (Map<String, Object> map : readList(DONATIONS)) {
            User donor = model.getUser(number(map, "donorId"));
            if (donor instanceof Donor) {
                model.addDonation(new Donation(number(map, "id"), (Donor) donor, text(map, "type"),
                        decimal(map, "amount"), text(map, "paymentMethod"), LocalDate.parse(text(map, "date"))));
            }
        }

        for (Map<String, Object> map : readList(EVENTS)) {
            Event event = new Event(number(map, "id"), text(map, "title"), text(map, "category"),
                    text(map, "date"), text(map, "location"), number(map, "capacity"));
            event.setRequiredSkill(text(map, "requiredSkill"));
            for (Object id : (List<?>) map.get("registeredUserIds")) {
                User user = model.getUser(((Number) id).intValue());
                if (user != null) {
                    event.attach(user);
                }
            }
            for (Object id : (List<?>) map.get("attendedUserIds")) {
                User user = model.getUser(((Number) id).intValue());
                if (user != null) {
                    event.markAttended(user);
                }
            }
            for (Map<String, Object> assignment : mapList(map, "assignments")) {
                User user = model.getUser(number(assignment, "volunteerId"));
                if (user instanceof Volunteer) {
                    event.assignVolunteer((Volunteer) user, text(assignment, "task"));
                }
            }
            model.addEvent(event);
        }

        for (Map<String, Object> map : readList(ADOPTIONS)) {
            User adopter = model.getUser(number(map, "adopterId"));
            Animal animal = model.getAnimal(number(map, "animalId"));
            if (adopter instanceof Adopter && animal != null) {
                // DECORATOR: the package is rebuilt from the saved add-on names.
                AdoptionRequest request = new AdoptionRequest(number(map, "id"), (Adopter) adopter, animal,
                        textList(map, "addOns"));
                request.setStatus(text(map, "status"));
                model.addAdoption(request);
            }
        }

        Map<String, Object> lookups = readMap(LOOKUPS);
        for (LookupTable table : model.getAllLookupTables()) {
            if (lookups.containsKey(table.getName())) {
                table.setValues(textList(lookups, table.getName()));
            }
        }

        model.recalculateIds();
    }

    // ============================== FILE HELPERS ==============================

    private void writeFile(String name, String content) {
        try {
            Files.write(new File(FOLDER, name).toPath(), content.getBytes(StandardCharsets.UTF_8));
        } catch (IOException e) {
            System.out.println("Warning: could not save " + name + " (" + e.getMessage() + ")");
        }
    }

    private Object readFile(String name) {
        try {
            String content = new String(Files.readAllBytes(new File(FOLDER, name).toPath()), StandardCharsets.UTF_8);
            return Json.parse(content);
        } catch (IOException | RuntimeException e) {
            System.out.println("Warning: could not read " + name + " (" + e.getMessage() + "). Starting it empty.");
            return null;
        }
    }

    @SuppressWarnings("unchecked")
    private List<Map<String, Object>> readList(String name) {
        Object data = readFile(name);
        return data instanceof List ? (List<Map<String, Object>>) data : new ArrayList<>();
    }

    @SuppressWarnings("unchecked")
    private Map<String, Object> readMap(String name) {
        Object data = readFile(name);
        return data instanceof Map ? (Map<String, Object>) data : new LinkedHashMap<>();
    }

    // ============================== VALUE HELPERS ==============================

    private String text(Map<String, Object> map, String key) {
        Object value = map.get(key);
        return value == null ? "" : value.toString();
    }

    private int number(Map<String, Object> map, String key) {
        return ((Number) map.get(key)).intValue();
    }

    private double decimal(Map<String, Object> map, String key) {
        return ((Number) map.get(key)).doubleValue();
    }

    private List<String> textList(Map<String, Object> map, String key) {
        List<String> result = new ArrayList<>();
        Object value = map.get(key);
        if (value instanceof List) {
            for (Object item : (List<?>) value) {
                result.add(item.toString());
            }
        }
        return result;
    }

    @SuppressWarnings("unchecked")
    private List<Map<String, Object>> mapList(Map<String, Object> map, String key) {
        Object value = map.get(key);
        return value instanceof List ? (List<Map<String, Object>>) value : new ArrayList<>();
    }
}
