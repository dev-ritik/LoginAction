package com.example.android.loginlibrary;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.util.Log;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.regex.Pattern;

/**
 * Created by ritik on 10-03-2018.
 */

public class SimpleEmailLogin {
    public static FirebaseAuth mAuth;

    public SimpleEmailLogin() {

    }

    private OnEmailLoginResult mOnEmailLoginResult;
    private OnPasswordChangeResult mOnPasswordChangeResult;

    public interface OnEmailLoginResult {
        public void resultSuccessful(FirebaseUser registeredUser);

        public void noAccountFound(Exception errorResult);

        public void resultError(Exception errorResult);

        public void wrongCredentials(String doubtfulCredentials, String errorMessage);
    }

    public interface OnPasswordChangeResult {
        public void resultSuccessful();

        public void noAccountFound(Exception errorResult);

        public void resultError(Exception errorResult);

        public void wrongEmail(String errorMessage);
    }

    public void setOnEmailLoginResult(OnEmailLoginResult eventListener) {
        mOnEmailLoginResult = eventListener;
    }

    public void setOnPasswordChangeResult(OnPasswordChangeResult passwordeventListener) {
        mOnPasswordChangeResult = passwordeventListener;
    }

    public void attemptLogin(@NonNull Activity var1, String email, String passwordinput) {
        email = email.trim();
        if (checkCredentials(email, passwordinput)) {
            mAuth = FirebaseAuth.getInstance();

            mAuth.signInWithEmailAndPassword(email, passwordinput)
                    .addOnCompleteListener(var1, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                // Sign in success, update UI with the signed-in user's information
                                Log.i("signInWithEmail:success", "point 57");
                                FirebaseUser user = mAuth.getCurrentUser();
                                if (mOnEmailLoginResult != null) {
                                    mOnEmailLoginResult.resultSuccessful(user);
                                }
                            } else {
                                try {
                                    throw task.getException();
                                } catch (com.google.firebase.auth.FirebaseAuthInvalidUserException e) {
                                    if (mOnEmailLoginResult != null) {
                                        mOnEmailLoginResult.noAccountFound(task.getException());
                                    }
                                } catch (Exception ee) {
                                    if (mOnEmailLoginResult != null) {
                                        mOnEmailLoginResult.resultError(task.getException());
                                    }
                                }
                                Log.i("point 70", "Login Id or Password is incorrect");

                            }
                        }
                    });
        }
    }

    private boolean checkCredentials(String email, String passwordinput) {
        if (!isEmailValid(email))
            return false;

        if (!passwordCheck(passwordinput)) {
            return false;
        }
        return true;
    }

    private boolean passwordCheck(String password) {

        if (TextUtils.isEmpty(password)) {
            if (mOnEmailLoginResult != null) {
                mOnEmailLoginResult.wrongCredentials("password", "empty");
            }
            return false;
        } else if (password.length() < 7) {
            if (mOnEmailLoginResult != null) {
                mOnEmailLoginResult.wrongCredentials("password", "short");
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
            if (mOnEmailLoginResult != null) {
                mOnEmailLoginResult.wrongCredentials("email", "empty");
            }if (mOnPasswordChangeResult != null) {
                mOnPasswordChangeResult.wrongEmail("empty email");
            }
            return false;
        }
        if (!pat.matcher(email).matches()) {
            if (mOnEmailLoginResult != null) {
                mOnEmailLoginResult.wrongCredentials("email", "invalid");
            }
            if (mOnPasswordChangeResult != null) {
                mOnPasswordChangeResult.wrongEmail("invalid");
            }
            return false;
        } else return true;
    }

    public void attemptPasswordReset(String email) {
        email = email.trim();
        if (isEmailValid(email)) {
            mAuth = FirebaseAuth.getInstance();
            mAuth.sendPasswordResetEmail(email)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                Log.i("point 134", "Email sent.");
                                if (mOnPasswordChangeResult != null) {
                                    mOnPasswordChangeResult.resultSuccessful();
                                }
                            } else {
                                Log.i("point 140", "Password reset error");
                                try {
                                    throw task.getException();
                                } catch (com.google.firebase.auth.FirebaseAuthInvalidUserException e) {
                                    if (mOnPasswordChangeResult != null) {
                                        mOnPasswordChangeResult.noAccountFound(task.getException());
                                    }
                                } catch (Exception ee) {
                                    if (mOnPasswordChangeResult != null) {
                                        mOnPasswordChangeResult.resultError(task.getException());
                                    }
                                }
                            }
                        }
                    });
        }
    }
}
