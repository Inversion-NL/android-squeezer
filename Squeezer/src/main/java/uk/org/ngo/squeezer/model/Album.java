/*
 * Copyright (c) 2011 Kurt Aaholst <kaaholst@gmail.com>
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

package uk.org.ngo.squeezer.model;

import android.net.Uri;
import android.os.Parcel;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.google.common.base.Strings;

import java.util.Map;

import uk.org.ngo.squeezer.Util;
import uk.org.ngo.squeezer.framework.ArtworkItem;

public class Album extends ArtworkItem {

    @Override
    public String getPlaylistTag() {
        return "album_id";
    }

    @Override
    public String getFilterTag() {
        return "album_id";
    }

    private String name;

    @Override
    public String getName() {
        return name;
    }

    @NonNull
    private Uri mArtworkUrl = Uri.EMPTY;

    @NonNull
    public Uri getArtworkUrl() {
        return mArtworkUrl;
    }

    public void setArtworkUrl(@NonNull Uri artworkUrl) {
        mArtworkUrl = artworkUrl;
    }

    public Album setName(String name) {
        this.name = name;
        return this;
    }

    private String artist;

    public String getArtist() {
        return artist;
    }

    public void setArtist(String model) {
        this.artist = model;
    }

    private int year;

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public Album(String albumId, String album) {
        setId(albumId);
        setName(album);
    }

    public Album(Map<String, Object> record) {
        setId(getString(record, record.containsKey("album_id") ? "album_id" : "id"));
        setName(getString(record, "album"));
        setArtist(getString(record, "artist"));
        setYear(getInt(record, "year"));
        setArtwork_track_id(getString(record, "artwork_track_id"));
        mArtworkUrl = Uri.parse(getStringOrEmpty(record, "artwork_url"));
    }

    public static final Creator<Album> CREATOR = new Creator<Album>() {
        @Override
        public Album[] newArray(int size) {
            return new Album[size];
        }

        @Override
        public Album createFromParcel(Parcel source) {
            return new Album(source);
        }
    };

    public Album() {

    }

    private Album(Parcel source) {
        setId(source.readString());
        name = source.readString();
        artist = source.readString();
        year = source.readInt();
        setArtwork_track_id(source.readString());
        mArtworkUrl = Uri.parse(Strings.nullToEmpty(source.readString()));
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(getId());
        dest.writeString(name);
        dest.writeString(artist);
        dest.writeInt(year);
        dest.writeString(getArtwork_track_id());
        dest.writeString(mArtworkUrl.toString());
    }

    @Override
    public String toStringOpen() {
        return super.toStringOpen() + ", artist: " + artist + ", year: " + year;
    }

}
