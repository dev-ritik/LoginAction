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
    private FirebaseAuth mAuth;

    public SimpleEmailLogin() {

    }

    private OnEmailLoginResult mOnEmailLoginResult;
    private OnPasswordChangeResult mOnPasswordChangeResult;

    public interface OnEmailLoginResult {
        void resultSuccessful(FirebaseUser registeredUser);

        void noAccountFound(Exception errorResult);

        void invalidCredentials(Exception errorResult);

        void resultError(Exception errorResult);

        void networkError(Exception errorResult);

        void wrongCredentials(String doubtfulCredentials, String errorMessage);
    }

    public interface OnPasswordChangeResult {
        void resultSuccessful();

        void noAccountFound(Exception errorResult);

        void resultError(Exception errorResult);

        void networkError(Exception errorResult);

        void wrongEmail(String errorMessage);
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
                                FirebaseUser user = mAuth.getCurrentUser();
                                if (mOnEmailLoginResult != null) {
                                    mOnEmailLoginResult.resultSuccessful(user);
                                }
                            } else {
                                try {
                                    Log.i("81", task.getException().toString());
                                    throw task.getException();
                                } catch (com.google.firebase.auth.FirebaseAuthInvalidUserException e) {
                                    if (mOnEmailLoginResult != null) {
                                        mOnEmailLoginResult.noAccountFound(task.getException());
                                    }
                                } catch (com.google.firebase.FirebaseNetworkException e) {
                                    if (mOnEmailLoginResult != null) {
                                        mOnEmailLoginResult.networkError(task.getException());
                                    }
                                } catch (com.google.firebase.auth.FirebaseAuthInvalidCredentialsException e) {
                                    if (mOnEmailLoginResult != null) {
                                        mOnEmailLoginResult.invalidCredentials(task.getException());
                                    }
                                } catch (Exception e) {
                                    Log.i("90", e.toString());
                                    if (mOnEmailLoginResult != null) {
                                        mOnEmailLoginResult.resultError(task.getException());
                                    }
                                }

                            }
                        }
                    });
        }
    }

    private boolean checkCredentials(String email, String passwordinput) {
        return isEmailValid(email) && passwordCheck(passwordinput);
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

    private boolean isEmailValid(String email) {
        String emailRegex = "^[a-zA-Z0-9_+&*-]+(?:\\." +
                "[a-zA-Z0-9_+&*-]+)*@" +
                "(?:[a-zA-Z0-9-]+\\.)+[a-z" +
                "A-Z]{2,7}$";

        Pattern pat = Pattern.compile(emailRegex);
        if (email == null) {
            if (mOnEmailLoginResult != null) {
                mOnEmailLoginResult.wrongCredentials("email", "empty");
            }
            if (mOnPasswordChangeResult != null) {
                mOnPasswordChangeResult.wrongEmail("empty");
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
                                Log.i("163", "Email sent.");
                                if (mOnPasswordChangeResult != null) {
                                    mOnPasswordChangeResult.resultSuccessful();
                                }
                            } else {
                                try {
                                    Log.i("170", task.getException().toString());
                                    throw task.getException();
                                } catch (com.google.firebase.auth.FirebaseAuthInvalidUserException e) {
                                    if (mOnPasswordChangeResult != null) {
                                        mOnPasswordChangeResult.noAccountFound(task.getException());
                                    }
                                } catch (com.google.firebase.FirebaseNetworkException e) {
                                    if (mOnPasswordChangeResult != null) {
                                        mOnPasswordChangeResult.networkError(task.getException());
                                    }
                                } catch (com.google.firebase.FirebaseException e) {
                                    if (mOnPasswordChangeResult != null) {
                                        mOnPasswordChangeResult.noAccountFound(task.getException());
                                    }
                                } catch (Exception e) {
                                    Log.i("191", e.toString());
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
