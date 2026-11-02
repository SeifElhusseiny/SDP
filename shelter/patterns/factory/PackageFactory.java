package shelter.patterns.factory;

import java.util.List;

import shelter.patterns.decorator.AdoptionPackage;
import shelter.patterns.decorator.BasicAdoption;
import shelter.patterns.decorator.MicrochipDecorator;
import shelter.patterns.decorator.NeuteringDecorator;
import shelter.patterns.decorator.StarterKitDecorator;
import shelter.patterns.decorator.VaccinationDecorator;

// FACTORY for the DECORATOR chain: wraps a package with an add-on by name,
// or rebuilds a whole package from a saved list of add-on names.
public class PackageFactory {
    public static final String[] ADD_ONS = {"Vaccination", "Microchip", "Neutering", "Starter kit"};
    public static final String[] ADD_ON_LABELS = {"Vaccination (+300)", "Microchip (+200)", "Neutering (+400)",
            "Starter kit: food, bed, toys (+250)"};

    public static AdoptionPackage addOn(AdoptionPackage adoptionPackage, String addOn) {
        switch (addOn) {
            case "Vaccination": return new VaccinationDecorator(adoptionPackage);
            case "Microchip": return new MicrochipDecorator(adoptionPackage);
            case "Neutering": return new NeuteringDecorator(adoptionPackage);
            case "Starter kit": return new StarterKitDecorator(adoptionPackage);
            default: throw new IllegalArgumentException("Unknown add-on: " + addOn);
        }
    }

    public static AdoptionPackage build(List<String> addOns) {
        AdoptionPackage adoptionPackage = new BasicAdoption();
        for (String addOn : addOns) {
            adoptionPackage = addOn(adoptionPackage, addOn);
        }
        return adoptionPackage;
    }
}
