/*
 * Copyright (c) 2015 Google Inc.  All Rights Reserved.
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

import com.bluelinelabs.logansquare.annotation.JsonField;
import com.bluelinelabs.logansquare.annotation.JsonObject;
import com.google.common.base.Objects;

import java.util.List;

/**
 * Represents a request to an LMS.
 */
@JsonObject
public class ClientPlayersResult {
    @JsonField
    public int count;

    @JsonField(name = "players_loop")
    public List<Player> players;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ClientPlayersResult that = (ClientPlayersResult) o;
        return Objects.equal(count, that.count) &&
                Objects.equal(players, that.players);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(count, players);
    }
}
