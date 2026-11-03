package shelter.patterns.factory.decorator;

import shelter.patterns.decorator.AdoptionPackage;

public abstract class AbstractPackageAddonFactory {
    public abstract String getAddonName();

    public abstract String getDisplayLabel();

    public abstract AdoptionPackage addTo(AdoptionPackage adoptionPackage);

    @Override
    public final String toString() {
        return getDisplayLabel();
    }
}
