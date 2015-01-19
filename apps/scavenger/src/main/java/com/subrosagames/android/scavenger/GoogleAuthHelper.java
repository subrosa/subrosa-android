package com.subrosagames.android.scavenger;

import android.app.Activity;
import android.content.Intent;
import android.content.IntentSender;
import android.os.Bundle;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.plus.Plus;

/**
 * A base class to wrap communication with the Google Play Services PlusClient.
 */
public class GoogleAuthHelper implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    // A magic number we will use to know that our sign-in error resolution activity has completed
    private static final int RC_SIGN_IN = 298583;

    private GoogleApiClient apiClient;
    private Activity activity;
    private OnGoogleAuthListener authListener;

    private boolean intentInProgress;
    private ConnectionResult connectionResult;

    public GoogleAuthHelper(Activity activity, OnGoogleAuthListener authListener) {
        this.activity = activity;
        this.authListener = authListener;
        apiClient = new GoogleApiClient.Builder(activity)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(Plus.API)
                .addScope(Plus.SCOPE_PLUS_LOGIN)
                .build();
    }

    public GoogleApiClient getApiClient() {
        return apiClient;
    }

    /**
     * This must be called from your {@link Activity#onActivityResult}.
     *
     * @param requestCode  request code
     * @param responseCode response code
     * @param intent       intent
     */
    public void onActivityResult(int requestCode, int responseCode, Intent intent) {
        if (requestCode == RC_SIGN_IN) {
            intentInProgress = false;
            if (!apiClient.isConnecting()) {
                apiClient.connect();
            }
        }
    }

    public void onStart() {
        initiatePlusClientConnect();
    }

    public void onStop() {
        initiatePlusClientDisconnect();
    }

    @Override
    public void onConnected(Bundle bundle) {
        authListener.updateConnectButtonState(apiClient.isConnected());
        authListener.onGoogleApiClientBlockingUI(false);
        authListener.onGoogleApiClientSignIn();
    }

    @Override
    public void onConnectionSuspended(int i) {
        authListener.updateConnectButtonState(apiClient.isConnected());
        authListener.onGoogleApiClientBlockingUI(false);
        authListener.onGoogleApiClientSignOut();
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        if (!intentInProgress && connectionResult.hasResolution()) {
            try {
                intentInProgress = true;
                activity.startIntentSenderForResult(connectionResult.getResolution().getIntentSender(),
                        RC_SIGN_IN, null, 0, 0, 0);
            } catch (IntentSender.SendIntentException e) {
                // The intent was canceled before it was sent.  Return to the default
                // state and attempt to connect to get an updated ConnectionResult.
                intentInProgress = false;
                apiClient.connect();
            }
        }
    }

    public void signIn() {
        if (!apiClient.isConnected()) {
            authListener.onGoogleApiClientBlockingUI(true);
            // Make sure that we will start the resolution (e.g. fire the intent and pop up a
            // dialog for the user) for any errors that come in.
            intentInProgress = false;
            // We should always have a connection result ready to resolve,
            // so we can start that process.
            if (connectionResult != null) {
                startResolution();
            } else {
                // If we don't have one though, we can start connect in
                // order to retrieve one.
                initiatePlusClientConnect();
            }
        }
        authListener.updateConnectButtonState(apiClient.isConnected());
    }

    public void signOut() {
        // We only want to sign out if we're connected.
        if (apiClient.isConnected()) {
            // Clear the default account in order to allow the user to potentially choose a
            // different account from the account chooser.
            // Disconnect from Google Play Services, then reconnect in order to restart the
            // process from scratch.
            apiClient.clearDefaultAccountAndReconnect();
        }
    }

    public void revokeAccess() {
        if (apiClient.isConnected()) {
            Plus.AccountApi.clearDefaultAccount(apiClient);
            Plus.AccountApi
                    .revokeAccessAndDisconnect(apiClient)
                    .setResultCallback(new ResultCallback<Status>() {
                        @Override
                        public void onResult(Status status) {
                            authListener.onGoogleApiClientRevokeAccess();
                        }
                    });
        }
    }

    private void startResolution() {
        try {
            // If we can resolve the error, then call start resolution and pass it an integer tag
            // we can use to track.
            // This means that when we get the onActivityResult callback we'll know it's from
            // being started here.
            connectionResult.startResolutionForResult(activity, RC_SIGN_IN);
        } catch (IntentSender.SendIntentException e) {
            // Any problems, just try to connect() again so we get a new ConnectionResult.
            connectionResult = null;
            initiatePlusClientConnect();
        }
    }

    private void initiatePlusClientConnect() {
        if (!apiClient.isConnected() && !apiClient.isConnecting()) {
            apiClient.connect();
        }
    }

    private void initiatePlusClientDisconnect() {
        if (apiClient.isConnected()) {
            apiClient.disconnect();
        }
    }

    public static interface OnGoogleAuthListener {

        /**
         * Called when the {@link GoogleApiClient} is successfully connected.
         */
        void onGoogleApiClientSignIn();

        /**
         * Called when the {@link GoogleApiClient} is disconnected.
         */
        void onGoogleApiClientSignOut();

        /**
         * Called when the {@link GoogleApiClient} is disconnected.
         */
        void onGoogleApiClientRevokeAccess();

        /**
         * Called when the {@link GoogleApiClient} is blocking the UI.  If you have a progress bar widget,
         * this tells you when to show or hide it.
         */
        void onGoogleApiClientBlockingUI(boolean blocking);

        /**
         * Called when there is a change in connection state.  If you have "Sign in"/ "Connect",
         * "Sign out"/ "Disconnect", or "Revoke access" buttons, this lets you know when their states
         * need to be updated.
         */
        void updateConnectButtonState(boolean connected);
    }

}
