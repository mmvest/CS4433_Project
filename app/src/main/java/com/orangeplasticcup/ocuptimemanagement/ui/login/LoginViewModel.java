package com.orangeplasticcup.ocuptimemanagement.ui.login;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
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

public class LoginViewModel extends ValidationViewModel {

    private MutableLiveData<LoginFormState> loginFormState = new MutableLiveData<>();
    private MutableLiveData<Result<LoggedInUser>> loginResult = new MutableLiveData<>();

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

        StringRequest loginPOSTRequest = new StringRequest(Request.Method.POST, URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                System.out.println("Server Response: " + response);
                if (response.equals("Login successful")) {
                    System.out.println("Login successful");
                    loginResult.setValue(new Result.Success<LoggedInUser>(new LoggedInUser(username, username)));
                }
                else {
                    loginResult.setValue(new Result.Error(new Exception("Server error response")));
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                System.out.println("Error Response: " + error);
                loginResult.setValue(new Result.Error(new Exception("Server error response")));
            }
        }) {
            @Override
            public byte[] getBody() throws AuthFailureError {
                return body.toString().getBytes();
            }
        };

        loginPOSTRequest.setRetryPolicy(new DefaultRetryPolicy(
                150,
                5,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
        ));
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