package com.example.diogoaleixo.firebase_login;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.support.annotation.NonNull;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

/**
 * Created by diogoaleixo on 03/05/2017.
 */

interface OnAuthRequestListener {

    void didFinishAuthRequest(Boolean forPasswordReset);
    void didStartAuthRequest(Boolean forPasswordReset);
    void didFinishAuthRequestWithError(Boolean forPasswordReset);
}

public class FirebaseWrapper {

    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private OnAuthRequestListener mCallBack;



    public void FirebaseWrapperWitAuth () {

        addAuthListner();

        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                String TAG = "w";
                if (user != null) {

                    Log.d(TAG, "onAuthStateChanged:signed_in:" + user.getUid());
                } else {
                    // User is signed out
                    Log.d(TAG, "onAuthStateChanged:signed_out");
                }
                // ...
            }
        };
    }

    public void setAuth(FirebaseAuth mAuth) {

        this.mAuth = mAuth;
    }

    public void addAuthListner() {
        mAuth.addAuthStateListener(mAuthListener);
    }

    public void removeAuthListner() {

        if (mAuthListener != null) {

            mAuth.removeAuthStateListener(mAuthListener);
        }
    }

    public void signResetUser(final String email,
                              final String password,
                              final Boolean resetPass,
                              final String newPass,
                              final Context context,
                              final PreferencesUtils prefsHelper) {

        try{

            mCallBack = (OnAuthRequestListener) context;
        }catch (ClassCastException e) {

            throw new ClassCastException(context.toString()
                    + " must implement OnAuthRequestListener");
        }
        mCallBack.didStartAuthRequest(false);
        
        mAuth.signInWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {

                if (!task.isSuccessful()) {

                    mCallBack.didFinishAuthRequestWithError(false);
                } else {

                    if (resetPass) {

                        final FirebaseUser user;
                        user = FirebaseAuth.getInstance().getCurrentUser();

                        user.updatePassword(newPass).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if(!task.isSuccessful()){

                                    mCallBack.didFinishAuthRequestWithError(true);
                                }else {

                                    mCallBack.didFinishAuthRequest(true);
                                    prefsHelper.saveLogin(email,newPass);
                                }
                            }
                        });
                    } else {

                        mCallBack.didFinishAuthRequest(false);
                        prefsHelper.saveLogin(email,password);
                    }
                }
            }
        });
    }
}
