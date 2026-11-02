package shelter.controller;

import java.util.Arrays;

import shelter.model.AdoptionRequest;
import shelter.model.Animal;
import shelter.model.CareRecord;
import shelter.view.ConsoleView;

// Full CRUD for the shelter's animals.
public class AnimalController extends BaseController {

    public AnimalController(ConsoleView view) {
        super(view);
    }

    @Override
    public void run() {
        String[] options = {"Add animal", "View all animals", "View available animals",
                "View animal by ID", "Update animal", "Delete animal",
                "Record care / resources given", "View an animal's care history"};
        while (true) {
            int choice = view.showMenu("ANIMALS", options, "Back");
            switch (choice) {
                case 1: addAnimal(); break;
                case 2: view.showList("All animals", model.getAllAnimals()); break;
                case 3: viewAvailable(); break;
                case 4: viewById(); break;
                case 5: updateAnimal(); break;
                case 6: deleteAnimal(); break;
                case 7: recordCare(); break;
                case 8: careHistory(); break;
                default: return;
            }
            save();
        }
    }

    private void addAnimal() {
        String name = view.readText("Name: ");
        String species = view.chooseFrom("Species", model.getSpecies().getAll());
        String breed = view.readText("Breed: ");
        int age = view.readInt("Age (years): ", 0, 40);
        String health = view.chooseFrom("Health status", Arrays.asList(Animal.HEALTH));
        Animal animal = new Animal(model.nextAnimalId(), name, species, breed, age, health);
        animal.setNeeds(view.readText("Special needs (or None): "));
        model.addAnimal(animal);
        view.showMessage("Added: " + animal);
    }

    private void viewAvailable() {
        view.showMessage("--- Animals available for adoption ---");
        for (Animal animal : model.getAllAnimals()) {
            if (animal.getStatus().equals("Available")) {
                view.showMessage(animal.toString());
            }
        }
    }

    private void viewById() {
        Animal animal = findAnimal();
        if (animal != null) {
            view.showMessage(animal.toString());
        }
    }

    private void updateAnimal() {
        Animal animal = findAnimal();
        if (animal == null) {
            return;
        }
        animal.setName(view.readOptional("Name", animal.getName()));
        if (view.confirm("Change species?")) {
            animal.setSpecies(view.chooseFrom("Species", model.getSpecies().getAll()));
        }
        animal.setBreed(view.readOptional("Breed", animal.getBreed()));
        if (view.confirm("Change age?")) {
            animal.setAge(view.readInt("Age (years): ", 0, 40));
        }
        if (view.confirm("Change health status?")) {
            animal.setHealthStatus(view.chooseFrom("Health status", Arrays.asList(Animal.HEALTH)));
        }
        animal.setNeeds(view.readOptional("Special needs", animal.getNeeds()));
        if (view.confirm("Change shelter status?")) {
            animal.setStatus(view.chooseFrom("Status", Arrays.asList(Animal.STATUSES)));
        }
        view.showMessage("Updated: " + animal);
    }

    private void deleteAnimal() {
        Animal animal = findAnimal();
        if (animal == null) {
            return;
        }
        for (AdoptionRequest request : model.getAllAdoptions()) {
            if (request.getAnimal() == animal) {
                view.showMessage("Cannot delete " + animal.getName() + ": it has adoption requests.");
                return;
            }
        }
        if (view.confirm("Delete " + animal.getName() + "?")) {
            model.deleteAnimal(animal.getId());
            view.showMessage("Animal deleted.");
        }
    }

    // Tracks the distribution of resources and services to an animal.
    private void recordCare() {
        Animal animal = findAnimal();
        if (animal == null) {
            return;
        }
        String item = view.chooseFrom("What was given", model.getCareTypes().getAll());
        int quantity = view.readInt("Quantity: ", 1, 1000);
        animal.addCareRecord(new CareRecord(item, quantity));
        view.showMessage("Recorded: " + item + " x" + quantity + " for " + animal.getName() + ".");
    }

    private void careHistory() {
        Animal animal = findAnimal();
        if (animal != null) {
            view.showMessage(animal.toString());
            view.showList("Care history of " + animal.getName(), animal.getCareRecords());
        }
    }

    private Animal findAnimal() {
        view.showList("Animals", model.getAllAnimals());
        Animal animal = model.getAnimal(view.readInt("Animal ID: "));
        if (animal == null) {
            view.showMessage("No animal with that ID.");
        }
        return animal;
    }
}
