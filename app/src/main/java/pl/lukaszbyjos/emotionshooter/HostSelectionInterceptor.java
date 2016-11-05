package pl.lukaszbyjos.emotionshooter;


import android.net.Uri;

import java.io.IOException;

import okhttp3.HttpUrl;
import okhttp3.Interceptor;
import okhttp3.Request;

/**
 * An interceptor that allows runtime changes to the URL hostname.
 */
public final class HostSelectionInterceptor implements Interceptor {
    private volatile String host;
    private volatile int port;
    private volatile String pathSegment;

    public void setParameters(String fullPath) {
        Uri uri = Uri.parse(fullPath);
        this.host = uri.getHost();
        this.port = uri.getPort();
        this.pathSegment = uri.getEncodedPath();
    }

    @Override
    public okhttp3.Response intercept(Chain chain) throws IOException {
        Request request = chain.request();
        String host = this.host;
        if (host != null) {
            HttpUrl newUrl = request.url().newBuilder()
                    .host(host)
                    .port(port)
                    .addPathSegment(pathSegment)
                    .build();
            request = request.newBuilder()
                    .url(newUrl)
                    .build();
        }
        return chain.proceed(request);
    }
}
