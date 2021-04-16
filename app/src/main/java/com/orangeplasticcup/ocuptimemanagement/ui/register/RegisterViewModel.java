package com.orangeplasticcup.ocuptimemanagement.ui.register;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.orangeplasticcup.ocuptimemanagement.R;
import com.orangeplasticcup.ocuptimemanagement.ui.ValidationViewModel;
import com.orangeplasticcup.ocuptimemanagement.ui.login.LoginFormState;
import com.orangeplasticcup.ocuptimemanagement.ui.login.Result;

public class RegisterViewModel extends ValidationViewModel {

    private MutableLiveData<LoginFormState> registerFormState = new MutableLiveData<>();
    private MutableLiveData<Result> loginResult = new MutableLiveData<>();

    LiveData<LoginFormState> getRegisterFormState() {
        return registerFormState;
    }

    LiveData<Result> getRegisterResult() {
        return loginResult;
    }

    public void register(String username, String password) {

    }

    public void loginDataChanged(String username, String usernameConfirm, String password, String passwordConfirm) {
        if (!isUserNameValid(username)) {
            registerFormState.setValue(new LoginFormState(R.string.invalid_username, null));
        } else if (!isPasswordValid(password)) {
            registerFormState.setValue(new LoginFormState(null, R.string.invalid_password));
        } else {
            registerFormState.setValue(new LoginFormState(true));
        }
    }
}
