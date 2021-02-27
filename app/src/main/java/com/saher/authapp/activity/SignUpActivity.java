package com.saher.authapp.activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.GraphRequest;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.hbb20.CountryCodePicker;
import com.saher.authapp.R;
import com.saher.authapp.model.UserSetting;
import com.facebook.FacebookSdk;
import com.facebook.appevents.AppEventsLogger;

import org.json.JSONException;

import java.util.Collections;


public class SignUpActivity extends AppCompatActivity {
    EditText emailId, passwd;
    Button btnSignUp;
    FirebaseAuth firebaseAuth;
    Toolbar maintoolbar;
    CountryCodePicker ccp;
    String country;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    final CollectionReference userSettingsCollectionReference = db.collection("UserSetting");
    CallbackManager callbackmanager;
    GoogleSignInClient mGoogleSignInClient;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        FacebookSdk.fullyInitialize();
        callbackmanager = CallbackManager.Factory.create();
        AccessToken token = AccessToken.getCurrentAccessToken();
        if (token != null){
            handelFacebookResponse(token);
        }
        GoogleSignInOptions gso=new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).requestIdToken("41273658110-oid1mafvl4085brhfqfrd8u50pim0dp1.apps.googleusercontent.com").requestEmail().build();
         mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
        findViewById(R.id.sign_in_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                switch (view.getId()){
                    case R.id.sign_in_button:
                    signIn();
                    break;
                }
            }
        });



        maintoolbar = findViewById(R.id.main_activity_toolbar);
        setSupportActionBar(maintoolbar);
        firebaseAuth = FirebaseAuth.getInstance();
        emailId = findViewById(R.id.ETemail);
        passwd = findViewById(R.id.ETpassword);
        btnSignUp = findViewById(R.id.btnSignUp);
        ccp=findViewById(R.id.create_account_country);
        country=ccp.getSelectedCountryName();

        FirebaseUser useriuser = firebaseAuth.getCurrentUser();
        if (useriuser != null) {
            Intent ii = new Intent(SignUpActivity.this, HomeActivity.class);
            startActivity(ii);
            finish();
        }

        ccp.setOnCountryChangeListener(new CountryCodePicker.OnCountryChangeListener() {
            @Override
            public void onCountrySelected() {
                country=ccp.getSelectedCountryName();
            }
        });


        btnSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String emailID = emailId.getText().toString();
                String paswd = passwd.getText().toString();
                if (emailID.isEmpty()) {
                    emailId.setError("Provide your email first!");
                    emailId.requestFocus();
                } else if (paswd.isEmpty()) {
                    passwd.setError("set your password");
                    passwd.requestFocus();
                } else if (emailID.isEmpty() && paswd.isEmpty()) {
                    Toast.makeText(SignUpActivity.this, "fields empty", Toast.LENGTH_LONG).show();
                }else if(country.isEmpty()){
                    Toast.makeText(SignUpActivity.this,"select country",Toast.LENGTH_LONG).show();
                } else if (!(emailID.isEmpty() && paswd.isEmpty()&&country.isEmpty())) {
                    SharedPreferences sharedPreferences;
                    sharedPreferences = getSharedPreferences("emailandpassword", Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putString("password", paswd);
                    editor.putString("email", emailID);
                    editor.commit();
                    firebaseAuth.createUserWithEmailAndPassword(emailID, paswd).addOnCompleteListener(SignUpActivity.this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                emailId.setText("");
                                passwd.setText("");
                                FirebaseUser useeer=FirebaseAuth.getInstance().getCurrentUser();
                                String useeerid=useeer.getUid();
                                userSettingsCollectionReference.add(new UserSetting(useeerid,country,"","English"));
                                //Toast.makeText(SignUpActivity.this,"user account created..welcome "+emailID,Toast.LENGTH_LONG).show();
                                Intent i = new Intent(SignUpActivity.this, HomeActivity.class);
                                startActivity(i);
                            }
                        }
                    });

                }
            }
        });


        LoginButton loginButton = (LoginButton) findViewById(R.id.create_account_with_facebook_button);
        loginButton.setPermissions(Collections.singletonList("email"));
        // If you are using in a fragment, call loginButton.setFragment(this);

        // Callback registration
        loginButton.registerCallback(callbackmanager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                // App code
                Toast.makeText(SignUpActivity.this,"success",Toast.LENGTH_LONG).show();
                handelFacebookResponse(loginResult.getAccessToken());
                handelFacebookAuth(loginResult.getAccessToken());
                //Toast.makeText(SignUpActivity.this,"success",Toast.LENGTH_LONG).show();
                //Intent t=new Intent(SignUpActivity.this,HomeActivity.class);
                //startActivity(t);
            }

            @Override
            public void onCancel() {
                Toast.makeText(SignUpActivity.this,"cancel",Toast.LENGTH_LONG).show();
            }

            @Override
            public void onError(FacebookException exception) {
                Toast.makeText(SignUpActivity.this,"error "+exception.toString(),Toast.LENGTH_LONG).show();
            }
        });



    }
    private void signIn(){
        Intent signInIntent=mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent,1);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackmanager.onActivityResult(requestCode, resultCode, data);
        if(requestCode==1){
            Task<GoogleSignInAccount>task=GoogleSignIn.getSignedInAccountFromIntent(data);
            handleSignInResult(task);

        }


    }
    private void handleSignInResult(Task<GoogleSignInAccount> completedTask){
        try{
           GoogleSignInAccount account=completedTask.getResult(ApiException.class);
           firebaseAuthWithGoogle(account.getIdToken());
            Toast.makeText(SignUpActivity.this,account.getEmail(),Toast.LENGTH_LONG).show();
        }catch (ApiException e){
            Toast.makeText(SignUpActivity.this,e.toString(),Toast.LENGTH_LONG).show();
        }
    }
    private void firebaseAuthWithGoogle(String idToken){
        AuthCredential credential= GoogleAuthProvider.getCredential(idToken,null);
        firebaseAuth.signInWithCredential(credential).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    FirebaseUser user=firebaseAuth.getCurrentUser();
                    String useeerid=user.getUid();
                    userSettingsCollectionReference.add(new UserSetting(useeerid,country,"","English"));
                    Toast.makeText(SignUpActivity.this,user.getEmail(),Toast.LENGTH_LONG).show();
                    Intent i=new Intent(SignUpActivity.this,HomeActivity.class);
                    i.putExtra("comefromgoogle",5);
                    startActivity(i);
                }else{
                    Toast.makeText(SignUpActivity.this,"error habibi",Toast.LENGTH_LONG).show();
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
                    userSettingsCollectionReference.add(new UserSetting(useeerid,country,"","English"));
                    Intent i=new Intent(SignUpActivity.this,HomeActivity.class);
                    i.putExtra("comefromface",5);
                    startActivity(i);

                } else{
                    Toast.makeText(SignUpActivity.this,"authrnication failed",Toast.LENGTH_LONG).show();


                }
            }
        });

    }

    private void handelFacebookResponse(AccessToken token) {
        GraphRequest request = GraphRequest.newMeRequest(
                token, (object, response) -> {

                    try {
                        String first_name = object.getString("first_name");
                        String last_name = object.getString("last_name");
                        String email = object.getString("email");


                        Toast.makeText(SignUpActivity.this,first_name+" "+last_name+" "+email,Toast.LENGTH_LONG).show();

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