package com.baner.dsignguru.view.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.lifecycle.ViewModelProvider;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.baner.dsignguru.R;
import com.baner.dsignguru.model.User;
import com.baner.dsignguru.util.Constants;
import com.baner.dsignguru.viewmodel.SignInViewModel;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.GoogleAuthProvider;

import static com.baner.dsignguru.util.Constants.RC_SIGN_IN;
import static com.baner.dsignguru.util.Constants.USER;

public class EmailSignInActivity extends AppCompatActivity {

    private SignInViewModel signInViewModel;
    private GoogleSignInClient googleSignInClient;
    private CallbackManager callbackManager;
    private TextView signIn, signUp, forgotPassword;
    private TextInputEditText email, password;
    private TextInputLayout emailLayout, passwordLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FacebookSdk.sdkInitialize(this);
        setContentView(R.layout.activity_email_sign_in);

        CardView googleBtn = findViewById(R.id.google_btn);
        CardView fbBtn = findViewById(R.id.fb_btn);
        LoginButton facebookBtn = findViewById(R.id.facebook_btn);
        signIn = findViewById(R.id.sign_in_btn);
        signUp = findViewById(R.id.sign_up_txt);
        forgotPassword = findViewById(R.id.forgot_password_txt);
        email = findViewById(R.id.email_txt);
        password = findViewById(R.id.password_txt);
        emailLayout = findViewById(R.id.email);
        passwordLayout = findViewById(R.id.password);

        forgotPassword.setOnClickListener(v -> forgotPwd());

        fbBtn.setOnClickListener(v -> facebookBtn.performClick());

        signIn.setOnClickListener(v -> {
            if (TextUtils.isEmpty(email.getEditableText())){
                emailLayout.setError("Cannot be empty");
                return;
            } else {
                emailLayout.setError(null);
            }

            if (!isValidEmail(email.getEditableText())){
                emailLayout.setError("Invalid email id");
                return;
            } else {
                emailLayout.setError(null);
            }

            if (TextUtils.isEmpty(password.getEditableText())){
                passwordLayout.setError("Cannot be empty");
                return;
            } else {
                passwordLayout.setError(null);
            }

            emailSignIn();
        });

        signUp.setOnClickListener(v -> {
            Intent intent = new Intent(EmailSignInActivity.this, SignInActivity.class);
            startActivity(intent);
        });

        signInViewModel = new ViewModelProvider(this).get(SignInViewModel.class);

        initGoogleSignInClient();

        callbackManager = CallbackManager.Factory.create();
        facebookBtn.setReadPermissions("email", "public_profile");
        facebookBtn.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                Log.d(Constants.TAG, "facebook:onSuccess:" + loginResult);
                signInViewModel.signInWithFacebook(loginResult.getAccessToken());
                signInViewModel.authenticatedUserLiveData.observe(EmailSignInActivity.this, authenticatedUser -> {
                    if (authenticatedUser.isNew) {
                        createNewUser(authenticatedUser);
                    } else {
                        goToMainActivity(authenticatedUser);
                    }
                });
            }

            @Override
            public void onCancel() {
                Log.d(Constants.TAG, "facebook:onCancel");
            }

            @Override
            public void onError(FacebookException error) {
                Log.d(Constants.TAG, "facebook:onError", error);
            }
        });

        googleBtn.setOnClickListener(v -> googleSignIn());
    }

    public boolean isValidEmail(CharSequence target) {
        return (!TextUtils.isEmpty(target) && Patterns.EMAIL_ADDRESS.matcher(target).matches());
    }

    private void forgotPwd() {
        Intent intent = new Intent(EmailSignInActivity.this, ForgotPasswordActivity.class);
        startActivity(intent);
    }

    private void emailSignIn() {
        signInViewModel.signInWithEmail(email.getText().toString().trim(), password.getText().toString().trim());
        signInViewModel.authenticatedUserSignInMessage.observe(this, authenticatedMessage -> {
            if (authenticatedMessage.equals("Success")){
                Intent intent = new Intent(EmailSignInActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
            } else {
                Toast.makeText(this, "Sign in failed", Toast.LENGTH_SHORT).show();
            }
        });
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
                Log.d(Constants.TAG, "onActivityResult: " + e.getMessage());
                e.printStackTrace();
            }
        } else {
            callbackManager.onActivityResult(requestCode, resultCode, data);
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
        Intent intent = new Intent(EmailSignInActivity.this, MainActivity.class);
        intent.putExtra(USER, user);
        startActivity(intent);
        finish();
    }
}