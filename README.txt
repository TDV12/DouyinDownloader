DOUYIN DOWNLOADER 3.0 — Android 10+

BUILD APK KHÔNG CẦN ANDROID STUDIO
1. Giải nén ZIP, upload toàn bộ nội dung ở thư mục gốc repository GitHub, gồm .github/workflows/build.yml.
2. Commit changes; vào Actions > Build Android APK.
3. Mở lượt build thành công > Artifacts > DouyinDownloader-APK, giải nén APK.
4. Nếu Android báo chữ ký không khớp với bản debug trước, gỡ bản cũ trước khi cài bản mới (có thể mất cài đặt ứng dụng).

SỬ DỤNG NETWORK MONITOR
1. Dán toàn bộ nội dung chia sẻ Douyin, bấm Phân tích liên kết / xem video.
2. Nếu trang cho phép, bấm phát video trong trang xem trước.
3. Bấm Quét nguồn video / Network Monitor để xem số yêu cầu nghi là MP4, HLS, phân đoạn và CDN; số thẻ video, đang phát, và nguồn blob.
4. Bấm Tải video nếu app xác nhận có nguồn được phép tải hoặc có tệp HTTPS MP4 trực tiếp được phép lưu. Nếu không, kiểm tra chức năng lưu chính thức của Douyin.

Network Monitor là công cụ chẩn đoán: nhãn loại tài nguyên và tên miền, không hiển thị đường dẫn đầy đủ, query token hay cookie. Không phát lại request, ghép HLS, loại watermark hoặc vượt quyền tải. URL phát video không phải lúc nào cũng là URL tải. Một số request (blob, media nội bộ, chuyển hướng) có thể không xuất hiện. Không cần VPS, không gửi nhật ký lên máy chủ riêng. WebView vẫn cần Internet để mở Douyin.

Kiểm thử cục bộ (dữ liệu giả lập):
  node tests/parser.test.js
  node tests/monitor.test.js
  javac -d /tmp/douyin-test app/src/main/java/vn/douyin/downloader/MediaMonitor.java tests/MediaMonitorTest.java
  java -cp /tmp/douyin-test vn.douyin.downloader.MediaMonitorTest
Chưa build APK thực tế hoặc thử trên Douyin/điện thoại. Workflow SDK được giữ từ V2.1.
