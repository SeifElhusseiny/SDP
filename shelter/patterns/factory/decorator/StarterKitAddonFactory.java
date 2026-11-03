package shelter.patterns.factory.decorator;

import shelter.patterns.decorator.AdoptionPackage;
import shelter.patterns.decorator.StarterKitDecorator;

public class StarterKitAddonFactory extends AbstractPackageAddonFactory {
    @Override
    public String getAddonName() {
        return "Starter kit";
    }

    @Override
    public String getDisplayLabel() {
        return "Starter kit: food, bed, toys (+250)";
    }

    @Override
    public AdoptionPackage addTo(AdoptionPackage adoptionPackage) {
        return new StarterKitDecorator(adoptionPackage);
    }
}
