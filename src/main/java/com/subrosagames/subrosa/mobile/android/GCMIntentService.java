package com.subrosagames.subrosa.mobile.android;

import android.app.Notification;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import com.google.android.gcm.GCMBaseIntentService;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.net.URL;

/**
 *
 */
public class GCMIntentService extends GCMBaseIntentService {

    private static final String TAG = GCMIntentService.class.getCanonicalName();

    @Override
    protected void onMessage(Context context, Intent intent) {
        Log.d(TAG, "I just got a message! Let's do something with it");
        Log.d(TAG, intent.getAction());
        Log.d(TAG, intent.getStringExtra("title"));
        Log.d(TAG, intent.getStringExtra("text"));

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this)
                .setSmallIcon(R.drawable.icon)
                .setContentTitle(intent.getStringExtra("title"))
                .setContentText(intent.getStringExtra("text"));

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

    private class RegisterDeviceTask extends AsyncTask<Void, Void, Void> {

        private static final String REGISTRATION_URL = "http://eng-dhcp-114.rdu.lulu.com:8080/subrosa-api/v1/android/register/";

        private String registrationId;

        public RegisterDeviceTask(String registrationId) {
            this.registrationId = registrationId;
        }

        @Override
        protected Void doInBackground(Void... voids) {
            String url = REGISTRATION_URL + registrationId;
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
