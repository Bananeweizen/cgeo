package cgeo.geocaching.filter;

import org.eclipse.jdt.annotation.NonNull;

import java.util.Locale;

abstract class AbstractRangeFilter extends AbstractFilter {

    protected final float rangeMin;
    protected final float rangeMax;

    protected AbstractRangeFilter(final int ressourceId, final float rangeMin, final float rangeMax) {
        super(ressourceId);
        this.rangeMin = rangeMin;
        this.rangeMax = rangeMax;
    }

    protected boolean isInRange(final float value) {
        return rangeMin <= value && value <= rangeMax;
    }

    @Override
    @NonNull
    public String getName() {
        return super.getName() + ' ' + formatNumber(rangeMin) + '…' + formatNumber(rangeMax);
    }

    private static String formatNumber(final float value) {
        if (Math.abs(value - Math.round(value)) < 0.01) {
            return String.valueOf(Math.round(value));
        }
        return String.format(Locale.getDefault(), "%1.1f", value);
    }

}