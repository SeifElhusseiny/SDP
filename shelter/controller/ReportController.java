package shelter.controller;

import java.util.LinkedHashMap;
import java.util.Map;

import shelter.model.AdoptionRequest;
import shelter.model.Animal;
import shelter.model.CareRecord;
import shelter.model.Donation;
import shelter.model.Event;
import shelter.model.User;
import shelter.model.Volunteer;
import shelter.view.ConsoleView;

// Monitoring and evaluating the impact of the shelter's programs.
public class ReportController extends BaseController {

    public ReportController(ConsoleView view) {
        super(view);
    }

    @Override
    public void run() {
        view.showTitle("SHELTER IMPACT REPORT");
        animalReport();
        adoptionReport();
        donationReport();
        volunteerReport();
        eventReport();
        careReport();
    }

    private void animalReport() {
        int available = 0;
        int treatment = 0;
        int adopted = 0;
        for (Animal animal : model.getAllAnimals()) {
            switch (animal.getStatus()) {
                case "Available": available++; break;
                case "Under treatment": treatment++; break;
                default: adopted++;
            }
        }
        view.showMessage("ANIMALS: " + model.getAllAnimals().size() + " total | " + available + " available | "
                + treatment + " under treatment | " + adopted + " adopted");
    }

    private void adoptionReport() {
        int approved = 0;
        int pending = 0;
        int rejected = 0;
        double fees = 0;
        for (AdoptionRequest request : model.getAllAdoptions()) {
            switch (request.getStatus()) {
                case "Approved":
                    approved++;
                    fees += request.getAdoptionPackage().getCost();
                    break;
                case "Pending": pending++; break;
                default: rejected++;
            }
        }
        view.showMessage("ADOPTIONS: " + model.getAllAdoptions().size() + " requests | " + approved + " approved | "
                + pending + " pending | " + rejected + " rejected | Adoption fees collected: " + fees + " EGP");
    }

    private void donationReport() {
        double money = 0;
        double inKind = 0;
        Map<String, Double> byType = new LinkedHashMap<>();
        for (Donation donation : model.getAllDonations()) {
            if (donation.isMoney()) {
                money += donation.getAmount();
            } else {
                inKind += donation.getAmount();
            }
            byType.put(donation.getType(), byType.getOrDefault(donation.getType(), 0.0) + donation.getAmount());
        }
        view.showMessage("DONATIONS: " + model.getAllDonations().size() + " donations | Money: " + money
                + " EGP | In-kind (estimated): " + inKind + " EGP");
        for (Map.Entry<String, Double> entry : byType.entrySet()) {
            view.showMessage("   - " + entry.getKey() + ": " + entry.getValue() + " EGP");
        }
    }

    private void volunteerReport() {
        double hours = 0;
        for (User user : model.getUsersByRole("Volunteer")) {
            hours += ((Volunteer) user).getHoursWorked();
        }
        view.showMessage("VOLUNTEERS: " + model.getUsersByRole("Volunteer").size() + " volunteers | "
                + hours + " hours worked in total");
    }

    private void eventReport() {
        int registered = 0;
        int attended = 0;
        for (Event event : model.getAllEvents()) {
            registered += event.getRegisteredUsers().size();
            attended += event.getAttendedCount();
        }
        String rate = registered == 0 ? "-" : (attended * 100 / registered) + "%";
        view.showMessage("EVENTS: " + model.getAllEvents().size() + " events | " + registered + " registrations | "
                + attended + " attended | Attendance rate: " + rate);
    }

    private void careReport() {
        Map<String, Integer> byItem = new LinkedHashMap<>();
        int records = 0;
        for (Animal animal : model.getAllAnimals()) {
            for (CareRecord record : animal.getCareRecords()) {
                records++;
                byItem.put(record.getItem(), byItem.getOrDefault(record.getItem(), 0) + record.getQuantity());
            }
        }
        view.showMessage("CARE GIVEN: " + records + " care records");
        for (Map.Entry<String, Integer> entry : byItem.entrySet()) {
            view.showMessage("   - " + entry.getKey() + ": " + entry.getValue());
        }
    }
}
