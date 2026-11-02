package ngo.model;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import ngo.patterns.observer.DonationObserver;

public final class NGOModel {
    private static final NGOModel INSTANCE = new NGOModel();

    private final Map<Integer, Donation> donations = new LinkedHashMap<>();
    private final Map<Integer, Volunteer> volunteers = new LinkedHashMap<>();
    private final Map<Integer, Event> events = new LinkedHashMap<>();
    private final Map<Integer, Beneficiary> beneficiaries = new LinkedHashMap<>();
    private final Map<Integer, CommunicationMessage> communications = new LinkedHashMap<>();
    private final List<DonationObserver> observers = new ArrayList<>();

    private final LookupTable<String> donationTypes = new LookupTable<>();
    private final LookupTable<String> volunteerSkills = new LookupTable<>();
    private final LookupTable<String> eventTypes = new LookupTable<>();
    private final LookupTable<String> communicationChannels = new LookupTable<>();

    private int nextDonationId = 1;
    private int nextVolunteerId = 1;
    private int nextEventId = 1;
    private int nextBeneficiaryId = 1;
    private int nextCommunicationId = 1;

    private NGOModel() {
        seedLookupTables();
    }

    public static NGOModel getInstance() {
        return INSTANCE;
    }

    private void seedLookupTables() {
        donationTypes.add("cash", "Cash");
        donationTypes.add("in-kind", "In-kind");
        donationTypes.add("online", "Online");

        volunteerSkills.add("education", "Education");
        volunteerSkills.add("logistics", "Logistics");
        volunteerSkills.add("health", "Health");

        eventTypes.add("fundraiser", "Fundraiser");
        eventTypes.add("workshop", "Workshop");
        eventTypes.add("outreach", "Outreach");

        communicationChannels.add("email", "Email");
        communicationChannels.add("sms", "SMS");
        communicationChannels.add("social", "Social media");
    }

    public void registerObserver(DonationObserver observer) {
        if (observer != null && !observers.contains(observer)) {
            observers.add(observer);
        }
    }

    public void removeObserver(DonationObserver observer) {
        observers.remove(observer);
    }

    public void notifyObservers(String message) {
        for (DonationObserver observer : observers) {
            observer.onDonationEvent(message);
        }
    }

    public List<String> getDonationTypes() {
        return new ArrayList<>(donationTypes.keys());
    }

    public List<String> getVolunteerSkills() {
        return new ArrayList<>(volunteerSkills.keys());
    }

    public List<String> getEventTypes() {
        return new ArrayList<>(eventTypes.keys());
    }

    public List<String> getCommunicationChannels() {
        return new ArrayList<>(communicationChannels.keys());
    }

    public void addDonationType(String key, String label) {
        donationTypes.add(key, label);
    }

    public void updateDonationType(String key, String newLabel) {
        donationTypes.update(key, newLabel);
    }

    public void deleteDonationType(String key) {
        donationTypes.delete(key);
    }

    public void addVolunteerSkill(String key, String label) {
        volunteerSkills.add(key, label);
    }

    public void updateVolunteerSkill(String key, String newLabel) {
        volunteerSkills.update(key, newLabel);
    }

    public void deleteVolunteerSkill(String key) {
        volunteerSkills.delete(key);
    }

    public void addEventType(String key, String label) {
        eventTypes.add(key, label);
    }

    public void updateEventType(String key, String newLabel) {
        eventTypes.update(key, newLabel);
    }

    public void deleteEventType(String key) {
        eventTypes.delete(key);
    }

    public void addCommunicationChannel(String key, String label) {
        communicationChannels.add(key, label);
    }

    public void updateCommunicationChannel(String key, String newLabel) {
        communicationChannels.update(key, newLabel);
    }

    public void deleteCommunicationChannel(String key) {
        communicationChannels.delete(key);
    }

    public Donation addDonation(Donation donation) {
        donations.put(donation.getId(), donation);
        notifyObservers("New donation received: " + donation);
        return donation;
    }

    public boolean updateDonation(int id, Donation donation) {
        if (!donations.containsKey(id)) {
            return false;
        }
        donations.put(id, donation);
        return true;
    }

    public boolean deleteDonation(int id) {
        Donation removed = donations.remove(id);
        return removed != null;
    }

    public Volunteer addVolunteer(Volunteer volunteer) {
        volunteers.put(volunteer.getId(), volunteer);
        return volunteer;
    }

    public boolean updateVolunteer(int id, Volunteer volunteer) {
        if (!volunteers.containsKey(id)) {
            return false;
        }
        volunteers.put(id, volunteer);
        return true;
    }

    public boolean deleteVolunteer(int id) {
        return volunteers.remove(id) != null;
    }

    public Event addEvent(Event event) {
        events.put(event.getId(), event);
        return event;
    }

    public boolean updateEvent(int id, Event event) {
        if (!events.containsKey(id)) {
            return false;
        }
        events.put(id, event);
        return true;
    }

    public boolean deleteEvent(int id) {
        return events.remove(id) != null;
    }

    public Beneficiary addBeneficiary(Beneficiary beneficiary) {
        beneficiaries.put(beneficiary.getId(), beneficiary);
        return beneficiary;
    }

    public boolean updateBeneficiary(int id, Beneficiary beneficiary) {
        if (!beneficiaries.containsKey(id)) {
            return false;
        }
        beneficiaries.put(id, beneficiary);
        return true;
    }

    public boolean deleteBeneficiary(int id) {
        return beneficiaries.remove(id) != null;
    }

    public CommunicationMessage addCommunication(CommunicationMessage message) {
        communications.put(message.getId(), message);
        return message;
    }

    public List<Donation> getDonations() {
        return new ArrayList<>(donations.values());
    }

    public List<Volunteer> getVolunteers() {
        return new ArrayList<>(volunteers.values());
    }

    public List<Event> getEvents() {
        return new ArrayList<>(events.values());
    }

    public List<Beneficiary> getBeneficiaries() {
        return new ArrayList<>(beneficiaries.values());
    }

    public List<CommunicationMessage> getCommunications() {
        return new ArrayList<>(communications.values());
    }

    public int getNextDonationId() {
        return nextDonationId++;
    }

    public int getNextVolunteerId() {
        return nextVolunteerId++;
    }

    public int getNextEventId() {
        return nextEventId++;
    }

    public int getNextBeneficiaryId() {
        return nextBeneficiaryId++;
    }

    public int getNextCommunicationId() {
        return nextCommunicationId++;
    }
}
