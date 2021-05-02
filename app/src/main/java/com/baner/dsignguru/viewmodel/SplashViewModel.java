package com.baner.dsignguru.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.baner.dsignguru.model.User;
import com.baner.dsignguru.repository.SplashRepository;

public class SplashViewModel extends ViewModel {

    private final SplashRepository splashRepository;
    public LiveData<User> isUserAuthenticatedLiveData;

    public SplashViewModel() {
        splashRepository = new SplashRepository();
    }

    public void checkIfUserIsAuthenticated() {
        isUserAuthenticatedLiveData = splashRepository.checkIfUserIsAuthenticatedInFirebase();
    }

}
