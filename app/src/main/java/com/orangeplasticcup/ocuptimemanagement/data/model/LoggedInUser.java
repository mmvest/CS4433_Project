package com.orangeplasticcup.ocuptimemanagement.data.model;

/**
 * Data class that captures user information for logged in users retrieved from LoginRepository
 */
public class LoggedInUser {
    private String userId;
    private String sessionToken;

    private static LoggedInUser loggedInUser;

    private LoggedInUser() {}

    public String getUserId() {
        return userId;
    }
    public String getSessionToken() {
        return sessionToken;
    }

    public void bindUserID(String userID) {
        this.userId = userID;
    }

    public void bindSessionToken(String sessionToken) {
        this.sessionToken = sessionToken;
    }

    public static LoggedInUser getInstance() {
        if(loggedInUser == null) loggedInUser = new LoggedInUser();
        return loggedInUser;
    }
}