package com.subrosagames.subrosa.mobile.android.home;

import java.net.URL;

import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.TextView;
import com.subrosagames.subrosa.model.Game;

/**
 *
 */
public class CreateGameTask extends AsyncTask<Void, Void, String> {

    private static final String TAG = CreateGameTask.class.getCanonicalName();

    private final TextView gameInfo;
    private final URL serverUrl;

    public CreateGameTask(TextView gameInfo, URL serverUrl) {
        this.gameInfo = gameInfo;
        this.serverUrl = serverUrl;
    }

    @Override
    protected String doInBackground(Void... voids) {
        String url = serverUrl.toString() + "/android";
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
