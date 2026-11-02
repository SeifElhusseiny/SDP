package shelter.patterns.decorator;

// DECORATOR pattern - a Concrete Decorator: adds "Neutering" to any package.
public class NeuteringDecorator extends PackageDecorator {
    public NeuteringDecorator(AdoptionPackage wrapped) {
        super(wrapped);
    }

    @Override
    public double getCost() {
        return wrapped.getCost() + 400;
    }

    @Override
    public String getDescription() {
        return wrapped.getDescription() + " + Neutering";
    }
}
