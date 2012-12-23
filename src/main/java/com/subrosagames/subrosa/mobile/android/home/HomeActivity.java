package com.subrosagames.subrosa.mobile.android.home;

import java.net.MalformedURLException;
import java.net.URL;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.webkit.CookieSyncManager;
import android.widget.EditText;
import android.widget.TextView;
import com.google.android.gcm.GCMRegistrar;
import com.subrosagames.subrosa.mobile.android.R;
import com.subrosagames.subrosa.mobile.android.authentication.AuthenticationActivity;
import com.subrosagames.subrosa.mobile.android.settings.SettingsActivity;
import com.subrosagames.subrosa.mobile.android.settings.SettingsFragment;
import com.subrosagames.subrosa.mobile.android.target.ViewTargetActivity;

/**
 * Initial screen for application.
 */
public class HomeActivity extends Activity {

    private static final String SENDER_ID = "145665360390";

    private static final String TAG = HomeActivity.class.getCanonicalName();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Workaround for bug in android that makes it such that async tasks will fail to call their onPostExecute() under certain circumstances
        try {
            Class.forName("android.os.AsyncTask");
        } catch (ClassNotFoundException e) {
            throw new IllegalStateException("Unable to load class android.os.AsyncTask during application start", e);
        }

        setContentView(R.layout.main);
        PreferenceManager.setDefaultValues(this, R.xml.preferences, false);
        CookieSyncManager.createInstance(getApplicationContext());

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        String authToken = sharedPreferences.getString("authToken", getString(R.string.not_authenticated));
        ((TextView) findViewById(R.id.authentication_token)).setText(authToken);

        registerDeviceWithGCM();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        Log.d(TAG, "Inflating main_activity menu for the HomeActivity");
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.main_activity, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_settings:
                Log.d(TAG, "Settings menu item selected: starting SettingsActivity");
                startActivity(new Intent(this, SettingsActivity.class));
                return true;
            case R.id.menu_authenticate:
                Log.d(TAG, "Authenticate menu item selected: starting AuthenticateActivity");
                startActivityForResult(new Intent(this, AuthenticationActivity.class), 0);
                return true;
            default:
                Log.d(TAG, "Could not resolve menu item selected");
                return super.onOptionsItemSelected(item);
        }
    }

    public void updateGameInfo(View view) {
        TextView greeting = (TextView) findViewById(R.id.message);
        greeting.setText("");

        String gameId = ((EditText) findViewById(R.id.input)).getText().toString();
        try {
            new PopulateGameNameTask(gameId, greeting, new URL(getApiServerPreference())).execute();
        } catch (MalformedURLException e) {
            greeting.setText(e.toString());
        }
    }

    private String getApiServerPreference() {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        return sharedPreferences.getString(SettingsFragment.KEY_PREF_API_SERVER, "http://localhost");
    }

    public void createGame(View view) {
        TextView gameInfo = (TextView) findViewById(R.id.created_game);
        try {
            new CreateGameTask(gameInfo, new URL(getApiServerPreference())).execute();
        } catch (MalformedURLException e) {
            gameInfo.setText(e.toString());
        }
    }

    public void viewTarget(View view) {
        startActivity(new Intent(this, ViewTargetActivity.class));
    }

    private void registerDeviceWithGCM() {
        GCMRegistrar.checkDevice(this);
        GCMRegistrar.checkManifest(this);
        final String registrationId = GCMRegistrar.getRegistrationId(this);
        if (registrationId.equals("")) {
            GCMRegistrar.register(this, SENDER_ID);
        } else {
            Log.v(TAG, "Already registered with GCM");
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.d(TAG, "Activity result: requestCode " + requestCode + " resultCode " + resultCode + " intent " + data);
        switch (requestCode) {
            case 0:
                if (resultCode != RESULT_OK || data == null) {
                    return;
                }
                String token = data.getStringExtra("token");
                Log.d(TAG, "AuthenticationActivity.onActivityResult: found token " + token);
                if (token != null) {
                    storeAuthenticationToken(token);
                }
                return;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void storeAuthenticationToken(String token) {
        Log.d(TAG, "Storing authToken " + token + " to the shared preferences");
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("authToken", token);
        editor.apply();
        ((TextView) findViewById(R.id.authentication_token)).setText(token);
    }

}