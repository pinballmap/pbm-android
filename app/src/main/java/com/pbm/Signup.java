package com.pbm;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.View;
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
                        String.format("%susers/signup.json?password=%s;confirm_password=%s;username=%s;email=%s", PinballMapActivity.regionlessBase, passwordWrapper.getEditText().getText().toString(), confirmPasswordWrapper.getEditText().getText().toString(), usernameWrapper.getEditText().getText().toString(), emailWrapper.getEditText().getText().toString()),
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
                    Toast.makeText(getBaseContext(), "A confirmation link has been emailed to you. Follow the link to activate your account.", Toast.LENGTH_LONG).show();

					Intent loginIntent = new Intent();
					loginIntent.setClassName("com.pbm", "com.pbm.Login");
					startActivityForResult(loginIntent, PinballMapActivity.QUIT_RESULT);
                }
            } catch (InterruptedException | ExecutionException | JSONException e) {
                e.printStackTrace();
            }
            }
        });
    }

    public boolean onPrepareOptionsMenu (Menu menu) {
        return false;
    }
}
