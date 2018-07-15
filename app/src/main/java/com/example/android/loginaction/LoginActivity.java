package com.example.android.loginaction;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.loginlibrary.SimpleEmailLogin;
import com.example.android.loginlibrary.SimpleFacebookLogin;
import com.example.android.loginlibrary.SimpleGoogleLogin;
import com.example.android.loginlibrary.SimpleRegistration;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;


public class LoginActivity extends AppCompatActivity {

    private final static int RC_SIGN_IN_GOOGLE = 1;
    private static final int RC_PHOTO_PICKER = 2;

    private EditText mEmailView;
    private EditText mPasswordView, emailRegister, userName, password1, password2;
    private View mProgressView;
    private Button mEmailSignInButton, submitRegistration;
    private LoginButton mloginButton;
    private SignInButton signInGoogleButton;
    RelativeLayout loginScreen;
    LinearLayout registerScreen;
    private ImageView dpChangeButton;
    private Uri selectedImageUri = null, downloadUrl = null;
    private SimpleGoogleLogin googleLogin;
    private TextView forgetPassword, registerText, cancelRegistration;
    private SimpleFacebookLogin facebookLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        bindViews();
        setListeners();

        mPasswordView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
                if (id == EditorInfo.IME_ACTION_DONE || id == EditorInfo.IME_NULL) {
                    attemptEmailLogin();
                    return true;
                }
                return false;
            }
        });

        password2.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
                if (id == EditorInfo.IME_ACTION_DONE || id == EditorInfo.IME_NULL) {
                    attemptRegistration();
                    return true;
                }
                return false;
            }
        });

        signInFacebook();
    }

    private void bindViews() {
        mEmailView = (EditText) findViewById(R.id.emailInput);
        mPasswordView = (EditText) findViewById(R.id.password);
        loginScreen = (RelativeLayout) findViewById(R.id.loginScreen);
        registerScreen = (LinearLayout) findViewById(R.id.registerScreen);
        mEmailSignInButton = (Button) findViewById(R.id.email_sign_in_button);
        forgetPassword = (TextView) findViewById(R.id.forgetPassword);
        registerText = (TextView) findViewById(R.id.registerText);
        cancelRegistration = (TextView) findViewById(R.id.cancelRegistration);
        submitRegistration = (Button) findViewById(R.id.submitRegistration);
        mProgressView = findViewById(R.id.login_progress);
        emailRegister = (EditText) findViewById(R.id.emailRegister);
        userName = (EditText) findViewById(R.id.userName);
        password1 = (EditText) findViewById(R.id.password1);
        password2 = (EditText) findViewById(R.id.password2);
        dpChangeButton = (ImageView) findViewById(R.id.dpChangeButton);
        mloginButton = findViewById(R.id.login_button);
        signInGoogleButton = findViewById(R.id.signInGoogle);
    }

    private void setListeners() {
        findViewById(R.id.github).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse("https://github.com/ritik1991998/LoginAction"));
                startActivity(i);
            }
        });
        mEmailSignInButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                attemptEmailLogin();
            }
        });

        forgetPassword.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                attemptEmailPasswordReset();
            }
        });

        registerText.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                loginScreen.setVisibility(View.INVISIBLE);
                registerScreen.setVisibility(View.VISIBLE);
                emailRegister.setText(mEmailView.getText());
            }
        });
        cancelRegistration.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                loginScreen.setVisibility(View.VISIBLE);
                registerScreen.setVisibility(View.GONE);
            }
        });

        submitRegistration.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                attemptRegistration();
            }
        });
        signInGoogleButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mProgressView.setVisibility(View.VISIBLE);
                signInGoogle();
            }
        });

        dpChangeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("image/jpej");
                intent.putExtra(Intent.EXTRA_LOCAL_ONLY, true);
                startActivityForResult(Intent.createChooser(intent, "Complete action using"), RC_PHOTO_PICKER);
            }
        });
    }

    private void attemptEmailLogin() {
        mProgressView.setVisibility(View.VISIBLE);

        // Reset errors.
        mEmailView.setError(null);
        mPasswordView.setError(null);

        String email = mEmailView.getText().toString();
        String password = mPasswordView.getText().toString();

        SimpleEmailLogin login = new SimpleEmailLogin();
        login.setOnEmailLoginResult(new SimpleEmailLogin.OnEmailLoginResult() {
            @Override
            public void resultSuccessful(FirebaseUser registeredUser) {
                loggedIn();
            }

            @Override
            public void noAccountFound(Exception errorResult) {
                error("no Account Found");
            }

            @Override
            public void invalidCredentials(Exception errorResult) {
                error("wrong password or this is a google or facebook loggedin account");
            }

            @Override
            public void resultError(Exception errorResult) {
                error("some error occurred");
            }

            @Override
            public void networkError(Exception errorResult) {
                error("network error occurred");
            }

            @Override
            public void wrongCredentials(String doubtfulCredentials, String errorMessage) {
                mProgressView.setVisibility(View.GONE);
                if (doubtfulCredentials.equals("email")) {
                    mEmailView.setError(errorMessage);
                    mEmailView.requestFocus();
                } else if (doubtfulCredentials.equals("password")) {
                    mPasswordView.setError(errorMessage);
                    mPasswordView.requestFocus();
                } else
                    Toast.makeText(getApplicationContext(), "credential error", Toast.LENGTH_SHORT).show();
            }

        });
        login.attemptLogin(this, email, password);

    }

    private void attemptEmailPasswordReset() {
        mProgressView.setVisibility(View.VISIBLE);

        mEmailView.setError(null);
        mPasswordView.setText("");

        String email = mEmailView.getText().toString();

        SimpleEmailLogin passwordReset = new SimpleEmailLogin();
        passwordReset.setOnPasswordChangeResult(new SimpleEmailLogin.OnPasswordChangeResult() {
            @Override
            public void resultSuccessful() {
                mProgressView.setVisibility(View.GONE);
                Toast.makeText(LoginActivity.this, "Check your email for a reset link.", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void noAccountFound(Exception errorResult) {
                error("no account found");
            }

            @Override
            public void resultError(Exception errorResult) {
                error("some error occurred");
            }

            @Override
            public void networkError(Exception errorResult) {
                error("network error occurred");
            }

            @Override
            public void wrongEmail(String errorMessage) {
                mProgressView.setVisibility(View.GONE);
                mEmailView.setError(errorMessage);
                mEmailView.requestFocus();
                Toast.makeText(getApplicationContext(), errorMessage, Toast.LENGTH_SHORT).show();
            }

        });
        passwordReset.attemptPasswordReset(email);
    }

    private void signInGoogle() {

        googleLogin = new SimpleGoogleLogin(this, RC_SIGN_IN_GOOGLE, getString(R.string.default_web_client_id));
        googleLogin.setOnGoogleLoginResult(new SimpleGoogleLogin.OnGoogleLoginResult() {
            @Override
            public void resultSuccessful(FirebaseUser registeredUser) {
                loggedIn();
            }

            @Override
            public void signinCancelledByUser(Exception errorResult) {

            }

            @Override
            public void accountCollisionError(Exception errorResult) {
                error("account exists with same email Id");
            }

            @Override
            public void networkError(Exception errorResult) {
                error("network error occurred");
            }

            @Override
            public void resultError(Exception errorResult) {
                error("some error occurred");
            }
        });
        googleLogin.attemptGoogleLogin();

    }

    private void signInFacebook() {

        facebookLogin = new SimpleFacebookLogin(this);
        facebookLogin.setOnFacebookLoginResult(new SimpleFacebookLogin.OnFacebookLoginResult() {
            @Override
            public void resultFacebookLoggedIn(LoginResult loginResult) {
                mProgressView.setVisibility(View.VISIBLE);
            }

            @Override
            public void resultActualLoggedIn(FirebaseUser registeredUser) {
                loggedIn();
            }

            @Override
            public void resultCancel() {
                error("cancelled");
            }

            @Override
            public void accountCollisionError(Exception errorResult) {
                error("account already exists with the different sign-in credentials");
            }

            @Override
            public void networkError(Exception errorResult) {
                error("network error occurred");
            }

            @Override
            public void resultError(Exception errorResult) {
                error("some error occurred");
            }
        });
        facebookLogin.attemptFacebookLogin(mloginButton);

    }

    private void loggedIn() {
        Toast.makeText(getApplicationContext(), "login successful", Toast.LENGTH_SHORT).show();
        intentMainActivity();
    }

    private void intentMainActivity() {
        mProgressView.setVisibility(View.GONE);
        Log.i("point la348", "login successfully");

        Intent intent = new Intent(getApplicationContext(), com.example.android.loginaction.MainActivity.class);
        intent.putExtra("result", 1);
        setResult(Activity.RESULT_OK, intent);
        startActivity(intent);
    }

    private void error(String message) {
        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
        mProgressView.setVisibility(View.GONE);
    }

    private void attemptRegistration() {
        // Reset errors.
        mProgressView.setVisibility(View.VISIBLE);
        emailRegister.setError(null);
        userName.setError(null);
        password1.setError(null);
        password2.setError(null);

//         Store values at the time of the login attempt.
        final String email = emailRegister.getText().toString();
        final String userNameString = userName.getText().toString();
        final String password1String = password1.getText().toString();
        final String password2String = password2.getText().toString();

        SimpleRegistration register = new SimpleRegistration();
        register.setOnRegistrationResult(new SimpleRegistration.OnRegistrationResult() {
            @Override
            public void resultSuccessful(FirebaseUser registeredUser) {
                Toast.makeText(getApplicationContext(), "registration successful", Toast.LENGTH_SHORT).show();
                selectedImageUri = null;
                downloadUrl = null;
            }

            @Override
            public void sameEmailError(Exception errorResult) {
                error("account exists with same email Id");
                registerScreen.setVisibility(View.VISIBLE);
                loginScreen.setVisibility(View.GONE);
            }

            @Override
            public void networkError(Exception errorResult) {
                error("network error occurred");
            }

            @Override
            public void resultError(Exception errorResult) {
                error("some error occurred");
                registerScreen.setVisibility(View.VISIBLE);
                loginScreen.setVisibility(View.GONE);
            }

            @Override
            public void profileUpdateError(Exception errorResult) {
                error("some error occurred while updating profile");
                registerScreen.setVisibility(View.GONE);
                loginScreen.setVisibility(View.VISIBLE);
            }

            @Override
            public void resultName(FirebaseUser registeredUser) {

                Toast.makeText(getApplicationContext(), "data updated", Toast.LENGTH_SHORT).show();
                intentMainActivity();
            }

            @Override
            public void resultDp(Uri dpLink) {
                Toast.makeText(getApplicationContext(), "data updated", Toast.LENGTH_SHORT).show();
                intentMainActivity();
            }

            @Override
            public void wrongCredentials(String doubtfulCredential, String errorMessage) {
                mProgressView.setVisibility(View.GONE);
                switch (doubtfulCredential) {
                    case "email":
                        emailRegister.setError(errorMessage);
                        emailRegister.requestFocus();
                        break;
                    case "password1":
                        password1.setError(errorMessage);
                        password1.requestFocus();
                        break;
                    case "password2":
                        password2.setError(errorMessage);
                        password2.requestFocus();
                        break;
                    case "password1 and password2":
                        password1.setError(errorMessage);
                        password2.setError(errorMessage);
                        password1.requestFocus();
                        break;
                    default:
                        Toast.makeText(getApplicationContext(), "credential error", Toast.LENGTH_SHORT).show();
                }
            }
        });
        register.attemptRegistration(this, email, password1String, password2String, userNameString, downloadUrl);

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_SIGN_IN_GOOGLE) {
            googleLogin.onActivityResult(requestCode, resultCode, data);

        } else if (resultCode == RESULT_OK) {
            if (!(facebookLogin == null))
                facebookLogin.onActivityResult(requestCode, resultCode, data);

            if (requestCode == RC_PHOTO_PICKER) {
                selectedImageUri = data.getData();
                dpChangeButton.setImageURI(selectedImageUri);
                if (selectedImageUri != null) {
                    submitRegistration.setActivated(false);
                    StorageReference photoREf = MainActivity.mProfilePicStorageReference.child(selectedImageUri.getLastPathSegment());
//                              take last part of uri location link and make child of mChatPhotosStorageReference
                    photoREf.putFile(selectedImageUri).addOnSuccessListener(LoginActivity.this, new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        //                    upload file to firebase on success of upload
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            downloadUrl = taskSnapshot.getDownloadUrl();//url of uploaded image
                            Log.i("profile uploaded", "point 476");

                            submitRegistration.setActivated(true);
                        }

                    });

                }
            }
        }
    }

    @Override
    public void onBackPressed() {
        Intent startMain = new Intent(Intent.ACTION_MAIN);
        startMain.addCategory(Intent.CATEGORY_HOME);
        startMain.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        getApplicationContext().startActivity(startMain);
    }
}

