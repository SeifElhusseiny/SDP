package shelter.patterns.factory.decorator;

import shelter.patterns.decorator.AdoptionPackage;
import shelter.patterns.decorator.MicrochipDecorator;

public class MicrochipAddonFactory extends AbstractPackageAddonFactory {
    @Override
    public String getAddonName() {
        return "Microchip";
    }

    @Override
    public String getDisplayLabel() {
        return "Microchip (+200)";
    }

    @Override
    public AdoptionPackage addTo(AdoptionPackage adoptionPackage) {
        return new MicrochipDecorator(adoptionPackage);
    }
}
