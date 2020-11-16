package com.saher.authapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class ActivityLogin extends AppCompatActivity {
    EditText loginEmailId,logInpasswd;
    Button btnLogIn;
    TextView signup;
    FirebaseAuth firebaseAuth;
    FirebaseAuth.AuthStateListener authStateListener;
    Toolbar logintoolbar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        logintoolbar=findViewById(R.id.login_activity_toolbar);
        setSupportActionBar(logintoolbar);
        firebaseAuth=FirebaseAuth.getInstance();
        loginEmailId=findViewById(R.id.loginEmail);
        logInpasswd=findViewById(R.id.loginpaswd);
        btnLogIn=findViewById(R.id.btnLogIn);
        signup=findViewById(R.id.TVSignIn);
        authStateListener=new FirebaseAuth.AuthStateListener() {
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
        };
        signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i=new Intent(ActivityLogin.this,MainActivity.class);
                startActivity(i);
            }
        });
        btnLogIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String userEmail=loginEmailId.getText().toString();
                String userPaswd=logInpasswd.getText().toString();
                if(userEmail.isEmpty()){
                    loginEmailId.setError("provide your email first");
                    loginEmailId.requestFocus();
                }else if (userPaswd.isEmpty()){
                        logInpasswd.setError("enter password");
                        logInpasswd.requestFocus();
                    }else if(userEmail.isEmpty()&&userPaswd.isEmpty()){
                    Toast.makeText(ActivityLogin.this,"fields empty",Toast.LENGTH_LONG).show();
                }else if(!(userEmail.isEmpty()&&userPaswd.isEmpty())){
                    firebaseAuth.signInWithEmailAndPassword(userEmail,userPaswd).addOnCompleteListener(ActivityLogin.this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if(!task.isSuccessful()){
                                Toast.makeText(ActivityLogin.this,"not successful",Toast.LENGTH_LONG).show();
                            }else{
                                startActivity(new Intent(ActivityLogin.this,UserActivity.class));
                            }
                        }
                    });
                }else{
                    Toast.makeText(ActivityLogin.this,"error",Toast.LENGTH_LONG).show();
                }
                }

        });

    }
   // protected void onStart() {

      //  super.onStart();
       // firebaseAuth.addAuthStateListener(authStateListener);
   // }
}