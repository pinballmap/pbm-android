package com.pbm;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.concurrent.ExecutionException;

public class Signup extends AppCompatActivity {

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.signup);

        setupSignUpButton();
    }

    public void setupSignUpButton() {

        final TextInputLayout usernameWrapper = (TextInputLayout) findViewById(R.id.usernameWrapper);
        final TextInputLayout emailWrapper = (TextInputLayout) findViewById(R.id.emailWrapper);
        final TextInputLayout passwordWrapper = (TextInputLayout) findViewById(R.id.passwordWrapper);
        final TextInputLayout confirmPasswordWrapper = (TextInputLayout) findViewById(R.id.confirmPasswordWrapper);

        Button btn = (Button) findViewById(R.id.signUpButton);

        btn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
            try{
                String json = new RetrieveJsonTask().execute(
                    PinballMapActivity.regionlessBase +
                        "users/signup.json?password=" + passwordWrapper.getEditText().getText().toString() +
                        ";confirm_password=" + confirmPasswordWrapper.getEditText().getText().toString() +
                        ";username=" + usernameWrapper.getEditText().getText().toString() +
                        ";email=" + emailWrapper.getEditText().getText().toString(),
                    "POST"
                ).get();

                final JSONObject jsonObject = new JSONObject(json);

                if (jsonObject.has("errors")) {
                    Signup.super.runOnUiThread(new Runnable() {
                        public void run() {
                        String error = null;
                        try {
                            error = URLDecoder.decode(jsonObject.getString("errors"), "UTF-8");
                            error = error.replace("\\/", "/");
                        } catch (JSONException | UnsupportedEncodingException e) {
                            e.printStackTrace();
                        }

                        Toast.makeText(getBaseContext(), error, Toast.LENGTH_LONG).show();
                        }
                    });
                } else {
                    JSONObject userObject = new JSONObject(jsonObject.getJSONObject("user").toString());
                    final SharedPreferences settings = getSharedPreferences(PinballMapActivity.PREFS_NAME, 0);
                    SharedPreferences.Editor editor = settings.edit();
                    editor.putString("authToken", userObject.getString("authentication_token"));
                    editor.putString("username", userObject.getString("username"));
                    editor.putString("email", userObject.getString("email"));
                    editor.commit();
                    Toast.makeText(getBaseContext(), "A confirmation link has been emailed to you. Follow the link to activate your account.", Toast.LENGTH_LONG).show();

                    Intent splashIntent = new Intent();
                    splashIntent.setClassName("com.pbm", "com.pbm.SplashScreen");
                    startActivityForResult(splashIntent, PinballMapActivity.QUIT_RESULT);
                }
            } catch (InterruptedException | ExecutionException | JSONException e) {
                e.printStackTrace();
            }
            }
        });
    }

    private void hideKeyboard() {
        View view = getCurrentFocus();
        if (view != null) {
            ((InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE)).
                    hideSoftInputFromWindow(view.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }

    public boolean onPrepareOptionsMenu (Menu menu) {
        return false;
    }
}
