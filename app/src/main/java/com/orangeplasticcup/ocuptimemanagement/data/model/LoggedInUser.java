package com.orangeplasticcup.ocuptimemanagement.data.model;

/**
 * Data class that captures user information for logged in users retrieved from LoginRepository
 */
public class LoggedInUser {
    private String userId;
    private String sessionToken;

    private static LoggedInUser loggedInUser;

    private LoggedInUser() {}

    public LoggedInUser(String userId, String sessionToken) {
        this.userId = userId;
        this.sessionToken = sessionToken;
        loggedInUser = this;
    }

    public String getUserId() {
        return userId;
    }
    public String getSessionToken() {
        return sessionToken;
    }

    public void bindUserID(String userID) {
        if (userId == null) {
            this.userId = userID;
        }
        else {
            throw new RuntimeException("Rebinding of UserID");
        }
    }

    public void bindSessionToken(String sessionToken) {
        this.sessionToken = sessionToken;
        if (sessionToken == null) {
            //this.sessionToken = sessionToken;
        }
        else {
            //throw new RuntimeException("Rebinding of SessionToken");
        }
    }

    public static LoggedInUser getInstance() {
        if(loggedInUser == null) loggedInUser = new LoggedInUser();
        return loggedInUser;
    }
}