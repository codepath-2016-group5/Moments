package com.codepath.apps.findmate.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;

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
import com.parse.ParseQuery;
import com.parse.SaveCallback;

import org.json.JSONObject;

import java.util.List;

import static android.R.attr.name;

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
        parameters.putString("fields", "id,name,email,gender,birthday,friends");

        GraphRequest request = GraphRequest.newMeRequest(
                accessToken,
                new GraphRequest.GraphJSONObjectCallback() {
                    @Override
                    public void onCompleted(JSONObject object, GraphResponse response) {
                        Log.v("LoginActivity", response.toString());

                        try {
                            final User user = User.fromJSONObject(accessToken, object);
                            if (!alreadyExists) {
                                saveUserAndGoToMaps(user);
                            } else {
                                List<User> users = ParseQuery.getQuery(User.class)
                                        .whereEqualTo(User.FB_ID, accessToken.getUserId()).find();
                                if (users.isEmpty()) {
                                    saveUserAndGoToMaps(user);
                                } else {
                                    goToMaps(users.get(0).getObjectId());
                                }
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });

        request.setParameters(parameters);
        request.executeAsync();
    }

    private void saveUserAndGoToMaps(final User user) {
        user.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if (e == null) {
                    goToMaps(user.getObjectId());
                } else {
                    Toast.makeText(LoginActivity.this,
                            "Failed to save user", Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    private void goToMaps(String userId) {
        Intent intent = new Intent(this, MapsActivity.class);
        intent.putExtra(MapsActivity.USER_ID_EXTRA, userId);
        startActivity(intent);
    }
}
