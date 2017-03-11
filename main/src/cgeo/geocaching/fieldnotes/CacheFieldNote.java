package cgeo.geocaching.fieldnotes;

import cgeo.geocaching.log.LogEntry;
import cgeo.geocaching.models.Geocache;

import android.support.v4.util.Pair;

public class CacheFieldNote extends Pair<Geocache, LogEntry> {

    public CacheFieldNote(final Geocache geocache, final LogEntry fieldNote) {
        super(geocache, fieldNote);
    }

}
