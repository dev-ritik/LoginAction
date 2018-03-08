package com.example.android.loginaction;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;

import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;


import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import static android.Manifest.permission.READ_CONTACTS;

/**
 * A login screen that offers login via email/password.
 */
public class LoginActivity extends AppCompatActivity {

    /**
     * Id to identity READ_CONTACTS permission request.
     */
    private static final int REQUEST_READ_CONTACTS = 0;

    private EditText mEmailView;
    private EditText mPasswordView, emailRegister, userName, password1, password2;
    private View mProgressView;
    private View mLoginFormView;
    private Button mEmailSignInButton, registerButton, cancelRegistration, submitRegistration;
    private FirebaseAuth mAuth;
    RelativeLayout loginScreen;
    LinearLayout registerScreen;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        // Set up the login form.
        mEmailView = (EditText) findViewById(R.id.email);
//        populateAutoComplete();

        mPasswordView = (EditText) findViewById(R.id.password);
        mPasswordView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
                if (id == EditorInfo.IME_ACTION_DONE || id == EditorInfo.IME_NULL) {
                    attemptLogin();
                    return true;
                }
                return false;
            }
        });

        loginScreen = (RelativeLayout) findViewById(R.id.loginScreen);
        registerScreen = (LinearLayout) findViewById(R.id.registerScreen);

        mEmailSignInButton = (Button) findViewById(R.id.email_sign_in_button);
        registerButton = (Button) findViewById(R.id.registerButton);
        cancelRegistration = (Button) findViewById(R.id.cancelRegistration);
        submitRegistration = (Button) findViewById(R.id.submitRegistration);

        mEmailSignInButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {

                Log.i("login started", "point 69");
//                    attemptLogin(mEmailView.getText().toString(), mPasswordView.getText().toString());
                attemptLogin();
            }
        });

        registerButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                loginScreen.setVisibility(View.INVISIBLE);
                registerScreen.setVisibility(View.VISIBLE);
                emailRegister.setText(mEmailView.getText());
            }
        });

        mLoginFormView = findViewById(R.id.login_form);
        mProgressView = findViewById(R.id.login_progress);

        emailRegister = (EditText) findViewById(R.id.emailRegister);
        userName = (EditText) findViewById(R.id.userName);
        password1 = (EditText) findViewById(R.id.password1);
        password2 = (EditText) findViewById(R.id.password2);

        password2.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
                if (id == EditorInfo.IME_ACTION_DONE || id == EditorInfo.IME_NULL) {
                    attemptLogin();
                    return true;
                }
                return false;
            }
        });

        cancelRegistration.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                loginScreen.setVisibility(View.VISIBLE);
                registerScreen.setVisibility(View.INVISIBLE);
            }
        });

        submitRegistration.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                attemptRegistration();
            }
        });
    }

//    private void populateAutoComplete() {
//        if (!mayRequestContacts()) {
//            return;
//        }
//
//    }

    private boolean mayRequestContacts() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            return true;
        }
        if (checkSelfPermission(READ_CONTACTS) == PackageManager.PERMISSION_GRANTED) {
            return true;
        }
        if (shouldShowRequestPermissionRationale(READ_CONTACTS)) {
            Snackbar.make(mEmailView, R.string.permission_rationale, Snackbar.LENGTH_INDEFINITE)
                    .setAction(android.R.string.ok, new View.OnClickListener() {
                        @Override
                        @TargetApi(Build.VERSION_CODES.M)
                        public void onClick(View v) {
                            requestPermissions(new String[]{READ_CONTACTS}, REQUEST_READ_CONTACTS);
                        }
                    });
        } else {
            requestPermissions(new String[]{READ_CONTACTS}, REQUEST_READ_CONTACTS);
        }
        return false;
    }

    /**
     * Callback received when a permissions request has been completed.
     */
//    @Override
//    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
//                                           @NonNull int[] grantResults) {
//        if (requestCode == REQUEST_READ_CONTACTS) {
//            if (grantResults.length == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
//                populateAutoComplete();
//            }
//        }
//    }


    /**
     * Attempts to sign in or register the account specified by the login form.
     * If there are form errors (invalid email, missing fields, etc.), the
     * errors are presented and no actual login attempt is made.
     */
    private void attemptLogin() {
//        if (mAuthTask != null) {
//            return;
//        }

        // Reset errors.
        mEmailView.setError(null);
        mPasswordView.setError(null);

        // Store values at the time of the login attempt.
        String email = mEmailView.getText().toString();
        String password = mPasswordView.getText().toString();

        boolean cancel = false;
        View focusView = null;

        // Check for a valid password, if the user entered one.

        passwordCheck(mPasswordView);
//        if (!TextUtils.isEmpty(password) && !isPasswordValid(password)) {
//            mPasswordView.setError(getString(R.string.error_invalid_password));
//            focusView = mPasswordView;
//            cancel = true;
//        }

        // Check for a valid email address.
//        if (TextUtils.isEmpty(email)) {
//            mEmailView.setError(getString(R.string.error_field_required));
//            focusView = mEmailView;
//            cancel = true;
//        } else if (!isEmailValid(email)) {
//            mEmailView.setError(getString(R.string.error_invalid_email));
//            focusView = mEmailView;
//            cancel = true;
//        }

        emailCheck(mEmailView);

//        if (cancel) {
//            // There was an error; don't attempt login and focus the first
//            // form field with an error.
//            focusView.requestFocus();
//        } else {
        // Show a progress spinner, and kick off a background task to
        // perform the user login attempt.

        if (passwordCheck(mPasswordView) && (emailCheck(mEmailView))) {

            Log.i("login attempted", "point 171");
            showProgress(true);

            mAuth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                // Sign in success, update UI with the signed-in user's information
                                Log.i("signInWithEmail:success", "point 187");
                                FirebaseUser user = mAuth.getCurrentUser();
                                try {
                                    Log.i(user.getDisplayName(), "point 189");
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            } else {
                                // If sign in fails, display a message to the user.
                                Log.i("point 236", task.getException().toString());
                                Toast.makeText(LoginActivity.this, "Login Id or Password is incorrect",
                                        Toast.LENGTH_SHORT).show();
                            }

                        }
                    });
        } else {
            Toast.makeText(LoginActivity.this, "Try again", Toast.LENGTH_SHORT).show();
        }
    }

    private void attemptRegistration() {
//        if (mAuthTask != null) {
//            return;
//        }

        // Reset errors.
        emailRegister.setError(null);
        userName.setError(null);
        password1.setError(null);
        password2.setError(null);

        // Store values at the time of the login attempt.
        String email = emailRegister.getText().toString();
        String userNameString = userName.getText().toString();
        String password1String = password1.getText().toString();
        String password2String = password2.getText().toString();

//        boolean cancel = false;
//        View focusView = null;

        // Check for a valid password, if the user entered one.

        emailCheck(emailRegister);

        passwordCheck(mPasswordView);

//        if (cancel) {
//            // There was an error; don't attempt login and focus the first
//            // form field with an error.
//            focusView.requestFocus();
//        } else {
        // Show a progress spinner, and kick off a background task to
        // perform the user login attempt.

        if (passwordCheck(password1) && (emailCheck(emailRegister))) {

            Log.i("registration attempted", "point 298");
            showProgress(true);

            mAuth.signInWithEmailAndPassword(email, password2String)
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                // Sign in success, update UI with the signed-in user's information
                                Log.i("signInWithEmail:success", "point 307");
                                FirebaseUser user = mAuth.getCurrentUser();
                                try {
                                    Log.i(user.getDisplayName(), "point 310");
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            } else {
                                // If sign in fails, display a message to the user.
                                Log.i("point 316", task.getException().toString());
                                Toast.makeText(LoginActivity.this, "Login Id or Password is incorrect",
                                        Toast.LENGTH_SHORT).show();
                            }

                        }
                    });
        } else {
            Toast.makeText(LoginActivity.this, "Try again", Toast.LENGTH_SHORT).show();
        }
    }

    private boolean passwordCheck(EditText password) {
        String passwordString = password.getText().toString();
        View focusView;
        if (!TextUtils.isEmpty(passwordString) && passwordString.length() < 4) {
            mPasswordView.setError(getString(R.string.error_invalid_password));
            focusView = password;
            focusView.requestFocus();
            return false;
        }
        return true;
    }

    private boolean emailCheck(EditText email) {
        String emailString = email.getText().toString();
        View focusView;
        if (!TextUtils.isEmpty(emailString)) {
            mPasswordView.setError(getString(R.string.error_field_required));
            focusView = email;
            focusView.requestFocus();
            return false;
        } else if (!emailString.contains("@")) {
            mEmailView.setError(getString(R.string.error_invalid_email));
            focusView = email;
            focusView.requestFocus();
            return false;
        }
        return true;
    }

    private boolean isEmailValid(String email) {
        //TODO: Replace this with your own logic
        return email.contains("@");
    }

    private boolean isPasswordValid(String password) {
        //TODO: Replace this with your own logic
        return password.length() > 4;
    }

    /**
     * Shows the progress UI and hides the login form.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    private void showProgress(final boolean show) {
        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
        // for very easy animations. If available, use these APIs to fade-in
        // the progress spinner.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
            mLoginFormView.animate().setDuration(shortAnimTime).alpha(
                    show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
                }
            });

            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mProgressView.animate().setDuration(shortAnimTime).alpha(
                    show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
                }
            });
        } else {
            // The ViewPropertyAnimator APIs are not available, so simply show
            // and hide the relevant UI components.
            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }

}

