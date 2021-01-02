package com.saher.authapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.saher.authapp.activity.HomeActivity;

public class VerificationActivity extends AppCompatActivity {
    TextView signup;
    FirebaseAuth firebaseAuth;
    FirebaseAuth.AuthStateListener authStateListener;
    Toolbar verificationtoolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verification);
        verificationtoolbar=findViewById(R.id.verification_toolbar);
        setSupportActionBar(verificationtoolbar);
        firebaseAuth= FirebaseAuth.getInstance();
        signup=findViewById(R.id.TVSignIn);
        SharedPreferences mpref=getSharedPreferences("emailandpassword",0);
        final String userPaswd=mpref.getString("password","");
        final String userEmail=mpref.getString("email","");

       /* authStateListener=new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user=firebaseAuth.getCurrentUser();
                if(user!=null){
                    Toast.makeText(ActivityLogin.this,"user logged in",Toast.LENGTH_LONG).show();
                    Intent i=new Intent(ActivityLogin.this,UserActivity.class);
                    startActivity(i);
                }else{
                    Toast.makeText(ActivityLogin.this,"login to continu",Toast.LENGTH_LONG).show();
                }
            }
        };*/

        /*btnLogIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {*/
        firebaseAuth.signInWithEmailAndPassword(userEmail, userPaswd).addOnCompleteListener(VerificationActivity.this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (!task.isSuccessful()) {
                    Toast.makeText(VerificationActivity.this, "not successful", Toast.LENGTH_LONG).show();
                } else {
                    //Toast.makeText(ActivityLogin.this, "welcome", Toast.LENGTH_LONG).show();
                }
            }
        });
        FirebaseUser user=firebaseAuth.getCurrentUser();
        user.sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                Toast.makeText(VerificationActivity.this,"we sent you a link..please verify your email address and press continue",Toast.LENGTH_LONG).show();
            }
        });
               /* }

            });*/
        signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FirebaseUser usertask = firebaseAuth.getCurrentUser();
                usertask.reload().addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        FirebaseUser user=firebaseAuth.getCurrentUser();
                        if(user.isEmailVerified()){
                            Intent i=new Intent(VerificationActivity.this, HomeActivity.class);
                            startActivity(i);
                        }else{
                            Toast.makeText(VerificationActivity.this,"please verify your email",Toast.LENGTH_LONG).show();
                        }
                    }
                });
            }
        });

    }
}