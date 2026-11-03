package shelter.view;

import shelter.model.User;

public class VolunteerDashboardPanel extends RoleDashboardPanel {
    public VolunteerDashboardPanel(User user, Runnable logoutAction, Runnable actionHandler) {
        super(user, new String[] {"View animals", "Care records", "Assigned events", "Volunteer hours",
                "Volunteer certificate", "Notifications"}, logoutAction, actionHandler);
    }
}
