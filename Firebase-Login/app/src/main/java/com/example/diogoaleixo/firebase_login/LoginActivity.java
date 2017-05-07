package com.example.diogoaleixo.firebase_login;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LoginActivity extends AppCompatActivity implements OnAuthRequestListener {

    private EditText password;
    private EditText email;
    private Button submitButton;
    private Button resetButton;
    private ProgressDialog progressDialog;
    private PreferencesUtils prefsHelper;
    private FirebaseWrapper firebaseWrapper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        password = (EditText)findViewById(R.id.text_password);
        email = (EditText) findViewById(R.id.text_email);
        submitButton = (Button) findViewById(R.id.button_submit);
        resetButton = (Button) findViewById(R.id.button_reset);

        prefsHelper = new PreferencesUtils(this);

        firebaseWrapper = new FirebaseWrapper();
        firebaseWrapper.setAuth(FirebaseAuth.getInstance());

        if (!TextUtils.isEmpty(prefsHelper.getAuthEmail())) {

            presentMainScreen();
            return;
        }

        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (!isNetworkReachable()) {

                    showNoInternetError();
                    return;
                }

                if (TextUtils.isEmpty(email.getText().toString()) || TextUtils.isEmpty(password.getText().toString())) {

                    Toast.makeText(LoginActivity.this, "Email and / or password left empt",
                            Toast.LENGTH_SHORT).show();

                } else {
                    firebaseWrapper.signResetUser(
                            email.getText().toString(),
                            password.getText().toString(),
                            false,"",
                            LoginActivity.this,prefsHelper
                    );
                }
            }
        });

        resetButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (!isNetworkReachable()) {

                    showNoInternetError();
                    return;
                }

                if (!prefsHelper.isLoginSaved()) {

                    Toast.makeText(LoginActivity.this, R.string.resetPasswordNoLogin,
                            Toast.LENGTH_SHORT).show();
                } else {

                    resetAlert();
                }
            }
        });

        password.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {

                    hideKeyboard(v);
                }
            }
        });

        email.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {

                    hideKeyboard(v);
                }
            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();


    }

    @Override
    public void onStop() {
        super.onStop();

        firebaseWrapper.removeAuthListner();
    }

    public void hideKeyboard(View view) {

        InputMethodManager inputMethodManager =(InputMethodManager)getSystemService(LoginActivity.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    private void resetAlert () {

        AlertDialog.Builder builder1 = new AlertDialog.Builder(LoginActivity.this);
        builder1.setMessage("Write here your new password");
        builder1.setCancelable(true);
        final EditText edittext = new EditText(LoginActivity.this);
        builder1.setView(edittext);

        builder1.setPositiveButton(
                "Set new password",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {

                        firebaseWrapper.signResetUser(
                                prefsHelper.getAuthEmail(),
                                prefsHelper.getAuthPass(),
                                true,
                                edittext.getText().toString(),
                                LoginActivity.this,
                                prefsHelper
                        );
                    }
                });

        builder1.setNegativeButton(
                "Cancel",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });

        AlertDialog alert11 = builder1.create();
        alert11.show();
    }

    private boolean isNetworkReachable () {

        ConnectivityManager cm = (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
        return cm.getActiveNetworkInfo() != null;
    }

    private void showNoInternetError () {

        Toast.makeText(LoginActivity.this, "You do not have internet access",
                Toast.LENGTH_SHORT).show();
    }

    private void presentMainScreen() {

        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
        LoginActivity.this.startActivity(intent);
    }

    //Firebase wrapper interface methods
    @Override
    public void didFinishAuthRequest(Boolean forPasswordReset) {

        if (forPasswordReset) {

        } else {

        }
        presentMainScreen();
        progressDialog.hide();
        //Toast.makeText(LoginActivity.this,"Authenticated",Toast.LENGTH_LONG).show();

    }

    @Override
    public void didStartAuthRequest(Boolean forPasswordReset) {

        String msg;
        if (forPasswordReset) {
            msg = "Resetting password";
        } else {
            msg = "Authenticating";
        }

        progressDialog = new ProgressDialog(LoginActivity.this);
        progressDialog.setMessage(msg);
        progressDialog.show();
    }

    @Override
    public void didFinishAuthRequestWithError(Boolean forPasswordReset) {

        String msg;
        if (forPasswordReset) {
            msg = "Error resetting password";
        } else {
            msg = "Error while authenticating";
        }

        Toast.makeText(LoginActivity.this,msg,Toast.LENGTH_LONG).show();
        progressDialog.hide();
    }
}
