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

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.Volley;
import org.codeandmagic.promise.sample.R;
import org.codeandmagic.promise.volley.BitmapLruCache;
import org.codeandmagic.promise.volley.ImageResult;
import org.codeandmagic.promise.volley.VolleyImagePromise;
import org.codeandmagic.promise.volley.VolleyJsonPromise;

import static org.codeandmagic.promise.Either.trying;

/**
 * Created by evelina on 18/03/2014.
 */
public class PatternsActivity extends ActionBarActivity {

    public static int sImageSize;
    private static final String URL = "http://www.colourlovers.com/api/patterns/new?format=json";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sImageSize = getWindowManager().getDefaultDisplay().getWidth() / 2;

        if (savedInstanceState == null) {
            getSupportFragmentManager()
                .beginTransaction()
                .replace(android.R.id.content, PatternsFragment.newInstance())
                .commit();
        }
    }


    private static class PatternsFragment extends Fragment {

        private RequestQueue mRequestQueue;
        private ImageLoader mImageLoader;
        private ImageAdapter mAdapter;

        public static PatternsFragment newInstance() {
            return new PatternsFragment();
        }

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setRetainInstance(true);
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            return inflater.inflate(R.layout.patterns_fragment, container, false);
        }

        @Override
        public void onActivityCreated(Bundle savedInstanceState) {
            super.onActivityCreated(savedInstanceState);
            if (mAdapter == null) {
                mRequestQueue = Volley.newRequestQueue(getActivity());
                mImageLoader = new ImageLoader(mRequestQueue, new BitmapLruCache());
                mAdapter = new ImageAdapter(getActivity());
                ((GridView) getView().findViewById(R.id.grid)).setAdapter(mAdapter);
                doMagic();
            } else {
                ((GridView) getView().findViewById(R.id.grid)).setAdapter(mAdapter);
            }
        }

        /**
         * The ENTIRE code needed to:
         * 1. Retrieve a JSON
         * 2. Parse the JSON and for each element which contains an URL
         * 2.1. Download an image and add it to the GridView adapter
         */
        private void doMagic() {
            VolleyJsonPromise
                .jsonArrayPromise(mRequestQueue, URL)
                .flatMap(value -> trying(() -> PatternDeserializer.getInstance().deserialize(value)))
                .split((Pattern pattern) -> VolleyImagePromise
                        .imagePromise(mImageLoader, pattern.imageUrl, sImageSize, sImageSize)
                        .map(result -> pattern.withBitmap(result.imageContainer.getBitmap()))
                )
                .runOnUiThread()
                .onSuccess(mAdapter::addItem);
        }

        private void logCached(ImageResult result) {
            Log.i("PROMISE", result.imageContainer.getRequestUrl() + (result.fromCache ? " is cached." : " is NOT cached."));
        }
    }

}
