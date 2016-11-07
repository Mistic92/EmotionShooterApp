package pl.lukaszbyjos.emotionshooter;


import android.net.Uri;

import java.io.IOException;
import java.util.List;

import okhttp3.HttpUrl;
import okhttp3.Interceptor;
import okhttp3.Request;

/**
 * An interceptor that allows runtime changes to the URL hostname.
 */
public final class HostSelectionInterceptor implements Interceptor {
    private volatile String host;
    private volatile int port;
    private volatile List<String> pathSegment;
    private volatile String scheme;

    public void setParameters(String fullPath) {
        Uri uri = Uri.parse(fullPath);
        this.host = uri.getHost();
        this.port = uri.getPort();
        this.scheme = uri.getScheme();
        this.pathSegment = uri.getPathSegments();
    }

    @Override
    public okhttp3.Response intercept(Chain chain) throws IOException {
        Request request = chain.request();
        String host = this.host;
        if (host != null) {
            HttpUrl.Builder builder = request.url().newBuilder()
                    .host(host);
            if (pathSegment != null && !pathSegment.isEmpty())
                for (int i = 0; i < pathSegment.size(); i++) {
                    builder.setEncodedPathSegment(i, pathSegment.get(i));
                }
            if (port > 0)
                builder.port(port);
            else
                builder.port(8080);
            builder.scheme(scheme);

            HttpUrl newUrl = builder.build();
            request = request.newBuilder()
                    .url(newUrl)
                    .build();
        }
        return chain.proceed(request);
    }
}
