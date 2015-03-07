package cgeo.geocaching.filter;

import cgeo.geocaching.Geocache;

import org.apache.commons.lang3.StringUtils;
import org.eclipse.jdt.annotation.NonNull;

import java.util.ArrayList;
import java.util.List;

class CombinedFilter extends AbstractFilter {

    private final List<IFilter> filters = new ArrayList<>();

    public CombinedFilter(final IFilter... filterComponents) {
        super("Combined filter");
        for (final IFilter filter : filterComponents) {
            if (filter != null) {
                filters.add(filter);
            }
        }
    }

    @Override
    public boolean accepts(@NonNull final Geocache cache) {
        if (filters.isEmpty()) {
            return true;
        }
        for (final IFilter filter : filters) {
            if (!filter.accepts(cache)) {
                return false;
            }
        }
        return true;
    }

    @Override
    @NonNull
    public String getName() {
        return StringUtils.join(filters, ", ");
    }
}
