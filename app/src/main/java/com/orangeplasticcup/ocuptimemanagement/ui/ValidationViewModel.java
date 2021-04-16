package com.orangeplasticcup.ocuptimemanagement.ui;

import androidx.lifecycle.ViewModel;

public class ValidationViewModel extends ViewModel {
    protected boolean isUserNameValid(String username) {
        if (username == null || username.contains("@")) {
            return false;
        }
        return !username.trim().isEmpty();
    }

    // A placeholder password validation check
    protected boolean isPasswordValid(String password) {
        return password != null && password.trim().length() >= 8;
    }
}
