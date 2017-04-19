package com.example.diogoaleixo.firebase_login;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

/**
 * Created by diogoaleixo on 19/04/2017.
 */

public class PreferencesManager {

    Context context;

    public PreferencesManager(Context context) {

        this.context = context;
    }

    public void saveLogin(String email, String password) {

        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = sharedPref.edit();

        editor.putString(context.getString(R.string.prefsEmailKey),email);
        editor.commit();

        editor.putString(context.getString(R.string.prefsPasswordKey),password);
        editor.commit();
    }

    public Boolean isLoginSaved() {

        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(context);
        String email = settings.getString(context.getString(R.string.prefsEmailKey),null);

        if (email != null) {

            if (email.length() > 0) {

                return true;
            }
        }
        return false;
    }

    public String getAuthEmail() {

        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(context);
        String email = settings.getString(context.getString(R.string.prefsEmailKey),null);
        return email;
    }

    public String getAuthPass() {

        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(context);
        String pass = settings.getString(context.getString(R.string.prefsPasswordKey),null);
        return pass;
    }

}
