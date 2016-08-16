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

public class Login extends AppCompatActivity {

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);

        initializeSignupButton();
        initializeNoLoginButton();
        initializeLoginButton();
    }

    public void initializeSignupButton() {
        Button signup = (Button) findViewById(R.id.signup);
        signup.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setClassName("com.pbm", "com.pbm.Signup");
                startActivity(intent);
            }
        });
    }

    public void initializeNoLoginButton() {
        Button noLogin = (Button) findViewById(R.id.no_login);
        noLogin.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent splashIntent = new Intent();
                splashIntent.setClassName("com.pbm", "com.pbm.SplashScreen");
                startActivityForResult(splashIntent, PinballMapActivity.QUIT_RESULT);
            }
        });
    }

    public void initializeLoginButton() {
        Button loginButton = (Button) findViewById(R.id.loginButton);
        loginButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                try {
                    TextInputLayout usernameWrapper = (TextInputLayout) findViewById(R.id.usernameWrapper);
                    TextInputLayout passwordWrapper = (TextInputLayout) findViewById(R.id.passwordWrapper);

                    String json = new RetrieveJsonTask().execute(
                        PinballMapActivity.regionlessBase +
                            "users/auth_details.json?password=" + passwordWrapper.getEditText().getText().toString() +
                            ";login=" + usernameWrapper.getEditText().getText().toString(),
                        "GET"
                    ).get();

                    final JSONObject jsonObject = new JSONObject(json.toString());
                    if (jsonObject.has("errors")) {
                        Login.super.runOnUiThread(new Runnable() {
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

                        Intent splashIntent = new Intent();
                        splashIntent.setClassName("com.pbm", "com.pbm.SplashScreen");
                        startActivityForResult(splashIntent, PinballMapActivity.QUIT_RESULT);
                    }
                } catch (InterruptedException | ExecutionException e) {
                    e.printStackTrace();
                } catch (JSONException e) {
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

    public void onBackPressed() {}
}
