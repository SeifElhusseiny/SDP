package shelter.controller;

import java.time.LocalDate;
import java.util.List;

import shelter.model.Donation;
import shelter.model.Donor;
import shelter.model.User;
import shelter.patterns.factory.strategy.AbstractPaymentFactory;
import shelter.patterns.strategy.PaymentStrategy;
import shelter.view.ConsoleView;

// Donations: CRUD + Strategy (payment) + Observer (donor and admins are notified).
public class DonationController extends BaseController {
    private final List<AbstractPaymentFactory> paymentFactories;

    public DonationController(ConsoleView view, List<AbstractPaymentFactory> paymentFactories) {
        super(view);
        this.paymentFactories = paymentFactories;
    }

    @Override
    public void run() {
        String[] options = {"Make a donation", "View all donations", "View donation by ID",
                "View a donor's history", "Update donation", "Delete donation",
                "Print donation receipt", "Generate yearly tax document"};
        while (true) {
            int choice = view.showMenu("DONATIONS", options, "Back");
            switch (choice) {
                case 1: makeDonation(); break;
                case 2: view.showList("All donations", model.getAllDonations()); break;
                case 3: viewById(); break;
                case 4: donorHistory(); break;
                case 5: updateDonation(); break;
                case 6: deleteDonation(); break;
                case 7: printReceipt(); break;
                case 8: taxDocument(); break;
                default: return;
            }
            save();
        }
    }

    private void makeDonation() {
        Donor donor = chooseDonor();
        if (donor == null) {
            return;
        }
        String type = view.chooseFrom("Donation type", model.getDonationTypes().getAll());

        double amount;
        String paymentMethod;
        if (type.equals("Money")) {
            amount = view.readPositiveDouble("Amount (EGP): ");
            AbstractPaymentFactory paymentFactory = view.chooseObjectFrom("Payment method", paymentFactories);
            // STRATEGY: the chosen payment method decides which details to ask for,
            // how to check them, and how the payment is processed.
            PaymentStrategy payment = paymentFactory.createPayment();
            String[] fields = payment.getRequiredDetails();
            String[] details = new String[fields.length];
            for (int i = 0; i < fields.length; i++) {
                final int index = i;
                details[i] = view.readValid(fields[i] + ": ",
                        value -> payment.isValidDetail(index, value), "Invalid " + fields[i] + ". Please try again.");
            }
            view.showMessage(payment.pay(donor.getName(), amount, details));
            paymentMethod = payment.getReference(details);
        } else {
            amount = view.readPositiveDouble("Estimated value of the items (EGP): ");
            paymentMethod = "In-kind";
        }

        Donation donation = new Donation(model.nextDonationId(), donor, type, amount, paymentMethod);
        model.addDonation(donation);
        donor.addToTotal(amount);

        // OBSERVER: the donor and every admin are attached and notified.
        donation.attach(donor);
        for (User admin : model.getUsersByRole("Admin")) {
            donation.attach(admin);
        }
        view.showMessage("Notifications sent:");
        donation.notifyObservers("Donation #" + donation.getId() + " received: " + type + ", "
                + amount + " EGP from " + donor.getName() + ". Thank you!");
        view.showMessage(donation.generateReceipt());
    }

    private void printReceipt() {
        Donation donation = findDonation();
        if (donation != null) {
            view.showMessage(donation.generateReceipt());
        }
    }

    // Tax document: a yearly statement of everything a donor gave.
    private void taxDocument() {
        Donor donor = chooseDonor();
        if (donor == null) {
            return;
        }
        int year = LocalDate.now().getYear();
        double moneyTotal = 0;
        double inKindTotal = 0;
        view.showMessage("=============== YEARLY DONATION TAX STATEMENT " + year + " ===============");
        view.showMessage(" Organization: Paws & Whiskers Animal Shelter (registered non-profit)");
        view.showMessage(" Donor: " + donor.getName() + " | " + donor.getEmail() + " | " + donor.getPhone());
        for (Donation donation : model.getAllDonations()) {
            if (donation.getDonor() == donor && donation.getDate().getYear() == year) {
                view.showMessage("  R-" + String.format("%04d", donation.getId()) + " | " + donation.getDate()
                        + " | " + donation.getType() + " | " + donation.getAmount() + " EGP | " + donation.getPaymentMethod());
                if (donation.isMoney()) {
                    moneyTotal += donation.getAmount();
                } else {
                    inKindTotal += donation.getAmount();
                }
            }
        }
        view.showMessage(" Total money donations: " + moneyTotal + " EGP");
        view.showMessage(" Total in-kind donations (estimated value): " + inKindTotal + " EGP");
        view.showMessage(" No goods or services were given in return for these donations.");
        view.showMessage("======================================================================");
    }

    private void viewById() {
        Donation donation = findDonation();
        if (donation != null) {
            view.showMessage(donation.toString());
        }
    }

    private void donorHistory() {
        Donor donor = chooseDonor();
        if (donor == null) {
            return;
        }
        view.showMessage("--- Donations by " + donor.getName() + " ---");
        for (Donation donation : model.getAllDonations()) {
            if (donation.getDonor() == donor) {
                view.showMessage(donation.toString());
            }
        }
        view.showMessage("Total donated: " + donor.getTotalDonated() + " EGP");
    }

    private void updateDonation() {
        Donation donation = findDonation();
        if (donation == null) {
            return;
        }
        if (view.confirm("Change amount?")) {
            double newAmount = view.readPositiveDouble("New amount (EGP): ");
            donation.getDonor().addToTotal(newAmount - donation.getAmount());
            donation.setAmount(newAmount);
        }
        if (view.confirm("Change type?")) {
            donation.setType(view.chooseFrom("Donation type", model.getDonationTypes().getAll()));
        }
        view.showMessage("Updated: " + donation);
    }

    private void deleteDonation() {
        Donation donation = findDonation();
        if (donation != null && view.confirm("Delete donation #" + donation.getId() + "?")) {
            donation.getDonor().addToTotal(-donation.getAmount());
            model.deleteDonation(donation.getId());
            view.showMessage("Donation deleted.");
        }
    }

    private Donation findDonation() {
        view.showList("Donations", model.getAllDonations());
        Donation donation = model.getDonation(view.readInt("Donation ID: "));
        if (donation == null) {
            view.showMessage("No donation with that ID.");
        }
        return donation;
    }

    private Donor chooseDonor() {
        List<User> donors = model.getUsersByRole("Donor");
        if (donors.isEmpty()) {
            view.showMessage("No donors yet. Add one from the User Control Panel first.");
            return null;
        }
        view.showList("Donors", donors);
        User user = model.getUser(view.readInt("Donor ID: "));
        if (!(user instanceof Donor)) {
            view.showMessage("That ID is not a donor.");
            return null;
        }
        return (Donor) user;
    }
}
