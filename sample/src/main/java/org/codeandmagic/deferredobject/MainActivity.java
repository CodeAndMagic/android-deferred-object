package org.codeandmagic.deferredobject;

import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import org.codeandmagic.deferredobject.android.DeferredHttpUrlConnection;
import org.codeandmagic.deferredobject.merge.MergedPromiseReject;
import org.codeandmagic.deferredobject.merge.MergedPromiseResult3;
import org.codeandmagic.deferredobject.pipe.ResolvePipe;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

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

        progress1 = (TextView) findViewById(R.id.progress1);
        progress2 = (TextView) findViewById(R.id.progress2);
        progress3 = (TextView) findViewById(R.id.progress3);

        start = (Button) findViewById(R.id.start);
        start.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    startTasks();
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                }
            }
        });

        log = (TextView) findViewById(R.id.log);
    }

    private ResolvePipe<HttpURLConnection, String, Throwable, Float> downloadFilter(final String fileName) {
        return new ResolvePipe<HttpURLConnection, String, Throwable, Float>() {

            final DeferredObject<String, Throwable, Float> downloadPromise = new DeferredObject<String, Throwable, Float>() {
            };

            @Override
            public Promise<String, Throwable, Float> pipeResolved(HttpURLConnection connection) {
                downloadInBackground(connection);
                return downloadPromise;
            }

            void downloadInBackground(final HttpURLConnection connection) {
                new Thread() {
                    @Override
                    public void run() {
                        download(connection);
                    }
                }.start();
            }

            void download(HttpURLConnection connection) {
                InputStream in = null;
                OutputStream out = null;
                try {
                    in = new BufferedInputStream(connection.getInputStream());
                    final int length = connection.getContentLength();
                    int downloaded = 0;
                    final File sdCard = Environment.getExternalStorageDirectory();
                    sdCard.mkdirs();
                    final File path = new File(sdCard, fileName);
                    if (!path.exists()) {
                        path.createNewFile();
                    }
                    out = new BufferedOutputStream(new FileOutputStream(path));
                    final byte[] buf = new byte[1024];
                    downloadPromise.notify(0f);
                    int read = 0;
                    while ((read = in.read(buf)) > 0) {
                        downloaded += read;
                        out.write(buf);
                        if (length > 0){
                            float percent = ((float)downloaded*100)/length;
                            downloadPromise.notify(percent);
                        }
                    }
                    out.flush();
                    downloadPromise.notify(100f);
                    downloadPromise.resolve(path.getAbsolutePath());
                } catch (IOException e) {
                    e.printStackTrace();
                    downloadPromise.reject(e);
                } finally {
                    if (in != null) try {
                        in.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    if (out != null) try {
                        out.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        };
    }

    private ProgressCallback<Float> progressReporter(final TextView t){
        return new ProgressCallback<Float>() {
            @Override
            public void onProgress(final Float p) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        t.setText(String.format("%.2f%%", p));
                    }
                });
            }
        };
    }

    private ResolveCallback<String> successReporter(final TextView t){
        return new ResolveCallback<String>() {
            @Override
            public void onResolve(final String s) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        log.append("\n" + "Downloaded " + s);
                        done(t);
                    }
                });
            }
        };
    }

    private RejectCallback<Throwable> failReporter(final String t){
        return new RejectCallback<Throwable>() {
            @Override
            public void onReject(final Throwable e) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        log.append("\nFailed " + t + " because " + e.getClass().getName() + " " + e.getMessage());
                    }
                });
            }
        };
    }


    private void startTasks() throws MalformedURLException {
        start.setText(getString(R.string.start));
        start.setEnabled(false);
        reset(progress1, progress2, progress3);
        log.setText("");

        Promise<String, Throwable, Float> task1 = new DeferredHttpUrlConnection(new URL(EARTH))
                .pipe(downloadFilter("earth.jpg"))
                .progress(progressReporter(progress1))
                .done(successReporter(progress1))
                .fail(failReporter("task 1"));

        Promise<String, Throwable, Float> task2 = new DeferredHttpUrlConnection(new URL(MARS))
                .pipe(downloadFilter("mars.png"))
                .progress(progressReporter(progress2))
                .done(successReporter(progress2))
                .fail(failReporter("task 2"));

        Promise<String, Throwable, Float> task3 = new DeferredHttpUrlConnection(new URL(VENUS))
                .pipe(downloadFilter("venus.jpg"))
                .progress(progressReporter(progress3))
                .done(successReporter(progress3))
                .fail(failReporter("task 3"));

        DeferredObject.when(task1, task2, task3)
            .done(new ResolveCallback<MergedPromiseResult3<String, String, String>>() {
                @Override
                public void onResolve(MergedPromiseResult3<String, String, String> stringStringStringMergedPromiseResult3) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            start.setText(R.string.all_done);
                            start.setEnabled(true);
                        }
                    });
                }
            }).fail(new RejectCallback<MergedPromiseReject>() {
                @Override
                public void onReject(MergedPromiseReject mergedPromiseReject) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            start.setText(R.string.failure);
                            start.setEnabled(true);
                        }
                    });
                }
            });
    }

    private void done(TextView progress) {
        progress.getCompoundDrawables()[1].setLevel(1);
    }

    private void reset(TextView...progress) {
        for(TextView p : progress) {
            p.getCompoundDrawables()[1].setLevel(0);
            p.setText(R.string.no_progress);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_main, menu);
        return true;
    }
}
