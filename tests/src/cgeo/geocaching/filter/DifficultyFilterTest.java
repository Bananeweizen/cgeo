package cgeo.geocaching.filter;

import static org.assertj.core.api.Assertions.assertThat;

import cgeo.CGeoTestCase;
import cgeo.geocaching.Geocache;

public class DifficultyFilterTest extends CGeoTestCase {

    public static void testTerrainFilter() {
        final Geocache easy = new Geocache();
        easy.setDifficulty(1.5f);

        final Geocache hard = new Geocache();
        hard.setDifficulty(5f);

        final DifficultyFilter easyFilter = DifficultyFilter.create(1, 2);
        assert easyFilter != null;

        assertThat(easyFilter.accepts(easy)).isTrue();
        assertThat(easyFilter.accepts(hard)).isFalse();
    }

}
