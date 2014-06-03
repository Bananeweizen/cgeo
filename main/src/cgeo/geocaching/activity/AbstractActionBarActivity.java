package cgeo.geocaching.activity;

import cgeo.geocaching.CacheListActivity;
import cgeo.geocaching.DataStore;
import cgeo.geocaching.R;
import cgeo.geocaching.settings.Settings;

import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.util.SparseBooleanArray;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ExpandableListView;
import android.widget.ExpandableListView.OnChildClickListener;
import android.widget.ExpandableListView.OnGroupClickListener;

/**
 * Classes actually having an ActionBar (as opposed to the Dialog activities)
 */
public class AbstractActionBarActivity extends AbstractActivity {

    /**
     * drawer toggle coordinates between navigation drawer and action bar
     */
    private ActionBarDrawerToggle drawerToggle;
    /**
     * navigation drawer list containing the navigation items
     */
    private ExpandableListView drawerList;
    /**
     * container of the main layout and the navigation drawer
     */
    private DrawerLayout drawerLayout;
    /**
     * remember the current title, so it can be restored after closing the navigation drawer
     */
    private CharSequence title;
    /**
     * hide action bar menus as long as navigation drawer is opened
     */
    protected boolean hideActionBarMenus = false;
    private final SparseBooleanArray previousMenuVisibility = new SparseBooleanArray();
    /**
     * remember the icon that was shown before the navigation drawer opens
     */
    private int previousIcon = R.drawable.cgeo;

    @Override
    protected void onCreate(final Bundle savedInstanceState, final int resourceLayoutID) {
        super.onCreate(savedInstanceState, resourceLayoutID);
        initialize();
    }

    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initialize();
    }

    private void initialize() {
        initUpAction();
        showProgress(false);
    }

    private void initUpAction() {
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public void setTitle(final CharSequence title) {
        super.setTitle(title);
        // reflect the title in the actionbar
        ActivityMixin.setTitle(this, title);
        // remember title, so it can be restored after closing navigation drawer
        this.title = title;
    }

    protected void setIcon(final int resourceId) {
        previousIcon = resourceId;
        getSupportActionBar().setIcon(resourceId);
    }

    @Override
    public boolean onOptionsItemSelected(final MenuItem item) {
        // first let the navigation drawer do its magic
        if (drawerToggle != null && drawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void initializeNavigationDrawer(final int layoutResID) {
        drawerList = (ExpandableListView) findViewById(R.id.navigation_drawer);
        // not all activity layouts include the drawer, so we may exit early
        if (drawerList == null) {
            return;
        }

        title = getTitle();
        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);

        // set a custom shadow that overlays the main content when the drawer opens
        drawerLayout.setDrawerShadow(R.drawable.drawer_shadow, GravityCompat.START);
        // set up the drawer's list view with items and click listener
        final NavigationDrawerAdapter adapter = new NavigationDrawerAdapter(this);
        drawerList.setAdapter(adapter);
        drawerList.setOnChildClickListener(new StoredListClickListener());
        drawerList.setOnGroupClickListener(new DrawerItemClickListener());

        // enable ActionBar app icon to behave as action to toggle nav drawer
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        // ActionBarDrawerToggle ties together the the proper interactions
        // between the sliding drawer and the action bar app icon
        drawerToggle = new ActionBarDrawerToggle(
                this, /* host Activity */
                drawerLayout, /* DrawerLayout object */
                R.drawable.ic_drawer, /* nav drawer image to replace 'Up' caret */
                R.string.drawer_open, /* "open drawer" description for accessibility */
                R.string.drawer_close /* "close drawer" description for accessibility */
                ) {
                    private float previousSlideOffset = 0;

                    @Override
                    public void onDrawerSlide(final View view, final float slideOffset) {
                        // using this callback we can switch before the drawer is fully opened/closed
                        super.onDrawerSlide(view, slideOffset);
                        if (slideOffset > previousSlideOffset && !hideActionBarMenus) {
                            showActionBarMenus(false);
                        } else if (previousSlideOffset > slideOffset && slideOffset < 0.5f && hideActionBarMenus) {
                            showActionBarMenus(true);
                        }
                        previousSlideOffset = slideOffset;

                    }
                };
        drawerLayout.setDrawerListener(drawerToggle);
        drawerToggle.setDrawerIndicatorEnabled(NavigationDrawerItem.isNavigationDrawerIndicatorVisible(layoutResID));
    }

    protected void showActionBarMenus(final boolean show) {
        hideActionBarMenus = !show;
        if (hideActionBarMenus) {
            getSupportActionBar().setTitle(R.string.app_name);
            getSupportActionBar().setIcon(R.drawable.cgeo);
        }
        else {
            getSupportActionBar().setTitle(title);
            getSupportActionBar().setIcon(previousIcon);
        }
        invalidateOptionsMenuCompatible();
    }

    /* The click listener for ListView in the navigation drawer */
    private class DrawerItemClickListener implements OnGroupClickListener {

        @Override
        public boolean onGroupClick(final ExpandableListView parent, final View v, final int groupPosition, final long id) {
            // don't handle the event for the stored group, such that it can trigger collapse/expand
            if (groupPosition == NavigationDrawerItem.STORED.getIndex()) {
                return false;
            }
            // handle the event for all others
            selectDrawerItem(groupPosition);
            return true;
        }
    }

    private class StoredListClickListener implements OnChildClickListener {

        @Override
        public boolean onChildClick(final ExpandableListView parent, final View v, final int groupPosition, final int childPosition, final long id) {
            Settings.saveLastList(DataStore.getLists().get(childPosition).id);
            CacheListActivity.startActivityOffline(AbstractActionBarActivity.this);
            return true;
        }

    }

    private void selectDrawerItem(final int position) {
        // update selected item and title, then close the drawer
        drawerList.setItemChecked(position, true);
        drawerLayout.closeDrawer(drawerList);
        final NavigationDrawerItem navigationDrawerItem = NavigationDrawerItem.ITEMS.get(position);
        navigationDrawerItem.selectItem(this);
    }

    /**
     * When using the ActionBarDrawerToggle, you must call it during
     * onPostCreate() and onConfigurationChanged()...
     */

    @Override
    protected void onPostCreate(final Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        // Sync the toggle state after onRestoreInstanceState has occurred.
        if (drawerToggle != null) {
            drawerToggle.syncState();
        }
    }

    @Override
    public void onConfigurationChanged(final Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        // Pass any configuration change to the drawer toggle
        if (drawerToggle != null) {
            drawerToggle.onConfigurationChanged(newConfig);
        }
    }

    protected boolean isNavigationDrawerOpen() {
        return drawerLayout != null && drawerLayout.isDrawerOpen(drawerList);
    }

    @Override
    public void setContentView(final int layoutResID) {
        super.setContentView(layoutResID);
        initializeNavigationDrawer(layoutResID);
    }

    @Override
    public boolean onPrepareOptionsMenu(final Menu menu) {
        final boolean result = super.onPrepareOptionsMenu(menu);
        toggleActionBarMenusOnNavigationDrawer(menu);
        return result;
    }

    private void toggleActionBarMenusOnNavigationDrawer(final Menu menu) {
        if (hideActionBarMenus) {
            previousMenuVisibility.clear();
            for(int i = 0; i < menu.size(); i++){
                final MenuItem item = menu.getItem(i);
                final int itemId = item.getItemId();
                previousMenuVisibility.put(itemId, item.isVisible());
                item.setVisible(itemId == R.id.menu_about || itemId == R.id.menu_settings);
            }
        }
        else {
            for(int i = 0; i < menu.size(); i++){
                final MenuItem item = menu.getItem(i);
                item.setVisible(previousMenuVisibility.get(item.getItemId(), true));
            }
        }
    }

}
