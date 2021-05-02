package com.baner.dsignguru.view.activities;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.baner.dsignguru.R;
import com.baner.dsignguru.model.UserModel;
import com.baner.dsignguru.viewmodel.SplashViewModel;

import static com.baner.dsignguru.util.Constants.USER;

public class SplashActivity extends AppCompatActivity {

    private SplashViewModel splashViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        splashViewModel = new ViewModelProvider(this).get(SplashViewModel.class);
        checkIfUserIsAuthenticated();

    }

    private void checkIfUserIsAuthenticated() {
        splashViewModel.checkIfUserIsAuthenticated();
        splashViewModel.isUserAuthenticatedLiveData.observe(this, user -> {
            if (!user.isAuthenticated) {
                goToAuthInActivity();
            } else {
                goToMainActivity();
            }
        });
    }

    private void goToAuthInActivity() {
        Intent intent = new Intent(SplashActivity.this, SignInActivity.class);
        new Handler().postDelayed(() -> {
            startActivity(intent);
            finish();
        }, 2000);
    }

    private void goToMainActivity() {
        Intent intent = new Intent(SplashActivity.this, MainActivity.class);
        new Handler().postDelayed(() -> {
            startActivity(intent);
            finish();
        }, 2000);
    }
}