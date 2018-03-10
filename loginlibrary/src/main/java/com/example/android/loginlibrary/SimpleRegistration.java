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

public class SimpleRegistration {

    private static Context loginContext;
    private static Class reDirectClass;
    public static FirebaseUser user;
    private CallbackManager mCallbackManager;
    public static FirebaseAuth mAuth;
    private SignInButton signInGoogleButton;
    private GoogleSignInClient mGoogleSignInClient;

    public SimpleRegistration(Context context, Class reDirectClass) {
        this.loginContext = context;
        this.reDirectClass = reDirectClass;

    }

    private OnRegistrationResult mOnRegistrationResult;

    public interface OnRegistrationResult {
        public void resultSuccessful(FirebaseUser registeredUser);

        public void resultError(Exception errorResult);

        public void resultName(FirebaseUser registeredUser);

        public void resultDp(Uri uploadUriLink);

        public void wrongCrudentials(String errorMessage);
    }

    public void setOnRegistrationResult(OnRegistrationResult eventListener) {
        mOnRegistrationResult = eventListener;

    }

    public void attemptRegistration(@NonNull Activity var1, String email, String passwordinput, String passwordRecheck, final String userName, final Uri uploadedDpLink) {
        email = email.trim();
        if (!checkCrudentials(email, passwordinput, passwordRecheck).equals("valid")) {
            if (mOnRegistrationResult != null) {
                mOnRegistrationResult.wrongCrudentials(checkCrudentials(email, passwordinput, passwordRecheck));
                return;
            }
        }
        mAuth = FirebaseAuth.getInstance();

        Log.i("registration attempted", "point 298");

        mAuth.createUserWithEmailAndPassword(email, passwordinput)
                .addOnCompleteListener(var1, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            user = mAuth.getCurrentUser();
                            if (mOnRegistrationResult != null) {
                                mOnRegistrationResult.resultSuccessful(user);
                            }

                            UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                                    .setDisplayName(userName)
                                    .setPhotoUri(uploadedDpLink)
                                    .build();
                            user.updateProfile(profileUpdates)
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()) {
                                                Log.i("point 325", "User profile successfully updated.");
                                                if (mOnRegistrationResult != null) {
                                                    mOnRegistrationResult.resultName(user);
                                                    mOnRegistrationResult.resultDp(uploadedDpLink);
                                                }
                                            }
                                        }
                                    });
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.i("crteUserWithEmail:fail", "point 371");
//                            task.getResult();
                            if (mOnRegistrationResult != null) {
                                mOnRegistrationResult.resultError(task.getException());
                                System.out.println("36");
                            }
                        }
                    }
                });
    }

    public static String checkCrudentials(String email, String passwordinput, String password2) {
        if (!emailCheck(email)) {
            return "invalid email";
        }
        if (!passwordCheck(passwordinput)) {
            return "invalid passwordinput";
        }
        if (!passwordCheck(password2)) {
            return "invalid passwordrecheck";
        }
        if (!passwordinput.equals(password2)) {
            return "passwords doesnot match";
        }
        return "valid";
    }

    public static boolean passwordCheck(String password) {
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
