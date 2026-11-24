package vn.douyin.downloader;

import java.net.URI;
import java.util.Locale;

/** Diagnostic labels only: no URL paths, query tokens, cookies, or request replay. */
final class MediaMonitor {
    private MediaMonitor() {}
    static String describe(String url) {
        try {
            URI u = URI.create(url);
            if (!"https".equalsIgnoreCase(u.getScheme()) || u.getHost() == null || u.getUserInfo()!=null) return null;
            String host = u.getHost().toLowerCase(Locale.ROOT);
            String path = u.getPath()==null?"":u.getPath().toLowerCase(Locale.ROOT);
            String type;
            if (path.endsWith(".mp4")) type="MP4";
            else if (path.endsWith(".m3u8")) type="HLS";
            else if (path.endsWith(".m4s") || path.endsWith(".ts")) type="Phân đoạn";
            else if (host.endsWith(".douyinvod.com") || host.equals("douyinvod.com") || host.endsWith(".bytecdn.cn") || host.endsWith(".bytevcloudcdn.com")) type="CDN (chưa xác minh loại tệp)";
            else return null;
            return type + " | " + host;
        } catch (RuntimeException e) { return null; }
    }
}
