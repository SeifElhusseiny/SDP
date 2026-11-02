package shelter.patterns.decorator;

// DECORATOR pattern - a Concrete Decorator: adds "Starter kit (food, bed, toys)" to any package.
public class StarterKitDecorator extends PackageDecorator {
    public StarterKitDecorator(AdoptionPackage wrapped) {
        super(wrapped);
    }

    @Override
    public double getCost() {
        return wrapped.getCost() + 250;
    }

    @Override
    public String getDescription() {
        return wrapped.getDescription() + " + Starter kit (food, bed, toys)";
    }
}
