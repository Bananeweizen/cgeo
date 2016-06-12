package cgeo.geocaching.playservices;

import com.google.android.gms.location.GeofenceStatusCodes;

public final class GeofenceErrorMessages {

    private GeofenceErrorMessages() {
        // utility class
    }

    /**
     * Returns the error string for a geofencing error code.
     */
    public static String getErrorString(final int errorCode) {
        switch (errorCode) {
            case GeofenceStatusCodes.GEOFENCE_NOT_AVAILABLE:
                return "Geofence not available";
            case GeofenceStatusCodes.GEOFENCE_TOO_MANY_GEOFENCES:
                return "Too many geofences";
            case GeofenceStatusCodes.GEOFENCE_TOO_MANY_PENDING_INTENTS:
                return "Too many pending intents";
            default:
                return "Unknown geofence error";
        }
    }

}
