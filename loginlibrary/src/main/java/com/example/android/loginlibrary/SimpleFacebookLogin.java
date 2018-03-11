package com.example.android.loginlibrary;

import android.app.Activity;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.firebase.ui.auth.AuthUI;
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
        public void resultAccountCreated();

        public void resultLoggedIn(FirebaseUser registeredUser);

        public void resultCancel();

        public void resultError(Exception errorResult);
    }

    public void setOnFacebookLoginResult(SimpleFacebookLogin.OnFacebookLoginResult eventListener) {
        mOnFacebookLoginResult = eventListener;
    }

    public void attemptFacebookLogin(LoginButton facebookButton) {

        Log.i("point 54", "reached facebook login library");

        mCallbackManager = CallbackManager.Factory.create();

        facebookButton.setReadPermissions("email", "public_profile");
        facebookButton.registerCallback(mCallbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                Log.i("got that", "facebook:onSuccess:" + loginResult);
                handleFacebookAccessToken(loginResult.getAccessToken());

            }

            @Override
            public void onCancel() {
                Log.i("cancelled!!", "facebook:onCancel");
                if (mOnFacebookLoginResult != null) {
                    mOnFacebookLoginResult.resultCancel();
                }
            }

            @Override
            public void onError(FacebookException error) {
                Log.i("error!!", "facebook:onError", error);
                if (mOnFacebookLoginResult != null) {
                    mOnFacebookLoginResult.resultError(error);
                }
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
                            if (mOnFacebookLoginResult != null) {
                                mOnFacebookLoginResult.resultLoggedIn(user);
                            }
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.i("signInWithCredentl:fail", "Please use your Google account");
                            Toast.makeText(activity, "Please use your Google account", Toast.LENGTH_SHORT).show();
                            AuthUI.getInstance().signOut(activity);
                        }
                    }
                });
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.i("point 102", "activity result");
        mCallbackManager.onActivityResult(requestCode, resultCode, data);

    }
}
