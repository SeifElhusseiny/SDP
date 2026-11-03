package shelter.controller;

import java.util.ArrayList;
import java.util.List;

import shelter.model.Animal;
import shelter.model.User;
import shelter.patterns.factory.decorator.AbstractPackageAddonFactory;
import shelter.patterns.factory.strategy.AbstractNotificationFactory;
import shelter.patterns.factory.strategy.AbstractPaymentFactory;
import shelter.patterns.factory.user.AbstractUserFactory;
import shelter.patterns.strategy.NotificationService;
import shelter.patterns.strategy.SocialMediaNotification;
import shelter.view.ConsoleView;

// MVC - the main controller: shows the main menu and hands work to the module controllers.
public class MainController extends BaseController {
    private final UserController userController;
    private final AnimalController animalController;
    private final DonationController donationController;
    private final EventController eventController;
    private final AdoptionController adoptionController;
    private final LookupController lookupController;
    private final ReportController reportController;
    private final List<AbstractUserFactory> userFactories;
    private final List<AbstractNotificationFactory> notificationFactories;

    public MainController(ConsoleView view, List<AbstractUserFactory> userFactories,
            List<AbstractNotificationFactory> notificationFactories,
            List<AbstractPaymentFactory> paymentFactories,
            List<AbstractPackageAddonFactory> addonFactories) {
        super(view);
        this.userFactories = userFactories;
        this.notificationFactories = notificationFactories;
        userController = new UserController(view, userFactories, notificationFactories);
        animalController = new AnimalController(view);
        donationController = new DonationController(view, paymentFactories);
        eventController = new EventController(view);
        adoptionController = new AdoptionController(view, addonFactories);
        lookupController = new LookupController(view);
        reportController = new ReportController(view);
    }

    @Override
    public void run() {
        view.showMessage("Welcome to the Paws & Whiskers Animal Shelter system!");
        String[] options = {"User Control Panel (Donors, Volunteers, Adopters, Admins)", "Animals",
                "Donations", "Events", "Adoptions", "Lookup tables", "Communication",
                "Shelter impact report"};
        while (true) {
            int choice = view.showMenu("MAIN MENU", options, "Exit");
            switch (choice) {
                case 1: userController.run(); break;
                case 2: animalController.run(); break;
                case 3: donationController.run(); break;
                case 4: eventController.run(); break;
                case 5: adoptionController.run(); break;
                case 6: lookupController.run(); break;
                case 7: communication(); break;
                case 8: reportController.run(); break;
                default:
                    save();
                    view.showMessage("All data saved. Goodbye!");
                    return;
            }
        }
    }

    private void communication() {
        String[] options = {"Message one user (choose the channel)",
                "Message a whole group (each by their preferred channel)",
                "Post available animals on social media"};
        while (true) {
            int choice = view.showMenu("COMMUNICATION", options, "Back");
            switch (choice) {
                case 1: messageOneUser(); break;
                case 2: messageGroup(); break;
                case 3: postAvailableAnimals(); break;
                default: return;
            }
            save();
        }
    }

    // STRATEGY: the same NotificationService sends through whichever channel is chosen.
    private void messageOneUser() {
        view.showList("Users", model.getAllUsers());
        User user = model.getUser(view.readInt("User ID: "));
        if (user == null) {
            view.showMessage("No user with that ID.");
            return;
        }
        AbstractNotificationFactory notificationFactory =
                view.chooseObjectFrom("Send by", notificationFactories);
        String message = view.readText("Message: ");
        NotificationService service = new NotificationService(notificationFactory.createNotification());
        service.notify(user, message);
    }

    // Each user receives it on their own preferred channel (Strategy chosen per user).
    private void messageGroup() {
        List<String> userTypes = new ArrayList<>();
        for (AbstractUserFactory userFactory : userFactories) {
            userTypes.add(userFactory.getUserType());
        }
        String type = view.chooseFrom("Send to all", userTypes);
        if (model.getUsersByRole(type).isEmpty()) {
            view.showMessage("There are no " + type + "s yet.");
            return;
        }
        String message = view.readText("Message: ");
        view.showMessage("Sent:");
        for (User user : model.getUsersByRole(type)) {
            user.update(message);
        }
    }

    // Outreach: one public post per animal that is waiting for a home.
    private void postAvailableAnimals() {
        SocialMediaNotification social = new SocialMediaNotification();
        int posted = 0;
        for (Animal animal : model.getAllAnimals()) {
            if (animal.getStatus().equals("Available")) {
                social.postToPage("Meet " + animal.getName() + ", a " + animal.getAge() + "-year-old "
                        + animal.getBreed() + " (" + animal.getSpecies() + ") looking for a loving home! #AdoptDontShop");
                posted++;
            }
        }
        view.showMessage(posted == 0 ? "No animals are available right now." : posted + " post(s) published.");
    }
}
