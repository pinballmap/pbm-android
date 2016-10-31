package com.pbm;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.ActivityCompat;
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

public class Login extends AppCompatActivity implements ActivityCompat.OnRequestPermissionsResultCallback{

    private boolean shouldCheckPermission = true;
    private static final int PERMISSION_RESULT = 1;

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        if(grantResults[0] == PackageManager.PERMISSION_DENIED){
            if (!ActivityCompat.shouldShowRequestPermissionRationale(this, android.Manifest.permission.ACCESS_FINE_LOCATION)) {
                final Intent i = new Intent();
                i.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                i.addCategory(Intent.CATEGORY_DEFAULT);
                i.setData(Uri.parse("package:" + getPackageName()));
                i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                i.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                i.addFlags(Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
                startActivity(i);
                Toast.makeText(this, R.string.please_allow_location_permission, Toast.LENGTH_LONG).show();
                shouldCheckPermission = true;
            } else
                requestLocationPermission();
        }
    }

    private boolean requestLocationPermission() {
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{
                    android.Manifest.permission.ACCESS_FINE_LOCATION}, PERMISSION_RESULT);
            return false;
        }
        return true;
    }

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestLocationPermission();
        shouldCheckPermission = false;
        setContentView(R.layout.login);
        initializeSignupButton();
        initializeNoLoginButton();
        initializeLoginButton();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        if(shouldCheckPermission){
            requestLocationPermission();
        }
        shouldCheckPermission = false;
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
            splashIntent.putExtra("isGuestLogin", true);
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

                final JSONObject jsonObject = new JSONObject(json);
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
                    editor.putString("id", userObject.getString("id"));
                    editor.commit();

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

    public void onBackPressed() {}
}
