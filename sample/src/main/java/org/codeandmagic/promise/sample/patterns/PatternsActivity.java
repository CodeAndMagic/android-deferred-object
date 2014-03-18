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

import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.widget.GridView;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import org.codeandmagic.promise.*;
import org.codeandmagic.promise.Either.Left;
import org.codeandmagic.promise.Either.Right;
import org.codeandmagic.promise.sample.R;
import org.codeandmagic.promise.volley.VolleyImagePromise;
import org.codeandmagic.promise.volley.VolleyJsonPromise;
import org.json.JSONArray;
import org.json.JSONException;

import java.util.List;

/**
 * Created by evelina on 18/03/2014.
 */
public class PatternsActivity extends ActionBarActivity {

    public static int sImageSize;
    private static final String URL = "http://www.colourlovers.com/api/patterns/new?format=json";

    private RequestQueue mRequestQueue;
    private ImageAdapter mAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(org.codeandmagic.promise.sample.R.layout.patterns_activity);

        sImageSize = getWindowManager().getDefaultDisplay().getWidth() / 2;

        mRequestQueue = Volley.newRequestQueue(this);
        mAdapter = new ImageAdapter(this);
        ((GridView) findViewById(R.id.grid)).setAdapter(mAdapter);

        doMagic();
    }

    /**
     * The ENTIRE code needed to:
     * 1. Retrieve a JSON
     * 2. Parse the JSON and for each element which contains an URL
     * 2.1. Download an image and add it to the GridView adapter
     */
    //TODO Add caching with Volley ImageLoader
    private void doMagic() {
        VolleyJsonPromise
            .jsonArrayPromise(mRequestQueue, URL)
            .flatMap(new Transformation<JSONArray, Either<Throwable, List<Pattern>>>() {
                @Override
                public Either<Throwable, List<Pattern>> transform(JSONArray value) {
                    try {
                        return new Right<Throwable, List<Pattern>>(PatternDeserializer.getInstance().deserialize(value));
                    } catch (JSONException e) {
                        return new Left<Throwable, List<Pattern>>(e);
                    }
                }
            })
            .split(new Pipe<Pattern, Pattern>() {
                @Override
                public Promise<Pattern> transform(final Pattern pattern) {
                    return VolleyImagePromise
                        .imagePromise(mRequestQueue, pattern.imageUrl, sImageSize, sImageSize, Bitmap.Config.ARGB_8888)
                        .map(new Transformation<Bitmap, Pattern>() {
                            @Override
                            public Pattern transform(Bitmap value) {
                                return pattern.withBitmap(value);
                            }
                        });
                }
            })
            .runOnUiThread()
            .onSuccess(new Callback<Pattern>() {
                @Override
                public void onCallback(Pattern result) {
                    mAdapter.addItem(result);
                }
            });
    }

}
