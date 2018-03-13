# LoginAction

**version 1.0**

Almost all Android Apps have a login page to allow users to login into their app. It is generally the first activity with which your user interacts with.  
For that you need to implement your own logic to login and sign up along with designing a UI for the activity  

Simple Login Library helps skip the repetitive work and helps in concentrating on the app logic by providing methods for signing in. 
#### Login Page: 
 
##### A library to help making login page with [Firebase](https://firebase.google.com/docs/auth/) authentication having:
 * [Email](https://firebase.google.com/docs/auth/android/password-auth)
 * [Facebook](https://firebase.google.com/docs/auth/android/facebook-login)
 * [Gmail](https://firebase.google.com/docs/auth/android/google-signin)
 
 login methods for faster login page building for android applications on java.


**Setup**

1. For Email and Google login:
    * Sign up and create a new [Firebase](https://console.firebase.google.com/u/0/).
    * Enable Facebook, Google, Email providers on the Firebase Dashboard for your app.
    * For Facebook proceed [here](https://developers.facebook.com/docs/facebook-login/android) while excluding step 2 and 9 (and may be 10)
    * proceed with the following dependency:
    
2. Dependency:
    * Add the following dependency to your app's build.gradle file:


```
allprojects {
    repositories {
		...
	    maven { url 'https://jitpack.io' }
	}
}

```
```
dependencies {
	        compile 'com.github.ritik1991998:LoginAction:v1.0'
	}
```

3. Proceed calling login classes as [here](https://github.com/ritik1991998/LoginAction/blob/master/app/src/main/java/com/example/android/loginaction/LoginActivity.java)
    * Original methods for logging in is available [here](https://github.com/ritik1991998/LoginAction/blob/actual_code/app/src/main/java/com/example/android/loginaction/LoginActivity.java)

## Usage
1. Check if your:
* Project's build.gradle looks [this](https://github.com/ritik1991998/LoginAction/blob/master/build.gradle)
* App's build.gradle looks [this](https://github.com/ritik1991998/LoginAction/blob/master/app/build.gradle)
* Manifest looks [this](https://github.com/ritik1991998/LoginAction/blob/master/app/src/main/AndroidManifest.xml)
* Your String.xml file has:
    * facebook_app_id
    * fb_login_protocol_scheme  
    
2. Methods for:

* Email login
```
SimpleEmailLogin login = new SimpleEmailLogin();
        login.setOnEmailLoginResult(new SimpleEmailLogin.OnEmailLoginResult() {
            @Override
            public void resultSuccessful(FirebaseUser registeredUser) {
            }

            @Override
            public void resultError(Exception errorResult) {
            }

            @Override
            public void wrongCrudentials(String errorMessage) {
            }
        });
        login.attemptLogin(this, email, password);
```
* Email register
```
SimpleRegistration register = new SimpleRegistration();
        register.setOnRegistrationResult(new SimpleRegistration.OnRegistrationResult() {
            @Override
            public void resultSuccessful(FirebaseUser registeredUser) {
            }

            @Override
            public void resultError(Exception errorResult) {
            }

            @Override
            public void resultName(FirebaseUser registeredUser) {
            }

            @Override
            public void resultDp(Uri dpLink) {
            }

            @Override
            public void wrongCrudentials(String errorMessage) {
            }
        });
        register.attemptRegistration(this, email, password1String, password2String, userNameString, downloadUrl);

```
* Password changing
```
SimpleEmailLogin passwordReset = new SimpleEmailLogin();
        passwordReset.setOnPasswordChangeResult(new SimpleEmailLogin.OnPasswordChangeResult() {
            @Override
            public void resultSuccessful() {
            }
            @Override
            public void resultError(Exception errorResult) {
            }

            @Override
            public void wrongCrudentials() {
            }
        });
        passwordReset.attemptPasswordReset(this, email);
```
* Google login
```
 googleLogin = new SimpleGoogleLogin(this, RC_SIGN_IN_GOOGLE, getString(R.string.default_web_client_id));
        googleLogin.setOnGoogleLoginResult(new SimpleGoogleLogin.OnGoogleLoginResult() {
            @Override
            public void resultSuccessful(FirebaseUser registeredUser) {
            }
            @Override
            public void resultError(Exception errorResult) {
            }
        });
        googleLogin.attemptGoogleLogin();
```
* In onActivityResult

```
 @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
            if (requestCode == RC_SIGN_IN_GOOGLE) {
                googleLogin.onActivityResult(requestCode, resultCode, data);
            }
        }
    }

```
* Facebook login (**call it in oncreate method**)

```
facebookLogin = new SimpleFacebookLogin(this);
        facebookLogin.setOnFacebookLoginResult(new SimpleFacebookLogin.OnFacebookLoginResult() {
            @Override
            public void resultLoggedIn(FirebaseUser registeredUser) {
            }

            @Override
            public void resultAccountCreated() {
            }

            @Override
            public void resultCancel() {
            }

            @Override
            public void resultError(Exception errorResult) {
            }
        });
        facebookLogin.attemptFacebookLogin(mloginButton);

```
* In onActivityResult
```
 @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
            if (!(facebookLogin == null))
                facebookLogin.onActivityResult(requestCode, resultCode, data);
            }
        }
    }

```

## Contributors
   
   - [Ritik kumar](https://github.com/ritik1991998)
   
## Contribution

   All contributions are welcome. Encounter any issue? Don't hesitate to [open an issue](https://github.com/ritik1991998/LoginAction/issues).

## Bugs

 * Google login followed by same facebook id login leads to [this](https://i.stack.imgur.com/DDuxC.png).  
  [This](https://firebase.google.com/docs/auth/android/account-linking) seems to be addressing the issue, but still.