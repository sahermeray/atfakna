package com.saher.authapp.activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.appevents.AppEventsLogger;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.saher.authapp.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;
import java.util.Collections;

public class LoginActivity extends AppCompatActivity {
    EditText loginEmailId, logInpasswd;
    Button btnLogIn;
    TextView signup;
    FirebaseAuth firebaseAuth;
    FirebaseAuth.AuthStateListener authStateListener;
    Toolbar logintoolbar;
    CallbackManager callbackmanager;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FacebookSdk.fullyInitialize();
        callbackmanager = CallbackManager.Factory.create();
        AccessToken token = AccessToken.getCurrentAccessToken();
        if (token != null){
            handelFacebookResponse(token);
        }
        setContentView(R.layout.activity_login);
        logintoolbar = findViewById(R.id.login_activity_toolbar);
        setSupportActionBar(logintoolbar);
        firebaseAuth = FirebaseAuth.getInstance();
        loginEmailId = findViewById(R.id.loginEmail);
        logInpasswd = findViewById(R.id.loginpaswd);
        btnLogIn = findViewById(R.id.btnLogIn);
        signup = findViewById(R.id.TVSignIn);
        authStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    Toast.makeText(LoginActivity.this, "user logged in", Toast.LENGTH_LONG).show();
                    Intent i = new Intent(LoginActivity.this, HomeActivity.class);
                    startActivity(i);
                } else {
                    Toast.makeText(LoginActivity.this, "login to continue", Toast.LENGTH_LONG).show();
                }
            }
        };
        signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(LoginActivity.this, SignUpActivity.class);
                startActivity(i);
            }
        });
        btnLogIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String userEmail = loginEmailId.getText().toString();
                final String userPaswd = logInpasswd.getText().toString();
                if (userEmail.isEmpty()) {
                    loginEmailId.setError("provide your email first");
                    loginEmailId.requestFocus();
                } else if (userPaswd.isEmpty()) {
                    logInpasswd.setError("enter password");
                    logInpasswd.requestFocus();
                } else if (userEmail.isEmpty() && userPaswd.isEmpty()) {
                    Toast.makeText(LoginActivity.this, "fields empty", Toast.LENGTH_LONG).show();
                } else if (!(userEmail.isEmpty() && userPaswd.isEmpty())) {
                    firebaseAuth.signInWithEmailAndPassword(userEmail, userPaswd).addOnCompleteListener(LoginActivity.this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (!task.isSuccessful()) {
                                Toast.makeText(LoginActivity.this, "not successful", Toast.LENGTH_LONG).show();
                            } else {
                                SharedPreferences sharedPreferences;
                                sharedPreferences = getSharedPreferences("emailandpassword", Context.MODE_PRIVATE);
                                SharedPreferences.Editor editor = sharedPreferences.edit();
                                editor.putString("password", userPaswd);
                                editor.putString("email", userEmail);
                                editor.commit();
                                startActivity(new Intent(LoginActivity.this, HomeActivity.class));
                            }
                        }
                    });
                } else {
                    Toast.makeText(LoginActivity.this, "error", Toast.LENGTH_LONG).show();
                }
            }

        });

        LoginButton loginButton = (LoginButton) findViewById(R.id.login_button);
        loginButton.setPermissions(Collections.singletonList("email"));
        // If you are using in a fragment, call loginButton.setFragment(this);

        // Callback registration
        loginButton.registerCallback(callbackmanager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                // App code
                handelFacebookResponse(loginResult.getAccessToken());
            }

            @Override
            public void onCancel() {
                Log.i("DEBUG", "canceled");
            }

            @Override
            public void onError(FacebookException exception) {
                Log.i("DEBUG", exception.toString());
            }
        });

        loginButton.callOnClick();

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackmanager.onActivityResult(requestCode, resultCode, data);

//        AccessToken token = AccessToken.getCurrentAccessToken();
//        if (token != null){
//            handelFacebookResponse(token);
//        }
    }

    private void handelFacebookResponse(AccessToken token) {

        Log.i("DEBUG", "token: " + token.getToken());

        GraphRequest request = GraphRequest.newMeRequest(
                token, (object, response) -> {

                    try {
                        String first_name = object.getString("first_name");
                        String last_name = object.getString("last_name");
                        String email = object.getString("email");

                        Log.i("DEBUG", email);

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                });
        Bundle parameters = new Bundle();
        parameters.putString("fields", "first_name,last_name,email,id");
        request.setParameters(parameters);
        request.executeAsync();
    }
}