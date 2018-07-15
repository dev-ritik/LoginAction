package com.example.android.loginaction;

import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.firebase.ui.auth.AuthUI;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.makeramen.roundedimageview.RoundedTransformationBuilder;
import com.squareup.picasso.Picasso;


public class MainActivity extends AppCompatActivity {

    public static final String ANONYMOUS = "anonymous";
    private static final int RC_SIGN_IN = 1;

    private static String mUserName;
    private static Uri mUserDp;
    private String mEmailId;

    private FirebaseAuth mFirebaseAuth;
    private FirebaseAuth.AuthStateListener mAuthStateListener;
    private FirebaseStorage mFirebaseStorage;
    public static StorageReference mProfilePicStorageReference;
    private LinearLayout mainLayout;
    TextView userName;
    ImageView profilePic;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mFirebaseStorage = FirebaseStorage.getInstance();
        mProfilePicStorageReference = mFirebaseStorage.getReference("profile_pic");

        mAuthStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                //to find if user is signed or not
                FirebaseUser user = firebaseAuth.getCurrentUser();

                if (user != null) {
                    //user is signed
                    Log.i("point m57", "user != null");
                    onSignInitilize(user.getUid(), user.getEmail(), user.getPhotoUrl(), user.getDisplayName());
                } else {
                    //user signed out
                    Log.i("point m61", "user = null");

                    onSignOutCleaner();
                    startActivityForResult((new Intent(getApplicationContext(), com.example.android.loginaction.LoginActivity.class)),
                            RC_SIGN_IN);
                    Snackbar snackbar = Snackbar.make(mainLayout, "Logged out successfully", Snackbar.LENGTH_SHORT);
                    View sbView = snackbar.getView();
                    TextView textView = sbView.findViewById(android.support.design.R.id.snackbar_text);
                    textView.setTextColor(Color.GREEN);
                    snackbar.show();
                }
            }

        };

        getSupportActionBar().setTitle("Profile");

        mFirebaseAuth = FirebaseAuth.getInstance();

        userName = findViewById(R.id.userName);
        mainLayout = (LinearLayout) findViewById(R.id.main_content);

        profilePic = findViewById(R.id.profile_image);


    }

    private void onSignInitilize(String userid, String email, Uri profilePicNew, String userNameNew) {
        mEmailId = email;
        mUserDp = profilePicNew;
        mUserName = userNameNew;

        if (mUserName != null) {
            userName.setText(mUserName);
        } else {
            userName.setVisibility(View.GONE);
        }

        TextView emailId = findViewById(R.id.email);
        emailId.setText(mEmailId);
        try {
            if (mUserDp != null) {
                Log.i(mUserDp.toString(), "point m99");
                com.squareup.picasso.Transformation transformation = new RoundedTransformationBuilder()
                        .cornerRadiusDp(30)
                        .oval(false)
                        .build();
                Picasso.get()
                        .load(mUserDp)
                        .transform(transformation)
                        .fit()
                        .centerCrop()
                        .placeholder(R.drawable.icon_profile_empty)
                        .error(R.drawable.ic_launcher_background)
                        .into(profilePic);

            } else {
                profilePic.setImageResource(R.drawable.icon_profile_empty);
            }
        } catch (Exception e) {
            profilePic.setImageResource(R.drawable.icon_profile_empty);
        }

    }

    private void onSignOutCleaner() {
        mEmailId = "";
        mUserName = ANONYMOUS;
        mUserDp = null;

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {//signing prosses result called before onResume
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_SIGN_IN) { //if returns request code sign in
            if (resultCode == RESULT_OK) {//successful login
                Snackbar snackbar = Snackbar.make(mainLayout, "Logged in successfully", Snackbar.LENGTH_SHORT);//snackbar
                View sbView = snackbar.getView();
                TextView textView = sbView.findViewById(android.support.design.R.id.snackbar_text);
                textView.setTextColor(Color.GREEN);
                snackbar.show();

            } else if (resultCode == RESULT_CANCELED) {//dont login
                Snackbar snackbar = Snackbar.make(mainLayout, "Logging in failed!!", Snackbar.LENGTH_SHORT);
                View sbView = snackbar.getView();
                TextView textView = sbView.findViewById(android.support.design.R.id.snackbar_text);
                textView.setTextColor(Color.RED);
                snackbar.show();
            }
        }
    }

    public void logout(View v) {
        mUserDp = null;
        mEmailId = "";
        AuthUI.getInstance().signOut(this);//from login providers and smart lock//redirects to onpause and on resume

    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mAuthStateListener != null)
            mFirebaseAuth.removeAuthStateListener(mAuthStateListener);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mFirebaseAuth.addAuthStateListener(mAuthStateListener);

    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }

    @Override
    public void onBackPressed() {
        Intent startMain = new Intent(Intent.ACTION_MAIN);
        startMain.addCategory(Intent.CATEGORY_HOME);
        startMain.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        getApplicationContext().startActivity(startMain);
    }
}