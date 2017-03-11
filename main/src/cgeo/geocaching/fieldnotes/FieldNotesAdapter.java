package cgeo.geocaching.fieldnotes;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import butterknife.BindView;
import cgeo.geocaching.R;
import cgeo.geocaching.log.LogEntry;
import cgeo.geocaching.models.Geocache;
import cgeo.geocaching.ui.recyclerview.AbstractRecyclerViewHolder;
import cgeo.geocaching.utils.Formatter;

class FieldNotesAdapter extends RecyclerView.Adapter<FieldNotesAdapter.FieldNotesViewHolder> {

    @NonNull private final List<CacheFieldNote> logs;
    @NonNull private final FieldNoteClickListener clickListener;

    protected static final class FieldNotesViewHolder extends AbstractRecyclerViewHolder {

        @BindView(R.id.name) TextView name;
        @BindView(R.id.date) TextView date;

        FieldNotesViewHolder(final View itemView) {
            super(itemView);
        }

    }

    FieldNotesAdapter(@NonNull final List<CacheFieldNote> logs, @NonNull final FieldNoteClickListener fieldNoteClickListener) {
        this.logs = logs;
        this.clickListener = fieldNoteClickListener;
    }

    @Override
    public int getItemCount() {
        return logs.size();
    }

    @Override
    public FieldNotesViewHolder onCreateViewHolder(final ViewGroup parent, final int viewType) {
        final View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.fieldnotes_item, parent, false);
        return new FieldNotesViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final FieldNotesViewHolder holder, final int position) {
        final CacheFieldNote fieldNote = logs.get(position);
        final Geocache cache = fieldNote.first;
        final LogEntry logEntry = fieldNote.second;
        holder.name.setText(cache.getName());
        holder.date.setText(Formatter.formatDate(logEntry.date));
        holder.itemView.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(final View v) {
                clickListener.onClickFieldNote(fieldNote);
            }
        });
    }

}
