package cgeo.geocaching.activity;

import cgeo.geocaching.CacheListActivity;
import cgeo.geocaching.CgeoApplication;
import cgeo.geocaching.NavigateAnyPointActivity;
import cgeo.geocaching.R;
import cgeo.geocaching.SearchActivity;
import cgeo.geocaching.geopoint.Geopoint;
import cgeo.geocaching.maps.CGeoMap;

import org.eclipse.jdt.annotation.NonNull;

import rx.functions.Action1;

import android.app.Activity;
import android.content.Intent;

import java.util.ArrayList;

/**
 * An item of the navigation drawer, including its icon, text and click function.
 */
public final class NavigationDrawerItem {
    static final NavigationDrawerItem MAP = new NavigationDrawerItem(R.string.live_map_button, R.drawable.main_live, R.layout.map_google, new Action1<Activity>() {

        @Override
        public void call(final Activity context) {
            CGeoMap.startActivityLiveMap(context);
        }
    });
    static final NavigationDrawerItem NEARBY = new NavigationDrawerItem(R.string.caches_nearby_button, R.drawable.main_nearby, R.layout.cacheslist_activity, new Action1<Activity>() {

        @Override
        public void call(final Activity context) {
            final Geopoint coords = CgeoApplication.getInstance().currentGeo().getCoords();
            if (coords == null) {
                return;
            }

            CacheListActivity.startActivityNearest(context, coords);
        }
    });
    static final NavigationDrawerItem STORED = new NavigationDrawerItem(R.string.stored_caches_button, R.drawable.main_stored, R.layout.cacheslist_activity, new Action1<Activity>() {

        @Override
        public void call(final Activity context) {
            CacheListActivity.startActivityOffline(context);
        }
    });
    static final NavigationDrawerItem SEARCH = new NavigationDrawerItem(R.string.advanced_search_button, R.drawable.main_search, R.layout.search_activity, new Action1<Activity>() {

        @Override
        public void call(final Activity context) {
            context.startActivity(new Intent(context, SearchActivity.class));
        }
    });
    static final NavigationDrawerItem ANY = new NavigationDrawerItem(R.string.any_button, R.drawable.main_any, R.layout.navigateanypoint_activity, new Action1<Activity>() {

        @Override
        public void call(final Activity context) {
            context.startActivity(new Intent(context, NavigateAnyPointActivity.class));
        }
    });
    /**
     * resource id of the text label shown in the navigation drawer
     */
    private final int labelResourceId;
    private final Action1<Activity> runnable;
    /**
     * resource id of the icon shown in the navigation drawer
     */
    private final int iconResourceId;
    /**
     * resource id of the layout used in the associated activity (by that we calculate if the drawer indicator shall be
     * visible)
     */
    private final int layoutResourceId;

    public NavigationDrawerItem(final int labelResourceId, final int iconResourceId, final int layoutResourceId, final Action1<Activity> runnable) {
        this.labelResourceId = labelResourceId;
        this.iconResourceId = iconResourceId;
        this.layoutResourceId = layoutResourceId;
        this.runnable = runnable;
    }

    static final ArrayList<NavigationDrawerItem> ITEMS;

    static {
        final ArrayList<NavigationDrawerItem> items = new ArrayList<NavigationDrawerItem>();
        items.add(MAP);
        items.add(NEARBY);
        items.add(STORED);
        items.add(SEARCH);
        items.add(ANY);
        ITEMS = items;
    }

    int getLabelResource() {
        return labelResourceId;
    }

    int getIconResource() {
        return iconResourceId;
    }

    void selectItem(@NonNull final Activity context) {
        runnable.call(context);
    }

    /**
     * The android guidelines require to show the navigation drawer indicator only on activities which are listed in the
     * navigation drawer. All other activities are required to show the normal up indicator.
     *
     * @param layoutResID
     * @return
     */
    public static boolean isNavigationDrawerIndicatorVisible(final int layoutResID) {
        // enable on all activities which are actually listed in navigation drawer
        for (final NavigationDrawerItem item : ITEMS) {
            if (layoutResID == item.layoutResourceId) {
                return true;
            }
        }
        // additionally enable on main screen
        return layoutResID == R.layout.main_activity;
    }

    public int getIndex() {
        return ITEMS.indexOf(this);
    }
}
