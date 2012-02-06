/*
 * Copyright (c) 2012 Google Inc.
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

package uk.org.ngo.squeezer.itemlists;

import uk.org.ngo.squeezer.R;
import uk.org.ngo.squeezer.framework.SqueezerBaseItemView;
import uk.org.ngo.squeezer.framework.SqueezerItemListActivity;
import uk.org.ngo.squeezer.model.SqueezerMusicFolder;
import android.os.RemoteException;
import android.view.ContextMenu;

public class SqueezerMusicFolderView extends SqueezerBaseItemView<SqueezerMusicFolder> {
    public SqueezerMusicFolderView(SqueezerItemListActivity activity) {
        super(activity);
    }

    public void onItemSelected(int index, SqueezerMusicFolder item) throws RemoteException {
        SqueezerMusicFolderListActivity.show(getActivity(), item);
    };

    public void setupContextMenu(ContextMenu menu, int index, SqueezerMusicFolder item) {
    }

    public String getQuantityString(int quantity) {
        return getActivity().getResources().getQuantityString(R.plurals.musicfolder, quantity);
    }
}
