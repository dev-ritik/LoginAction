package com.example.android.loginlibrary;

import android.app.Activity;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.util.Log;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;

/**
 * Created by ritik on 10-03-2018.
 */

public class SimpleGoogleLogin {
    private static FirebaseAuth mAuth;
    private GoogleSignInClient mGoogleSignInClient;
    private int resultCodeSignIn;
    private Activity activity;
    private String googleToken;

    public SimpleGoogleLogin(Activity loginActivity, int resultCodeSignIn, String googleToken) {
        this.activity = loginActivity;
        this.resultCodeSignIn = resultCodeSignIn;
        this.googleToken = googleToken;
    }

    private OnGoogleLoginResult mOnGoogleLoginResult;

    public interface OnGoogleLoginResult {
        public void resultSuccessful(FirebaseUser registeredUser);

        public void resultError(Exception errorResult);
    }

    public void setOnGoogleLoginResult(OnGoogleLoginResult eventListener) {
        mOnGoogleLoginResult = eventListener;
    }

    public void attemptGoogleLogin() {
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(googleToken)
                .requestEmail()
                .build();

        mGoogleSignInClient = GoogleSignIn.getClient(activity, gso);
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        activity.startActivityForResult(signInIntent, resultCodeSignIn);

        Log.i("point 64", "reached");
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.i("point 88", (resultCode == activity.RESULT_OK) + "");
        Log.i("point 83", "activity result");

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == resultCodeSignIn) {

            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                // Google Sign In was successful, authenticate with Firebase
                Log.i("point 75", "activity result");
                GoogleSignInAccount account = task.getResult(ApiException.class);
                firebaseAuthWithGoogle(account);
            } catch (ApiException e) {
                // Google Sign In failed, update UI appropriately
                e.printStackTrace();
                if (mOnGoogleLoginResult != null) {
                    mOnGoogleLoginResult.resultError(e);
                }
            }

        } else {
            Log.i("point 83", "signInWithCredential:failure");
        }

    }

    private void firebaseAuthWithGoogle(GoogleSignInAccount account) {
        Log.i("point 92", "firebaseAuthWithGoogle:" + account.getId());
        mAuth = FirebaseAuth.getInstance();

        AuthCredential credential = GoogleAuthProvider.getCredential(account.getIdToken(), null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(activity, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.i("signInWithCrential:suce", "point 101");
                            FirebaseUser user = mAuth.getCurrentUser();

                            if (mOnGoogleLoginResult != null) {
                                mOnGoogleLoginResult.resultSuccessful(user);
                            }
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.i("point 109", "signInWithCredential:failure", task.getException());
                            if (mOnGoogleLoginResult != null) {
                                mOnGoogleLoginResult.resultError(task.getException());
                            }
                        }
                        Log.i("reached", "point 114");
                    }
                });
    }
}
