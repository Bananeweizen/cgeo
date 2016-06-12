package cgeo.geocaching.playservices;

import cgeo.geocaching.utils.Log;

import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingEvent;

import android.app.IntentService;
import android.content.Intent;

import java.util.List;

public class GeofenceIntentService extends IntentService {
    public GeofenceIntentService() {
        super("cgeo_GeofenceIntentService");
    }

    @Override
    protected void onHandleIntent(final Intent intent) {
        final GeofencingEvent geofencingEvent = GeofencingEvent.fromIntent(intent);
        if (geofencingEvent.hasError()) {
            Log.e(GeofenceErrorMessages.getErrorString(geofencingEvent.getErrorCode()));
            return;
        }

        // Get the transition type.
        final int geofenceTransition = geofencingEvent.getGeofenceTransition();

        // Test that the reported transition was of interest.
        if (geofenceTransition == Geofence.GEOFENCE_TRANSITION_ENTER) {

            final List<Geofence> triggeringGeofences = geofencingEvent.getTriggeringGeofences();
            final Geofence geofence = triggeringGeofences.get(0);
            final String geocode = getGeocode(geofence);

            // Send notification and log the transition details.
            GeofenceNotification.sendNotification(getBaseContext(), geocode);
        } else {
            // Log the error.
            Log.e("invalid geofence transition " + geofenceTransition);
        }
    }

    private static String getGeocode(final Geofence geofence) {
        return geofence.getRequestId();
    }

}