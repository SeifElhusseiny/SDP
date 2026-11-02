package shelter.patterns.decorator;

// DECORATOR pattern - the Decorator: it IS a package and it WRAPS a package.
public abstract class PackageDecorator implements AdoptionPackage {
    protected AdoptionPackage wrapped;

    public PackageDecorator(AdoptionPackage wrapped) {
        this.wrapped = wrapped;
    }
}
