/*
 * Copyright (C) 2012 Soomla Inc.
 *
 *   Licensed under the Apache License, Version 2.0 (the "License");
 *   you may not use this file except in compliance with the License.
 *   You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *   Unless required by applicable law or agreed to in writing, software
 *   distributed under the License is distributed on an "AS IS" BASIS,
 *   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   See the License for the specific language governing permissions and
 *   limitations under the License.
 */

package com.soomla.social.example.util;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.http.AndroidHttpClient;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.ImageView;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.params.HttpClientParams;

import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by oriargov on 5/21/14.
 */
public class ImageUtils {
    public static class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {
        ImageView bmImage;

        public DownloadImageTask(ImageView bmImage) {
            this.bmImage = bmImage;
        }

        protected Bitmap doInBackground(String... urls) {
            String url = urls[0];
            Bitmap bmp = downloadBitmapWithClient(url);

            return bmp;
        }

        // doesn't follow https redirect!
        private Bitmap downloadBitmap(String stringUrl) {
            URL url = null;
            HttpURLConnection connection = null;
            InputStream inputStream = null;

            try {
                url = new URL(stringUrl);
                connection = (HttpURLConnection) url.openConnection();
                connection.setUseCaches(true);
                inputStream = connection.getInputStream();

                return BitmapFactory.decodeStream(new FlushedInputStream(inputStream));
            } catch (Exception e) {
                Log.w("DownloadImageTask", "Error while retrieving bitmap from " + stringUrl, e);
            } finally {
                if (connection != null) {
                    connection.disconnect();
                }
            }

            return null;
        }

        private Bitmap downloadBitmapWithClient(String url) {
            final AndroidHttpClient httpClient = AndroidHttpClient.newInstance("Android");
            HttpClientParams.setRedirecting(httpClient.getParams(), true);
            final HttpGet request = new HttpGet(url);

            try {
                HttpResponse response = httpClient.execute(request);
                final int statusCode = response.getStatusLine().getStatusCode();

                if (statusCode != HttpStatus.SC_OK) {
                    Header[] headers = response.getHeaders("Location");

                    if (headers != null && headers.length != 0) {
                        String newUrl = headers[headers.length - 1].getValue();
                        // call again with new URL
                        return downloadBitmap(newUrl);
                    } else {
                        return null;
                    }
                }

                final HttpEntity entity = response.getEntity();
                if (entity != null) {
                    InputStream inputStream = null;
                    try {
                        inputStream = entity.getContent();

                        // do your work here
                        return BitmapFactory.decodeStream(inputStream);
                    } finally {
                        if (inputStream != null) {
                            inputStream.close();
                        }
                        entity.consumeContent();
                    }
                }
            } catch (Exception e) {
                request.abort();
            } finally {
                if (httpClient != null) {
                    httpClient.close();
                }
            }

            return null;
        }

        protected void onPostExecute(Bitmap result) {
            bmImage.setImageBitmap(result);
        }
    }

    static class FlushedInputStream extends FilterInputStream {
        public FlushedInputStream(InputStream inputStream) {
            super(inputStream);
        }

        @Override
        public long skip(long n) throws IOException {
            long totalBytesSkipped = 0L;
            while (totalBytesSkipped < n) {
                long bytesSkipped = in.skip(n - totalBytesSkipped);
                if (bytesSkipped == 0L) {
                    int byteValue = read();
                    if (byteValue < 0) {
                        break; // we reached EOF
                    } else
                    {
                        bytesSkipped = 1; // we read one byte
                    }
                }
                totalBytesSkipped += bytesSkipped;
            }
            return totalBytesSkipped;
        }
    }
}
