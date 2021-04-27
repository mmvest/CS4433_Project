package com.orangeplasticcup.ocuptimemanagement.ui.register;

import android.content.Context;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.orangeplasticcup.ocuptimemanagement.R;
import com.orangeplasticcup.ocuptimemanagement.networking.NetworkManager;
import com.orangeplasticcup.ocuptimemanagement.ui.ValidationViewModel;
import com.orangeplasticcup.ocuptimemanagement.data.Result;

import org.json.JSONObject;

public class RegisterViewModel extends ValidationViewModel {

    private final MutableLiveData<RegisterFormState> registerFormState = new MutableLiveData<>();
    private final MutableLiveData<Result<String>> registerResult = new MutableLiveData<>();

    private static final String URL = "http://66.103.121.23/api/register.php";

    LiveData<RegisterFormState> getRegisterFormState() { return registerFormState; }
    LiveData<Result<String>> getRegisterResult() {
        return registerResult;
    }

    public void register(Context context, String username, String password) {

        final JSONObject body = new JSONObject();
        try {
            body.put("username", username);
            body.put("password", password);
        }
        catch(Exception ignored) {
            registerResult.setValue(new Result.Error(new Exception(context.getString(R.string.register_unknown_error))));
            return;
        }

        StringRequest registerPOSTRequest = new StringRequest(Request.Method.POST, URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                switch (response) {
                    case "Please do not include spaces in your username entry. Registration could not be completed.":
                    case "Please do not include spaces in your password entry. Registration could not be completed.":
                        registerResult.setValue(new Result.Error(new Exception(context.getString(R.string.register_bad_format))));
                        break;
                    case "Registration could not be completed.":
                        registerResult.setValue(new Result.Error(new Exception(context.getString(R.string.register_bad_username))));
                        break;
                    case "Registered Successfully.":
                        registerResult.setValue(new Result.Success<String>("Successfully registered user '" + username + "'"));
                        break;
                    default:
                        registerResult.setValue(new Result.Error(new Exception(context.getString(R.string.register_unknown_error))));
                        break;
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                System.out.println("Error Response: " + error);
                registerResult.setValue(new Result.Error(new Exception(context.getString(R.string.register_unknown_error))));
            }
        }) {
            @Override
            public byte[] getBody() throws AuthFailureError {
                return body.toString().getBytes();
            }
        };
        
        NetworkManager.getInstance().addToRequestQueue(registerPOSTRequest);
    }

    public void registerDataChanged(String username, String usernameConfirm, String password, String passwordConfirm) {
        if (!isUserNameValid(username)) {
            registerFormState.setValue(new RegisterFormState(R.string.invalid_username, null, null, null));
        } else if (!username.trim().equals(usernameConfirm.trim())) {
            registerFormState.setValue(new RegisterFormState(null, R.string.username_nonmatch, null, null));
        } else if (!isPasswordValid(password)) {
            registerFormState.setValue(new RegisterFormState(null, null, R.string.invalid_password, null));
        } else if (!password.trim().equals(passwordConfirm.trim())) {
            registerFormState.setValue(new RegisterFormState(null, null, null, R.string.password_nonmatch));
        } else {
            registerFormState.setValue(new RegisterFormState(true));
        }
    }
}
