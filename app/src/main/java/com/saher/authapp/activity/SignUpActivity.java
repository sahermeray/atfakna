package com.saher.authapp.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
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
import com.saher.authapp.VerificationActivity;

public class SignUpActivity extends AppCompatActivity {
    EditText emailId,passwd;
    Button btnSignUp;
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

        FirebaseUser useriuser=firebaseAuth.getCurrentUser();
        if(useriuser!=null){
            Intent ii=new Intent(SignUpActivity.this,HomeActivity.class);
            startActivity(ii);
            finish();
        }



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
                } else if (!(emailID.isEmpty() && paswd.isEmpty())) {
                    SharedPreferences sharedPreferences;
                    sharedPreferences=getSharedPreferences("emailandpassword", Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor=sharedPreferences.edit();
                    editor.putString("password",paswd);
                    editor.putString("email",emailID);
                    editor.commit();
                    firebaseAuth.createUserWithEmailAndPassword(emailID, paswd).addOnCompleteListener(SignUpActivity.this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                emailId.setText("");
                                passwd.setText("");
                                Toast.makeText(SignUpActivity.this,"user account created..welcome "+emailID,Toast.LENGTH_LONG).show();
                                Intent i = new Intent(SignUpActivity.this, VerificationActivity.class);
                                startActivity(i);
                            }
                        }
                    });

                }
            }
        });
    }

}