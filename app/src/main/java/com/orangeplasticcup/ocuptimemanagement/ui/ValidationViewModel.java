package com.orangeplasticcup.ocuptimemanagement.ui;

import androidx.lifecycle.ViewModel;

public class ValidationViewModel extends ViewModel {
    protected boolean isUserNameValid(String username) {
        if (username == null || username.trim().isEmpty()) {
            return false;
        }

        return !username.trim().contains(" ");
    }

    protected boolean isPasswordValid(String password) {
        if (password == null || password.trim().length() < 8) {
            return false;
        }

        return !password.trim().contains(" ");
    }
}
