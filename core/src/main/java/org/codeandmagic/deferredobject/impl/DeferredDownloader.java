package org.codeandmagic.deferredobject.impl;

import org.codeandmagic.deferredobject.AbstractSimplePromise;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * Created by evelina on 23/02/2014.
 */
public class DeferredDownloader extends AbstractSimplePromise<File> {

    private static final ThreadPoolExecutor DEFAULT_EXECUTOR = new ScheduledThreadPoolExecutor(4);

    private final URL url;
    private final File output;
    private final ThreadPoolExecutor executor;

    public DeferredDownloader(final URL url, final File output) {
        this(url, output, DEFAULT_EXECUTOR);
    }

    public DeferredDownloader(final URL url, final File output, final ThreadPoolExecutor executor) {
        this.url = url;
        this.output = output;
        this.executor = executor;

        executor.submit(new Runnable() {
            @Override
            public void run() {
                InputStream in = null;
                OutputStream out = null;

                try {
                    final HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                    final int length = connection.getContentLength();
                    final byte[] buff = new byte[1024];

                    in = new BufferedInputStream(connection.getInputStream());
                    out = new BufferedOutputStream(new FileOutputStream(output));

                    progress(0f);

                    int downloaded = 0;
                    int read = 0;
                    while ((read = in.read(buff)) > 0) {
                        downloaded += read;
                        out.write(buff);
                        if (length > 0) {
                            float percent = ((float) downloaded * 100) / length;
                            progress(percent);
                        }
                    }
                    out.flush();

                    progress(100f);
                    success(output);

                } catch (IOException e) {
                    failure(e);
                } finally {
                    close(in);
                    close(out);
                }
            }
        });
    }

    private void close(Closeable stream) {
        if (stream != null) {
            try {
                stream.close();
            } catch (IOException e) {
            }
        }
    }

}
