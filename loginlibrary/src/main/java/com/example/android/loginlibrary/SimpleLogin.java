package com.example.android.loginlibrary;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.facebook.CallbackManager;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;

/**
 * Created by ritik on 10-03-2018.
 */

//public class SimpleLogin {
//
//    private static Context loginContext;
//    private static Class reDirectClass;
//    public static FirebaseUser user;
//    private CallbackManager mCallbackManager;
//    public static FirebaseAuth mAuth;
//    private SignInButton signInGoogleButton;
//    private GoogleSignInClient mGoogleSignInClient;
//
//    public SimpleLogin(Context context, Class reDirectClass) {
//        this.loginContext = context;
//        this.reDirectClass = reDirectClass;
//    }
//
//    public static String checkCrudentials(String email, String password1, String password2) {
//        if (!emailCheck(email)) {
////            mProgressView.setVisibility(View.INVISIBLE);
//            return "invalid email";
//        }
//
////        View focusView;
//        if (!passwordCheck(password1)) {
////            mProgressView.setVisibility(View.INVISIBLE);
//            return "invalid password";
//        }
//        if (!passwordCheck(password2)) {
////            mProgressView.setVisibility(View.INVISIBLE);
//            return "invalid password";
//        }
//
//        if (!password1.equals(password2)) {
////            Toast.makeText(LoginActivity.this, "Passwords doesn't match", Toast.LENGTH_SHORT).show();
////            focusView = password1;
////            focusView.requestFocus();
////            mProgressView.setVisibility(View.INVISIBLE);
//            return "passwords doesnot match";
//        }
//        return "valid";
//    }
//
//    public static boolean passwordCheck(String password) {
//        View focusView;
//        if (!TextUtils.isEmpty(password) && password.length() < 7) {
//
//            return false;
//        }
//        return true;
//    }
//
//    public static boolean emailCheck(String email) {
//        if (TextUtils.isEmpty(email)) {
//            Log.i("point 506", "email null");
//            return false;
//        } else if (!email.contains("@")) {
//
//            return false;
//        } else if (!email.contains(".")) {
//
//            return false;
//        }
//        return true;
//    }
//
//    public static boolean isEmailValid(String email) {
//        //TODO: Replace this with your own logic
//        return email.contains("@") && email.contains(".");
//    }
//
//    public static boolean isPasswordValid(String password, int passwordLengthMin) {
//        //TODO: Replace this with your own logic
//        return password.length() >= passwordLengthMin;
//    }
//
//}

public class SimpleLogin {

    private static Context loginContext;
    private static Class reDirectClass;
    public static FirebaseUser user;
    private CallbackManager mCallbackManager;
    public static FirebaseAuth mAuth;
    private SignInButton signInGoogleButton;
    private GoogleSignInClient mGoogleSignInClient;

    public SimpleLogin(Context context, Class reDirectClass) {
        this.loginContext = context;
        this.reDirectClass = reDirectClass;

    }

    private OnRegistrationResult mOnRegistrationResult;

    public interface OnRegistrationResult {
        public void resultSuccessful(FirebaseUser registeredUser);

        public void resultError(AuthResult errorResult);
    }

    public void setOnRegistrationResult(String abc, OnRegistrationResult eventListener) {
        mOnRegistrationResult = eventListener;
        System.out.println(abc);

    }

    public String attemptRegistration(@NonNull Activity var1, final Context registerContext, String email, String password1, String passwordRecheck) {

        if (!checkCrudentials(email, password1, passwordRecheck).equals("valid")) {
            return checkCrudentials(email, password1, passwordRecheck);
        }
        mAuth = FirebaseAuth.getInstance();

        Log.i("registration attempted", "point 298");
        mAuth.createUserWithEmailAndPassword(email, password1)
                .addOnCompleteListener(var1, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            user = mAuth.getCurrentUser();
                            if (mOnRegistrationResult != null) {
                                mOnRegistrationResult.resultSuccessful(user);
                            }
//                            Toast.makeText(registerContext, "logged in", Toast.LENGTH_SHORT).show();
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.i("crteUserWithEmail:fail", "point 371");
                            task.getResult();
                            if (mOnRegistrationResult != null) {
                                mOnRegistrationResult.resultError(task.getResult());
                                System.out.println("36");
                            }
//                            Toast.makeText(loginContext, "Authentication failed.", Toast.LENGTH_SHORT).show();
                        }

                    }
                });
        return "registration attempted";
    }

    public static String checkCrudentials(String email, String password1, String password2) {
        if (!emailCheck(email)) {
//            mProgressView.setVisibility(View.INVISIBLE);
            return "invalid email";
        }

//        View focusView;
        if (!passwordCheck(password1)) {
//            mProgressView.setVisibility(View.INVISIBLE);
            return "invalid password";
        }
        if (!passwordCheck(password2)) {
//            mProgressView.setVisibility(View.INVISIBLE);
            return "invalid password";
        }
        if (!password1.equals(password2)) {
//            Toast.makeText(LoginActivity.this, "Passwords doesn't match", Toast.LENGTH_SHORT).show();
//            focusView = password1;
//            focusView.requestFocus();
//            mProgressView.setVisibility(View.INVISIBLE);
            return "passwords doesnot match";
        }
        return "valid";
    }

    public static boolean passwordCheck(String password) {
        View focusView;
        if (!TextUtils.isEmpty(password) && password.length() < 7) {

            return false;
        }
        return true;
    }

    public static boolean emailCheck(String email) {
        if (TextUtils.isEmpty(email)) {
            Log.i("point 506", "email null");
            return false;
        } else if (!email.contains("@")) {

            return false;
        } else if (!email.contains(".")) {

            return false;
        }
        return true;
    }

}
