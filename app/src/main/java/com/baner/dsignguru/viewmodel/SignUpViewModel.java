package com.baner.dsignguru.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.baner.dsignguru.model.User;
import com.baner.dsignguru.model.UserModel;
import com.baner.dsignguru.repository.AuthRepository;
import com.google.firebase.auth.AuthCredential;

public class SignUpViewModel extends ViewModel {
    private final AuthRepository authRepository;
    public LiveData<UserModel> authenticatedUserLiveData;
    public LiveData<UserModel> createdUserLiveData;

    public SignUpViewModel() {
        authRepository = new AuthRepository();
    }

    public void signUpWithEmail(UserModel userModel, String email, String password) {
        authenticatedUserLiveData = authRepository.firebaseSignUpWithEmail(userModel, email, password);
    }

    public void createUser(UserModel authenticatedUser) {
        createdUserLiveData = authRepository.createEmailUserInFirestoreIfNotExists(authenticatedUser);
    }
}
