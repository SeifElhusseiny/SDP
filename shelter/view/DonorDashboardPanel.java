package shelter.view;

import shelter.model.User;

public class DonorDashboardPanel extends RoleDashboardPanel {
    public DonorDashboardPanel(User user, Runnable logoutAction, Runnable actionHandler) {
        super(user, new String[] {"Make a donation", "Donation history", "Shelter events", "My profile"},
                logoutAction, actionHandler);
    }
}
