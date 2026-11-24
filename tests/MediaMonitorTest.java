package vn.douyin.downloader;
public class MediaMonitorTest {
    private static void eq(String a,String b){if(!java.util.Objects.equals(a,b))throw new AssertionError(a+" != "+b);}
    public static void main(String[] args){
        eq(MediaMonitor.describe("https://example.org/a.MP4?token=secret"),"MP4 | example.org");
        eq(MediaMonitor.describe("https://example.org/a.m3u8"),"HLS | example.org");
        eq(MediaMonitor.describe("https://example.org/a.m4s"),"Phân đoạn | example.org");
        eq(MediaMonitor.describe("https://v1.douyinvod.com/a"),"CDN (chưa xác minh loại tệp) | v1.douyinvod.com");
        eq(MediaMonitor.describe("https://douyinvod.com.fake.org/a"),null);
        eq(MediaMonitor.describe("https://example.org/page?x=.mp4"),null);
        eq(MediaMonitor.describe("http://example.org/a.mp4"),null);
        eq(MediaMonitor.describe("blob:https://example.org/a"),null);
        System.out.println("8 media classifier tests passed");
    }
}
