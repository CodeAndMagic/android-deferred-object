package org.codeandmagic.promise.sample;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import org.codeandmagic.promise.Callback;
import org.codeandmagic.promise.SimpleDeferredObject;
import org.codeandmagic.promise.SimplePromise;
import org.codeandmagic.promise.impl.DeferredDownloader;

import java.io.File;
import java.io.IOException;
import java.net.URL;

import static android.os.Environment.*;

public class MainActivity extends ActionBarActivity {

    private final static String EARTH = "https://farm3.staticflickr.com/2116/2222523978_f48bf28571_o.jpg";
    private final static String MARS = "https://farm6.staticflickr.com/5328/6897598788_a748a78bb8_o.png";
    private final static String VENUS = "https://farm8.staticflickr.com/7233/7302574832_ed3fa543b2_o.jpg";

    private TextView progress1, progress2, progress3, log;
    private Button start;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        log = (TextView) findViewById(R.id.log);

        progress1 = (TextView) findViewById(R.id.progress1);
        progress2 = (TextView) findViewById(R.id.progress2);
        progress3 = (TextView) findViewById(R.id.progress3);

        start = (Button) findViewById(R.id.start);
        start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startTasks();
            }
        });
    }

    private void startTasks() {
        final SimplePromise<File> promise1 = createPromise(EARTH, "Earth", progress1);
        final SimplePromise<File> promise2 = createPromise(MARS, "Mars", progress2);
        final SimplePromise<File> promise3 = createPromise(VENUS, "Venus", progress3);

        SimpleDeferredObject.merge(File.class, promise1, promise2, promise3)
                .runOnUiThread()
                .onSuccess(new Callback<File[]>() {
                    @Override
                    public void onCallback(File[] result) {
                        start.setText(R.string.all_done);
                        start.setEnabled(true);
                    }
                })
                .onFailure(new Callback<Throwable>() {
                    @Override
                    public void onCallback(Throwable result) {
                        start.setText(R.string.failure);
                        start.setEnabled(true);
                    }
                })
                .onProgress(new Callback<Float>() {
                    @Override
                    public void onCallback(Float result) {
                        log.append("Downloaded " + result + " out of 3.");
                    }
                });

    }

    private File getSdCardOutput(String fileName) throws IOException {
        final String sdCardState = getExternalStorageState();
        if (MEDIA_MOUNTED.equals(sdCardState)) {
            final File sdCard = getExternalStorageDirectory();
            sdCard.mkdirs();
            final File path = new File(sdCard, fileName);
            if (!path.exists()) {
                path.createNewFile();
            }
            return path;
        }
        throw new IOException("Can't write on the SDCard!");
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

    private SimplePromise<File> createPromise(String url, String fileName, TextView progress) {
        try {
            return new DeferredDownloader(new URL(url), getSdCardOutput(fileName))
                    .runOnUiThread()
                    .onSuccess(successCallback(progress))
                    .onFailure(failureCallback(url))
                    .onProgress(progressCallback(progress));
        } catch (IOException e) {
            log.setText("Can't start download " + url + " due to exception: " + e.getMessage());
            return null;
        }
    }

    private Callback<File> successCallback(final TextView textView) {
        return new Callback<File>() {
            @Override
            public void onCallback(File result) {
                log.append("\nDownloaded file " + result.getPath());
                done(textView);
            }
        };
    }

    private Callback<Throwable> failureCallback(final String fileName) {
        return new Callback<Throwable>() {
            @Override
            public void onCallback(Throwable e) {
                log.append("\nFailed " + fileName + " because " + e.getClass().getName() + " " + e.getMessage());
            }
        };
    }

    private Callback<Float> progressCallback(final TextView textView) {
        return new Callback<Float>() {
            @Override
            public void onCallback(Float result) {
                textView.setText(String.format("%.2f%%", result));
            }
        };
    }

}
