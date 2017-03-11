package cgeo.geocaching.fieldnotes;

import cgeo.geocaching.R;
import cgeo.geocaching.activity.AbstractActionBarActivity;
import cgeo.geocaching.models.Geocache;
import cgeo.geocaching.storage.DataStore;
import cgeo.geocaching.ui.recyclerview.RecyclerViewProvider;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class FieldNotesActivity extends AbstractActionBarActivity implements FieldNoteClickListener {

    @NonNull
    private final List<CacheFieldNote> fieldNotes = new ArrayList<>();

    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState, R.layout.fieldnotes_activity);

        fieldNotes.addAll(DataStore.getFieldNotes());

        final FieldNotesAdapter adapter = new FieldNotesAdapter(fieldNotes, this);
        final RecyclerView view = RecyclerViewProvider.provideRecyclerView(this, R.id.fieldnotes, false, true);
        view.setAdapter(adapter);
    }

    @Override
    public void onClickFieldNote(final CacheFieldNote fieldNote) {
        final Geocache cache = fieldNote.first;
        cache.logVisit(this);
    }

}
