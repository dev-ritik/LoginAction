package com.example.android.loginaction;


import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
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

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.regex.Pattern;


/**
 * A login screen that offers login via email/password.
 */
public class LoginActivity extends AppCompatActivity {

    /**
     * Id to identity READ_CONTACTS permission request.
     */
    private final static int RC_SIGN_IN = 1;
    private static final int RC_PHOTO_PICKER = 2;

    private EditText mEmailView;
    private EditText mPasswordView, emailRegister, userName, password1, password2;
    private View mProgressView;
    private View mLoginFormView;
    private Button mEmailSignInButton, submitRegistration;
    private LoginButton mloginButton;
    private CallbackManager mCallbackManager;
    private FirebaseAuth mAuth;
    private SignInButton signInGoogleButton;
    private TextView forgetPassword, registerButton, cancelRegistration;
    RelativeLayout loginScreen;
    LinearLayout registerScreen;
    private FirebaseUser user;
    private ImageView dpChangeButton;
    private Uri selectedImageUri = null, downloadUrl = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        findViewById(R.id.github).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse("https://github.com/ritik1991998/LoginAction"));
                startActivity(i);
            }
        });

        mEmailView = (EditText) findViewById(R.id.emailInput);

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
        registerButton = (TextView) findViewById(R.id.registerText);
        cancelRegistration = (TextView) findViewById(R.id.cancelRegistration);
        submitRegistration = (Button) findViewById(R.id.submitRegistration);
        forgetPassword = (TextView) findViewById(R.id.forgetPassword);

        mEmailSignInButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {

                attemptLogin();
            }
        });

        registerButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                loginScreen.setVisibility(View.GONE);
                registerScreen.setVisibility(View.VISIBLE);
                emailRegister.setText(mEmailView.getText());
            }
        });

        forgetPassword.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                mProgressView.setVisibility(View.VISIBLE);
                if (!emailCheck(mEmailView)) {
                    mProgressView.setVisibility(View.GONE);
                } else {
                    mAuth.sendPasswordResetEmail(mEmailView.getText().toString())
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        mProgressView.setVisibility(View.GONE);
                                        Toast.makeText(LoginActivity.this, "Check your email for a reset link.", Toast.LENGTH_SHORT).show();
                                    } else {
                                        Log.i("168", task.getException().toString());
                                        try {
                                            throw task.getException();
                                        } catch (com.google.firebase.auth.FirebaseAuthInvalidUserException e) {
                                            error("no account found");

                                        } catch (com.google.firebase.FirebaseNetworkException e) {
                                            error("network error occurred");

                                        } catch (com.google.firebase.FirebaseException e) {
                                            error("no account found");

                                        } catch (Exception e) {
                                            error("some error occurred");

                                        }
                                    }
                                }
                            });
                }

            }
        });

        mLoginFormView = findViewById(R.id.login_form);
        mProgressView = findViewById(R.id.login_progress);

        emailRegister = (EditText) findViewById(R.id.emailRegister);
        userName = (EditText) findViewById(R.id.userName);
        password1 = (EditText) findViewById(R.id.password1);
        password2 = (EditText) findViewById(R.id.password2);
        dpChangeButton = (ImageView) findViewById(R.id.dpChangeButton);

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

        mAuth = FirebaseAuth.getInstance();

        mCallbackManager = CallbackManager.Factory.create();

        mloginButton = findViewById(R.id.login_button);
        mloginButton.setReadPermissions("email", "public_profile");
        mloginButton.registerCallback(mCallbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                Log.i("got that", "facebook:onSuccess:" + loginResult);
                mProgressView.setVisibility(View.VISIBLE);
                handleFacebookAccessToken(loginResult.getAccessToken());
            }

            @Override
            public void onCancel() {
                error("cancelled");
            }

            @Override
            public void onError(FacebookException error) {
                error("some error occurred");
            }
        });

        signInGoogleButton = findViewById(R.id.signInGoogle);
        signInGoogleButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mProgressView.setVisibility(View.VISIBLE);
                GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                        .requestIdToken(getString(R.string.default_web_client_id))
                        .requestEmail()
                        .build();

                Intent signInIntent = GoogleSignIn.getClient(LoginActivity.this, gso).getSignInIntent();
                startActivityForResult(signInIntent, RC_SIGN_IN);
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

    private void attemptLogin() {
        // Reset errors.
        mProgressView.setVisibility(View.VISIBLE);

        mEmailView.setError(null);
        mPasswordView.setError(null);

        // Store values at the time of the login attempt.
        String email = mEmailView.getText().toString();
        String password = mPasswordView.getText().toString();

        // Check for a valid password, if the user entered one.

        if (passwordCheck(mPasswordView) && (emailCheck(mEmailView))) {

            mAuth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                // Sign in success, update UI with the signed-in user's information
                                Log.i("signInWithEmail:success", "point 311");
                                loggedIn();
                            } else {
                                // If sign in fails, display a message to the user.
                                try {
                                    throw task.getException();
                                } catch (com.google.firebase.auth.FirebaseAuthInvalidUserException e) {
                                    error("no Account Found");

                                } catch (com.google.firebase.FirebaseNetworkException e) {
                                    error("network error occurred");

                                } catch (com.google.firebase.auth.FirebaseAuthInvalidCredentialsException e) {
                                    error("wrong password or this is a google or facebook loggedin account");

                                } catch (Exception e) {
                                    Log.i("point 327", e.toString());
                                    error("some error occurred");

                                }
                            }

                        }
                    });
        } else {
            error("wrong credentials");
        }
    }

    private void attemptRegistration() {
        // Reset errors.
        emailRegister.setError(null);
        userName.setError(null);
        password1.setError(null);
        password2.setError(null);

        // Store values at the time of the login attempt.
        String email = emailRegister.getText().toString();
        final String userNameString = userName.getText().toString();
        String password1String = password1.getText().toString();
        String password2String = password2.getText().toString();

        if (!emailCheck(emailRegister) || !passwordCheck(password1) || !passwordCheck(password2))
            return;
        View focusView;

        if (!password1String.equals(password2String)) {
            Toast.makeText(LoginActivity.this, "Passwords doesn't match", Toast.LENGTH_SHORT).show();
            focusView = password1;
            focusView.requestFocus();
            return;
        }

        mProgressView.setVisibility(View.VISIBLE);

        mAuth.createUserWithEmailAndPassword(email, password1String)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
//                            loginScreen.setVisibility(View.VISIBLE);
//                            registerScreen.setVisibility(View.GONE);
                            // Sign in success, update UI with the signed-in user's information
                            user = mAuth.getCurrentUser();
                            if (selectedImageUri != null) {
                                if (downloadUrl != null) {
                                    Log.i(downloadUrl.toString(), "point 389");
                                    UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                                            .setDisplayName(userNameString)
                                            .setPhotoUri(downloadUrl)
                                            .build();
                                    Log.i(selectedImageUri.toString(), "point 394");
                                    user.updateProfile(profileUpdates)
                                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    if (task.isSuccessful()) {
                                                        Log.i("point 400", "User profile successfully updated.");
                                                        selectedImageUri = null;
                                                        downloadUrl = null;
                                                        loggedIn();
                                                    } else {
                                                        Log.i("82", task.getException().toString());
                                                        error("profile update failed");
                                                    }
                                                }
                                            });
                                } else {
                                    Log.i("point 411", "User profile pic upload failed.");

                                    UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                                            .setDisplayName(userNameString)
                                            .build();
                                    Log.i(selectedImageUri.toString(), "point 416");
                                    user.updateProfile(profileUpdates)
                                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    if (task.isSuccessful()) {
                                                        Log.i("point 422", "User name successfully updated.");
                                                        selectedImageUri = null;
                                                        downloadUrl = null;
                                                        loggedIn();
                                                    } else {
                                                        Log.i("point 427", task.getException().toString());
                                                        error("profile upload failed");
                                                    }
                                                }
                                            });
                                }
                            } else {
                                UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                                        .setDisplayName(userNameString)
                                        .build();
                                user.updateProfile(profileUpdates)
                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                if (task.isSuccessful()) {
                                                    Log.i("point 442", "User profile successfully updated.");
                                                    selectedImageUri = null;
                                                    downloadUrl = null;
                                                    loggedIn();
                                                } else {
                                                    Log.i("82", task.getException().toString());
                                                    error("profile upload failed");
                                                }
                                            }
                                        });
                            }
                        } else {
                            registerScreen.setVisibility(View.VISIBLE);
                            loginScreen.setVisibility(View.GONE);
                            // If sign in fails, display a message to the user.
                            Log.i("91", task.getException().toString());
                            try {
                                throw task.getException();
                            } catch (com.google.firebase.auth.FirebaseAuthUserCollisionException e) {
                                error("account exists with same email Id");
                            } catch (com.google.firebase.FirebaseNetworkException e) {
                                error("network error occurred");
                            } catch (Exception e) {
                                error("some error occurred");

                            }
                        }

                    }
                });


    }

    private boolean passwordCheck(EditText password) {
        String passwordString = password.getText().toString();
        View focusView;
        if (TextUtils.isEmpty(passwordString) || passwordString.length() < 7) {
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
        String emailRegex = "^[a-zA-Z0-9_+&*-]+(?:\\." +
                "[a-zA-Z0-9_+&*-]+)*@" +
                "(?:[a-zA-Z0-9-]+\\.)+[a-z" +
                "A-Z]{2,7}$";
        Pattern pat = Pattern.compile(emailRegex);
        if (TextUtils.isEmpty(emailString)) {
            email.setError(getString(R.string.error_field_required));
            focusView = email;
            focusView.requestFocus();
            return false;
        } else if (!pat.matcher(emailString).matches()) {

            email.setError(getString(R.string.error_invalid_email));
            focusView = email;
            focusView.requestFocus();
            return false;
        }
        return true;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        mCallbackManager.onActivityResult(requestCode, resultCode, data);


        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            try {
                Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = task.getResult(ApiException.class);
                firebaseAuthWithGoogle(account);
            } catch (ApiException e) {
                // Google Sign In failed, update UI appropriately
                e.printStackTrace();
                Log.i("81", e.getStatusCode() + "");
                Log.i("82", e.getMessage() + "");

                if (e.getStatusCode() == 7) {
                    error("network error occurred");

                } else if (e.getStatusCode() == 12501) {
                    mProgressView.setVisibility(View.GONE);
                } else {
                    error("some error occurred");

                }
            }
        } else if (requestCode == RC_PHOTO_PICKER && resultCode == RESULT_OK) {
            selectedImageUri = data.getData();
            dpChangeButton.setImageURI(selectedImageUri);
            if (selectedImageUri != null) {
                submitRegistration.setActivated(false);
                final StorageReference photoREf = MainActivity.mProfilePicStorageReference.child(selectedImageUri.getLastPathSegment());
//                       take last part of uri location link and make child of mChatPhotosStorageReference
                photoREf.putFile(selectedImageUri).continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                    @Override
                    public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) {
                        if (!task.isSuccessful()) {
                            error("failed while uploading picture");
                        }
                        return photoREf.getDownloadUrl();
                    }
                }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                    //   upload file to firebase on success of upload
                    @Override
                    public void onComplete(@NonNull Task<Uri> task) {
                        if (task.isSuccessful()) {
                            downloadUrl = task.getResult();
                            submitRegistration.setActivated(true);

                        } else {
                            error("failed while uploading picture");
                        }
                    }
                });
            }
        }
    }

    private void firebaseAuthWithGoogle(GoogleSignInAccount account) {

        AuthCredential credential = GoogleAuthProvider.getCredential(account.getIdToken(), null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.i("signInWithCrential:suce", "point 575");
                            loggedIn();
                        } else {
                            Log.i("119", task.getException().toString());
                            try {
                                throw task.getException();
                            } catch (com.google.firebase.auth.FirebaseAuthUserCollisionException e) {
                                Log.i("123", "An account already exists with the same email address but different sign-in credentials");
                                error("account exists with same email Id");

                            } catch (com.google.firebase.FirebaseNetworkException e) {
                                error("network error occurred");

                            } catch (Exception e) {
                                error("some error occurred");

                            }
                        }

                    }
                });
    }

    @Override
    public void onStart() {
        super.onStart();
//        mAuth.addAuthStateListener(mAuthListener);
        // Check if user is signed in (non-null) and update UI accordingly.
    }

    @Override
    protected void onResume() {
        super.onResume();
//        mAuth.addAuthStateListener(mAuthListener);
    }


    private void handleFacebookAccessToken(AccessToken token) {

        AuthCredential credential = FacebookAuthProvider.getCredential(token.getToken());
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            mloginButton.setEnabled(true);
                            loggedIn();
                        } else {
                            mloginButton.setEnabled(true);
                            Log.i("104", task.getException().toString());
                            try {
                                throw task.getException();
                            } catch (com.google.firebase.auth.FirebaseAuthUserCollisionException e) {
                                error("account already exists with the different sign-in credentials");

                            } catch (com.google.firebase.FirebaseNetworkException e) {
                                error("network error occurred");

                            } catch (Exception e) {
                                error("some error occurred");

                            }
                            AuthUI.getInstance().signOut(LoginActivity.this);
                        }


                    }
                });
    }

    private void loggedIn() {
        Toast.makeText(getApplicationContext(), "login successful", Toast.LENGTH_SHORT).show();
        intentMainActivity();
    }

    private void intentMainActivity() {
        mProgressView.setVisibility(View.GONE);
        Log.i("point 665", "login successfully");

        Intent intent = new Intent(getApplicationContext(), com.example.android.loginaction.MainActivity.class);
        intent.putExtra("result", 1);
        setResult(Activity.RESULT_OK, intent);
        startActivity(intent);
        finish();
    }

    private void error(String message) {
        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
        mProgressView.setVisibility(View.GONE);
    }

    @Override
    public void onBackPressed() {
        Intent startMain = new Intent(Intent.ACTION_MAIN);
        startMain.addCategory(Intent.CATEGORY_HOME);
        startMain.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        getApplicationContext().startActivity(startMain);
    }
}

