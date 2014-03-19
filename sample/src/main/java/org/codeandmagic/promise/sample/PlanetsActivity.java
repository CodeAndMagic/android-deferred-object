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

package org.codeandmagic.promise.sample;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import org.codeandmagic.promise.Callback;
import org.codeandmagic.promise.Promise;
import org.codeandmagic.promise.http.DownloadPromise;
import org.codeandmagic.promise.impl.DeferredObject;
import org.codeandmagic.promise.util.TextViewUtils;

import java.io.File;
import java.io.IOException;
import java.net.URL;

import static org.codeandmagic.promise.sample.Utils.getSdCardFile;

public class PlanetsActivity extends ActionBarActivity {

    private final static String EARTH = "https://farm3.staticflickr.com/2116/2222523978_f48bf28571_o.jpg";
    private final static String MARS = "https://farm6.staticflickr.com/5328/6897598788_a748a78bb8_o.png";
    private final static String VENUS = "https://farm8.staticflickr.com/7233/7302574832_ed3fa543b2_o.jpg";

    private TextView mProgress1, mProgress2, mProgress3, mLog;
    private Button mStart;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.planets_activity);

        mLog = (TextView) findViewById(R.id.log);

        mProgress1 = (TextView) findViewById(R.id.progress1);
        mProgress2 = (TextView) findViewById(R.id.progress2);
        mProgress3 = (TextView) findViewById(R.id.progress3);

        mStart = (Button) findViewById(R.id.start);
        mStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startTasks();
            }
        });
    }

    private void startTasks() {
        final Promise<File> promise1 = createPromise(EARTH, "Earth", mProgress1);
        final Promise<File> promise2 = createPromise(MARS, "Mars", mProgress2);
        final Promise<File> promise3 = createPromise(VENUS, "Venus", mProgress3);

        DeferredObject.merge(File.class, promise1, promise2, promise3)
            .runOnUiThread()
            .onSuccess(new Callback<File[]>() {
                @Override
                public void onCallback(File[] result) {
                    mStart.setText(R.string.all_done);
                    mStart.setEnabled(true);
                    reset(mProgress1, mProgress2, mProgress3);
                }
            })
            .onFailure(new Callback<Throwable>() {
                @Override
                public void onCallback(Throwable result) {
                    mStart.setText(R.string.failure);
                    mStart.setEnabled(true);
                }
            })
            .onProgress(new Callback<Float>() {
                @Override
                public void onCallback(Float result) {
                    mLog.append(" (" + result + " out of 3)");
                }
            });

    }

    private void done(TextView progress) {
        progress.getCompoundDrawables()[1].setLevel(1);
    }

    private void reset(TextView... progress) {
        for (TextView p : progress) {
            p.getCompoundDrawables()[1].setLevel(0);
            p.setText(R.string.no_progress);
        }
    }

    private Promise<File> createPromise(String url, String fileName, TextView progress) {
        try {
            return new DownloadPromise(new URL(url), getSdCardFile(fileName))
                .runOnUiThread()
                .onSuccess(successCallback(progress))
                .onFailure(failureCallback(url))
                .onProgress(TextViewUtils.setPercent(progress));
        } catch (IOException e) {
            mLog.setText("Can't mStart download " + url + " due to exception: " + e.getMessage());
            return null;
        }
    }

    private Callback<File> successCallback(final TextView textView) {
        return new Callback<File>() {
            @Override
            public void onCallback(File result) {
                mLog.append("\nFile stored in " + result.getPath());
                done(textView);
            }
        };
    }

    private Callback<Throwable> failureCallback(final String fileName) {
        return new Callback<Throwable>() {
            @Override
            public void onCallback(Throwable e) {
                mLog.append("\nFailed " + fileName + " because " + e.getClass().getName() + " " + e.getMessage());
            }
        };
    }
}
