package com.subrosagames.subrosa.mobile.android.authentication;

import java.lang.Override;
import java.lang.String;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import com.subrosagames.subrosa.mobile.android.R;
import com.subrosagames.subrosa.mobile.android.settings.SettingsFragment;

/**
 * Activity that drives authentication of the user.
 */
public class AuthenticationActivity extends Activity {

    private static final String TAG = AuthenticationActivity.class.getCanonicalName();

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_PROGRESS);

        setContentView(R.layout.authenticate);
        WebView webView = (WebView) findViewById(R.id.authentication_web_view);
        WebSettings webSettings = webView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webView.setWebChromeClient(new WebChromeClient() {
            // Show loading progress in activity's title bar.
            @Override
            public void onProgressChanged(WebView view, int progress) {
                setProgress(progress * 100);
            }
        });
        webView.setWebViewClient(new WebViewClient() {
            // When start to load page, show url in activity's title bar
            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                Log.d(TAG, "Page started loading: " + url);
                setTitle(url);
            }
            @Override
            public void onPageFinished(WebView view, String url) {
                Log.d(TAG, "Page finished loading: " + url);
                CookieSyncManager.getInstance().sync();
                // Get the cookie from cookie jar.
                String cookie = CookieManager.getInstance().getCookie(url);
                Log.d(TAG, "Got cookie string for page: " + cookie);
                if (cookie == null) {
                    return;
                }
                // Cookie is a string like NAME=VALUE [; NAME=VALUE]
                String[] pairs = cookie.split(";");
                for (String pair : pairs) {
                    String[] parts = pair.trim().split("=", 2);
                    // If token is found, return it to the calling activity.
                    if (parts.length == 2 && parts[0].equalsIgnoreCase("subrosa_auth_token")) {
                        Intent result = new Intent();
                        String token = parts[1];
                        Log.d(TAG, "Received auth token " + token);
                        result.putExtra("token", token);
                        setResult(RESULT_OK, result);
                        finish();
                    }
                }
            }
        });
        String url = getAuthServerPreference() + "/sign-in.html";
        Log.d(TAG, "Navigating to " + url + " for authentication");
        webView.loadUrl(url);
    }

    @Override
    protected void onPause() {
        super.onPause();
        CookieSyncManager.getInstance().stopSync();
    }

    @Override
    protected void onResume() {
        super.onResume();
        CookieSyncManager.getInstance().startSync();
    }

    private String getAuthServerPreference() {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        return sharedPreferences.getString(SettingsFragment.KEY_PREF_AUTH_SERVER, "http://localhost");
    }

}