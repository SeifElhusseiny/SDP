package shelter.patterns.decorator;

// DECORATOR pattern - a Concrete Decorator: adds "Vaccination" to any package.
public class VaccinationDecorator extends PackageDecorator {
    public VaccinationDecorator(AdoptionPackage wrapped) {
        super(wrapped);
    }

    @Override
    public double getCost() {
        return wrapped.getCost() + 300;
    }

    @Override
    public String getDescription() {
        return wrapped.getDescription() + " + Vaccination";
    }
}
