package shelter.controller;

import java.util.ArrayList;
import java.util.List;

import shelter.model.Adopter;
import shelter.model.AdoptionRequest;
import shelter.model.Animal;
import shelter.model.User;
import shelter.patterns.decorator.AdoptionPackage;
import shelter.patterns.decorator.BasicAdoption;
import shelter.patterns.factory.PackageFactory;
import shelter.view.ConsoleView;

// Adoptions: Decorator (build a package from add-ons) + approve/reject + CRUD.
public class AdoptionController extends BaseController {

    public AdoptionController(ConsoleView view) {
        super(view);
    }

    @Override
    public void run() {
        String[] options = {"New adoption request", "View all requests", "Approve request",
                "Reject request", "Delete request"};
        while (true) {
            int choice = view.showMenu("ADOPTIONS", options, "Back");
            switch (choice) {
                case 1: newRequest(); break;
                case 2: view.showList("Adoption requests", model.getAllAdoptions()); break;
                case 3: decide(true); break;
                case 4: decide(false); break;
                case 5: deleteRequest(); break;
                default: return;
            }
            save();
        }
    }

    private void newRequest() {
        List<User> adopters = model.getUsersByRole("Adopter");
        if (adopters.isEmpty()) {
            view.showMessage("No adopters yet. Add one from the User Control Panel first.");
            return;
        }
        view.showList("Adopters", adopters);
        User user = model.getUser(view.readInt("Adopter ID: "));
        if (!(user instanceof Adopter)) {
            view.showMessage("That ID is not an adopter.");
            return;
        }

        List<Animal> available = new ArrayList<>();
        for (Animal animal : model.getAllAnimals()) {
            if (animal.getStatus().equals("Available")) {
                available.add(animal);
            }
        }
        if (available.isEmpty()) {
            view.showMessage("No animals are available for adoption right now.");
            return;
        }
        view.showList("Available animals", available);
        Animal animal = model.getAnimal(view.readInt("Animal ID: "));
        if (animal == null || !animal.getStatus().equals("Available")) {
            view.showMessage("That animal is not available.");
            return;
        }

        List<String> addOns = chooseAddOns();
        AdoptionRequest request = new AdoptionRequest(model.nextAdoptionId(), (Adopter) user, animal, addOns);
        model.addAdoption(request);
        view.showMessage("Request created: " + request);
    }

    // DECORATOR: start with a basic adoption and wrap it with each add-on the adopter picks.
    private List<String> chooseAddOns() {
        List<String> addOns = new ArrayList<>();
        AdoptionPackage adoptionPackage = new BasicAdoption();
        while (true) {
            view.showMessage("Current package: " + adoptionPackage.getDescription()
                    + " = " + adoptionPackage.getCost() + " EGP");
            int choice = view.showMenu("ADD-ONS", PackageFactory.ADD_ON_LABELS, "Done");
            if (choice == 0) {
                return addOns;
            }
            String addOn = PackageFactory.ADD_ONS[choice - 1];
            adoptionPackage = PackageFactory.addOn(adoptionPackage, addOn);
            addOns.add(addOn);
        }
    }

    private void decide(boolean approve) {
        AdoptionRequest request = findRequest();
        if (request == null) {
            return;
        }
        if (!request.getStatus().equals("Pending")) {
            view.showMessage("This request is already " + request.getStatus() + ".");
            return;
        }
        if (approve && !request.getAnimal().getStatus().equals("Available")) {
            view.showMessage(request.getAnimal().getName() + " is no longer available.");
            return;
        }
        view.showMessage("Notification sent:");
        if (approve) {
            request.approve();
        } else {
            request.reject();
        }
        view.showMessage("Request is now " + request.getStatus() + ".");
    }

    private void deleteRequest() {
        AdoptionRequest request = findRequest();
        if (request != null && view.confirm("Delete request #" + request.getId() + "?")) {
            model.deleteAdoption(request.getId());
            view.showMessage("Request deleted.");
        }
    }

    private AdoptionRequest findRequest() {
        view.showList("Adoption requests", model.getAllAdoptions());
        AdoptionRequest request = model.getAdoption(view.readInt("Request ID: "));
        if (request == null) {
            view.showMessage("No request with that ID.");
        }
        return request;
    }
}
