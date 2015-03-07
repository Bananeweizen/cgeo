package cgeo.geocaching.filter;

import cgeo.geocaching.Geocache;
import cgeo.geocaching.R;

import org.eclipse.jdt.annotation.NonNull;
import org.eclipse.jdt.annotation.Nullable;

class TerrainFilter extends AbstractRangeFilter {

    public static final int TERRAIN_MIN = 1;
    public static final int TERRAIN_MAX = 7;

    private TerrainFilter(final float min, final float max) {
        super(R.string.cache_terrain, min, max);
    }

    @Override
    public boolean accepts(@NonNull final Geocache cache) {
        return isInRange(cache.getTerrain());
    }

    @Nullable
    static TerrainFilter create(final float min, final float max) {
        if (min == TERRAIN_MIN && max == TERRAIN_MAX) {
            return null;
        }
        return new TerrainFilter(min, max);
    }

}
