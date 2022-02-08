package com.bedessee.salesca;

import android.os.Build;
import android.os.StrictMode;
import android.util.Log;

import androidx.multidex.MultiDexApplication;

import com.tonyodev.fetch2.Fetch;
import com.tonyodev.fetch2.FetchConfiguration;
import com.tonyodev.fetch2.HttpUrlConnectionDownloader;
import com.tonyodev.fetch2.NetworkType;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Method;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;
import java.util.Map;

import okhttp3.OkHttpClient;
import timber.log.Timber;

public class BedesseeApp extends MultiDexApplication {
    public Fetch fetchManager;

    @Override
    public void onCreate() {
        super.onCreate();
        Timber.plant(new Timber.DebugTree());

        if (Build.VERSION.SDK_INT >= 24) {
            try {
                @SuppressWarnings("JavaReflectionMemberAccess")
                Method m = StrictMode.class.getMethod("disableDeathOnFileUriExposure");
                m.invoke(null);
            } catch (Exception e) {
                Timber.e(Log.getStackTraceString(e));
            }
        }

        FetchConfiguration fetchConfiguration = new FetchConfiguration.Builder(this)
                .setDownloadConcurrentLimit(3)
                .setHttpDownloader(new customDownloader())
                .setGlobalNetworkType(NetworkType.ALL)
                .enableAutoStart(true)
                .enableLogging(true)
                .build();


        fetchManager = Fetch.Impl.getInstance(fetchConfiguration);
        fetchManager.deleteAll();

    }

    static class customDownloader extends HttpUrlConnectionDownloader {
        @Nullable
        @Override
        public Void onPreClientExecute(@NotNull HttpURLConnection client, @NotNull ServerRequest request) {
            super.onPreClientExecute(client, request);
            try {
                final URL url = new URL(request.getUrl());
                final HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setInstanceFollowRedirects(true);
                for (String key : request.getHeaders().keySet()) {
                    connection.addRequestProperty(key, request.getHeaders().get(key));
                }
                connection.setInstanceFollowRedirects(true);
                connection.connect();

                final Map<String, List<String>> responseHeaders = connection.getHeaderFields();
                final List<String> rangeResponseHeaders = responseHeaders.get("Accept-Ranges");
                boolean acceptRanges = false;

                if (connection.getResponseCode() == HttpURLConnection.HTTP_PARTIAL ||
                        (rangeResponseHeaders != null && rangeResponseHeaders.size() > 0 &&
                                "bytes".equals(rangeResponseHeaders.get(0)))) {
                    acceptRanges = true;
                }

                if (!acceptRanges) {
                    client.addRequestProperty("Range", ""); // or client.addRequestProperty("Range", null);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }
    }
}