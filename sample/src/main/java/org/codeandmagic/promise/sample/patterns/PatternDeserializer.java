/*
 * Copyright (c) 2014 CodeAndMagic
 * Cristian Vrabie, Evelina Vrabie
 *
 * This file is part of android-promise.
 * android-promise is free software: you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as published by the
 * Free Software Foundation, either version 3 of the License,or (at your option)
 * any later version.
 *
 * android-promise is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
 * or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with android-promise. If not, see <http://www.gnu.org/licenses/>.
 */

package org.codeandmagic.promise.sample.patterns;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by evelina on 18/03/2014.
 */
public class PatternDeserializer {

    private static final PatternDeserializer INSTANCE = new PatternDeserializer();

    public static final PatternDeserializer getInstance() {
        return INSTANCE;
    }

    public List<Pattern> deserialize(JSONArray jsonArray) throws JSONException {
        final int length = jsonArray.length();
        if (length == 0) return Collections.emptyList();

        final List<Pattern> patterns = new ArrayList<Pattern>();
        for (int i = 0; i < length; ++i) {
            patterns.add(deserialize(jsonArray.getJSONObject(i)));
        }
        return patterns;
    }

    public Pattern deserialize(JSONObject jsonObject) throws JSONException {
        final String id = jsonObject.getString("id");
        final String title = jsonObject.getString("title");
        final String imageUrl = jsonObject.getString("imageUrl");
        return new Pattern(id, title, imageUrl, null);
    }
}
