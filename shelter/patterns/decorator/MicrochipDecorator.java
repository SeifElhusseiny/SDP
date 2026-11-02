package shelter.patterns.decorator;

// DECORATOR pattern - a Concrete Decorator: adds "Microchip" to any package.
public class MicrochipDecorator extends PackageDecorator {
    public MicrochipDecorator(AdoptionPackage wrapped) {
        super(wrapped);
    }

    @Override
    public double getCost() {
        return wrapped.getCost() + 200;
    }

    @Override
    public String getDescription() {
        return wrapped.getDescription() + " + Microchip";
    }
}
