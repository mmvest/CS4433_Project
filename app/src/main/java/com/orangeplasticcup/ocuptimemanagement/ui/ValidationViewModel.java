package com.orangeplasticcup.ocuptimemanagement.ui;

import androidx.lifecycle.ViewModel;

public class ValidationViewModel extends ViewModel {
    protected boolean isUserNameValid(String username) {
        return username != null && !username.isEmpty() && !username.contains(" ");
    }

    public static boolean isPasswordValid(String password) {
        return password != null && !password.contains(" ") && password.length() >= 8;
    }
}
