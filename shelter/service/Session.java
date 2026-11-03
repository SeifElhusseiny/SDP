package shelter.service;

import shelter.model.User;

public class Session {
    private User currentUser;

    public User getCurrentUser() {
        return currentUser;
    }

    public void login(User user) {
        currentUser = user;
    }

    public void logout() {
        currentUser = null;
    }

    public boolean isLoggedIn() {
        return currentUser != null;
    }
}
