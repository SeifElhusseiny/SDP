package ngo.controller;

import java.util.ArrayList;
import java.util.List;

import ngo.model.Beneficiary;
import ngo.model.CommunicationMessage;
import ngo.model.Donation;
import ngo.model.Event;
import ngo.model.NGOModel;
import ngo.model.Volunteer;
import ngo.patterns.decorator.BasicDonationReceipt;
import ngo.patterns.decorator.DonationReceipt;
import ngo.patterns.decorator.TaxDocumentDecorator;
import ngo.patterns.decorator.ThankYouNoteDecorator;
import ngo.patterns.factory.EntityFactory;
import ngo.patterns.observer.BranchOfficeObserver;
import ngo.patterns.strategy.CommunicationStrategy;
import ngo.patterns.strategy.PaymentStrategy;
import ngo.view.ConsoleView;

public class NGOController {
    private final NGOModel model;
    private final ConsoleView view;

    public NGOController(NGOModel model, ConsoleView view) {
        this.model = model;
        this.view = view;
        registerDefaultObservers();
    }

    private void registerDefaultObservers() {
        model.registerObserver(new BranchOfficeObserver("Cairo"));
        model.registerObserver(new BranchOfficeObserver("Alexandria"));
    }

    public void run() {
        view.printHeader();
        boolean running = true;
        while (running) {
            view.showMainMenu();
            int choice = view.readInt("Select an action: ");
            switch (choice) {
                case 1:
                    addDonation();
                    break;
                case 2:
                    addVolunteer();
                    break;
                case 3:
                    addEvent();
                    break;
                case 4:
                    addBeneficiary();
                    break;
                case 5:
                    manageLookupTables();
                    break;
                case 6:
                    sendCommunication();
                    break;
                case 7:
                    viewAllRecords();
                    break;
                case 8:
                    generateDonationReceipt();
                    break;
                case 9:
                    running = false;
                    view.printMessage("Exiting NGO Management System.");
                    break;
                default:
                    view.printMessage("Invalid option. Please choose a valid menu item.");
            }
        }
    }

    private void addDonation() {
        String donorName = view.readString("Donor name: ");
        int donorTypeChoice = view.readChoice("Select donor type", view.getDonorTypeOptions());
        String donorType = view.resolveDonorType(donorTypeChoice);

        int donationTypeChoice = view.readChoice("Select donation type", view.getDonationTypeOptions());
        String donationType = view.resolveDonationType(donationTypeChoice);

        double amount = view.readDouble("Donation amount: ");

        int paymentMethodChoice = view.readChoice("Select payment method", view.getPaymentMethodOptions());
        String paymentMethod = view.resolvePaymentMethod(paymentMethodChoice);

        PaymentStrategy paymentStrategy = EntityFactory.createPaymentStrategy(paymentMethod);
        String paymentConfirmation = paymentStrategy.processPayment(donorName, amount);
        view.printMessage(paymentConfirmation);

        Donation donation = EntityFactory.createDonation(model.getNextDonationId(), donorName, donorType, donationType, amount, paymentMethod);
        model.addDonation(donation);
        view.printMessage("Donation added successfully.");
    }

    private void addVolunteer() {
        String name = view.readString("Volunteer name: ");
        String skill = view.readString("Skill: ");
        String availability = view.readString("Availability: ");

        Volunteer volunteer = EntityFactory.createVolunteer(model.getNextVolunteerId(), name, skill, availability);
        model.addVolunteer(volunteer);
        view.printMessage("Volunteer added successfully.");
    }

    private void addEvent() {
        String title = view.readString("Event title: ");
        String type = view.readString("Event type: ");
        String date = view.readString("Event date: ");
        int capacity = view.readInt("Event capacity: ");

        Event event = EntityFactory.createEvent(model.getNextEventId(), title, type, date, capacity);
        model.addEvent(event);
        view.printMessage("Event added successfully.");
    }

    private void addBeneficiary() {
        String name = view.readString("Beneficiary name: ");
        String need = view.readString("Need: ");
        String status = view.readString("Status: ");

        Beneficiary beneficiary = EntityFactory.createBeneficiary(model.getNextBeneficiaryId(), name, need, status);
        model.addBeneficiary(beneficiary);
        view.printMessage("Beneficiary added successfully.");
    }

    private void sendCommunication() {
        String recipient = view.readString("Recipient: ");
        int channelChoice = view.readChoice("Select communication channel", view.getCommunicationChannelOptions());
        String channel = view.resolveCommunicationChannel(channelChoice);
        String messageText = view.readString("Message text: ");

        CommunicationStrategy strategy = EntityFactory.createCommunicationStrategy(channel);
        strategy.send(recipient, messageText);

        CommunicationMessage message = new CommunicationMessage(model.getNextCommunicationId(), recipient, channel, messageText);
        model.addCommunication(message);
        view.printMessage("Communication scheduled successfully.");
    }

    private void manageLookupTables() {
        List<String> categories = new ArrayList<>();
        categories.add("Donation Types");
        categories.add("Volunteer Skills");
        categories.add("Event Types");
        categories.add("Communication Channels");
        categories.add("Back");

        int categoryChoice = view.readChoice("Select lookup table to manage", categories);
        if (categoryChoice == 5) {
            return;
        }

        List<String> actions = new ArrayList<>();
        actions.add("Add item");
        actions.add("Update item");
        actions.add("Delete item");
        actions.add("List items");
        actions.add("Back");

        int actionChoice = view.readChoice("Choose action", actions);
        if (actionChoice == 5) {
            return;
        }

        switch (categoryChoice) {
            case 1:
                manageLookupTable(model.getDonationTypes(), "Donation Types", actionChoice, "donation");
                break;
            case 2:
                manageLookupTable(model.getVolunteerSkills(), "Volunteer Skills", actionChoice, "skill");
                break;
            case 3:
                manageLookupTable(model.getEventTypes(), "Event Types", actionChoice, "event");
                break;
            case 4:
                manageLookupTable(model.getCommunicationChannels(), "Communication Channels", actionChoice, "communication");
                break;
            default:
                view.printMessage("Returning to main menu.");
        }
    }

    private void manageLookupTable(List<String> entries, String label, int actionChoice, String type) {
        switch (actionChoice) {
            case 1:
                String newKey = view.readString("New " + label + " key: ");
                String newLabel = view.readString("New " + label + " label: ");
                if ("donation".equals(type)) {
                    model.addDonationType(newKey, newLabel);
                } else if ("skill".equals(type)) {
                    model.addVolunteerSkill(newKey, newLabel);
                } else if ("event".equals(type)) {
                    model.addEventType(newKey, newLabel);
                } else if ("communication".equals(type)) {
                    model.addCommunicationChannel(newKey, newLabel);
                }
                view.printMessage(label + " updated.");
                break;
            case 2:
                String existingKey = view.readString("Enter key to update: ");
                String replacement = view.readString("New label: ");
                if ("donation".equals(type)) {
                    model.updateDonationType(existingKey, replacement);
                } else if ("skill".equals(type)) {
                    model.updateVolunteerSkill(existingKey, replacement);
                } else if ("event".equals(type)) {
                    model.updateEventType(existingKey, replacement);
                } else if ("communication".equals(type)) {
                    model.updateCommunicationChannel(existingKey, replacement);
                }
                view.printMessage(label + " updated.");
                break;
            case 3:
                String removeKey = view.readString("Enter key to delete: ");
                if ("donation".equals(type)) {
                    model.deleteDonationType(removeKey);
                } else if ("skill".equals(type)) {
                    model.deleteVolunteerSkill(removeKey);
                } else if ("event".equals(type)) {
                    model.deleteEventType(removeKey);
                } else if ("communication".equals(type)) {
                    model.deleteCommunicationChannel(removeKey);
                }
                view.printMessage(label + " entry removed.");
                break;
            case 4:
                view.printList(label, entries);
                break;
            default:
                view.printMessage("Returning to selection menu.");
        }
    }

    private void viewAllRecords() {
        view.printSeparator();
        view.printMessage("Donations:");
        for (Donation donation : model.getDonations()) {
            view.printMessage(donation.toString());
        }
        view.printMessage("\nVolunteers:");
        for (Volunteer volunteer : model.getVolunteers()) {
            view.printMessage(volunteer.toString());
        }
        view.printMessage("\nEvents:");
        for (Event event : model.getEvents()) {
            view.printMessage(event.toString());
        }
        view.printMessage("\nBeneficiaries:");
        for (Beneficiary beneficiary : model.getBeneficiaries()) {
            view.printMessage(beneficiary.toString());
        }
        view.printMessage("\nCommunications:");
        for (CommunicationMessage message : model.getCommunications()) {
            view.printMessage(message.toString());
        }
        view.printSeparator();
    }

    private void generateDonationReceipt() {
        String donor = view.readString("Donor name: ");
        double amount = view.readDouble("Donation amount: ");

        DonationReceipt receipt = new BasicDonationReceipt(donor, amount);
        receipt = new TaxDocumentDecorator(receipt);
        receipt = new ThankYouNoteDecorator(receipt);

        view.printMessage("Receipt: " + receipt.getDescription());
        view.printMessage("Total amount: " + receipt.getAmount());
    }
}
