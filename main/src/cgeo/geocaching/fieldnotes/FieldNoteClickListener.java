package cgeo.geocaching.fieldnotes;

import android.support.annotation.NonNull;

interface FieldNoteClickListener {
    void onClickFieldNote(@NonNull final CacheFieldNote fieldNote);
}
