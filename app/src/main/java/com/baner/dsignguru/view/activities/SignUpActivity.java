package com.baner.dsignguru.view.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import com.baner.dsignguru.R;
import com.baner.dsignguru.model.User;
import com.baner.dsignguru.model.UserModel;
import com.baner.dsignguru.viewmodel.SignUpViewModel;
import com.google.android.material.textfield.TextInputEditText;

import static com.baner.dsignguru.util.Constants.USER;

public class SignUpActivity extends AppCompatActivity {

    private SignUpViewModel signUpViewModel;
    private TextView signUp;
    private TextInputEditText firstName, lastName, email, password, confirm, occupation, phone, language;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        firstName = (TextInputEditText) findViewById(R.id.first_name_txt);
        lastName = (TextInputEditText) findViewById(R.id.last_name_txt);
        email = (TextInputEditText) findViewById(R.id.email_txt);
        password = (TextInputEditText) findViewById(R.id.password_txt);
        confirm = (TextInputEditText) findViewById(R.id.confirm_password_txt);
        occupation = (TextInputEditText) findViewById(R.id.occupation_txt);
        phone = (TextInputEditText) findViewById(R.id.phone_txt);
        language = (TextInputEditText) findViewById(R.id.language_txt);
        signUp = (TextView) findViewById(R.id.sign_up_btn);

        signUpViewModel = new ViewModelProvider(this).get(SignUpViewModel.class);

        signUp.setOnClickListener(v -> {
            signUp();
        });
    }

    private void signUp(){
        UserModel userModel = new UserModel(
                "",
                firstName.getText().toString().trim(),
                lastName.getText().toString().trim(),
                email.getText().toString().trim(),
                phone.getText().toString().trim(),
                occupation.getText().toString().trim(),
                language.getText().toString().trim()
        );

        signUpViewModel.signUpWithEmail(userModel, email.getText().toString().trim(),
                password.getText().toString().trim());
        signUpViewModel.authenticatedUserLiveData.observe(this, authenticatedUser -> {
            if (authenticatedUser.isNew) {
                createNewUser(authenticatedUser);
            } else {
                goToMainActivity(authenticatedUser);
            }
        });

    }

    private void createNewUser(UserModel authenticatedUser) {
        signUpViewModel.createUser(authenticatedUser);
        signUpViewModel.createdUserLiveData.observe(this, user -> {
            if (user.isCreated) {
                toastMessage(user.firstName);
            }
            goToMainActivity(user);
        });
    }

    private void toastMessage(String name) {
        Toast.makeText(this, "Hi " + name + "!\n" + "Your account was successfully created.",
                Toast.LENGTH_LONG).show();
    }

    private void goToMainActivity(UserModel user) {
        Intent intent = new Intent(SignUpActivity.this, MainActivity.class);
        intent.putExtra(USER, user);
        startActivity(intent);
        finish();
    }
}