package com.orangeplasticcup.ocuptimemanagement.ui.login;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.orangeplasticcup.ocuptimemanagement.data.LoginRepository;
import com.orangeplasticcup.ocuptimemanagement.data.model.LoggedInUser;
import com.orangeplasticcup.ocuptimemanagement.R;
import com.orangeplasticcup.ocuptimemanagement.ui.ValidationViewModel;

public class LoginViewModel extends ValidationViewModel {

    private MutableLiveData<LoginFormState> loginFormState = new MutableLiveData<>();
    private MutableLiveData<Result> loginResult = new MutableLiveData<>();
    private LoginRepository loginRepository;

    LoginViewModel(LoginRepository loginRepository) {
        this.loginRepository = loginRepository;
    }

    LiveData<LoginFormState> getLoginFormState() {
        return loginFormState;
    }

    LiveData<Result> getLoginResult() {
        return loginResult;
    }

    public void login(String username, String password) {
        // can be launched in a separate asynchronous job
        com.orangeplasticcup.ocuptimemanagement.data.Result result = loginRepository.login(username, password);

        if (result instanceof com.orangeplasticcup.ocuptimemanagement.data.Result.Success) {
            LoggedInUser data = ((com.orangeplasticcup.ocuptimemanagement.data.Result.Success<LoggedInUser>) result).getData();
            loginResult.setValue(new Result(new LoggedInUserView(data.getDisplayName())));
        } else {
            loginResult.setValue(new Result(R.string.login_failed));
        }
    }

    public void loginDataChanged(String username, String password) {
        if (!isUserNameValid(username)) {
            loginFormState.setValue(new LoginFormState(R.string.invalid_username, null));
        } else if (!isPasswordValid(password)) {
            loginFormState.setValue(new LoginFormState(null, R.string.invalid_password));
        } else {
            loginFormState.setValue(new LoginFormState(true));
        }
    }
}