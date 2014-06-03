package cgeo.geocaching.activity;

import cgeo.geocaching.DataStore;
import cgeo.geocaching.R;
import cgeo.geocaching.list.StoredList;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.TextView;

/**
 * Adapter to display navigation items in the navigation drawer with text and icon.
 *
 */
public class NavigationDrawerAdapter extends BaseExpandableListAdapter {
    private final Context context;
    private final LayoutInflater inflater;

    public NavigationDrawerAdapter(final Context context) {
        this.context = context;
        inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getGroupCount() {
        return NavigationDrawerItem.ITEMS.size();
    }

    @Override
    public int getChildrenCount(final int groupPosition) {
        if (groupPosition == NavigationDrawerItem.STORED.getIndex()) {
            return DataStore.getLists().size();
        }
        return 0;
    }

    @Override
    public Object getGroup(final int groupPosition) {
        return NavigationDrawerItem.ITEMS.get(groupPosition);
    }

    @Override
    public Object getChild(final int groupPosition, final int childPosition) {
        return DataStore.getLists().get(childPosition);
    }

    @Override
    public long getGroupId(final int groupPosition) {
        return groupPosition;
    }

    @Override
    public long getChildId(final int groupPosition, final int childPosition) {
        return groupPosition * 100 + childPosition;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public View getGroupView(final int groupPosition, final boolean isExpanded, final View convertView, final ViewGroup parent) {
        final View rowView = inflater.inflate(R.layout.navigation_drawer_item, parent, false);

        final TextView labelView = (TextView) rowView.findViewById(android.R.id.text1);
        final NavigationDrawerItem item = NavigationDrawerItem.ITEMS.get(groupPosition);
        labelView.setText(item.getLabelResource());

        final Drawable drawable = context.getResources().getDrawable(item.getIconResource());
        labelView.setCompoundDrawablesWithIntrinsicBounds(drawable, null, null, null);

        return rowView;
    }

    @Override
    public View getChildView(final int groupPosition, final int childPosition, final boolean isLastChild, final View convertView, final ViewGroup parent) {
        final View rowView = inflater.inflate(R.layout.navigation_drawer_stored_item, parent, false);

        final TextView labelView = (TextView) rowView.findViewById(android.R.id.text1);
        final StoredList storedList = DataStore.getLists().get(childPosition);
        labelView.setText(storedList.getTitleAndCount());

        return rowView;
    }

    @Override
    public boolean isChildSelectable(final int groupPosition, final int childPosition) {
        return true;
    }
}
