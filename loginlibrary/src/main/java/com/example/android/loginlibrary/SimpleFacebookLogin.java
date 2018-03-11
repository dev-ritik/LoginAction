package com.example.android.loginlibrary;

import android.app.Activity;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.View;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;

/**
 * Created by ritik on 11-03-2018.
 */

public class SimpleFacebookLogin {
    private static FirebaseAuth mAuth;
    private int resultCodeSignInFacebokk;
    private Activity activity;
    private String FacebookToken;
    private CallbackManager mCallbackManager;
    private LoginButton mloginButton;

    public SimpleFacebookLogin(Activity loginActivity, int resultCodeSignInFacebokk) {
        this.activity = loginActivity;
        this.resultCodeSignInFacebokk = resultCodeSignInFacebokk;
    }

    private SimpleFacebookLogin.OnFacebookLoginResult mOnFacebookLoginResult;

    public interface OnFacebookLoginResult {
        public void resultSuccessful(FirebaseUser registeredUser);

        public void resultError(Exception errorResult);
    }

    public void setOnFacebookLoginResult(SimpleFacebookLogin.OnFacebookLoginResult eventListener) {
        mOnFacebookLoginResult = eventListener;
    }

    public void attemptFacebookLogin() {

        Log.i("point 54", "reached facebook login library");

        mCallbackManager = CallbackManager.Factory.create();

//        mloginButton.setReadPermissions("email", "public_profile");

        ArrayList<String> permissions = new ArrayList<>();
        permissions.add("email");
        permissions.add("public_profile");

        LoginManager.getInstance().logInWithReadPermissions(activity, permissions);
        LoginManager.getInstance().registerCallback(mCallbackManager,new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                Log.i("got that", "point 59 facebook:onSuccess:" + loginResult);
                handleFacebookAccessToken(loginResult.getAccessToken());

            }

            @Override
            public void onCancel() {
                Log.i("cancelled!!", "point 66");
            }

            @Override
            public void onError(FacebookException error) {
                Log.i("error!!", "point 66", error);
            }
        });
    }

    private void handleFacebookAccessToken(AccessToken token) {
        Log.i("point 77", "handleFacebookAccessToken:" + token);

        mAuth = FirebaseAuth.getInstance();

        AuthCredential credential = FacebookAuthProvider.getCredential(token.getToken());
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(activity, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.i("signInWthCredntialscess", "point 88");
                            FirebaseUser user = mAuth.getCurrentUser();

                        } else {
                            // If sign in fails, display a message to the user.
                            Log.i("signInWithCredentl:fail", "point 93");
                        }

                    }
                });
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.i("point 101", (resultCode == activity.RESULT_OK) + "");
        Log.i("point 102", "activity result");

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == resultCodeSignInFacebokk) {
            mCallbackManager.onActivityResult(requestCode, resultCode, data);
        } else {
            Log.i("point 108", "signInWithCredential:failure");
        }

    }
}
