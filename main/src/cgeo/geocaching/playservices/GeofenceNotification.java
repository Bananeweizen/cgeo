package cgeo.geocaching.playservices;

import cgeo.geocaching.CompassActivity;
import cgeo.geocaching.MainActivity;
import cgeo.geocaching.R;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;

public final class GeofenceNotification {

    private GeofenceNotification() {
        // utility class
    }

    /**
     * Posts a notification in the notification bar when a transition is detected.
     * If the user clicks the notification, control goes to the MainActivity.
     */
    public static void sendNotification(final Context context, final String geocode) {
        // Create an explicit content Intent that starts the main Activity.
        final Intent notificationIntent = new Intent(context.getApplicationContext(), CompassActivity.class);

        // Construct a task stack.
        final TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);

        // Add the main Activity to the task stack as the parent.
        stackBuilder.addParentStack(MainActivity.class);

        // Push the content Intent onto the stack.
        stackBuilder.addNextIntent(notificationIntent);

        // Get a PendingIntent containing the entire back stack.
        final PendingIntent notificationPendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);

        // Get a notification builder that's compatible with platform versions >= 4
        final NotificationCompat.Builder builder = new NotificationCompat.Builder(context);

        // Define the notification settings.
        builder.setSmallIcon(R.drawable.cgeo);
        // In a real app, you may want to use a library like Volley to decode the Bitmap.
        // builder.setLargeIcon(BitmapFactory.decodeResource(getResources(), R.drawable.ic_launcher));
        //        builder.setColor(Color.RED);
        builder.setContentTitle("Near a cache");
        builder.setContentText(geocode);
        builder.setContentIntent(notificationPendingIntent);

        // TODO: update support lib needed?
        // builder.setVisibility();
        // builder.setCategory();

        // TODO: add intent for deleting the geofence
        //        builder.setDeleteIntent()

        // Dismiss notification once the user touches it.
        builder.setAutoCancel(true);

        // Get an instance of the Notification manager
        final NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        // Issue the notification
        notificationManager.notify(getNotificationId(geocode), builder.build());
    }

    private static int getNotificationId(final String geocode) {
        return geocode.hashCode();
    }
}
