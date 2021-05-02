package com.baner.dsignguru.view.activities;

import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.baner.dsignguru.R;
import com.baner.dsignguru.util.Constants;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;

public class ForgotPasswordActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);

        FirebaseAuth auth = FirebaseAuth.getInstance();

        Button reset = findViewById(R.id.reset_password);
        TextInputEditText email = findViewById(R.id.email_txt);

        reset.setOnClickListener(v ->
                {
                    auth.sendPasswordResetEmail(email.getText().toString().trim())
                            .addOnCompleteListener(task -> {
                                if (task.isSuccessful()) {
                                    Log.d(Constants.TAG, "Email sent.");
                                    Toast.makeText(this, "Email has been sent", Toast.LENGTH_SHORT).show();
                                } else {
                                    Toast.makeText(this, "Failed to send email", Toast.LENGTH_SHORT).show();
                                    task.getException().printStackTrace();
                                }
                            });
                }
        );
    }
}