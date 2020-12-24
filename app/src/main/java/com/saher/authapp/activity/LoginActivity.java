package com.saher.authapp.activity;

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
import com.saher.authapp.R;

public class LoginActivity extends AppCompatActivity {
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
                    Toast.makeText(LoginActivity.this,"user logged in",Toast.LENGTH_LONG).show();
                    Intent i=new Intent(LoginActivity.this,UserActivity.class);
                    startActivity(i);
                }else{
                    Toast.makeText(LoginActivity.this,"login to continu",Toast.LENGTH_LONG).show();
                }
            }
        };
        signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i=new Intent(LoginActivity.this,HomeActivity.class);
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
                    Toast.makeText(LoginActivity.this,"fields empty",Toast.LENGTH_LONG).show();
                }else if(!(userEmail.isEmpty()&&userPaswd.isEmpty())){
                    firebaseAuth.signInWithEmailAndPassword(userEmail,userPaswd).addOnCompleteListener(LoginActivity.this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if(!task.isSuccessful()){
                                Toast.makeText(LoginActivity.this,"not successful",Toast.LENGTH_LONG).show();
                            }else{
                                startActivity(new Intent(LoginActivity.this,UserActivity.class));
                            }
                        }
                    });
                }else{
                    Toast.makeText(LoginActivity.this,"error",Toast.LENGTH_LONG).show();
                }
                }

        });

    }
}