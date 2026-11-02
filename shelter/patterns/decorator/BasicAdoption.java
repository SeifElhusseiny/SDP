package shelter.patterns.decorator;

// DECORATOR pattern - the Concrete Component: the plain adoption fee.
public class BasicAdoption implements AdoptionPackage {
    private static final double BASE_FEE = 500;

    @Override
    public double getCost() {
        return BASE_FEE;
    }

    @Override
    public String getDescription() {
        return "Basic adoption";
    }
}
