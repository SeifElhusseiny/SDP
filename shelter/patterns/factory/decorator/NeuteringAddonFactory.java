package shelter.patterns.factory.decorator;

import shelter.patterns.decorator.AdoptionPackage;
import shelter.patterns.decorator.NeuteringDecorator;

public class NeuteringAddonFactory extends AbstractPackageAddonFactory {
    @Override
    public String getAddonName() {
        return "Neutering";
    }

    @Override
    public String getDisplayLabel() {
        return "Neutering (+400)";
    }

    @Override
    public AdoptionPackage addTo(AdoptionPackage adoptionPackage) {
        return new NeuteringDecorator(adoptionPackage);
    }
}
