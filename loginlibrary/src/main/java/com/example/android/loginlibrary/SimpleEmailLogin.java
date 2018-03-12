package com.example.android.loginlibrary;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

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

        public void resultError(Exception errorResult);

        public void wrongCrudentials(String errorMessage);
    }

    public interface OnPasswordChangeResult {
        public void resultSuccessful();

        public void resultError(Exception errorResult);

        public void wrongCrudentials();
    }

    public void setOnEmailLoginResult(OnEmailLoginResult eventListener) {
        mOnEmailLoginResult = eventListener;
    }

    public void setOnPasswordChangeResult(OnPasswordChangeResult passwordeventListener) {
        mOnPasswordChangeResult = passwordeventListener;
    }

    public void attemptLogin(@NonNull Activity var1, String email, String passwordinput) {
        email = email.trim();
        if (!checkCrudentials(email, passwordinput).equals("valid")) {
            if (mOnEmailLoginResult != null) {
                mOnEmailLoginResult.wrongCrudentials(checkCrudentials(email, passwordinput));
            }
        } else {
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
                                } else {
                                    // If sign in fails, display a message to the user.
                                    if (mOnEmailLoginResult != null) {
                                        mOnEmailLoginResult.resultError(task.getException());
                                    }
                                }

                            } else {
                                Log.i("point 70", "Login Id or Password is incorrect");
                                if (mOnEmailLoginResult != null) {
                                    mOnEmailLoginResult.resultError(task.getException());
                                }
                            }
                        }

                    });
        }
    }

    private static String checkCrudentials(String email, String passwordinput) {
        if (!emailCheck(email)) {
            return "invalid email";
        }
        if (!passwordCheck(passwordinput)) {
            return "invalid passwordinput";
        }
        return "valid";
    }

    private static boolean passwordCheck(String password) {
        return TextUtils.isEmpty(password) || !(password.length() < 7);
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

    public void attemptPasswordReset(@NonNull Activity var1, String email) {
        email = email.trim();
        if (!emailCheck(email)) {
            if (mOnPasswordChangeResult != null) {
                mOnPasswordChangeResult.wrongCrudentials();
                Log.i("point 59", "wrong email");
            }
        } else {
            Log.i("point 64", "right email");
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
                            }else {
                                Log.i("point 140", "Password reset error");
                                if (mOnPasswordChangeResult != null) {
                                    mOnPasswordChangeResult.resultError(task.getException());
                                }
                            }
                        }
                    });
        }
    }
}
