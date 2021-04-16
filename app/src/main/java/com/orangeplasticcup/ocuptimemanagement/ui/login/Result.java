package com.orangeplasticcup.ocuptimemanagement.ui.login;

import androidx.annotation.Nullable;

/**
 * Authentication result : success (user details) or error message.
 */
public class Result {
    @Nullable
    private LoggedInUserView success;
    @Nullable
    private Integer error;

    Result(@Nullable Integer error) {
        this.error = error;
    }

    Result(@Nullable LoggedInUserView success) {
        this.success = success;
    }

    @Nullable
    public LoggedInUserView getSuccess() {
        return success;
    }

    @Nullable
    public Integer getError() {
        return error;
    }
}