/*
 * Copyright (c) 2014 Kurt Aaholst <kaaholst@gmail.com>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package uk.org.ngo.squeezer.itemlist;

import android.util.Log;
import android.view.ContextMenu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.TextView;

import com.google.common.base.Joiner;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Multimap;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import uk.org.ngo.squeezer.R;
import uk.org.ngo.squeezer.framework.ItemAdapter;
import uk.org.ngo.squeezer.model.Player;
import uk.org.ngo.squeezer.model.Song;
import uk.org.ngo.squeezer.util.ImageFetcher;

class PlayerListAdapter extends BaseExpandableListAdapter implements View.OnCreateContextMenuListener {

    private final PlayerListActivity mActivity;

    private final List<ItemAdapter<Player>> mChildAdapters = new ArrayList<ItemAdapter<Player>>();

    /** The group position of the item that was most recently selected. */
    private int mLastGroupPosition;

    private Player mActivePlayer;

    private List<Player> mPlayers = new ImmutableList.Builder<Player>().build();

    private final ImageFetcher mImageFetcher;

    /** Joins elements together with ' - ', skipping nulls. */
    private static final Joiner mJoiner = Joiner.on(" - ").skipNulls();

    /** Count of how many players are in the adapter. */
    private int mPlayerCount;

    public PlayerListAdapter(PlayerListActivity activity, ImageFetcher imageFetcher) {
        mActivity = activity;
        mImageFetcher = imageFetcher;
    }

    public void onChildClick(int groupPosition, int childPosition) {
        mChildAdapters.get(groupPosition).onItemSelected(childPosition);
    }

    public void clear() {
        mChildAdapters.clear();
        mPlayerCount = 0;
        notifyDataSetChanged();
    }

    /**
     * Sets the players in to the adapter.
     *
     * @param playerSyncGroups Multimap, mapping from the player ID of the syncmaster to the
     *     Players synced to that master. See
     *     {@link PlayerListActivity#updateSyncGroups(List, Player)} for how this map is
     *     generated.
     */
    public void setSyncGroups(Multimap<String, Player> playerSyncGroups) {
        clear();

        List<String> masters = new ArrayList<String>(playerSyncGroups.keySet());
        Log.v("PlayerListAdapter", masters.toString());
        Collections.sort(masters);

        for (String masterId : masters) {
            ItemAdapter<Player> childAdapter = new ItemAdapter<Player>(new PlayerView(mActivity), mImageFetcher);

            List<Player> slaves = new ArrayList<Player>(playerSyncGroups.get(masterId));
            mPlayerCount += slaves.size();
            Collections.sort(slaves, Player.compareById);
            childAdapter.update(slaves.size(), 0, slaves);
            mChildAdapters.add(childAdapter);
        }

        notifyDataSetChanged();
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        ExpandableListView.ExpandableListContextMenuInfo contextMenuInfo = (ExpandableListView.ExpandableListContextMenuInfo) menuInfo;
        long packedPosition = contextMenuInfo.packedPosition;
        if (ExpandableListView.getPackedPositionType(packedPosition)
                == ExpandableListView.PACKED_POSITION_TYPE_CHILD) {
            int groupPosition = ExpandableListView.getPackedPositionGroup(packedPosition);
            int childPosition = ExpandableListView.getPackedPositionChild(packedPosition);

            AdapterView.AdapterContextMenuInfo adapterContextMenuInfo
                    = new AdapterView.AdapterContextMenuInfo(
                    contextMenuInfo.targetView, childPosition, contextMenuInfo.id);

            mChildAdapters.get(groupPosition).onCreateContextMenu(menu, v, adapterContextMenuInfo);

            // Enable player sync menu options if there's more than one player.
            if (mPlayerCount > 1) {
                menu.findItem(R.id.player_sync).setVisible(true);
            }
        }
    }

    public boolean doItemContext(MenuItem menuItem, int groupPosition, int childPosition) {
        mLastGroupPosition = groupPosition;
        return mChildAdapters.get(groupPosition).doItemContext(menuItem, childPosition);
    }

    /**
     * Handle sub menu items of context menus.
     *
     * @param menuItem
     * @return
     */
    public boolean doItemContext(MenuItem menuItem) {
        return mChildAdapters.get(mLastGroupPosition).doItemContext(menuItem);
    }

    @Override
    public boolean areAllItemsEnabled() {
        return true; // Should be false, but then there is no divider
    }

    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
        return mChildAdapters.get(groupPosition).getView(childPosition, convertView, parent);
    }

    @Override
    public int getGroupCount() {
        return mChildAdapters.size();
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return mChildAdapters.get(groupPosition).getCount();
    }

    @Override
    public Object getGroup(int groupPosition) {
        return mChildAdapters.get(groupPosition);
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        return mChildAdapters.get(groupPosition).getItem(childPosition);
    }

    /**
     * Generates a 64 bit group ID by using the 48 bits of the ID of the first player in
     * the group OR'd with 0x00FF000000000000.
     * <p/>
     * {@inheritDoc}
     * @param groupPosition
     * @return
     */
    @Override
    public long getGroupId(int groupPosition) {
        return mChildAdapters.get(groupPosition).getItem(0).getIdAsLong() | 0x00FF000000000000L;
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return mChildAdapters.get(groupPosition).getItem(childPosition).getIdAsLong();
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
        View row = mActivity.getLayoutInflater().inflate(R.layout.group_player, parent, false);

        TextView text1 = (TextView) row.findViewById(R.id.text1);
        TextView text2 = (TextView) row.findViewById(R.id.text2);

        ItemAdapter<Player> adapter = mChildAdapters.get(groupPosition);
        List<String> playerNames = new ArrayList<String>();
        for (int i = 0; i < adapter.getCount(); i++) {
            Player p = adapter.getItem(i);
            playerNames.add(p.getName());
        }
        String header = Joiner.on(", ").join(playerNames);
        text1.setText(mActivity.getString(R.string.player_group_header, header));

        Song groupSong = adapter.getItem(0).getPlayerState().getCurrentSong();

        if (groupSong != null) {
            text2.setText(mJoiner.join(groupSong.getName(), groupSong.getArtist(),
                    groupSong.getAlbumName()));
        }
        return row;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return false;
    }
}
