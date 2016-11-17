package com.codepath.apps.findmate.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.codepath.apps.findmate.R;
import com.codepath.apps.findmate.models.User;
import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.parse.ParseException;
import com.parse.SaveCallback;

import org.json.JSONObject;

public class LoginActivity extends AppCompatActivity {


    CallbackManager callbackManager;
    LoginButton loginButton;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        FacebookSdk.sdkInitialize(getApplicationContext());
        callbackManager = CallbackManager.Factory.create();

        setContentView(R.layout.activity_login);

        loginButton = (LoginButton) findViewById(R.id.login_button);

        loginButton.setReadPermissions("email", "public_profile", "user_friends");

        AccessToken accesstoken = AccessToken.getCurrentAccessToken();

        if (accesstoken != null && !accesstoken.getPermissions().isEmpty()) {
            getUserDetails(accesstoken, true);
        } else {

            // Callback registration
            loginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
                @Override
                public void onSuccess(LoginResult loginResult) {
                    getUserDetails(loginResult.getAccessToken(), false);
                }

                @Override
                public void onCancel() {
                    // App code
                }

                @Override
                public void onError(FacebookException exception) {
                    // App code
                }
            });
        }

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }

    private void getUserDetails(final AccessToken accessToken, final boolean alreadyExists) {
        final Bundle parameters = new Bundle();
        parameters.putString("fields", "id,name,email,gender,birthday, friends");

        GraphRequest request = GraphRequest.newMeRequest(
                accessToken,
                new GraphRequest.GraphJSONObjectCallback() {
                    @Override
                    public void onCompleted(JSONObject object, GraphResponse response) {
                        Log.v("LoginActivity", response.toString());

                        try {
                            String email = object.getString("email");

                            String name = object.getString("name"); // 01/31/1980 format
                            System.out.println("*************" + email + name);

                            User user = new User();
                            user.setFullName(name);
                            user.setName(name);
                            user.setEmail(email);
                            user.setFbId(accessToken.getUserId());
                            if(!alreadyExists) {
                                user.saveInBackground(new SaveCallback() {
                                    @Override
                                    public void done(ParseException e) {

                                    }
                                });
                            }

                            goToMaps(user);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                    }
                });

        request.setParameters(parameters);
        request.executeAsync();
    }

    private void goToMaps(User user) {
        Intent intent = new Intent(this, MapsActivity.class);
       // Intent intent = new Intent(this, MainActivity.class);
        intent.putExtra("User", user);
        startActivity(intent);
    }
}
