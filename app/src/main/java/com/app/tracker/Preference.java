package com.app.tracker;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;

import androidx.annotation.Nullable;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;

public class Preference {
    private static final String PREF_NAME = "Tracker";
    private static final String KEY_LOGGED_IN = "loggedIn";
    private static SharedPreferences mPref;
    private static Preference preference;


    private static SharedPreferences getSharedPreference(Context context) {
        return context.getSharedPreferences(PREF_NAME, Activity.MODE_PRIVATE);
    }

    private Preference(Context context) {
        mPref = getSharedPreference(context);
    }

    public static Preference get(Context applicationContext) {
        if (preference == null) {
            preference = new Preference(applicationContext);
        }
        return preference;
    }


    @Nullable
    public ArrayList<LocationData> getStats() {
        ArrayList<LocationData> arrayItems = new ArrayList<>();
        String data = mPref.getString("stats", null);
        if (data != null) {
            Gson gson = new Gson();
            Type type = new TypeToken<ArrayList<LocationData>>() {
            }.getType();
            arrayItems = gson.fromJson(data, type);

        }
        return arrayItems;
    }

    public void add(LocationData data) {
        ArrayList<LocationData> users = getStats();
        users.add(data);
        Gson gson = new Gson();
        String json = gson.toJson(users);
        mPref.edit().putString("stats", json).apply();
    }

    public boolean isSendToken() {
        return  mPref.getBoolean("tokenSent",false);
    }


    public void setSendToken() {
          mPref.edit().putBoolean("tokenSent",true).apply();
    }

}
