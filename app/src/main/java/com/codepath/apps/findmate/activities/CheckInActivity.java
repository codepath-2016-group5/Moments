package com.codepath.apps.findmate.activities;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.view.GravityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.EditText;

import com.codepath.apps.findmate.R;
import com.codepath.apps.findmate.fragments.CheckInFragment;
import com.codepath.apps.findmate.models.CheckIn;
import com.parse.ParseException;
import com.parse.SaveCallback;

import static android.R.attr.description;
import static com.codepath.apps.findmate.R.id.drawerLayout;

public class CheckInActivity extends AppCompatActivity implements CheckInFragment.Listener {

    public static final String DESCRIPTION_EXTRA = "description";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_check_in);

        Toolbar toolbar = (Toolbar) findViewById(R.id.check_in_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finishActivityWithCanceled();
                return true;

            case R.id.miCheckIn:
                EditText etDescription = (EditText) findViewById(R.id.etDescription);
                onCheckInClick(etDescription.getText().toString());
                return true;
        }

        return false;
    }

    @Override
    public void onCancelClick() {
        finishActivityWithCanceled();
    }

    @Override
    public void onCheckInClick(String description) {
        Intent data = new Intent();
        data.putExtra(DESCRIPTION_EXTRA, description);
        setResult(RESULT_OK, data);

        finish();
    }

    private void finishActivityWithCanceled() {
        Intent data = new Intent();
        setResult(RESULT_CANCELED, data);
        finish();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_check_in, menu);
        return super.onCreateOptionsMenu(menu);
    }
}
