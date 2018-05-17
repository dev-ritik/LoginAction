package com.example.android.loginlibrary;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.util.Log;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;
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

        public void wrongCrudentials(String doubtfulCredentials, String errorMessage);
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
        if (checkCrudentials(email, passwordinput)) {
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
                                } catch(com.google.firebase.auth.FirebaseAuthInvalidUserException e) {
                                    if (mOnEmailLoginResult != null) {
                                        mOnEmailLoginResult.noAccountFound(task.getException());
                                    }
                                }catch (Exception ee){
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

    private boolean checkCrudentials(String email, String passwordinput) {
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
                mOnEmailLoginResult.wrongCrudentials("password", "empty password");
            }
            return false;
        } else if (password.length() < 7) {
            if (mOnEmailLoginResult != null) {
                mOnEmailLoginResult.wrongCrudentials("password", "short password");
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
                mOnEmailLoginResult.wrongCrudentials("email", "empty email");
            }
            return false;
        }
        if (!pat.matcher(email).matches()) {
            if (mOnEmailLoginResult != null) {
                mOnEmailLoginResult.wrongCrudentials("email", "invalid email");
            }
            return false;
        } else return true;
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


//    public static void main(String[] args) {
//        String email = "rkumar1@cs.iitr.ac.in";
//        if (isEmailValid(email))
//            System.out.print("Yes");
//        else
//            System.out.print("No");
//    }


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
                            } else {
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
