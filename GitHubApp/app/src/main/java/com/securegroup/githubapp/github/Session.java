package com.securegroup.githubapp.github;

/**
 * Singleton class that holds the logged user information
 * <p>
 *
 * @author Stefomir Dimitrov </p>
 */
public class Session {

    private static Session instance;

    private long userId;
    private String userName;

    private Session() {}

    public static synchronized Session getInstance() {
        if(instance == null) {
            instance = new Session();
        }

        return instance;
    }

    public long getUserId() {
        return userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }
}
