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

        public void signinCancelledByUser(Exception errorResult);

        public void accountCollisionError(Exception errorResult);

        public void networkError(Exception errorResult);

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
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == resultCodeSignIn) {

            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);

            try {
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = task.getResult(ApiException.class);
                firebaseAuthWithGoogle(account);
            } catch (ApiException e) {
                // Google Sign In failed, update UI appropriately
                e.printStackTrace();
                Log.i("81", e.getStatusCode() + "");
                Log.i("82", e.getMessage() + "");

                if (e.getStatusCode() == 7) {
                    if (mOnGoogleLoginResult != null) {
                        mOnGoogleLoginResult.networkError(e);
                    }
                } else if (e.getStatusCode() == 7) {
                    if (mOnGoogleLoginResult != null) {
                        mOnGoogleLoginResult.signinCancelledByUser(e);
                    }
                } else {
                    if (mOnGoogleLoginResult != null) {
                        mOnGoogleLoginResult.resultError(e);
                    }
                }
            }
        }
    }

    private void firebaseAuthWithGoogle(GoogleSignInAccount account) {
        Log.i("102", "firebaseAuthWithGoogle:" + account.getId());

        mAuth = FirebaseAuth.getInstance();
        AuthCredential credential = GoogleAuthProvider.getCredential(account.getIdToken(), null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(activity, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            FirebaseUser user = mAuth.getCurrentUser();

                            if (mOnGoogleLoginResult != null) {
                                mOnGoogleLoginResult.resultSuccessful(user);
                            }
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.i("119", "signInWithCredential:failure", task.getException());
                            try {
                                throw task.getException();
                            } catch (com.google.firebase.auth.FirebaseAuthUserCollisionException e) {
                                Log.i("123", "An account already exists with the same email address but different sign-in credentials");
                                if (mOnGoogleLoginResult != null) {
                                    mOnGoogleLoginResult.accountCollisionError(task.getException());
                                }
                            } catch (com.google.firebase.FirebaseNetworkException e) {
                                if (mOnGoogleLoginResult != null) {
                                    mOnGoogleLoginResult.networkError(task.getException());
                                }
                            } catch (Exception ee) {
                                if (mOnGoogleLoginResult != null) {
                                    mOnGoogleLoginResult.resultError(task.getException());
                                }
                            }
                        }
                    }
                });
    }
}
