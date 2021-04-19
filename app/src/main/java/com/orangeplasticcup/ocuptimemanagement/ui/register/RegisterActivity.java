package com.orangeplasticcup.ocuptimemanagement.ui.register;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.Observer;

import android.text.Editable;
import android.view.View;
import android.text.TextWatcher;
import android.widget.EditText;
import android.widget.Button;
import android.widget.Toast;

import com.orangeplasticcup.ocuptimemanagement.R;
import com.orangeplasticcup.ocuptimemanagement.data.Result;

public class RegisterActivity extends AppCompatActivity {

    private RegisterActivity instance;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        instance = this;

        final Toolbar toolbar = findViewById(R.id.toolbar);

        final RegisterViewModel registerViewModel = new RegisterViewModel();
        final EditText username = findViewById(R.id.newUsername);
        final EditText usernameConfirm = findViewById(R.id.newUsernameConfirm);
        final EditText password = findViewById(R.id.newPassword);
        final EditText passwordConfirm = findViewById(R.id.newPasswordConfirm);
        final Button registerButton = findViewById(R.id.registerUser);
        final Button backButton = findViewById(R.id.back);

        setSupportActionBar(toolbar);

        registerViewModel.getRegisterFormState().observe(this, new Observer<RegisterFormState>() {
            @Override
            public void onChanged(RegisterFormState registerFormState) {
                if (registerFormState == null) {
                    return;
                }
                registerButton.setEnabled(registerFormState.isDataValid());
                if (registerFormState.getUsernameError() != null) {
                    username.setError(getString(registerFormState.getUsernameError()));
                }
                if (registerFormState.getUsernameMatchError() != null) {
                    usernameConfirm.setError(getString(registerFormState.getUsernameMatchError()));
                }
                if (registerFormState.getPasswordError() != null) {
                    password.setError(getString(registerFormState.getPasswordError()));
                }
                if (registerFormState.getPasswordMatchError() != null) {
                    passwordConfirm.setError(getString(registerFormState.getPasswordMatchError()));
                }
            }
        });

        registerViewModel.getRegisterResult().observe(this, new Observer<Result>() {
            @Override
            public void onChanged(Result loginResult) {
                if(loginResult == null) {
                    return;
                }
                if (loginResult instanceof Result.Error) {
                    Result.Error loginError = (Result.Error) loginResult;
                    Toast.makeText(getApplicationContext(), loginError.getError().getMessage(), Toast.LENGTH_LONG).show();
                    registerButton.setEnabled(true);
                }
                if (loginResult instanceof Result.Success) {
                    Result.Success loginSuccess = (Result.Success) loginResult;
                    Toast.makeText(getApplicationContext(), loginSuccess.getData().toString(), Toast.LENGTH_LONG).show();

                    finish();
                }
            }
        });

        TextWatcher afterTextChangedListener = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                registerViewModel.registerDataChanged(
                        username.getText().toString().trim(),
                        usernameConfirm.getText().toString().trim(),
                        password.getText().toString().trim(),
                        passwordConfirm.getText().toString().trim());
            }
        };

        username.addTextChangedListener(afterTextChangedListener);
        usernameConfirm.addTextChangedListener(afterTextChangedListener);
        password.addTextChangedListener(afterTextChangedListener);
        passwordConfirm.addTextChangedListener(afterTextChangedListener);

        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                registerViewModel.register(instance, username.getText().toString().trim(), password.getText().toString().trim());
                registerButton.setEnabled(false);
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