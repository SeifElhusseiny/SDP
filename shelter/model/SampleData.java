package shelter.model;

import java.util.Arrays;
import java.util.List;

import shelter.patterns.factory.strategy.AbstractNotificationFactory;
import shelter.patterns.factory.user.AdminUserFactory;
import shelter.patterns.factory.user.AdopterUserFactory;
import shelter.patterns.factory.user.DonorUserFactory;
import shelter.patterns.factory.user.VolunteerUserFactory;
import shelter.service.AuthenticationService;

// Fills the system with a few records so it is ready for a demo.
public class SampleData {
    public static void load(ShelterModel model, List<AbstractNotificationFactory> notificationFactories) {
        String demoPasswordHash = AuthenticationService.hash("demo1234".toCharArray());
        model.addUser(new AdminUserFactory(notificationFactories).createUser(model.nextUserId(), "Ahmed", "ahmed@shelter.org",
                "01000000001", Arrays.asList("Email"), demoPasswordHash));
        model.addUser(new DonorUserFactory(notificationFactories).createUser(model.nextUserId(), "Seif", "seif@mail.com",
                "01000000002", Arrays.asList("SMS", "Email"), demoPasswordHash));

        Volunteer moaz = (Volunteer) new VolunteerUserFactory(notificationFactories).createUser(model.nextUserId(), "Moaz",
                "moaz@mail.com", "01000000003", Arrays.asList("Email"), demoPasswordHash);
        moaz.setSkill("Dog walking");
        moaz.setAvailability("Weekends");
        model.addUser(moaz);

        Adopter patrick = (Adopter) new AdopterUserFactory(notificationFactories).createUser(model.nextUserId(), "Patrick",
                "patrick@mail.com", "01000000004", Arrays.asList("Social media", "SMS"), demoPasswordHash);
        patrick.setHomeType("House with garden");
        model.addUser(patrick);

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
