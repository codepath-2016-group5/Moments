package com.codepath.apps.findmate;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.codepath.apps.findmate.models.User;
import com.crashlytics.android.Crashlytics;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;

import java.util.List;

import io.fabric.sdk.android.Fabric;

public class MainActivity extends AppCompatActivity {
    TextView tvName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Fabric.with(this, new Crashlytics());

        ParseQuery<User> query = ParseQuery.getQuery(User.class);
           tvName = (TextView)findViewById(R.id.tvText);

        // Configure limit and sort order

        query.findInBackground(new FindCallback<User>() {

            public void done(List<User> users, ParseException e) {

                if (e == null) {
                    //Toast.makeText(tvName.getContext(), users.get(0).getName(), Toast.LENGTH_SHORT).show();

                      tvName.setText(users.get(0).getName());

                } else {

                    Log.e("message", "Error Loading Messages" + e);

                }

            }

        });


    }

    public void forceCrash(View view) {
        throw new RuntimeException("This is a crash");
    }
}
