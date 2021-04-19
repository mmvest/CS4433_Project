package com.orangeplasticcup.ocuptimemanagement.data.networking;

import android.content.Context;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

public class NetworkManager {
    private static NetworkManager instance;
    private final RequestQueue requestQueue;

    private NetworkManager(Context context) {
        requestQueue = Volley.newRequestQueue(context);
    }

    public static synchronized NetworkManager getInstance() {
        return getInstance(null);
    }

    public static synchronized NetworkManager getInstance(Context context) {
        if(instance == null)
            instance = new NetworkManager(context);

        return instance;
    }

    public <T> void addToRequestQueue(Request<T> request) {
        requestQueue.add(request);
    }
}
