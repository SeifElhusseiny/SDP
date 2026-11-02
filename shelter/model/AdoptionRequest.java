package shelter.model;

import shelter.patterns.decorator.AdoptionPackage;

public class AdoptionRequest {
    private final int id;
    private final Adopter adopter;
    private final Animal animal;
    private final AdoptionPackage adoptionPackage;
    private String status = "Pending";

    public AdoptionRequest(int id, Adopter adopter, Animal animal, AdoptionPackage adoptionPackage) {
        this.id = id;
        this.adopter = adopter;
        this.animal = animal;
        this.adoptionPackage = adoptionPackage;
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

    @Override
    public String toString() {
        return "#" + id + " | Adopter: " + adopter.getName() + " | Animal: " + animal.getName()
                + " | Package: " + adoptionPackage.getDescription() + " | Total: " + adoptionPackage.getCost()
                + " EGP | Status: " + status;
    }
}
