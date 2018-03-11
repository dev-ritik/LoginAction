package com.example.android.loginaction;

import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
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
import com.squareup.picasso.Picasso;
import com.makeramen.roundedimageview.RoundedTransformationBuilder;


public class MainActivity extends AppCompatActivity {

    public static final String ANONYMOUS = "anonymous";
    private static final int RC_SIGN_IN = 1;

    private static String mUser;
    private static Uri mUserProfile;
    private String mEmailId;

    private FirebaseAuth mFirebaseAuth;
    private FirebaseAuth.AuthStateListener mAuthStateListener;
    private FirebaseStorage mFirebaseStorage;
    public static StorageReference mProfilePicStorageReference;
    private FirebaseUser user;
    private LinearLayout mainLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
//        AuthUI.getInstance().signOut(this);//

        mFirebaseStorage = FirebaseStorage.getInstance();
        mProfilePicStorageReference = mFirebaseStorage.getReference("profile_pic");

        Log.i("point m49","oncreate");

        mAuthStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                //to find if user is signed or not
                FirebaseUser user = firebaseAuth.getCurrentUser();

                if (user != null) {
                    //user is signed
                    Log.i("point m58","reached");
                    onSignInitilize(user.getUid(), user.getEmail(), user.getPhotoUrl(), user.getDisplayName());
                } else {
                    //user signed out
                    Log.i("point m62","reached");

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
        user=mFirebaseAuth.getCurrentUser();
        if (user!=null) {
            onSignInitilize(user.getUid(), user.getEmail(), user.getPhotoUrl(), user.getDisplayName());
        }
        TextView userName = findViewById(R.id.userName);
        mainLayout = (LinearLayout) findViewById(R.id.main_content);

        ImageView profilePic = findViewById(R.id.profile_image);



        if (mUser != null) {
            Log.i("point m84","null");

            userName.setText(mUser);
        } else {
            userName.setVisibility(View.GONE);
        }

        TextView emailId = findViewById(R.id.email);
        emailId.setText(mEmailId);
        try {
            if (mUserProfile != null) {
                Log.i(mUserProfile.toString(), "point m87");
                com.squareup.picasso.Transformation transformation = new RoundedTransformationBuilder()
                        .cornerRadiusDp(30)
                        .oval(false)
                        .build();
                Picasso.with(this)
                        .load(mUserProfile)
                        .transform(transformation)
                        .fit()
                        .centerCrop()
                        .placeholder(R.drawable.icon_profile_empty)
                        .error(R.drawable.ic_launcher_background)
                        .into(profilePic);

            } else {
                Log.i("profile pic=null", "point 83");

                profilePic.setImageResource(R.drawable.icon_profile_empty);
            }
        } catch (Exception e) {
            profilePic.setImageResource(R.drawable.icon_profile_empty);
        }

    }

    private void onSignInitilize(String userid, String email, Uri profilePic, String userName) {
        mEmailId = email;
        mUserProfile = profilePic;
        mUser = userName;
        Log.i("point m120",userid);
        Log.i("point m121",email);
//        Log.i("point m122",profilePic.toString());
        Log.i("point m123",userName);

    }

    private void onSignOutCleaner() {
        mEmailId = "";
        mUser=ANONYMOUS;
        mUserProfile = null;

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
        mUserProfile = null;
        mEmailId = "";
        AuthUI.getInstance().signOut(this);//from login providers and smart lock//redirects to onpause and on resume

    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mAuthStateListener != null)
            mFirebaseAuth.removeAuthStateListener(mAuthStateListener);
        Log.i("onpause", "point m138");
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.i("resume", "point m144");
        mFirebaseAuth.addAuthStateListener(mAuthStateListener);

    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }

}