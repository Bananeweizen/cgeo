package cgeo.geocaching.filter;

import cgeo.geocaching.Geocache;
import cgeo.geocaching.R;

import org.eclipse.jdt.annotation.NonNull;
import org.eclipse.jdt.annotation.Nullable;

class DifficultyFilter extends AbstractRangeFilter {

    static final int DIFFICULTY_MIN = 1;
    static final int DIFFICULTY_MAX = 5;

    private DifficultyFilter(final float min, final float max) {
        super(R.string.cache_difficulty, min, max);
    }

    @Override
    public boolean accepts(@NonNull final Geocache cache) {
        return isInRange(cache.getDifficulty());
    }

    @Nullable
    static DifficultyFilter create(final float min, final float max) {
        if (min == DIFFICULTY_MIN && max == DIFFICULTY_MAX) {
            return null;
        }
        return new DifficultyFilter(min, max);
    }

}
