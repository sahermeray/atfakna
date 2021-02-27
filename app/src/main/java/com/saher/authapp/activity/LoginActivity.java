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
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.saher.authapp.R;
import com.saher.authapp.model.UserSetting;

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
    GoogleSignInClient mGoogleSignInClient;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    final CollectionReference userSettingsCollectionReference = db.collection("UserSetting");




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        FacebookSdk.fullyInitialize();
        callbackmanager = CallbackManager.Factory.create();
        AccessToken token = AccessToken.getCurrentAccessToken();
        if (token != null){
            handelFacebookResponse(token);
        }
        GoogleSignInOptions gso=new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).requestIdToken("41273658110-oid1mafvl4085brhfqfrd8u50pim0dp1.apps.googleusercontent.com").requestEmail().build();
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
        findViewById(R.id.sign_in_google_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                switch (view.getId()){
                    case R.id.sign_in_google_button:
                        signIn();
                        break;
                }


            }
        });


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
                handelFacebookAuth(loginResult.getAccessToken());

            }

            @Override
            public void onCancel() {
                Toast.makeText(LoginActivity.this,"cancel",Toast.LENGTH_LONG).show();
            }

            @Override
            public void onError(FacebookException exception) {
                Toast.makeText(LoginActivity.this,"error "+exception.toString(),Toast.LENGTH_LONG).show();
            }
        });

       // loginButton.callOnClick();

    }
    private void signIn(){
        Intent signInIntent=mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent,1);
    }

    private void handleSignInResult(Task<GoogleSignInAccount> completedTask){
        try{
            GoogleSignInAccount account=completedTask.getResult(ApiException.class);
            firebaseAuthWithGoogle(account.getIdToken());
            Toast.makeText(LoginActivity.this,account.getEmail(),Toast.LENGTH_LONG).show();
        }catch (ApiException e){
            Toast.makeText(LoginActivity.this,e.toString(),Toast.LENGTH_LONG).show();
        }
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackmanager.onActivityResult(requestCode, resultCode, data);
        if(requestCode==1){
            Task<GoogleSignInAccount>task=GoogleSignIn.getSignedInAccountFromIntent(data);
            handleSignInResult(task);}}

    private void firebaseAuthWithGoogle(String idToken){
        AuthCredential credential= GoogleAuthProvider.getCredential(idToken,null);
        firebaseAuth.signInWithCredential(credential).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    FirebaseUser user=firebaseAuth.getCurrentUser();
                    String useeerid=user.getUid();
                    userSettingsCollectionReference.whereEqualTo(UserSetting.FIELD_USER_ID,useeerid).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                        @Override
                        public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                            if (queryDocumentSnapshots.getDocuments().size() == 0) {
                                userSettingsCollectionReference.add(new UserSetting(useeerid,"syria","","English"));
                            } else {
                                return;
                            }
                            finish();
                        }
                    });
                    Toast.makeText(LoginActivity.this,user.getEmail(),Toast.LENGTH_LONG).show();
                    Intent i=new Intent(LoginActivity.this,HomeActivity.class);
                    i.putExtra("comefromgoogle",5);
                    startActivity(i);
                }else{
                    Toast.makeText(LoginActivity.this,"error habibi",Toast.LENGTH_LONG).show();
                }
            }
        });
    }






    private void handelFacebookAuth(AccessToken token) {
        AuthCredential credential= FacebookAuthProvider.getCredential(token.getToken());
        firebaseAuth.signInWithCredential(credential).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    FirebaseUser user=firebaseAuth.getCurrentUser();
                    String useeerid=user.getUid();
                    userSettingsCollectionReference.whereEqualTo(UserSetting.FIELD_USER_ID,useeerid).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                        @Override
                        public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                            if (queryDocumentSnapshots.getDocuments().size() == 0) {
                                userSettingsCollectionReference.add(new UserSetting(useeerid,"syria","","English"));
                            } else {
                               return;
                            }
                            finish();
                        }
                    });
                    Intent i=new Intent(LoginActivity.this,HomeActivity.class);
                    i.putExtra("comefromface",5);
                    startActivity(i);

                } else{
                    Toast.makeText(LoginActivity.this,"authrnication failed",Toast.LENGTH_LONG).show();


                }
            }
        });

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