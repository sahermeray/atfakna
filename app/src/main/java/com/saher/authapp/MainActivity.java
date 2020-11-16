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

public class MainActivity extends AppCompatActivity {
    EditText emailId,passwd;
    Button btnSignUp;
    TextView signIn;
    FirebaseAuth firebaseAuth;
    Toolbar maintoolbar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        maintoolbar=findViewById(R.id.main_activity_toolbar);
        setSupportActionBar(maintoolbar);
        firebaseAuth=FirebaseAuth.getInstance();
        emailId=findViewById(R.id.ETemail);
        passwd=findViewById(R.id.ETpassword);
        btnSignUp=findViewById(R.id.btnSignUp);
        signIn=findViewById(R.id.TVSignIn);
        btnSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String emailID=emailId.getText().toString();
                String paswd=passwd.getText().toString();
                if(emailID.isEmpty()){
                    emailId.setError("Provide your email first!");
                    emailId.requestFocus();
                }else if(paswd.isEmpty()){
                    passwd.setError("set your password");
                    passwd.requestFocus();
                }else if(emailID.isEmpty()&&paswd.isEmpty()){
                    Toast.makeText(MainActivity.this,"fields empty",Toast.LENGTH_LONG).show();
                }else if(!(emailID.isEmpty()&&paswd.isEmpty())){
                    firebaseAuth.createUserWithEmailAndPassword(emailID,paswd).addOnCompleteListener(MainActivity.this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if(!task.isSuccessful()){
                                Toast.makeText(MainActivity.this.getApplicationContext(),"SignUp unsuccessful",Toast.LENGTH_LONG).show();
                            }else{
                                startActivity(new Intent(MainActivity.this,UserActivity.class));
                            }
                        }
                    });
                }else{
                    Toast.makeText(MainActivity.this,"error",Toast.LENGTH_LONG).show();
                }

            }
        });
        signIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


               Intent i=new Intent(MainActivity.this,ActivityLogin.class);
               startActivity(i);
            }
        });
    }

}