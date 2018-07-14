# LoginAction

A library to help making login activity with firebase

**version 1.0**

Almost all Android Apps have a login activity to allow users to login into their app. It is generally the first activity with which your user interacts with. 
For that you need to implement your own logic to login and sign up along with designing a UI for the activity. This requires lots of unproductive and 
repetitive efforts to make the login smooth while checking and catching various errors and exceptions. 

Simple Login Library helps skip the repetitive work and lets you concentrate on the app logic by providing methods for signing in, when working with 
Firebase Authentication. 
#### Login Activity: 
 
##### A library to help making login activity with [Firebase](https://firebase.google.com/docs/auth/) authentication having:
 * [Email](https://firebase.google.com/docs/auth/android/password-auth)
 * [Facebook](https://firebase.google.com/docs/auth/android/facebook-login)
 * [Gmail](https://firebase.google.com/docs/auth/android/google-signin)
 
 login methods for faster login activity building for android applications on java.


**Setup**

1. For Email and Google login:
    * Sign up and create a new [Firebase](https://console.firebase.google.com/u/0/).
    * Enable Facebook, Google, Email providers on the Firebase Dashboard for your app.
    * For Facebook proceed [here](https://developers.facebook.com/docs/facebook-login/android) while excluding step 2 and 9 (and may be 10)
    * proceed with the following dependency:
    
2. Dependency:
    * Add the following dependency to your app's build.gradle file:


```groovy
allprojects {
    repositories {
	    maven { url 'https://jitpack.io' }
	}
}

```
```groovy
dependencies {
	        implementation 'com.github.ritik1991998:LoginAction:cc4bd1759e'
	}
```

3. Proceed calling login classes as [here](https://github.com/ritik1991998/LoginAction/blob/master/app/src/main/java/com/example/android/loginaction/LoginActivity.java)
    * Original methods for logging in is available [here](https://github.com/ritik1991998/LoginAction/blob/actual_code/app/src/main/java/com/example/android/loginaction/LoginActivity.java)

4. Modify your manifest (if using Facebook login) from :
```xml
 <meta-data
            android:name="com.facebook.sdk.ApplicationId"
            android:value="@string/facebook_app_id"
            />
```
to
```xml
 <meta-data
            android:name="com.facebook.sdk.ApplicationId"
            android:value="@string/facebook_app_id"
            tools:replace="android:value" />
```
5. Check if your:
* you got your google-services.json file right.
* Project's build.gradle looks [this](https://github.com/ritik1991998/LoginAction/blob/master/build.gradle)
* App's build.gradle looks [this](https://github.com/ritik1991998/LoginAction/blob/master/app/build.gradle)
* Manifest looks [this](https://github.com/ritik1991998/LoginAction/blob/master/app/src/main/AndroidManifest.xml)
* Your String.xml file has:
    * facebook_app_id
    * fb_login_protocol_scheme  
    
## Usage

2. Methods for:
* Email register
```java

        SimpleRegistration register = new SimpleRegistration();
        register.setOnRegistrationResult(new SimpleRegistration.OnRegistrationResult() {
            @Override
            public void resultSuccessful(FirebaseUser registeredUser) {
                 //registered but not logged in 
            }

            @Override
            public void sameEmailError(Exception errorResult) {
                //account exists with same email Id
            }

            @Override
            public void networkError(Exception errorResult) {
                //network error occurred
            }
            
            @Override
            public void resultError(Exception errorResult) {
                //some error occurred
            }

            @Override
            public void resultName(FirebaseUser registeredUser) {
                //name updated(user already registered)
            }

            @Override
            public void resultDp(Uri dpLink) {
               //DP link updated(user already registered)
            }

            @Override
            public void wrongCredentials(String doubtfulCredential, String errorMessage) {
                //doubtfulCredential : "email" or "password1" or "password2" or "password1 and password2"
                //errorMessage : "empty" or "invalid" or "short" or "mismatch"
                }
            }
        });
        register.attemptRegistration(this, email, password1, password2, userNameString, downloadUrl);
        
```
* Email login
```java

        SimpleEmailLogin login = new SimpleEmailLogin();
        login.setOnEmailLoginResult(new SimpleEmailLogin.OnEmailLoginResult() {
            @Override
            public void resultSuccessful(FirebaseUser registeredUser) {
                //login successful : registeredUser
            }

            @Override
            public void noAccountFound(Exception errorResult) {
                //no account found
            }

            @Override
            public void invalidCredentials(Exception errorResult) {
                //wrong password or this is a google or facebook loggededin account");
            }
                        
            @Override
            public void resultError(Exception errorResult) {
                //some error occurred
            }
            
            @Override
            public void networkError(Exception errorResult) {
                //network error occurred
            }

            @Override
            public void wrongCredentials(String doubtfulCredentials, String errorMessage) {
                //doubtfulCredentials : "email" or "password"
                //errorMessage : "empty" or "invalid" or "short"
            }

        });
        login.attemptLogin(this, email, password);

```
* Password changing
```java
        SimpleEmailLogin passwordReset = new SimpleEmailLogin();
        passwordReset.setOnPasswordChangeResult(new SimpleEmailLogin.OnPasswordChangeResult() {
            @Override
            public void resultSuccessful() {
                //Check your email for a reset link
            }

            @Override
            public void noAccountFound(Exception errorResult) {
                //no account found
            }
            
            @Override
            public void resultError(Exception errorResult) {
                //some error occurred
            }

            @Override
            public void networkError(Exception errorResult) {
                //network error occurred
            }
            
            @Override
            public void wrongEmail(String errorMessage) {
                //errorMessage : "empty" or "invalid"
            }
        });
        passwordReset.attemptPasswordReset(email);

```
* Google login
```java
        googleLogin = new SimpleGoogleLogin(this, RC_SIGN_IN_GOOGLE, getString(R.string.default_web_client_id));
        googleLogin.setOnGoogleLoginResult(new SimpleGoogleLogin.OnGoogleLoginResult() {
            @Override
            public void resultSuccessful(FirebaseUser registeredUser) {
                //login successful
            }
            
            @Override
            public void signinCancelledByUser(Exception errorResult) {
                //signin cancelled by user
            }
            
            @Override
            public void accountCollisionError(Exception errorResult) {
                //account exists with same email Id
            }

            @Override
            public void networkError(Exception errorResult) {
                //network error occurred
            }
            
            @Override
            public void resultError(Exception errorResult) {
                //error occurred
            }
        });
        googleLogin.attemptGoogleLogin();
        //googleLogin is the instance of SimpleGoogleLogin
```
* In onActivityResult

```java
        @Override
        public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
            if (requestCode == RC_SIGN_IN_GOOGLE) {
                googleLogin.onActivityResult(requestCode, resultCode, data);
            }
        }
        //googleLogin is the instance of same SimpleGoogleLogin
```
* Facebook login (**call it in oncreate method**)

```java
        facebookLogin = new SimpleFacebookLogin(this);
        facebookLogin.setOnFacebookLoginResult(new SimpleFacebookLogin.OnFacebookLoginResult() {
            @Override
            public void resultFacebookLoggedIn(LoginResult loginResult) {
                //Facebook login successful, yet to authenticate Firebase
            }

            @Override
            public void resultActualLoggedIn(FirebaseUser registeredUser) {
                //Facebook and Firebase login successful
            }

            @Override
            public void resultCancel() {
                //Facebook login cancelled
            }

            @Override
            public void accountCollisionError(Exception errorResult) {
                //account already exists with the different sign-in credentials
            }
            
            @Override
            public void networkError(Exception errorResult) {
                //network error occurred
            }
            
            @Override
            public void resultError(Exception errorResult) {
                //Facebook or Firebase login error
            }
        });
        facebookLogin.attemptFacebookLogin(mloginButton);

        //mloginButton is LoginButton from FacebookButtonBase
        //facebookLogin is the instance of SimpleFacebookLogin
```
* In onActivityResult
```java
        @Override
        public void onActivityResult(int requestCode, int resultCode, Intent data) {
            super.onActivityResult(requestCode, resultCode, data);
            if (!(facebookLogin == null)){
                facebookLogin.onActivityResult(requestCode, resultCode, data);
            }
        } 
        //facebookLogin is the instance of same SimpleFacebookLogin
```
* Signout
````java
AuthUI.getInstance().signOut(this);
````

**Arguments for wrong credentials**

|Method         |library class       |doubtfulCredentials         |errorMessage      |
|---------------|--------------------|----------------------------|------------------|
|Registration   |SimpleRegistration  | email                      |empty             |
|Registration   |SimpleRegistration  | email                      |invalid           |
|Registration   |SimpleRegistration  | password1                  |empty             |
|Registration   |SimpleRegistration  | password1                  |short             |
|Registration   |SimpleRegistration  | password2                  |empty             |
|Registration   |SimpleRegistration  | password2                  |short             |
|Registration   |SimpleRegistration  | password1 and password2    |mismatch          |
|Email login    |SimpleEmailLogin    | email                      |empty             |
|Email login    |SimpleEmailLogin    | email                      |invalid           |
|Email login    |SimpleEmailLogin    | password                   |empty             |
|Email login    |SimpleEmailLogin    | password                   |short             |
|Password reset |SimpleEmailLogin    |  ----                      |empty             |
|Password reset |SimpleEmailLogin    |  ----                      |invalid           |

## Contributors
   
   - [Ritik kumar](https://github.com/ritik1991998)
   
## Contribution

   All contributions are welcome. Encounter any issue? Don't hesitate to [open an issue](https://github.com/ritik1991998/LoginAction/issues).

## Bugs

 * Google login followed by same facebook id login leads to [this](https://i.stack.imgur.com/DDuxC.png).  
  [This](https://firebase.google.com/docs/auth/android/account-linking) seems to be addressing the issue, but still.
