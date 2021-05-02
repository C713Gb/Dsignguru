package com.baner.dsignguru.view.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.util.Patterns;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.TextView;
import android.widget.Toast;

import com.baner.dsignguru.R;
import com.baner.dsignguru.model.User;
import com.baner.dsignguru.model.UserModel;
import com.baner.dsignguru.util.Constants;
import com.baner.dsignguru.viewmodel.SignUpViewModel;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import static com.baner.dsignguru.util.Constants.USER;

public class SignUpActivity extends AppCompatActivity {

    private SignUpViewModel signUpViewModel;
    private TextView signUp;
    private TextInputLayout firstNameLayout, lastNameLayout, emailLayout, phoneLayout, passwordLayout,
    confirmLayout, occupationLayout, languageLayout;
    private TextInputEditText firstName, lastName, email, password, confirm, phone;
    AutoCompleteTextView occupation, language;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        firstName = findViewById(R.id.first_name_txt);
        firstNameLayout = findViewById(R.id.first_name_layout);
        lastNameLayout = findViewById(R.id.last_name_layout);
        emailLayout = findViewById(R.id.email_layout);
        phoneLayout = findViewById(R.id.phone_layout);
        passwordLayout = findViewById(R.id.password_layout);
        confirmLayout = findViewById(R.id.confirm_layout);
        occupationLayout = findViewById(R.id.occupation_layout);
        languageLayout = findViewById(R.id.language_layout);
        lastName = findViewById(R.id.last_name_txt);
        email = findViewById(R.id.email_txt);
        password = findViewById(R.id.password_txt);
        confirm = findViewById(R.id.confirm_password_txt);
        occupation = findViewById(R.id.occupation_txt);
        phone = findViewById(R.id.phone_txt);
        language = findViewById(R.id.language_txt);
        signUp = findViewById(R.id.sign_up_btn);

        signUpViewModel = new ViewModelProvider(this).get(SignUpViewModel.class);

        String[] type = new String[]{"Owner", "Creator", "Seller"};
        String[] type2 = new String[]{"English", "German", "French"};

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                        R.layout.dropdown_menu_popup_item,
                        type);

        ArrayAdapter<String> adapter2 = new ArrayAdapter<>(this,
                R.layout.dropdown_menu_popup_item,
                type2);

        occupation.setAdapter(adapter);
        language.setAdapter(adapter2);

        confirm.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (confirm.getText().toString().equals(password.getText().toString())){
                    confirmLayout.setError(null);
                }
                else {
                    confirmLayout.setError("Password mismatch");
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        occupation.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (TextUtils.isEmpty(occupation.getEditableText())){
                    occupationLayout.setError("Cannot be empty");
                } else {
                    occupationLayout.setError(null);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        language.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (TextUtils.isEmpty(language.getEditableText())){
                    languageLayout.setError("Cannot be empty");
                } else {
                    languageLayout.setError(null);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        signUp.setOnClickListener(v -> {
            if (TextUtils.isEmpty(firstName.getEditableText())){
                firstNameLayout.setError("Cannot be empty");
                return;
            } else {
                firstNameLayout.setError(null);
            }

            if (TextUtils.isEmpty(lastName.getEditableText())){
                lastNameLayout.setError("Cannot be empty");
                return;
            } else {
                lastNameLayout.setError(null);
            }

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

            if (TextUtils.isEmpty(phone.getEditableText())){
                phoneLayout.setError("Cannot be empty");
                return;
            } else {
                phoneLayout.setError(null);
            }

            if (phone.getText().toString().length() == 10){
                phoneLayout.setError("Invalid phone number");
                return;
            } else {
                phoneLayout.setError(null);
            }

            if (TextUtils.isEmpty(password.getEditableText())){
                passwordLayout.setError("Cannot be empty");
                return;
            } else {
                passwordLayout.setError(null);
            }

            if (TextUtils.isEmpty(confirm.getEditableText())){
                confirmLayout.setError("Cannot be empty");
                return;
            } else {
                confirmLayout.setError(null);
            }

            if (TextUtils.isEmpty(occupation.getEditableText())){
                occupationLayout.setError("Cannot be empty");
                return;
            } else {
                occupationLayout.setError(null);
            }

            if (TextUtils.isEmpty(language.getEditableText())){
                languageLayout.setError("Cannot be empty");
                return;
            } else {
                languageLayout.setError(null);
            }

            signUp();
        });
    }

    public boolean isValidEmail(CharSequence target) {
        return (!TextUtils.isEmpty(target) && Patterns.EMAIL_ADDRESS.matcher(target).matches());
    }

    private void signUp() {
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