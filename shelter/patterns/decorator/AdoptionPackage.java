package shelter.patterns.decorator;

// DECORATOR pattern - the Component: every adoption package has a cost and a description.
public interface AdoptionPackage {
    double getCost();
    String getDescription();
}
