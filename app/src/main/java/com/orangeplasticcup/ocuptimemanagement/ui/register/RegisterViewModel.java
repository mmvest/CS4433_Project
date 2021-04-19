package com.orangeplasticcup.ocuptimemanagement.ui.register;

import android.content.Context;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.util.HashMap;
import java.util.Map;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.orangeplasticcup.ocuptimemanagement.R;
import com.orangeplasticcup.ocuptimemanagement.ui.ValidationViewModel;
import com.orangeplasticcup.ocuptimemanagement.ui.login.LoginResult;

public class RegisterViewModel extends ValidationViewModel {

    private MutableLiveData<RegisterFormState> registerFormState = new MutableLiveData<>();
    private MutableLiveData<LoginResult> registerResult = new MutableLiveData<>();

    private static final String URL = "http://66.103.121.23/api/register.php";

    LiveData<RegisterFormState> getRegisterFormState() { return registerFormState; }
    LiveData<LoginResult> getRegisterResult() {
        return registerResult;
    }

    public void register(Context context, String username, String password) {
        //System.out.println("{username: " + username + ", password: " + password + "}");

        // When reimplemented as an API, this will need to setup a memory cache and network protocol
        // https://developer.android.com/training/volley/requestqueue#java
        // https://developer.android.com/topic/performance/graphics/cache-bitmap
        RequestQueue requestQueue = Volley.newRequestQueue(context);

        Map<String, String> data = new HashMap<>();
        data.put("username", username);
        data.put("password", password);

        StringRequest registerPOSTRequest = new StringRequest(Request.Method.POST, URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                System.out.println("Server Response: " + response);
                registerResult.setValue(new LoginResult(R.string.register_success));
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                System.out.println("Error Response: " + error);
                registerResult.setValue(new LoginResult(R.string.register_failure));
            }
        }) {
            protected Map<String, String> getParams() {
                return data;
            }
        };

        registerPOSTRequest.setRetryPolicy(new DefaultRetryPolicy(
                150,
                5,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
        ));
        // NOTE: Current error on request "com.android.volley.TimeoutError"
        requestQueue.add(registerPOSTRequest);
    }

    public void registerDataChanged(String username, String usernameConfirm, String password, String passwordConfirm) {
        if (!isUserNameValid(username)) {
            registerFormState.setValue(new RegisterFormState(R.string.invalid_username, null, null, null));
        } else if (!isPasswordValid(password)) {
            registerFormState.setValue(new RegisterFormState(null, null, R.string.invalid_password, null));
        } else if (!username.trim().equals(usernameConfirm.trim())) {
            registerFormState.setValue(new RegisterFormState(null, R.string.username_nonmatch, null, null));
        } else if (!password.trim().equals(passwordConfirm.trim())) {
            registerFormState.setValue(new RegisterFormState(null, null, null, R.string.password_nonmatch));
        } else {
            registerFormState.setValue(new RegisterFormState(true));
        }
    }
}
