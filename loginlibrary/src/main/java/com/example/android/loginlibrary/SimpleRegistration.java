package com.example.android.loginlibrary;

import android.app.Activity;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.util.Log;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;

import java.util.regex.Pattern;

/**
 * Created by ritik on 10-03-2018.
 */

public class SimpleRegistration {

    private static FirebaseUser user;
    private static FirebaseAuth mAuth;

    public SimpleRegistration() {
    }

    private OnRegistrationResult mOnRegistrationResult;

    public interface OnRegistrationResult {
        public void resultSuccessful(FirebaseUser registeredUser);

        public void sameEmailError(Exception errorResult);

        public void resultError(Exception errorResult);

        public void resultName(FirebaseUser registeredUser);

        public void resultDp(Uri uploadUriLink);

        public void wrongCredentials(String doubtfulCredential, String errorMessage);
    }

    public void setOnRegistrationResult(OnRegistrationResult eventListener) {
        mOnRegistrationResult = eventListener;
    }

    public void attemptRegistration(@NonNull Activity var1, String email, String passwordInput, String passwordRecheck, final String userName, final Uri uploadedDpLink) {
        email = email.trim();
        if (checkCredentials(email, passwordInput, passwordRecheck)) {

            mAuth = FirebaseAuth.getInstance();
            mAuth.createUserWithEmailAndPassword(email, passwordInput)
                    .addOnCompleteListener(var1, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                user = mAuth.getCurrentUser();
                                if (mOnRegistrationResult != null) {
                                    mOnRegistrationResult.resultSuccessful(user);
                                }

                                UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                                        .setDisplayName(userName)
                                        .setPhotoUri(uploadedDpLink)
                                        .build();
                                user.updateProfile(profileUpdates)
                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                if (task.isSuccessful()) {
                                                    Log.i("point 78", "User profile successfully updated.");
                                                    if (mOnRegistrationResult != null) {
                                                        mOnRegistrationResult.resultName(user);
                                                        mOnRegistrationResult.resultDp(uploadedDpLink);
                                                    }
                                                } else {
                                                    Log.i("point 78", "error occurred while updating user profile");
                                                    Log.i("point 79", task.getException().toString());
                                                    if (mOnRegistrationResult != null) {
                                                        mOnRegistrationResult.resultError(task.getException());
                                                    }
                                                }
                                            }
                                        });
                            } else {
                                // If sign in fails, display a message to the user.
                                Log.i("crteUserWithEmail:fail", "point 93");
                                Log.i( "point 91",task.getException().toString());
                                try {
                                    throw task.getException();
                                } catch (com.google.firebase.auth.FirebaseAuthUserCollisionException e) {
                                    if (mOnRegistrationResult != null) {
                                        mOnRegistrationResult.sameEmailError(task.getException());
                                    }
                                } catch (Exception ee) {
                                    if (mOnRegistrationResult != null) {
                                        mOnRegistrationResult.resultError(task.getException());
                                    }
                                }
                            }
                        }
                    });
        }
    }

    private boolean checkCredentials(String email, String password1, String password2) {
        if (!isEmailValid(email)) {
            return false;
        }
        if (!passwordCheck(password1, 1)) {
            return false;
        }
        if (!passwordCheck(password2, 2)) {
            return false;
        }
        if (!password1.equals(password2)) {
            Log.i("point sr109", "unequal");
            if (mOnRegistrationResult != null) {
                mOnRegistrationResult.wrongCredentials("password1 and password2", "not equal");
            }
            return false;
        }
        return true;
    }

    public boolean isEmailValid(String email) {
        String emailRegex = "^[a-zA-Z0-9_+&*-]+(?:\\." +
                "[a-zA-Z0-9_+&*-]+)*@" +
                "(?:[a-zA-Z0-9-]+\\.)+[a-z" +
                "A-Z]{2,7}$";

        Pattern pat = Pattern.compile(emailRegex);
        if (email == null) {
            if (mOnRegistrationResult != null) {
                mOnRegistrationResult.wrongCredentials("email", "empty");
                return false;
            }
        }
        if (!pat.matcher(email).matches()) {
            if (mOnRegistrationResult != null) {
                mOnRegistrationResult.wrongCredentials("email", "invalid");
            }
            return false;
        } else return true;
    }

    private boolean passwordCheck(String password, int passwordNumber) {
        if (TextUtils.isEmpty(password)) {
            if (mOnRegistrationResult != null) {
                mOnRegistrationResult.wrongCredentials("password" + passwordNumber, "empty");
            }
            return false;
        } else if (password.length() < 7) {
            if (mOnRegistrationResult != null) {
                mOnRegistrationResult.wrongCredentials("password" + passwordNumber, "short");
            }
            return false;
        }
        return true;
    }

    private static boolean emailCheck(String email) {
        if (TextUtils.isEmpty(email)) {
            return false;
        } else if (!email.contains("@")) {

            return false;
        } else if (!email.contains(".")) {

            return false;
        }
        return true;
    }
}
