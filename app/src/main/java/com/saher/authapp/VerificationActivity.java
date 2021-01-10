package com.saher.authapp;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

public class VerificationActivity extends AppCompatActivity {
    Toolbar verificationtoolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verification);
        verificationtoolbar = findViewById(R.id.verification_toolbar);
        setSupportActionBar(verificationtoolbar);
    }
}