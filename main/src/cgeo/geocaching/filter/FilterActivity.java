package cgeo.geocaching.filter;

import butterknife.ButterKnife;
import butterknife.InjectView;

import cgeo.geocaching.R;
import cgeo.geocaching.activity.AbstractActionBarActivity;
import cgeo.geocaching.filter.FilterRegistry.FactoryEntry;
import cgeo.geocaching.utils.Log;

import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.OptionsItem;
import org.androidannotations.annotations.OptionsMenu;
import org.eclipse.jdt.annotation.NonNull;
import org.eclipse.jdt.annotation.Nullable;

import rx.functions.Func2;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ExpandableListView;
import android.widget.ExpandableListView.OnChildClickListener;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.SimpleExpandableListAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Show a filter selection using an {@code ExpandableListView}.
 */
@OptionsMenu(R.menu.filter_options)
@EActivity
public class FilterActivity extends AbstractActionBarActivity {

    public static final int REQUEST_SELECT_FILTER = 1234;

    private static final String KEY_FILTER_NAME = "filterName";
    private static final String KEY_FILTER_GROUP_NAME = "filterGroupName";

    @InjectView(R.id.terrainLabel) protected TextView terrainLabel;
    @InjectView(R.id.terrainMin) protected SeekBar terrainMin;
    @InjectView(R.id.terrainMax) protected SeekBar terrainMax;

    @InjectView(R.id.difficultyLabel) protected TextView difficultyLabel;
    @InjectView(R.id.difficultyMin) protected SeekBar difficultyMin;
    @InjectView(R.id.difficultyMax) protected SeekBar difficultyMax;

    @InjectView(R.id.distanceLabel) protected TextView distanceLabel;
    @InjectView(R.id.distanceMin) protected SeekBar distanceMin;
    @InjectView(R.id.distanceMax) protected SeekBar distanceMax;

    @InjectView(R.id.favoritesLabel) protected TextView favoritesLabel;
    @InjectView(R.id.favoritesMin) protected SeekBar favoritesMin;
    @InjectView(R.id.favoritesMax) protected SeekBar favoritesMax;

    @InjectView(R.id.filterList) protected ExpandableListView filterList;
    @InjectView(R.id.filters) protected LinearLayout filtersContainer;

    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState, R.layout.filter_activity);
        ButterKnife.inject(this);

        initializeSeekbars();
        createListAdapter();
    }

    private void initializeSeekbars() {
        initializeSeekbar(terrainMin, terrainMax, TerrainFilter.TERRAIN_MIN, TerrainFilter.TERRAIN_MAX, 0.5f, terrainLabel, R.string.cache_terrain, new Func2<Float, Float, IFilter>() {

            @Override
            public IFilter call(final Float min, final Float max) {
                return TerrainFilter.create(min, max);
            }
        });
        initializeSeekbar(difficultyMin, difficultyMax, DifficultyFilter.DIFFICULTY_MIN, DifficultyFilter.DIFFICULTY_MAX, 0.5f, difficultyLabel, R.string.cache_difficulty);
        initializeSeekbar(distanceMin, distanceMax, 0, 50, 1f, distanceLabel, R.string.cache_distance);
        initializeSeekbar(favoritesMin, favoritesMax, 0, 50, 1f, favoritesLabel, R.string.caches_filter_popularity);
    }

    private void initializeSeekbar(final SeekBar minBar, final SeekBar maxBar, final int minValue, final int maxValue, final float stepSize, final TextView labelView, final int labelResId, final Func2<Float, Float, IFilter> filterFunc) {
        final float range = (maxValue - minValue) / stepSize;
        final int limit = Math.round(range);
        minBar.setMax(limit);
        maxBar.setMax(limit);
        maxBar.setProgress(limit);
        final OnSeekBarChangeListener listener = createChangeListener(minBar, maxBar, minValue, stepSize, labelView, labelResId, filterFunc);
        minBar.setOnSeekBarChangeListener(listener);
        maxBar.setOnSeekBarChangeListener(listener);
    }

    private OnSeekBarChangeListener createChangeListener(final SeekBar minBar, final SeekBar maxBar, final int rangeMin, final float stepSize, final TextView labelView, final int labelResId, final Func2<Float, Float, IFilter> filterFunc) {
        return new OnSeekBarChangeListener() {

            @Override
            public void onStopTrackingTouch(final SeekBar seekBar) {
                // empty
            }

            @Override
            public void onStartTrackingTouch(final SeekBar seekBar) {
                // empty
            }

            @Override
            public void onProgressChanged(final SeekBar seekBar, final int progress, final boolean fromUser) {
                limitRange(minBar, maxBar, seekBar);
                updateLabel(minBar, maxBar, rangeMin, stepSize, labelView, labelResId);
                updateFilter(getSeekbarValue(minBar, rangeMin, stepSize), getSeekbarValue(maxBar, rangeMin, stepSize));
            }

        };
    }

    protected void updateFilter(final float minValue, final float maxValue) {
        // TODO Auto-generated method stub

    }

    protected void updateLabel(final SeekBar minBar, final SeekBar maxBar, final int rangeMin, final float stepSize, final TextView labelView, final int labelResId) {
        final float minValue = getSeekbarValue(minBar, rangeMin, stepSize);
        final float maxValue = getSeekbarValue(maxBar, rangeMin, stepSize);
        labelView.setText(getString(labelResId) + ": " + minValue + '…' + maxValue);
    }

    private static float getSeekbarValue(final SeekBar seekBar, final int rangeMin, final float stepSize) {
        return seekBar.getProgress() * stepSize + rangeMin;
    }

    protected static void limitRange(final SeekBar min, final SeekBar max, final SeekBar current) {
        if (current == min) {
            if (max.getProgress() < min.getProgress()) {
                max.setProgress(min.getProgress());
            }
        }
        if (current == max) {
            if (max.getProgress() < min.getProgress()) {
                min.setProgress(max.getProgress());
            }
        }
    }

    protected static float seekBarValue(final SeekBar seekBar) {
        return seekBar.getProgress() / 2f + 1f;
    }

    private void createListAdapter() {
        final SimpleExpandableListAdapter adapter =
                new SimpleExpandableListAdapter(
                        this,
                        // top level entries in the next 4 lines
                        createFilterTopLevelGroups(),
                        android.R.layout.simple_expandable_list_item_1,
                        new String[] { KEY_FILTER_GROUP_NAME },
                        new int[] { android.R.id.text1 },

                        // child level entries in the next 4 lines
                        createFilterChildren(),
                        android.R.layout.simple_expandable_list_item_2,
                        new String[] { KEY_FILTER_NAME, "CHILD_NAME" },
                        new int[] { android.R.id.text1 }
                );
        filterList.setAdapter(adapter);
        filterList.setOnChildClickListener(new OnChildClickListener() {

            @Override
            public boolean onChildClick(final ExpandableListView parent, final View v, final int groupPosition, final int childPosition, final long id) {
                setFilterResult(getFilterFromPosition(groupPosition, childPosition));
                return true;
            }

        });
    }

    private static @Nullable IFilter getFilterFromPosition(final int groupPosition, final int childPosition) {
        if (groupPosition < 0 || childPosition < 0) {
            return null;
        }
        final FactoryEntry factoryEntry = FilterRegistry.getInstance().getFactories().get(groupPosition);
        return createFilterFactory(factoryEntry.getFactory()).getFilters().get(childPosition);
    }

    /**
     * Creates the group list with the mapped properties.
     */
    private static List<Map<String, String>> createFilterTopLevelGroups() {
        final ArrayList<Map<String, String>> groups = new ArrayList<>();
        for (final FactoryEntry factoryEntry : FilterRegistry.getInstance().getFactories()) {
            final Map<String, String> map = new HashMap<>();
            map.put(KEY_FILTER_GROUP_NAME, factoryEntry.getName());
            groups.add(map);
        }
        return groups;
    }

    private static List<List<Map<String, String>>> createFilterChildren() {
        final List<List<Map<String, String>>> listOfChildGroups = new ArrayList<>();

        for (final FactoryEntry factoryEntry : FilterRegistry.getInstance().getFactories()) {
            final IFilterFactory factory = createFilterFactory(factoryEntry.getFactory());
            final List<? extends IFilter> filters = factory.getFilters();

            final List<Map<String, String>> childGroups = new ArrayList<>(filters.size());

            for (final IFilter filter : filters) {
                final HashMap<String, String> hashMap = new HashMap<>(1);
                hashMap.put(KEY_FILTER_NAME, filter.getName());
                hashMap.put("CHILD_NAME", filter.getName());
                childGroups.add(hashMap);
            }
            listOfChildGroups.add(childGroups);
        }
        return listOfChildGroups;
    }

    private static IFilterFactory createFilterFactory(final Class<? extends IFilterFactory> class1) {
        try {
            return class1.newInstance();
        } catch (final InstantiationException e) {
            Log.e("createFilterFactory", e);
        } catch (final IllegalAccessException e) {
            Log.e("createFilterFactory", e);
        }
        return null;
    }

    /**
     * After calling this method, the calling activity must implement onActivityResult.
     */
    public static void selectFilter(@NonNull final Activity context) {
        context.startActivityForResult(new Intent(context, FilterActivity_.class), REQUEST_SELECT_FILTER);
    }

    @OptionsItem(R.id.menu_reset_filter)
    void resetFilter() {
        setFilterResult(null);
    }

    private void setFilterResult(final IFilter filter) {
        FilterHolder.setCurrentFilter(filter);
        final Intent resultIntent = new Intent();
        setResult(Activity.RESULT_OK, resultIntent);
        finish();
    }

}
