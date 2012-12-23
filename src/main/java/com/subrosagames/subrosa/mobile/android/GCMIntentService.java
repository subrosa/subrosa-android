package com.subrosagames.subrosa.mobile.android;

import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.util.Log;
import com.google.android.gcm.GCMBaseIntentService;
import com.subrosagames.subrosa.mobile.android.settings.SettingsFragment;
import com.subrosagames.subrosa.mobile.android.target.ViewTargetActivity;

/**
 *
 */
public class GCMIntentService extends GCMBaseIntentService {

    private static final String TAG = GCMIntentService.class.getCanonicalName();

    @Override
    protected void onMessage(Context context, Intent intent) {
        Log.d(TAG, "Received notification: " + intent.getStringExtra("code"));

        Intent resultIntent = new Intent(this, ViewTargetActivity.class);
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
        stackBuilder.addParentStack(ViewTargetActivity.class);
        stackBuilder.addNextIntent(resultIntent);
        PendingIntent resultPendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this)
                .setSmallIcon(R.drawable.icon)
                .setContentTitle(intent.getStringExtra("title"))
                .setContentText(intent.getStringExtra("text"))
                .setAutoCancel(true)
                .setContentIntent(resultPendingIntent);

        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        int messageId = 0;
        notificationManager.notify(messageId, builder.build());
    }

    @Override
    protected void onError(Context context, String errorId) {
        // Something bad happened trying to register/unregister
    }

    /**
     * Send registrationId to server so it can be used to send messages back to this device.
     */
    @Override
    protected void onRegistered(Context context, String registrationId) {
        new RegisterDeviceTask(registrationId).execute();
    }

    @Override
    protected void onUnregistered(Context context, String s) {
        // Send registrationId to server so it can be unregistered
    }

    private String getApiServerPreference() {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        return sharedPreferences.getString(SettingsFragment.KEY_PREF_API_SERVER, "http://localhost");
    }

    private class RegisterDeviceTask extends AsyncTask<Void, Void, Void> {

        private String registrationId;

        public RegisterDeviceTask(String registrationId) {
            this.registrationId = registrationId;
        }

        @Override
        protected Void doInBackground(Void... voids) {

            String url = getApiServerPreference() + "/subrosa-api/v1/android/register/" + registrationId;
            Log.d(TAG, "Registering device using url " + url);
            RestTemplate restTemplate = new RestTemplate(true);
            try {
                restTemplate.postForObject(url, null, String.class);
            } catch (RestClientException e) {
                Log.e(TAG, "Service call to register device failed", e);
            }
            return null;
        }
    }
}
