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

package org.codeandmagic.promise.http;

import org.codeandmagic.promise.impl.AbstractPromise;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * Created by evelina on 23/02/2014.
 */
public class DownloadPromise extends AbstractPromise<File> {

    private static final ThreadPoolExecutor DEFAULT_EXECUTOR = new ScheduledThreadPoolExecutor(4);

    private final URL mUrl;
    private final File mOutput;
    private final ThreadPoolExecutor mExecutor;

    public DownloadPromise(final URL url, final File output) {
        this(url, output, DEFAULT_EXECUTOR);
    }

    public DownloadPromise(final URL url, final File output, final ThreadPoolExecutor executor) {
        mUrl = url;
        mOutput = output;
        mExecutor = executor;

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
