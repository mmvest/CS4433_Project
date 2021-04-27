package com.orangeplasticcup.ocuptimemanagement.ui.login;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.orangeplasticcup.ocuptimemanagement.data.model.LoggedInUser;
import com.orangeplasticcup.ocuptimemanagement.data.Result;
import com.orangeplasticcup.ocuptimemanagement.R;
import com.orangeplasticcup.ocuptimemanagement.networking.NetworkManager;
import com.orangeplasticcup.ocuptimemanagement.ui.ValidationViewModel;

import org.json.JSONObject;

import java.io.IOException;
import java.util.Map;

public class LoginViewModel extends ValidationViewModel {

    private final MutableLiveData<LoginFormState> loginFormState = new MutableLiveData<>();
    private final MutableLiveData<Result<LoggedInUser>> loginResult = new MutableLiveData<>();

    private static final String URL = "http://66.103.121.23/api/login.php";

    LiveData<LoginFormState> getLoginFormState() {
        return loginFormState;
    }

    LiveData<Result<LoggedInUser>> getLoginResult() {
        return loginResult;
    }

    public void login(String username, String password) {

        final JSONObject body = new JSONObject();
        try {
            body.put("username", username);
            body.put("password", password);
        }
        catch(Exception ignore) {}

        LoggedInUser user = LoggedInUser.getInstance();
        StringRequest loginPOSTRequest = new StringRequest(Request.Method.POST, URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                if (response.equals("Login successful")) {
                    user.bindUserID(username);
                    loginResult.setValue(new Result.Success<LoggedInUser>(user));
                }
                else if(response.equals("Account does not exist or you used an incorrect username and password. Please try again.")) {
                    loginResult.setValue(new Result.Error(new IOException("Login failed")));
                } else {
                    loginResult.setValue(new Result.Error(new Exception("Server error: " + response)));
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                loginResult.setValue(new Result.Error(new Exception("Server error response")));
            }
        }) {
            @Override
            public byte[] getBody() throws AuthFailureError {
                return body.toString().getBytes();
            }

            @Override
            protected Response<String> parseNetworkResponse(NetworkResponse response) {
                user.bindSessionToken(response.headers.get("Set-Cookie"));
                return super.parseNetworkResponse(response);
            }
        };

        NetworkManager.getInstance().addToRequestQueue(loginPOSTRequest);
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