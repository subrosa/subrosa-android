package com.subrosagames.subrosa.mobile.android;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import com.google.android.gcm.GCMRegistrar;
import com.subrosagames.subrosa.model.Game;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

/**
 * Initial screen for application.
 */
public class EntranceActivity extends Activity {

    private static final String SENDER_ID = "145665360390";
    private static final String TAG = EntranceActivity.class.getCanonicalName();

    private final LocationListener locationListener = new LocationListener() {

        public void onLocationChanged(Location location) {
        }

        public void onStatusChanged(String s, int i, Bundle bundle) {
        }

        public void onProviderEnabled(String s) {
        }

        public void onProviderDisabled(String s) {
        }

    };


    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        try {
            Class.forName("android.os.AsyncTask");
        } catch (ClassNotFoundException e) {
            throw new IllegalStateException("Unable to load class android.os.AsyncTask during application start", e);
        }

        setContentView(R.layout.main);

        registerDeviceWithGCM();
    }

    public void updateGame(View view) {
        TextView greeting = (TextView) findViewById(R.id.message);
        greeting.setText("");

        String gameId = ((EditText) findViewById(R.id.input)).getText().toString();
        new PopulateGameName(gameId, greeting).execute();
    }

    public void createGame(View view) {
        TextView gameInfo = (TextView) findViewById(R.id.createdGame);
        new CreateGame(gameInfo).execute();
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

    private void setupLocationManagement() {
        LocationManager locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            enableLocationSettings();
        }
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 10000, 10f, locationListener);
    }

    private void enableLocationSettings() {
        Intent settingsIntent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
        startActivity(settingsIntent);
    }

    public class PopulateGameName extends AsyncTask<Void, Void, String> {

        private final String gameId;
        private final TextView textView;

        public PopulateGameName(String gameId, TextView textView) {
            this.gameId = gameId;
            this.textView = textView;
        }

        @Override
        protected String doInBackground(Void... voids) {
            String url = "http://eng-dhcp-114.rdu.lulu.com:8080/subrosa-api/v1/game/" + gameId;
            RestTemplate restTemplate = new RestTemplate(true);
            try {
                Game game = restTemplate.getForObject(url, Game.class);
                Log.d(TAG, "Retrieved game with id " + game.getId());
                return game.getName();
            } catch (RestClientException e) {
                Log.e(TAG, "Failed to populate game info", e);
                return e.toString();
            }
        }

        @Override
        protected void onPostExecute(String text) {
            Log.v(TAG, "Updating text field with string " + text);
            textView.setText(text);
        }
    }

    public class CreateGame extends AsyncTask<Void, Void, String> {

        private final TextView gameInfo;

        public CreateGame(TextView gameInfo) {
            this.gameInfo = gameInfo;
        }

        @Override
        protected String doInBackground(Void... voids) {
            String url = "http://eng-dhcp-114.rdu.lulu.com:8080/subrosa-api/v1/android";
            RestTemplate restTemplate = new RestTemplate(true);
            try {
                Game game = restTemplate.getForObject(url, Game.class);
                Log.d(TAG, "Retrieved game with id " + game.getId());
                return game.getName();
            } catch (RestClientException e) {
                Log.e(TAG, "Failed to create game", e);
                return e.toString();
            }
        }

        @Override
        protected void onPostExecute(String text) {
            Log.v(TAG, "Updating text field with string " + text);
            gameInfo.setText(text);
        }
    }
}