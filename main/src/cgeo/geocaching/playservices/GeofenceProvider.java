package cgeo.geocaching.playservices;

import cgeo.geocaching.location.Geopoint;
import cgeo.geocaching.models.Geocache;
import cgeo.geocaching.utils.Log;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;

import java.util.Collections;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingRequest;
import com.google.android.gms.location.LocationServices;

public final class GeofenceProvider implements ConnectionCallbacks, OnConnectionFailedListener, ResultCallback<Status> {

    private static final float GEOFENCE_RADIUS_IN_METERS = 100f;
    private static final long GEOFENCE_EXPIRATION_IN_MILLISECONDS = 3_600_000l;
    private static GeofenceProvider instance;
    private GoogleApiClient googleApiClient;
    private final Context context;

    private GeofenceProvider(final Context context) {
        this.context = context;
    }

    public static synchronized GeofenceProvider getInstance(final Context context) {
        if (instance == null) {
            instance = new GeofenceProvider(context);
        }
        return instance;
    }

    public void startTracking(@NonNull final Geocache cache) {
        final GeofencingRequest request = createRequest(cache);
        LocationServices.GeofencingApi.addGeofences(getGoogleApiClient(), request, getGeofencePendingIntent()).setResultCallback(this);
    }

    public void stopTracking(@NonNull final Geocache cache) {
        LocationServices.GeofencingApi.removeGeofences(getGoogleApiClient(), Collections.singletonList(getRequestId(cache))).setResultCallback(this);
    }

    private synchronized GoogleApiClient getGoogleApiClient() {
        if (googleApiClient == null) {
            googleApiClient = new GoogleApiClient.Builder(context).addApi(LocationServices.API).build();
        }
        return googleApiClient;
    }

    private PendingIntent getGeofencePendingIntent() {
        final Intent intent = new Intent(context, GeofenceIntentService.class);
        // We use FLAG_UPDATE_CURRENT so that we get the same pending intent back when
        // calling addGeofences() and removeGeofences().
        return PendingIntent.getService(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
    }

    private static GeofencingRequest createRequest(@NonNull final Geocache cache) {
        final Geofence fence = createGeofence(cache);
        final GeofencingRequest.Builder builder = new GeofencingRequest.Builder();
        builder.setInitialTrigger(GeofencingRequest.INITIAL_TRIGGER_ENTER);
        builder.addGeofence(fence);
        return builder.build();
    }

    private static Geofence createGeofence(@NonNull final Geocache cache) {
        final Geopoint coords = cache.getCoords();
        final Geofence.Builder builder = new Geofence.Builder();
        builder.setRequestId(getRequestId(cache));
        builder.setCircularRegion(coords.getLatitude(), coords.getLongitude(), GEOFENCE_RADIUS_IN_METERS);
        builder.setExpirationDuration(GEOFENCE_EXPIRATION_IN_MILLISECONDS);
        builder.setTransitionTypes(Geofence.GEOFENCE_TRANSITION_ENTER);
        return builder.build();
    }

    /**
     * identify each geofence by the geocode of the cache that it tracks
     */
    private static String getRequestId(@NonNull final Geocache cache) {
        return cache.getGeocode();
    }

    /**
     * Runs when the result of calling addGeofences() and removeGeofences() becomes available.
     * Either method can complete successfully or with an error.
     *
     * @param status
     *            The Status returned through a PendingIntent when addGeofences() or
     *            removeGeofences() get called.
     */
    @Override
    public void onResult(final Status status) {
        if (!status.isSuccess()) {
            Log.e(GeofenceErrorMessages.getErrorString(status.getStatusCode()));
        }
    }

    @Override
    public void onConnectionFailed(final ConnectionResult result) {
        Log.i("Connection failed: ConnectionResult.getErrorCode() = " + result.getErrorCode());
    }

    /**
     * Runs when a GoogleApiClient object successfully connects.
     */
    @Override
    public void onConnected(final Bundle arg0) {
        Log.i("Connected to GoogleApiClient");
    }

    /**
     * The connection to Google Play services was lost for some reason. onConnected() will be called again automatically
     * when the service reconnects.
     */
    @Override
    public void onConnectionSuspended(final int arg0) {
        Log.i("Connection suspended");
    }

}
