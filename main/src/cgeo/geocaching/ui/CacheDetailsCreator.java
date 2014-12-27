package cgeo.geocaching.ui;

import butterknife.ButterKnife;

import cgeo.geocaching.CgeoApplication;
import cgeo.geocaching.Geocache;
import cgeo.geocaching.ICoordinates;
import cgeo.geocaching.R;
import cgeo.geocaching.connector.ConnectorFactory;
import cgeo.geocaching.location.Units;
import cgeo.geocaching.utils.Formatter;

import org.apache.commons.lang3.StringUtils;
import org.eclipse.jdt.annotation.NonNull;

import android.app.Activity;
import android.content.res.Resources;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RatingBar;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public final class CacheDetailsCreator {
    private static final int MAX_RATING = 5;
    private static final int MAX_DIFFICULTY = 5;
    private final Activity activity;
    private final ViewGroup parentView;
    private final Resources res;

    public CacheDetailsCreator(final Activity activity, final int parentViewId) {
        this.activity = activity;
        this.res = activity.getResources();
        this.parentView = ButterKnife.findById(activity, parentViewId);
    }

    private RatingBar setStars(final int valueViewId, final int starsViewId, final float value, final int max) {
        setText(valueViewId, String.format("%.1f", value) + ' ' + activity.getResources().getString(R.string.cache_rating_of) + " " + String.format("%d", max));

        final RatingBar starsView = ButterKnife.findById(parentView, starsViewId);
        starsView.setMax(max);
        starsView.setRating(value);
        return starsView;
    }

    @NonNull
    public String getCacheStatus(final Geocache cache) {
        final List<String> states = new ArrayList<>(5);
        String date = getVisitedDate(cache);
        if (cache.isLogOffline()) {
            states.add(res.getString(R.string.cache_status_offline_log) + date);
            // reset the found date, to avoid showing it twice
            date = "";
        }
        if (cache.isFound()) {
            states.add(res.getString(R.string.cache_status_found) + date);
        }
        if (cache.isArchived()) {
            states.add(res.getString(R.string.cache_status_archived));
        }
        if (cache.isDisabled()) {
            states.add(res.getString(R.string.cache_status_disabled));
        }
        if (cache.isPremiumMembersOnly()) {
            states.add(res.getString(R.string.cache_status_premium));
        }
        if (states.isEmpty()) {
            return StringUtils.EMPTY;
        }
        return StringUtils.join(states, ", ");
    }

    private static String getVisitedDate(final Geocache cache) {
        final long visited = cache.getVisitedDate();
        return visited != 0 ? " (" + Formatter.formatShortDate(visited) + ")" : "";
    }

    private static Float distanceNonBlocking(final ICoordinates target) {
        if (target.getCoords() == null) {
            return null;
        }
        return CgeoApplication.getInstance().currentGeo().getCoords().distanceTo(target);
    }

    public void addRating(final Geocache cache) {
        if (setVisible(R.id.ratingRow, cache.getRating() > 0)) {
            setStars(R.id.ratingValue, R.id.ratingStars, cache.getRating(), MAX_RATING);
            if (cache.getVotes() > 0) {
                final TextView itemAddition = findDetailView(R.id.ratingVotes);
                itemAddition.setText(" (" + cache.getVotes() + ')');
            }
        }
    }

    public void addOwnRating(final Geocache cache) {
        if (setVisible(R.id.myRatingRow, cache.getMyVote() > 0)) {
            setStars(R.id.myRatingValue, R.id.myRatingStars, cache.getMyVote(), MAX_RATING);
        }
    }

    public void addSize(final Geocache cache) {
        if (null != cache.getSize() && cache.showSize()) {
            add(R.string.cache_size, cache.getSize().getL10n());
        }
    }

    public void addDifficulty(final Geocache cache) {
        if (setVisible(R.id.difficultyRow, cache.getDifficulty() > 0)) {
            setStars(R.id.difficultyValue, R.id.difficultyStars, cache.getDifficulty(), MAX_DIFFICULTY);
        }
    }

    public void addTerrain(final Geocache cache) {
        if (setVisible(R.id.terrainRow, cache.getTerrain() > 0)) {
            setStars(R.id.terrainValue, R.id.terrainStars, cache.getTerrain(), ConnectorFactory.getConnector(cache).getMaxTerrain());
        }
    }

    public TextView setDistance(final Geocache cache) {
        Float distance = distanceNonBlocking(cache);
        if (distance == null) {
            if (cache.getDistance() != null) {
                distance = cache.getDistance();
            }
        }
        String text = "--";
        if (distance != null) {
            text = Units.getDistanceFromKilometers(distance);
        }
        final TextView distanceView = findDetailView(R.id.distanceValue);
        distanceView.setText(text);
        return distanceView;
    }

    public void addEventDate(@NonNull final Geocache cache) {
        if (!cache.isEventCache()) {
            return;
        }
        addHiddenDate(cache);
    }

    public TextView addHiddenDate(final @NonNull Geocache cache) {
        final String dateString = Formatter.formatHiddenDate(cache);
        if (StringUtils.isEmpty(dateString)) {
            return null;
        }
        final TextView view = add(cache.isEventCache() ? R.string.cache_event : R.string.cache_hidden, dateString).right;
        view.setId(R.id.date);
        return view;
    }

    public void setText(final int valueViewId, final CharSequence text) {
        findDetailView(valueViewId).setText(text);
    }

    public TextView findDetailView(final int valueViewId) {
        return ButterKnife.findById(parentView, valueViewId);
    }

    public boolean setVisible(final int rowViewId, final boolean visible) {
        ButterKnife.findById(parentView, rowViewId).setVisibility(visible ? View.VISIBLE : View.GONE);
        return visible;
    }
}
