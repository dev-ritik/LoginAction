# LoginAction

**version 1.0**

####Login Page:  
#####A library to help making login page with [Firebase](https://firebase.google.com/docs/auth/) authentication having:
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
	        compile 'com.github.ritik1991998:LoginAction:f7dd1f2d1f'
	}
```

3. Proceed calling login classes as [here](https://github.com/ritik1991998/LoginAction/blob/master/app/src/main/java/com/example/android/loginaction/LoginActivity.java)


## Contributors
   
   - [Ritik kumar](https://github.com/ritik1991998)
   
##Contribution

   All contributions are welcome. Encounter any issue? Don't hesitate to [open an issue](https://github.com/ritik1991998/LoginAction/issues).

##Bugs

 * Google login followed by same facebook id login leads to [this](https://i.stack.imgur.com/DDuxC.png).  
  [This](https://firebase.google.com/docs/auth/android/account-linking) seems to be addressing the issue, but still.