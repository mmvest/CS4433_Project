package com.orangeplasticcup.ocuptimemanagement.ui.register;

import androidx.annotation.Nullable;

public class RegisterFormState {
    @Nullable
    private final Integer usernameError;
    @Nullable
    private final Integer usernameMatchError;
    @Nullable
    private final Integer passwordError;
    @Nullable final Integer passwordMatchError;
    private final boolean isDataValid;

    public RegisterFormState(@Nullable Integer usernameError, @Nullable Integer usernameMatchError, @Nullable Integer passwordError, @Nullable Integer passwordMatchError) {
        this.usernameError = usernameError;
        this.usernameMatchError = usernameMatchError;
        this.passwordError = passwordError;
        this.passwordMatchError = passwordMatchError;
        this.isDataValid = false;
    }

    public RegisterFormState(boolean isDataValid) {
        this.usernameError = null;
        this.usernameMatchError = null;
        this.passwordError = null;
        this.passwordMatchError = null;
        this.isDataValid = isDataValid;
    }

    @Nullable
    public Integer getUsernameError() {
        return usernameError;
    }

    @Nullable
    public Integer getUsernameMatchError() { return usernameMatchError; }

    @Nullable
    public Integer getPasswordError() {
        return passwordError;
    }

    @Nullable
    public Integer getPasswordMatchError() { return passwordMatchError; }

    public boolean isDataValid() {
        return isDataValid;
    }
}
