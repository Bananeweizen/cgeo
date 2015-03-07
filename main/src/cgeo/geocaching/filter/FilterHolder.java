package cgeo.geocaching.filter;

import org.eclipse.jdt.annotation.Nullable;

/**
 * Singleton which remembers the currently active filter. This avoids having all filter classes implement the parcelable
 * interface.
 */
public final class FilterHolder {
    @Nullable private static IFilter currentFilter;
    @Nullable
    private static TerrainFilter terrainFilter;
    @Nullable
 private static DifficultyFilter difficultyFilter;

    private FilterHolder() {
        // utility class
    }

    public static IFilter getCurrentFilter() {
        return new CombinedFilter(terrainFilter, difficultyFilter, currentFilter);
    }

    public static void setCurrentFilter(final IFilter currentFilter) {
        FilterHolder.currentFilter = currentFilter;
    }

    static void setTerrainFilter(final float min, final float max) {
        terrainFilter = TerrainFilter.create(min, max);
    }

    public static void setDifficultyFilter(final float min, final float max) {
        difficultyFilter = DifficultyFilter.create(min, max);
    }
}
