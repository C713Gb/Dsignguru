package com.baner.dsignguru.repository;

import android.util.Log;

import androidx.lifecycle.MutableLiveData;

import com.baner.dsignguru.model.User;
import com.baner.dsignguru.model.UserModel;
import com.baner.dsignguru.util.Constants;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import static com.baner.dsignguru.util.Constants.USERS;

public class AuthRepository {

    private final FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
    private final FirebaseFirestore rootRef = FirebaseFirestore.getInstance();
    private final CollectionReference usersRef = rootRef.collection(USERS);

    public MutableLiveData<User> firebaseSignInWithGoogle(AuthCredential googleAuthCredential) {
        MutableLiveData<User> authenticatedUserMutableLiveData = new MutableLiveData<>();
        firebaseAuth.signInWithCredential(googleAuthCredential).addOnCompleteListener(authTask -> {
            if (authTask.isSuccessful()) {
                boolean isNewUser = authTask.getResult().getAdditionalUserInfo().isNewUser();
                FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
                if (firebaseUser != null) {
                    String uid = firebaseUser.getUid();
                    String name = firebaseUser.getDisplayName();
                    String email = firebaseUser.getEmail();
                    User user = new User(uid, name, email);
                    user.isNew = isNewUser;
                    authenticatedUserMutableLiveData.setValue(user);
                }
            } else {
                Log.d(Constants.TAG, "firebaseSignInWithGoogle: "+authTask.getException().getMessage());
                authTask.getException().printStackTrace();
            }
        });
        return authenticatedUserMutableLiveData;
    }

    public MutableLiveData<UserModel> firebaseSignUpWithEmail(UserModel userModel, String email, String password) {
        MutableLiveData<UserModel> authenticatedUserMutableLiveData = new MutableLiveData<>();
        firebaseAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(authTask -> {
                if (authTask.isSuccessful()) {
                    boolean isNewUser = authTask.getResult().getAdditionalUserInfo().isNewUser();
                    FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
                    if (firebaseUser != null) {
                        String uid = firebaseUser.getUid();

                        UserModel user = new UserModel
                                (uid, userModel.firstName, userModel.lastName, email,
                                        userModel.phone, userModel.occupation, userModel.language);
                        user.isNew = isNewUser;
                        authenticatedUserMutableLiveData.setValue(user);
                    }
                } else {
                    Log.d(Constants.TAG, "firebaseSignInWithGoogle: "+authTask.getException().getMessage());
                    authTask.getException().printStackTrace();
                }
        });
        return authenticatedUserMutableLiveData;
    }

    public MutableLiveData<String> firebaseSignInWithEmail(String email, String password) {
        MutableLiveData<String> authenticatedUserMutableLiveData = new MutableLiveData<>();
        firebaseAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(authTask -> {
                    if (authTask.isSuccessful()) {
                        FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
                        if (firebaseUser != null) {
                            String uid = firebaseUser.getUid();

                            authenticatedUserMutableLiveData.setValue("Success");
                        }
                    } else {
                        Log.d(Constants.TAG, "firebaseSignInWithGoogle: "+authTask.getException().getMessage());
                        authTask.getException().printStackTrace();
                        authenticatedUserMutableLiveData.setValue("Failed");
                    }
                });
        return authenticatedUserMutableLiveData;
    }

    public MutableLiveData<User> createUserInFirestoreIfNotExists(User authenticatedUser) {
        MutableLiveData<User> newUserMutableLiveData = new MutableLiveData<>();
        DocumentReference uidRef = usersRef.document(authenticatedUser.uid);
        uidRef.get().addOnCompleteListener(uidTask -> {
            if (uidTask.isSuccessful()) {
                DocumentSnapshot document = uidTask.getResult();
                if (!document.exists()) {
                    uidRef.set(authenticatedUser).addOnCompleteListener(userCreationTask -> {
                        if (userCreationTask.isSuccessful()) {
                            authenticatedUser.isCreated = true;
                            newUserMutableLiveData.setValue(authenticatedUser);
                        } else {
                            Log.d(Constants.TAG, "createUserInFirestoreIfNotExists: " +
                                    userCreationTask.getException().getMessage());
                            userCreationTask.getException().printStackTrace();
                        }
                    });
                } else {
                    newUserMutableLiveData.setValue(authenticatedUser);
                }
            } else {
                Log.d(Constants.TAG, "createUserInFirestoreIfNotExists: " +
                        uidTask.getException().getMessage());
                uidTask.getException().printStackTrace();
            }
        });
        return newUserMutableLiveData;
    }

    public MutableLiveData<UserModel> createEmailUserInFirestoreIfNotExists(UserModel authenticatedUser) {
        MutableLiveData<UserModel> newUserMutableLiveData = new MutableLiveData<>();
        DocumentReference uidRef = usersRef.document(authenticatedUser.uid);
        uidRef.get().addOnCompleteListener(uidTask -> {
            if (uidTask.isSuccessful()) {
                DocumentSnapshot document = uidTask.getResult();
                if (!document.exists()) {
                    uidRef.set(authenticatedUser).addOnCompleteListener(userCreationTask -> {
                        if (userCreationTask.isSuccessful()) {
                            authenticatedUser.isCreated = true;
                            newUserMutableLiveData.setValue(authenticatedUser);
                        } else {
                            Log.d(Constants.TAG, "createUserInFirestoreIfNotExists: " +
                                    userCreationTask.getException().getMessage());
                            userCreationTask.getException().printStackTrace();
                        }
                    });
                } else {
                    newUserMutableLiveData.setValue(authenticatedUser);
                }
            } else {
                Log.d(Constants.TAG, "createUserInFirestoreIfNotExists: " +
                        uidTask.getException().getMessage());
                uidTask.getException().printStackTrace();
            }
        });
        return newUserMutableLiveData;
    }

}
