package com.orangeplasticcup.ocuptimemanagement.ui.home;

import android.os.Bundle;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.tabs.TabLayout;

import androidx.viewpager.widget.ViewPager;
import androidx.appcompat.app.AppCompatActivity;

import android.view.View;

import com.orangeplasticcup.ocuptimemanagement.R;
import com.orangeplasticcup.ocuptimemanagement.networking.NetworkManager;
import com.orangeplasticcup.ocuptimemanagement.ui.home.main.SectionsPagerAdapter;

public class HomeScreenActivity extends AppCompatActivity {

    private static final String NEW_ENTRY_URL = "http://66.103.121.23/api/create_entry.php";
    private static final String LOGOUT_URL = "http://66.103.121.23/api/logout.php";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_screen);
        SectionsPagerAdapter sectionsPagerAdapter = new SectionsPagerAdapter(this, getSupportFragmentManager());
        ViewPager viewPager = findViewById(R.id.view_pager);
        viewPager.setAdapter(sectionsPagerAdapter);
        TabLayout tabs = findViewById(R.id.tabs);
        tabs.setupWithViewPager(viewPager);
        FloatingActionButton newEntry = findViewById(R.id.newEntry);
        FloatingActionButton logout = findViewById(R.id.logout);

        newEntry.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                StringRequest logoutPOSTRequest = new StringRequest(Request.Method.POST, LOGOUT_URL, new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {}
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {}
                });

                logoutPOSTRequest.setRetryPolicy(new DefaultRetryPolicy(
                        150,
                        5,
                        DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
                ));

                NetworkManager.getInstance(getApplicationContext()).addToRequestQueue(logoutPOSTRequest);
                finish();
            }
        });
    }
}