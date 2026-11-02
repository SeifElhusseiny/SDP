package shelter.controller;

import java.util.Arrays;
import java.util.List;

import shelter.model.Adopter;
import shelter.model.AdoptionRequest;
import shelter.model.Donation;
import shelter.model.Event;
import shelter.model.User;
import shelter.model.Validator;
import shelter.model.Volunteer;
import shelter.patterns.factory.StrategyFactory;
import shelter.patterns.factory.UserFactory;
import shelter.view.ConsoleView;

// User Control Panel: full CRUD for every user type.
public class UserController extends BaseController {
    private static final String[] HOME_TYPES = {"Apartment", "House with garden", "Farm"};

    public UserController(ConsoleView view) {
        super(view);
    }

    @Override
    public void run() {
        String[] options = {"Add user", "View all users", "View users by type", "View user by ID",
                "Update user", "Delete user", "Log volunteer hours", "Generate volunteer certificate"};
        while (true) {
            int choice = view.showMenu("USER CONTROL PANEL", options, "Back");
            switch (choice) {
                case 1: addUser(); break;
                case 2: view.showList("All users", model.getAllUsers()); break;
                case 3: viewByType(); break;
                case 4: viewById(); break;
                case 5: updateUser(); break;
                case 6: deleteUser(); break;
                case 7: logHours(); break;
                case 8: certificate(); break;
                default: return;
            }
            save();
        }
    }

    private void addUser() {
        String type = view.chooseFrom("User type", Arrays.asList(UserFactory.USER_TYPES));
        String name = view.readText("Name: ");
        String email = view.readValid("Email: ", Validator::isValidEmail, Validator.EMAIL_RULE);
        String phone = view.readValid("Phone: ", Validator::isValidPhone, Validator.PHONE_RULE);
        List<String> channels = view.chooseMultiple("Notification channels", Arrays.asList(StrategyFactory.CHANNELS));

        // FACTORY: the factory decides which subclass to create.
        User user = UserFactory.createUser(type, model.nextUserId(), name, email, phone, channels);

        if (user instanceof Volunteer) {
            Volunteer volunteer = (Volunteer) user;
            volunteer.setSkill(view.chooseFrom("Skill", model.getVolunteerSkills().getAll()));
            volunteer.setAvailability(view.readText("Availability (e.g. weekends): "));
        } else if (user instanceof Adopter) {
            ((Adopter) user).setHomeType(view.chooseFrom("Home type", Arrays.asList(HOME_TYPES)));
        }

        model.addUser(user);
        view.showMessage("Added: " + user);
    }

    private void viewByType() {
        String type = view.chooseFrom("User type", Arrays.asList(UserFactory.USER_TYPES));
        view.showList(type + "s", model.getUsersByRole(type));
    }

    private void viewById() {
        User user = findUser();
        if (user != null) {
            view.showMessage(user.toString());
        }
    }

    private void updateUser() {
        User user = findUser();
        if (user == null) {
            return;
        }
        user.setName(view.readOptional("Name", user.getName()));
        user.setEmail(view.readOptionalValid("Email", user.getEmail(), Validator::isValidEmail, Validator.EMAIL_RULE));
        user.setPhone(view.readOptionalValid("Phone", user.getPhone(), Validator::isValidPhone, Validator.PHONE_RULE));
        if (view.confirm("Change notification channels?")) {
            user.setPreferredChannels(view.chooseMultiple("Notification channels", Arrays.asList(StrategyFactory.CHANNELS)));
        }
        if (user instanceof Volunteer) {
            Volunteer volunteer = (Volunteer) user;
            if (view.confirm("Change skill?")) {
                volunteer.setSkill(view.chooseFrom("Skill", model.getVolunteerSkills().getAll()));
            }
            volunteer.setAvailability(view.readOptional("Availability", volunteer.getAvailability()));
        } else if (user instanceof Adopter && view.confirm("Change home type?")) {
            ((Adopter) user).setHomeType(view.chooseFrom("Home type", Arrays.asList(HOME_TYPES)));
        }
        view.showMessage("Updated: " + user);
    }

    private void deleteUser() {
        User user = findUser();
        if (user == null) {
            return;
        }
        // Donation and adoption records must keep their donor / adopter.
        for (Donation donation : model.getAllDonations()) {
            if (donation.getDonor() == user) {
                view.showMessage("Cannot delete " + user.getName() + ": they have donation records.");
                return;
            }
        }
        for (AdoptionRequest request : model.getAllAdoptions()) {
            if (request.getAdopter() == user) {
                view.showMessage("Cannot delete " + user.getName() + ": they have adoption requests.");
                return;
            }
        }
        if (view.confirm("Delete " + user.getName() + "?")) {
            for (Event event : model.getAllEvents()) {
                event.removeUser(user);
            }
            model.deleteUser(user.getId());
            view.showMessage("User deleted.");
        }
    }

    private void logHours() {
        Volunteer volunteer = findVolunteer();
        if (volunteer != null) {
            volunteer.logHours(view.readPositiveDouble("Hours worked: "));
            view.showMessage("Updated: " + volunteer);
        }
    }

    private void certificate() {
        Volunteer volunteer = findVolunteer();
        if (volunteer == null) {
            return;
        }
        if (volunteer.getHoursWorked() == 0) {
            view.showMessage(volunteer.getName() + " has no logged hours yet. Log hours first (option 7).");
            return;
        }
        view.showMessage(volunteer.generateCertificate());
    }

    // Lists only volunteers, then reads a volunteer ID.
    private Volunteer findVolunteer() {
        List<User> volunteers = model.getUsersByRole("Volunteer");
        if (volunteers.isEmpty()) {
            view.showMessage("No volunteers yet. Add one first.");
            return null;
        }
        view.showList("Volunteers", volunteers);
        User user = model.getUser(view.readInt("Volunteer ID: "));
        if (!(user instanceof Volunteer)) {
            view.showMessage("That ID is not a volunteer.");
            return null;
        }
        return (Volunteer) user;
    }

    private User findUser() {
        view.showList("Users", model.getAllUsers());
        User user = model.getUser(view.readInt("User ID: "));
        if (user == null) {
            view.showMessage("No user with that ID.");
        }
        return user;
    }
}
