package com.example.android.loginlibrary;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;

import com.facebook.CallbackManager;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.common.SignInButton;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

/**
 * Created by ritik on 10-03-2018.
 */

public class SimpleEmailLogin {
    private static Context loginContext;
    private static Class reDirectClass;
    public static FirebaseUser user;
    private CallbackManager mCallbackManager;
    public static FirebaseAuth mAuth;
    private SignInButton signInGoogleButton;
    private GoogleSignInClient mGoogleSignInClient;

    public SimpleEmailLogin(Context context, Class reDirectClass) {
        this.loginContext = context;
        this.reDirectClass = reDirectClass;

    }

    private OnEmailLoginResult mOnEmailLoginResult;

    public interface OnEmailLoginResult {
        public void resultSuccessful(FirebaseUser registeredUser);

        public void resultError(AuthResult errorResult);
    }

    public void setOnEmailLoginResult(OnEmailLoginResult eventListener) {
        mOnEmailLoginResult = eventListener;
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

    public static boolean isEmailValid(String email) {
        //TODO: Replace this with your own logic
        return email.contains("@") && email.contains(".");
    }

    public static boolean isPasswordValid(String password, int passwordLengthMin) {
        //TODO: Replace this with your own logic
        return password.length() >= passwordLengthMin;
    }


}
