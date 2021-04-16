package com.orangeplasticcup.ocuptimemanagement.ui.register;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.view.View;
import android.widget.EditText;
import android.widget.Button;

import com.orangeplasticcup.ocuptimemanagement.R;

public class RegisterActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        final Toolbar toolbar = findViewById(R.id.toolbar);
        final EditText username = findViewById(R.id.newUsername);
        final EditText usernameConfirm = findViewById(R.id.newUsernameConfirm);
        final EditText password = findViewById(R.id.newPassword);
        final EditText passwordConfirm = findViewById(R.id.newPasswordConfirm);
        final Button registerButton = findViewById(R.id.registerUser);
        final Button backButton = findViewById(R.id.back);

        setSupportActionBar(toolbar);

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
    }
}