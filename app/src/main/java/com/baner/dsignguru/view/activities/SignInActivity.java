package com.baner.dsignguru.view.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.baner.dsignguru.R;
import com.baner.dsignguru.model.User;
import com.baner.dsignguru.util.Constants;
import com.baner.dsignguru.viewmodel.SignInViewModel;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.GoogleAuthProvider;

import static com.baner.dsignguru.util.Constants.RC_SIGN_IN;
import static com.baner.dsignguru.util.Constants.USER;

public class SignInActivity extends AppCompatActivity {

    private SignInViewModel signInViewModel;
    private GoogleSignInClient googleSignInClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);

        Button googleBtn = (Button) findViewById(R.id.google_btn);
        Button facebookBtn = (Button) findViewById(R.id.facebook_btn);
        Button emailBtn = (Button) findViewById(R.id.email_btn);
        
        googleBtn.setOnClickListener(v -> googleSignIn());
        facebookBtn.setOnClickListener(v -> facebookSignIn());
        emailBtn.setOnClickListener(v -> {
            Intent intent = new Intent(SignInActivity.this, SignUpActivity.class);
            startActivity(intent);
        });

        signInViewModel = new ViewModelProvider(this).get(SignInViewModel.class);

        initGoogleSignInClient();
        
    }

    private void facebookSignIn() {
    }

    private void initGoogleSignInClient() {
        GoogleSignInOptions googleSignInOptions = new GoogleSignInOptions
                .Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        googleSignInClient = GoogleSignIn.getClient(this, googleSignInOptions);
    }

    private void googleSignIn() {
        Intent signInIntent = googleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                GoogleSignInAccount googleSignInAccount = task.getResult(ApiException.class);
                if (googleSignInAccount != null) {
                    getGoogleAuthCredential(googleSignInAccount);
                }
            } catch (ApiException e) {
                Log.d(Constants.TAG, "onActivityResult: "+e.getMessage());
                e.printStackTrace();
            }
        }
    }

    private void getGoogleAuthCredential(GoogleSignInAccount googleSignInAccount) {
        String googleTokenId = googleSignInAccount.getIdToken();
        AuthCredential googleAuthCredential = GoogleAuthProvider.getCredential(googleTokenId, null);
        signInWithGoogleAuthCredential(googleAuthCredential);
    }

    private void signInWithGoogleAuthCredential(AuthCredential googleAuthCredential) {
        signInViewModel.signInWithGoogle(googleAuthCredential);
        signInViewModel.authenticatedUserLiveData.observe(this, authenticatedUser -> {
            if (authenticatedUser.isNew) {
                createNewUser(authenticatedUser);
            } else {
                goToMainActivity(authenticatedUser);
            }
        });
    }

    private void createNewUser(User authenticatedUser) {
        signInViewModel.createUser(authenticatedUser);
        signInViewModel.createdUserLiveData.observe(this, user -> {
            if (user.isCreated) {
                toastMessage(user.name);
            }
            goToMainActivity(user);
        });
    }

    private void toastMessage(String name) {
        Toast.makeText(this, "Hi " + name + "!\n" + "Your account was successfully created.",
                Toast.LENGTH_LONG).show();
    }

    private void goToMainActivity(User user) {
        Intent intent = new Intent(SignInActivity.this, MainActivity.class);
        intent.putExtra(USER, user);
        startActivity(intent);
        finish();
    }
}