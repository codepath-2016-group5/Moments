package com.codepath.apps.findmate.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Menu;

import com.codepath.apps.findmate.R;
import com.parse.Parse;
import com.parse.ParseUser;
import com.parse.ui.ParseLoginBuilder;

/**
 * Copied from {@link com.parse.ui.ParseLoginDispatchActivity} with customization.
 */
public class DispatchActivity extends Activity {

    private final int SPLASH_DISPLAY_LENGTH = 600;

    protected Class<?> getTargetClass() {
        return MapsActivity.class;
    }

    private static final int LOGIN_REQUEST = 0;
    private static final int TARGET_REQUEST = 1;

    private static final String LOG_TAG = "ParseLoginDispatch";

    @Override
    final protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dispatch);

        new Handler().postDelayed(new Runnable(){
            @Override
            public void run() {
                runDispatch();
            }
        }, SPLASH_DISPLAY_LENGTH);
    }

    @Override
    final protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        setResult(resultCode);
        if (requestCode == LOGIN_REQUEST && resultCode == RESULT_OK) {
            runDispatch();
        } else {
            finish();
        }
    }

    /**
     * Override this to generate a customized intent for starting ParseLoginActivity.
     * However, the preferred method for configuring Parse Login UI components is by
     * specifying activity options in AndroidManifest.xml, not by overriding this.
     *
     * @return Intent that can be used to start ParseLoginActivity
     */
    protected Intent getParseLoginIntent() {
        ParseLoginBuilder builder = new ParseLoginBuilder(this);
        return builder.build();
    }

    private void runDispatch() {
        if (ParseUser.getCurrentUser() != null) {
            debugLog(getString(com.parse.ui.R.string.com_parse_ui_login_dispatch_user_logged_in) + getTargetClass());
            startActivityForResult(new Intent(this, getTargetClass()), TARGET_REQUEST);
        } else {
            debugLog(getString(com.parse.ui.R.string.com_parse_ui_login_dispatch_user_not_logged_in));
            startActivityForResult(getParseLoginIntent(), LOGIN_REQUEST);
        }
    }

    private void debugLog(String message) {
        if (Parse.getLogLevel() <= Parse.LOG_LEVEL_DEBUG &&
                Log.isLoggable(LOG_TAG, Log.DEBUG)) {
            Log.d(LOG_TAG, message);
        }
    }
}
