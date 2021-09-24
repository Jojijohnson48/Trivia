package com.example.trivia.controller;

import android.app.Application;
import android.content.Context;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

/*
 we are creating a singleton class and creating an instance of it using Application class
 and not by making its constructor private
*/
public class AppController extends Application {
    private static AppController instance;
    private RequestQueue queue;
/*
    private Context context;

    private AppController(Context context) {
        this.context = context;
        queue = getRequestQueue();
    }
*/

    public static synchronized AppController getInstance(/*context*/){
/*
        if(instance == null){
            instance = new AppController(context);
        }
*/
        return instance;
    }

    public RequestQueue getRequestQueue(){
        if(queue == null){
            queue = Volley.newRequestQueue(this.getApplicationContext());
            /* we have given this class a global access so do not require context variable
            to get the application context. The context of this activity is actually the
            application context. */
        }
        return queue;
    }
    public <T> void addToRequestQueue(Request<T> req){
        getRequestQueue().add(req);
    }

    // whenever this activity is created, its instance gets instantiated;
    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
    }
}
