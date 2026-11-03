package shelter.patterns.factory.decorator;

import shelter.patterns.decorator.AdoptionPackage;
import shelter.patterns.decorator.VaccinationDecorator;

public class VaccinationAddonFactory extends AbstractPackageAddonFactory {
    @Override
    public String getAddonName() {
        return "Vaccination";
    }

    @Override
    public String getDisplayLabel() {
        return "Vaccination (+300)";
    }

    @Override
    public AdoptionPackage addTo(AdoptionPackage adoptionPackage) {
        return new VaccinationDecorator(adoptionPackage);
    }
}
