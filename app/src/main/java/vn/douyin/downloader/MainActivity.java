package vn.douyin.downloader;

import android.app.*;
import android.content.*;
import android.net.Uri;
import android.os.*;
import android.view.*;
import android.webkit.*;
import android.widget.*;
import org.json.JSONObject;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.Locale;
import java.util.regex.*;

public class MainActivity extends Activity {
    private EditText input;
    private WebView web;
    private TextView status;
    private Button download;
    private String candidate, script, lastReason="no_metadata";
    private boolean pending, blocked;
    private int generation, requestId;
    private final Handler handler = new Handler(Looper.getMainLooper());
    private static final Pattern URL = Pattern.compile("https://[^\\s<>\\[\\]()*，。！；、]+",Pattern.CASE_INSENSITIVE);

    @Override public void onCreate(Bundle b) {
        super.onCreate(b);
        LinearLayout root=new LinearLayout(this); root.setOrientation(LinearLayout.VERTICAL);
        root.setPadding(20,20,20,20); root.setFitsSystemWindows(true); setContentView(root);
        TextView title=new TextView(this); title.setText("Douyin Downloader 2.1"); title.setTextSize(25); root.addView(title);
        input=new EditText(this); input.setHint("Dán nguyên văn nội dung chia sẻ Douyin"); input.setMaxLines(3); root.addView(input);
        button(root,"Dán nội dung chia sẻ",()->{ClipboardManager c=(ClipboardManager)getSystemService(CLIPBOARD_SERVICE); if(c.hasPrimaryClip())input.setText(c.getPrimaryClip().getItemAt(0).coerceToText(this));});
        button(root,"Phân tích liên kết / xem video",this::analyze);
        download=button(root,"Tải video",()->{if(candidate!=null&&!blocked)confirm(candidate);else analyze();});
        input.addTextChangedListener(new android.text.TextWatcher(){public void beforeTextChanged(CharSequence s,int start,int count,int after){} public void onTextChanged(CharSequence s,int start,int before,int count){candidate=null;blocked=false;pending=false;generation++;requestId++;} public void afterTextChanged(android.text.Editable e){}});
        button(root,"Mở trong ứng dụng Douyin / trình duyệt",()->{String u=url();if(u!=null)external(u);});
        button(root,"Video đã tải",()->{try{startActivity(new Intent(DownloadManager.ACTION_VIEW_DOWNLOADS));}catch(Exception e){say("Không mở được danh sách tải.");}});
        status=new TextView(this); status.setText("Dán liên kết rồi bấm Phân tích hoặc Tải video."); status.setPadding(0,8,0,8);root.addView(status);
        web=new WebView(this); WebSettings s=web.getSettings(); s.setJavaScriptEnabled(true);s.setDomStorageEnabled(true);s.setAllowFileAccess(false);s.setAllowContentAccess(false);s.setMixedContentMode(WebSettings.MIXED_CONTENT_NEVER_ALLOW);
        web.setWebViewClient(new WebViewClient(){
            @Override public boolean shouldOverrideUrlLoading(WebView v,WebResourceRequest r){return !"https".equalsIgnoreCase(r.getUrl().getScheme());}
            @Override public void onPageStarted(WebView v,String u,android.graphics.Bitmap icon){candidate=null;blocked=false;generation++;}
            @Override public void onPageFinished(WebView v,String u){if(pending&&douyin(u))scan(generation,0);}
            @Override public void onReceivedError(WebView v,WebResourceRequest r,WebResourceError e){if(r.isForMainFrame()){pending=false;show("Không mở được trang: "+e.getDescription());}}
            @Override public void onReceivedHttpError(WebView v,WebResourceRequest r,WebResourceResponse e){if(r.isForMainFrame()){pending=false;show("Trang trả về HTTP "+e.getStatusCode()+". Hãy thử mở trong Douyin.");}}
        });
        web.setDownloadListener((u,a,c,m,l)->{if(!blocked&&("video/mp4".equalsIgnoreCase(m)||direct(u))&&valid(u)){candidate=u;pending=false;confirm(u);}else say("Chưa có tệp MP4 được phép tải.");});
        root.addView(web,new LinearLayout.LayoutParams(-1,0,1));
        try(InputStream in=getAssets().open("download.js");ByteArrayOutputStream out=new ByteArrayOutputStream()){byte[] buf=new byte[4096];int n;while((n=in.read(buf))!=-1)out.write(buf,0,n);script=new String(out.toByteArray(),StandardCharsets.UTF_8);}catch(IOException e){show("Không đọc được bộ phân tích.");}
        receive(getIntent());
    }
    @Override protected void onNewIntent(Intent i){super.onNewIntent(i);setIntent(i);receive(i);}
    private void receive(Intent i){if(Intent.ACTION_SEND.equals(i.getAction())){String t=i.getStringExtra(Intent.EXTRA_TEXT);if(t!=null)input.setText(t);}}
    private Button button(LinearLayout r,String t,Runnable a){Button b=new Button(this);b.setText(t);b.setOnClickListener(v->a.run());r.addView(b);return b;}
    private boolean valid(String u){try{Uri p=Uri.parse(u);return "https".equalsIgnoreCase(p.getScheme())&&p.getHost()!=null&&p.getUserInfo()==null;}catch(Exception e){return false;}}
    private boolean douyin(String u){if(!valid(u))return false;String h=Uri.parse(u).getHost().toLowerCase(Locale.ROOT);return h.equals("douyin.com")||h.endsWith(".douyin.com")||h.equals("iesdouyin.com")||h.endsWith(".iesdouyin.com");}
    private boolean direct(String u){if(!valid(u))return false;String p=Uri.parse(u).getPath();return p!=null&&p.toLowerCase(Locale.ROOT).endsWith(".mp4");}
    private String url(){Matcher m=URL.matcher(input.getText());String mp4=null;while(m.find()){String u=m.group();if(douyin(u))return u;if(direct(u)&&mp4==null)mp4=u;}if(mp4!=null)return mp4;say("Không tìm thấy liên kết Douyin hoặc MP4 HTTPS.");return null;}
    private void analyze(){String u=url();if(u==null)return;generation++;final int req=++requestId;candidate=null;blocked=false;pending=false;lastReason="no_metadata";if(direct(u)){candidate=u;show("Đã nhận dạng link MP4 trực tiếp.");confirm(u);return;}pending=true;show("Đang mở và phân tích liên kết Douyin…");web.loadUrl(u);handler.postDelayed(()->{if(pending&&requestId==req){pending=false;show(reasonMessage());}},45000);}
    private String reasonMessage(){switch(lastReason){case "no_id":return "Chưa nhận diện được ID video từ trang chia sẻ. Hãy mở trong Douyin.";case "no_permission":return "Trang không cung cấp thông tin xác nhận quyền lưu video. Hãy kiểm tra nút 保存本地 trong Douyin.";case "no_address":return "Video được phép lưu nhưng trang chưa cung cấp địa chỉ tệp. Hãy dùng nút lưu trong Douyin.";default:return "Trang xem trước không cung cấp dữ liệu video cần thiết. Hãy kiểm tra nút lưu trong Douyin.";}}
    private void scan(int g,int attempt){if(g!=generation||!pending||blocked||script==null)return;web.evaluateJavascript(script,raw->{if(g!=generation||!pending)return;try{JSONObject r=new JSONObject(raw);lastReason=r.optString("reason","no_metadata");if(r.optBoolean("blocked")){blocked=true;pending=false;candidate=null;show("Video không cho phép tải xuống.");return;}String u=r.optString("url","");if(valid(u)){candidate=u;pending=false;show("Đã tìm thấy nguồn tải được phép. Bấm Tải video.\n"+r.optString("title",""));return;}}catch(Exception ignored){}if(attempt<14)handler.postDelayed(()->scan(g,attempt+1),2000);else{pending=false;show(reasonMessage());}});}
    private void external(String u){try{startActivity(new Intent(Intent.ACTION_VIEW,Uri.parse(u)));}catch(Exception e){say("Không có ứng dụng mở liên kết.");}}
    private void confirm(String u){if(!valid(u)||blocked)return;new AlertDialog.Builder(this).setTitle("Tải video MP4").setMessage("Chỉ tải video bạn sở hữu hoặc được tác giả cho phép tải xuống.").setNegativeButton("Hủy",null).setPositiveButton("Tải",(d,w)->{try{DownloadManager.Request r=new DownloadManager.Request(Uri.parse(u));r.setMimeType("video/mp4");r.setTitle("Video Douyin");r.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);r.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS,"Douyin_"+System.currentTimeMillis()+".mp4");long id=((DownloadManager)getSystemService(DOWNLOAD_SERVICE)).enqueue(r);show("Đã đưa vào hàng đợi tải (ID "+id+"). Xem trạng thái ở Video đã tải.");}catch(Exception e){show("Không thể bắt đầu tải: "+e.getMessage());}}).show();}
    private void show(String t){status.setText(t);}
    private void say(String t){Toast.makeText(this,t,Toast.LENGTH_LONG).show();}
    @Override protected void onDestroy(){handler.removeCallbacksAndMessages(null);web.destroy();super.onDestroy();}
}
