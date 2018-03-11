package com.example.android.loginaction;


import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;

import android.os.Bundle;
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


import com.example.android.loginlibrary.SimpleEmailLogin;
import com.example.android.loginlibrary.SimpleFacebookLogin;
import com.example.android.loginlibrary.SimpleGoogleLogin;
import com.example.android.loginlibrary.SimpleRegistration;
import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

/**
 * A login screen that offers login via email/password.
 */
public class LoginActivity extends AppCompatActivity {

    /**
     * Id to identity READ_CONTACTS permission request.
     */
    private final static int RC_SIGN_IN_GOOGLE = 1;
    private final static int RC_SIGN_IN_FACEBOOK = 3;
    private static final int RC_PHOTO_PICKER = 2;

    private EditText mEmailView;
    private EditText mPasswordView, emailRegister, userName, password1, password2;
    private View mProgressView;
    private View mLoginFormView;
    private Button mEmailSignInButton, registerButton, cancelRegistration, submitRegistration;
    private LoginButton mloginButton;
    private CallbackManager mCallbackManager;
    private FirebaseAuth mAuth;
    private SignInButton signInGoogleButton;
    //    private FirebaseAuth.AuthStateListener mAuthListener;
    RelativeLayout loginScreen;
    LinearLayout registerScreen;
    private FirebaseUser user;
    private ImageView dpChangeButton;
    private Uri selectedImageUri = null, downloadUrl = null;
    private SimpleGoogleLogin googleLogin;
    private SimpleFacebookLogin facebookLogin;

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
        registerButton = (Button) findViewById(R.id.registerButton);
        cancelRegistration = (Button) findViewById(R.id.cancelRegistration);
        submitRegistration = (Button) findViewById(R.id.submitRegistration);

        mEmailSignInButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {

                Log.i("login started", "point 69");
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
                registerScreen.setVisibility(View.INVISIBLE);
            }
        });

        submitRegistration.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                attemptRegistration();
            }
        });
        Log.i("point la167", "on create");

        mAuth = FirebaseAuth.getInstance();

//        mCallbackManager = CallbackManager.Factory.create();

        mloginButton = findViewById(R.id.login_button);

        mloginButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                signInFacebook();
            }
        });
//        mloginButton.setReadPermissions("email", "public_profile");
//        mloginButton.registerCallback(mCallbackManager, new FacebookCallback<LoginResult>() {
//            @Override
//            public void onSuccess(LoginResult loginResult) {
//                Log.i("got that", "facebook:onSuccess:" + loginResult);
//                mProgressView.setVisibility(View.VISIBLE);
//                handleFacebookAccessToken(loginResult.getAccessToken());
//
//            }
//
//            @Override
//            public void onCancel() {
//                mProgressView.setVisibility(View.INVISIBLE);
//                Log.i("cancelled!!", "facebook:onCancel");
//            }
//
//            @Override
//            public void onError(FacebookException error) {
//                mProgressView.setVisibility(View.INVISIBLE);
//                Log.i("error!!", "facebook:onError", error);
//            }
//        });

        signInGoogleButton = findViewById(R.id.signInGoogle);
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
                // TODO: Fire an intent to show an image picker
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

        String email = mEmailView.getText().toString();
        String password = mPasswordView.getText().toString();

        SimpleEmailLogin login = new SimpleEmailLogin();
        Log.i("point la308", "registration library");
        login.setOnEmailLoginResult(new SimpleEmailLogin.OnEmailLoginResult() {
            @Override
            public void resultSuccessful(FirebaseUser registeredUser) {
                mProgressView.setVisibility(View.INVISIBLE);
                Toast.makeText(getApplicationContext(), "login successful", Toast.LENGTH_SHORT).show();
                Log.i("point 325", "login successfully");

                Intent intent = new Intent(getApplicationContext(), com.example.android.loginaction.MainActivity.class);
                intent.putExtra("result", 1);
                setResult(Activity.RESULT_OK, intent);
                startActivity(intent);
            }

            @Override
            public void resultError(Exception errorResult) {
                Log.i("point la469", "error");
                Toast.makeText(getApplicationContext(), "error", Toast.LENGTH_SHORT).show();
                mProgressView.setVisibility(View.INVISIBLE);
            }

            @Override
            public void wrongCrudentials(String errorMessage) {
                mProgressView.setVisibility(View.INVISIBLE);
                if (errorMessage.contains("email")) {
                    emailRegister.setError(errorMessage);
                    emailRegister.requestFocus();
                } else if (errorMessage.contains("passwordinput")) {
                    password1.setError(errorMessage);
                    password1.requestFocus();
                } else
                    Toast.makeText(getApplicationContext(), "crudential error", Toast.LENGTH_SHORT).show();
            }
        });
        login.attemptLogin(this, email, password);

    }

    private void signInGoogle() {

        googleLogin = new SimpleGoogleLogin(this, RC_SIGN_IN_GOOGLE, getString(R.string.default_web_client_id));
        Log.i("point la303", "google login library");
        googleLogin.setOnGoogleLoginResult(new SimpleGoogleLogin.OnGoogleLoginResult() {
            @Override
            public void resultSuccessful(FirebaseUser registeredUser) {
                Log.i("point la307", "google login successful");
                mProgressView.setVisibility(View.INVISIBLE);
                Toast.makeText(getApplicationContext(), "login successful", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(getApplicationContext(), com.example.android.loginaction.MainActivity.class);
                intent.putExtra("result", 1);
                setResult(Activity.RESULT_OK, intent);
                startActivity(intent);
            }

            @Override
            public void resultError(Exception errorResult) {
                Log.i("point la312", "google login failed");
                Toast.makeText(getApplicationContext(), "some error occurred", Toast.LENGTH_SHORT).show();
                mProgressView.setVisibility(View.INVISIBLE);
            }
        });
        googleLogin.attemptGoogleLogin();

    }

    private void signInFacebook() {

        facebookLogin = new SimpleFacebookLogin(this, RC_SIGN_IN_FACEBOOK);
        Log.i("point la303", "google login library");
        facebookLogin.setOnFacebookLoginResult(new SimpleFacebookLogin.OnFacebookLoginResult() {
            @Override
            public void resultSuccessful(FirebaseUser registeredUser) {
                Log.i("point la307", "google login successful");
                mProgressView.setVisibility(View.INVISIBLE);
                Toast.makeText(getApplicationContext(), "login successful", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(getApplicationContext(), com.example.android.loginaction.MainActivity.class);
                intent.putExtra("result", 1);
                setResult(Activity.RESULT_OK, intent);
                startActivity(intent);
            }

            @Override
            public void resultError(Exception errorResult) {
                Log.i("point la312", "google login failed");
                Toast.makeText(getApplicationContext(), "some error occurred", Toast.LENGTH_SHORT).show();
                mProgressView.setVisibility(View.INVISIBLE);
            }
        });
        facebookLogin.attemptFacebookLogin();

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
        Log.i("point la460", "registration library");
        register.setOnRegistrationResult(new SimpleRegistration.OnRegistrationResult() {
            @Override
            public void resultSuccessful(FirebaseUser registeredUser) {
                Log.i("point la464", "successful");
                mProgressView.setVisibility(View.INVISIBLE);
                Toast.makeText(getApplicationContext(), "registration successful", Toast.LENGTH_SHORT).show();
                Log.i("point 325", "User profile successfully updated.");
                selectedImageUri = null;
                downloadUrl = null;

                if (TextUtils.isEmpty(email) && TextUtils.isEmpty(password1String) && TextUtils.isEmpty(password2String)) {
                    Log.i("point 325", "User profile successfully updated.");
                    Intent intent = new Intent(getApplicationContext(), com.example.android.loginaction.MainActivity.class);
                    intent.putExtra("result", 1);
                    setResult(Activity.RESULT_OK, intent);
                    startActivity(intent);
                }
            }

            @Override
            public void resultError(Exception errorResult) {
                Log.i("point la469", "error");
                Toast.makeText(getApplicationContext(), "error", Toast.LENGTH_SHORT).show();
                registerScreen.setVisibility(View.VISIBLE);
                loginScreen.setVisibility(View.INVISIBLE);
                mProgressView.setVisibility(View.INVISIBLE);
            }

            @Override
            public void resultName(FirebaseUser registeredUser) {
                Log.i("point la472", "name");
                mProgressView.setVisibility(View.INVISIBLE);
                Toast.makeText(getApplicationContext(), "data updated", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(getApplicationContext(), com.example.android.loginaction.MainActivity.class);
                intent.putExtra("result", 1);
                setResult(Activity.RESULT_OK, intent);
                startActivity(intent);
            }

            @Override
            public void resultDp(Uri dpLink) {
                Log.i("point la478", "dp");
                mProgressView.setVisibility(View.INVISIBLE);
                Toast.makeText(getApplicationContext(), "data updated", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(getApplicationContext(), com.example.android.loginaction.MainActivity.class);
                intent.putExtra("result", 1);
                setResult(Activity.RESULT_OK, intent);
                startActivity(intent);
            }

            @Override
            public void wrongCrudentials(String errorMessage) {
                mProgressView.setVisibility(View.INVISIBLE);
                if (errorMessage.contains("email")) {
                    emailRegister.setError(errorMessage);
                    emailRegister.requestFocus();
                } else if (errorMessage.contains("passwordinput")) {
                    password1.setError(errorMessage);
                    password1.requestFocus();
                } else if (errorMessage.contains("passwordrecheck")) {
                    password2.setError(errorMessage);
                    password2.requestFocus();
                } else if (errorMessage.contains("match")) {
                    password1.setError(errorMessage);
                    password2.setError(errorMessage);
                    password1.requestFocus();
                } else
                    Toast.makeText(getApplicationContext(), "crudential error", Toast.LENGTH_SHORT).show();
            }
        });
        register.attemptRegistration(this, email, password1String, password2String, userNameString, downloadUrl);

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
//        mCallbackManager.onActivityResult(requestCode, resultCode, data);
        Log.i("point la423", (resultCode == RESULT_OK) + "");

//        Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN_GOOGLE) {
            Log.i("google result", "point 427");

            googleLogin.onActivityResult(requestCode, resultCode, data);

        } else if (requestCode == RC_SIGN_IN_FACEBOOK) {
            Log.i("facebook result", "point 427");

            facebookLogin.onActivityResult(requestCode, resultCode, data);

        } else if (requestCode == RC_PHOTO_PICKER && resultCode == RESULT_OK) {
            selectedImageUri = data.getData();
            Log.i(selectedImageUri.toString(), "point 462");
            dpChangeButton.setImageURI(selectedImageUri);
            if (selectedImageUri != null) {
                submitRegistration.setActivated(false);
                StorageReference photoREf = MainActivity.mProfilePicStorageReference.child(selectedImageUri.getLastPathSegment());
//                              take last part of uri location link and make child of mChatPhotosStorageReference
                photoREf.putFile(selectedImageUri).addOnSuccessListener(LoginActivity.this, new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    //                    upload file to firebase onsucess of upload
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        downloadUrl = taskSnapshot.getDownloadUrl();//url of uploaded image
                        Log.i("success at profile up", "point 473");

                        submitRegistration.setActivated(true);
                    }

                });

            }
        }
    }


    private void handleFacebookAccessToken(AccessToken token) {
        Log.d("", "handleFacebookAccessToken:" + token);

        AuthCredential credential = FacebookAuthProvider.getCredential(token.getToken());
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            mloginButton.setEnabled(true);
                            // Sign in success, update UI with the signed-in user's information
                            Log.i("signInWthCredntialscess", "point 610");
                            FirebaseUser user = mAuth.getCurrentUser();
                            mProgressView.setVisibility(View.INVISIBLE);

                            Intent intent = new Intent(getApplicationContext(), com.example.android.loginaction.MainActivity.class);
                            intent.putExtra("result", 1);
                            setResult(Activity.RESULT_OK, intent);
                            Toast.makeText(LoginActivity.this, "logged in", Toast.LENGTH_SHORT).show();
                            finish();
                        } else {
                            mloginButton.setEnabled(true);
                            // If sign in fails, display a message to the user.
                            Log.i("signInWithCredentl:fail", "point 621");
                            mProgressView.setVisibility(View.INVISIBLE);

                            Toast.makeText(LoginActivity.this, "Please use your google acount to signin", Toast.LENGTH_SHORT).show();
                        }

                    }
                });
    }


    @Override
    public void onBackPressed() {
//        super.onBackPressed();
        Log.i("point 562", "back pressed");
        Intent startMain = new Intent(Intent.ACTION_MAIN);
        startMain.addCategory(Intent.CATEGORY_HOME);
        startMain.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        getApplicationContext().startActivity(startMain);
        return;

    }
}

