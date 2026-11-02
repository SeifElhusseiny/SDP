package shelter.model;

import java.util.Arrays;

import shelter.patterns.factory.UserFactory;

// Fills the system with a few records so it is ready for a demo.
public class SampleData {
    public static void load(ShelterModel model) {
        model.addUser(UserFactory.createUser("Admin", model.nextUserId(), "Sara Ali", "sara@shelter.org", "01000000001", Arrays.asList("Email")));
        model.addUser(UserFactory.createUser("Donor", model.nextUserId(), "Ahmed Hassan", "ahmed@mail.com", "01000000002", Arrays.asList("SMS", "Email")));

        Volunteer mona = (Volunteer) UserFactory.createUser("Volunteer", model.nextUserId(), "Mona Adel", "mona@mail.com", "01000000003", Arrays.asList("Email"));
        mona.setSkill("Dog walking");
        mona.setAvailability("Weekends");
        model.addUser(mona);

        Adopter omar = (Adopter) UserFactory.createUser("Adopter", model.nextUserId(), "Omar Khaled", "omar@mail.com", "01000000004", Arrays.asList("Social media", "SMS"));
        omar.setHomeType("House with garden");
        model.addUser(omar);

        model.addAnimal(new Animal(model.nextAnimalId(), "Max", "Dog", "Golden Retriever", 3, "Healthy"));
        model.addAnimal(new Animal(model.nextAnimalId(), "Luna", "Cat", "Persian", 2, "Healthy"));
        Animal rocky = new Animal(model.nextAnimalId(), "Rocky", "Dog", "German Shepherd", 5, "Recovering");
        rocky.setNeeds("Daily leg medication");
        rocky.setStatus("Under treatment");
        rocky.addCareRecord(new CareRecord("Medicine", 7));
        model.addAnimal(rocky);

        Event adoptionDay = new Event(model.nextEventId(), "Weekend Adoption Day", "Adoption day", "2026-11-20", "Shelter yard", 50);
        adoptionDay.setRequiredSkill("Dog walking");
        model.addEvent(adoptionDay);
    }
}
