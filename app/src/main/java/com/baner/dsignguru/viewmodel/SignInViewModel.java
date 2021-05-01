package com.baner.dsignguru.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.baner.dsignguru.model.User;
import com.baner.dsignguru.repository.AuthRepository;
import com.google.firebase.auth.AuthCredential;

public class SignInViewModel extends ViewModel {

    private final AuthRepository authRepository;
    public LiveData<User> authenticatedUserLiveData;
    public LiveData<User> createdUserLiveData;

    public SignInViewModel() {
        authRepository = new AuthRepository();
    }

    public void signInWithGoogle(AuthCredential googleAuthCredential) {
        authenticatedUserLiveData = authRepository.firebaseSignInWithGoogle(googleAuthCredential);
    }

    public void createUser(User authenticatedUser) {
        createdUserLiveData = authRepository.createUserInFirestoreIfNotExists(authenticatedUser);
    }
}
