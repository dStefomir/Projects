package trac.portableexpensesdiary.expense;

import trac.portableexpensesdiary.login.AuthenticationException;
import trac.portableexpensesdiary.model.User;

public class SessionManager {

    private static SessionManager instance = new SessionManager();

    private User currentUser;

    private SessionManager() {
    }

    public static SessionManager getInstance() {
        return instance;
    }

    public User getCurrentUser() {

        return currentUser;
    }

    public void attemptLogin(
            String userName,
            String userPassword) throws AuthenticationException {

        User currentUser = User.getUserByCredentials(userName);

        if (currentUser != null) {

            if (!currentUser.getUserPassword()
                    .trim()
                    .toLowerCase()
                    .equals(userPassword.trim().toLowerCase())) {

                this.currentUser = null;

                throw new AuthenticationException("User name or password are not correct!");
            }

            this.currentUser = currentUser;
            this.currentUser.setLastLoginDate(System.currentTimeMillis());
            this.currentUser.save();

        } else {
            this.currentUser = null;

            throw new AuthenticationException("There is no such User!");
        }
    }
}