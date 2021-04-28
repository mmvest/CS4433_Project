package com.orangeplasticcup.ocuptimemanagement.ui.home.main.settings;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.InputType;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.orangeplasticcup.ocuptimemanagement.R;
import com.orangeplasticcup.ocuptimemanagement.data.Result;
import com.orangeplasticcup.ocuptimemanagement.data.model.LoggedInUser;
import com.orangeplasticcup.ocuptimemanagement.networking.NetworkManager;
import com.orangeplasticcup.ocuptimemanagement.ui.ValidationViewModel;
import com.orangeplasticcup.ocuptimemanagement.ui.home.HomeScreenActivity;
import com.orangeplasticcup.ocuptimemanagement.ui.home.main.PageViewModel;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class SettingsFragment extends Fragment {
    private static final String RESET_PASSWORD_URL = "http://66.103.121.23/api/update_password.php";
    private static final String DELETE_USER_URL = "http://66.103.121.23/api/delete_user.php";
    private SettingsFragment instance;

    public static SettingsFragment newInstance() {
        return new SettingsFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(Bundle.EMPTY);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_settings_screen, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        instance = this;
        Button resetPasswordButton = view.findViewById(R.id.resetPassword);
        Button deleteAccountButton = view.findViewById(R.id.deleteAccount);

        resetPasswordButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                builder.setTitle("Set new password");
                EditText newPassword = new EditText(getContext());
                newPassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                builder.setView(newPassword);

                builder.setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String text = newPassword.getText().toString();

                        if(!ValidationViewModel.isPasswordValid(text)) {
                            Toast.makeText(view.getContext(), "Password must be longer than 8 characters", Toast.LENGTH_LONG).show();
                            return;
                        }

                        final JSONObject body = new JSONObject();
                        try {
                            body.put("password", text);
                        }
                        catch(Exception ignored) {
                            return;
                        }

                        StringRequest newPasswordPOSTRequest = new StringRequest(Request.Method.POST, RESET_PASSWORD_URL, new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {
                                System.out.println("Server Response: " + response);

                                switch (response) {
                                    case "Password change succesful.":
                                        Toast.makeText(view.getContext(), "Password successfully changed", Toast.LENGTH_LONG).show();
                                        break;
                                    case "Password change could not be completed.":
                                        Toast.makeText(view.getContext(), "Password change failed", Toast.LENGTH_LONG).show();
                                        break;
                                }
                            }
                        }, new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                System.out.println("Server Error: " + error);
                                Toast.makeText(view.getContext(), "Password change failed due to an unknown server error", Toast.LENGTH_LONG).show();
                            }
                        }) {
                            @Override
                            public byte[] getBody() throws AuthFailureError {
                                return body.toString().getBytes();
                            }

                            @Override
                            public Map<String, String> getHeaders() throws AuthFailureError {
                                Map<String,String> headers = new HashMap<>();
                                headers.put("Cookie", LoggedInUser.getInstance().getSessionToken());
                                return headers;
                            }
                        };

                        NetworkManager.getInstance(view.getContext().getApplicationContext()).addToRequestQueue(newPasswordPOSTRequest);
                    }
                });

                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });

                builder.show();
            }
        });

        deleteAccountButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                builder.setTitle("Delete account");
                EditText passwordConfirm = new EditText(getContext());
                passwordConfirm.setHint("Confirm password");
                passwordConfirm.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                builder.setView(passwordConfirm);

                builder.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String text = passwordConfirm.getText().toString();

                        if(!ValidationViewModel.isPasswordValid(text)) {
                            Toast.makeText(view.getContext(), "Password must be longer than 8 characters", Toast.LENGTH_LONG).show();
                            return;
                        }

                        final JSONObject body = new JSONObject();
                        try {
                            body.put("password", text);
                        }
                        catch(Exception ignored) {
                            return;
                        }

                        StringRequest newPasswordPOSTRequest = new StringRequest(Request.Method.POST, DELETE_USER_URL, new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {
                                System.out.println("Server Response" + response);
                                if ("User deleted. Logout Successful.".equals(response)) {
                                    Toast.makeText(view.getContext(), "User deleted. Logout Successful.", Toast.LENGTH_LONG).show();
                                    instance.getActivity().finish();
                                } else {
                                    Toast.makeText(view.getContext(), "Failed to delete account. Double check your password", Toast.LENGTH_LONG).show();
                                }
                            }
                        }, new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                System.out.println("Server Error: " + error);
                            }
                        }) {
                            @Override
                            public byte[] getBody() throws AuthFailureError {
                                return body.toString().getBytes();
                            }

                            @Override
                            public Map<String, String> getHeaders() throws AuthFailureError {
                                Map<String,String> headers = new HashMap<>();
                                headers.put("Cookie", LoggedInUser.getInstance().getSessionToken());
                                return headers;
                            }
                        };

                        NetworkManager.getInstance(view.getContext().getApplicationContext()).addToRequestQueue(newPasswordPOSTRequest);
                    }
                });

                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });

                builder.show();
            }
        });
    }
}
