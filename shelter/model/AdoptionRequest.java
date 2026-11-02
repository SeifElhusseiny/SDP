package shelter.model;

import java.util.ArrayList;
import java.util.List;

import shelter.patterns.decorator.AdoptionPackage;
import shelter.patterns.factory.PackageFactory;

public class AdoptionRequest {
    private final int id;
    private final Adopter adopter;
    private final Animal animal;
    private final List<String> addOns;
    private final AdoptionPackage adoptionPackage;
    private String status = "Pending";

    // The add-on names are kept so the DECORATOR chain can be rebuilt after loading from the database.
    public AdoptionRequest(int id, Adopter adopter, Animal animal, List<String> addOns) {
        this.id = id;
        this.adopter = adopter;
        this.animal = animal;
        this.addOns = new ArrayList<>(addOns);
        this.adoptionPackage = PackageFactory.build(addOns);
    }

    public void approve() {
        status = "Approved";
        animal.setStatus("Adopted");
        adopter.update("Your adoption of " + animal.getName() + " was approved! Total: " + adoptionPackage.getCost() + " EGP");
    }

    public void reject() {
        status = "Rejected";
        adopter.update("Sorry, your adoption request for " + animal.getName() + " was rejected.");
    }

    public int getId() { return id; }
    public Adopter getAdopter() { return adopter; }
    public Animal getAnimal() { return animal; }
    public AdoptionPackage getAdoptionPackage() { return adoptionPackage; }
    public String getStatus() { return status; }
    public List<String> getAddOns() { return new ArrayList<>(addOns); }
    public void setStatus(String status) { this.status = status; }

    @Override
    public String toString() {
        return "#" + id + " | Adopter: " + adopter.getName() + " | Animal: " + animal.getName()
                + " | Package: " + adoptionPackage.getDescription() + " | Total: " + adoptionPackage.getCost()
                + " EGP | Status: " + status;
    }
}
