package com.orangeplasticcup.ocuptimemanagement.ui.register;

import android.app.Activity;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.Observer;

import android.view.View;
import android.widget.EditText;
import android.widget.Button;

import com.orangeplasticcup.ocuptimemanagement.R;
import com.orangeplasticcup.ocuptimemanagement.ui.login.LoginFormState;
import com.orangeplasticcup.ocuptimemanagement.ui.login.Result;

public class RegisterActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        final Toolbar toolbar = findViewById(R.id.toolbar);

        final RegisterViewModel registerViewModel = new RegisterViewModel();
        final EditText username = findViewById(R.id.newUsername);
        final EditText usernameConfirm = findViewById(R.id.newUsernameConfirm);
        final EditText password = findViewById(R.id.newPassword);
        final EditText passwordConfirm = findViewById(R.id.newPasswordConfirm);
        final Button registerButton = findViewById(R.id.registerUser);
        final Button backButton = findViewById(R.id.back);

        setSupportActionBar(toolbar);

        registerViewModel.getRegisterFormState().observe(this, new Observer<LoginFormState>() {
            @Override
            public void onChanged(LoginFormState registerFormState) {
                if (registerFormState == null) {
                    return;
                }
                registerButton.setEnabled(registerFormState.isDataValid());
                if (registerFormState.getUsernameError() != null) {
                    username.setError(getString(registerFormState.getUsernameError()));
                }
                if (registerFormState.getPasswordError() != null) {
                    password.setError(getString(registerFormState.getPasswordError()));
                }
            }
        });

        registerViewModel.getRegisterResult().observe(this, new Observer<Result>() {
            @Override
            public void onChanged(Result result) {
                if(result == null) {
                    return;
                }
                if (result.getError() != null) {
                    //showLoginFailed(result.getError());
                }
                if (result.getSuccess() != null) {
                    //updateUiWithUser(result.getSuccess());

                    finish();
                }
                setResult(Activity.RESULT_OK);
            }
        });

        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
}