package ngo.patterns.observer;

public class BranchOfficeObserver implements DonationObserver {
    private final String officeName;

    public BranchOfficeObserver(String officeName) {
        this.officeName = officeName;
    }

    @Override
    public void onDonationEvent(String message) {
        System.out.println("[" + officeName + " office] " + message);
    }
}
