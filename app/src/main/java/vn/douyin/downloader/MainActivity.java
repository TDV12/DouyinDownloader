package vn.douyin.downloader;
import android.app.*;import android.os.*;import android.content.*;import android.net.Uri;import android.webkit.*;import android.widget.*;import android.view.*;import java.util.regex.*;
public class MainActivity extends Activity {
 EditText input; WebView web;
 public void onCreate(Bundle b){super.onCreate(b); LinearLayout root=new LinearLayout(this);root.setOrientation(1);root.setPadding(20,20,20,20);root.setFitsSystemWindows(true);setContentView(root);
 TextView title=new TextView(this);title.setText("Douyin Downloader");title.setTextSize(26);root.addView(title);
 input=new EditText(this);input.setHint("Dán link Douyin hoặc MP4 HTTPS");root.addView(input);
 button(root,"Dán liên kết",()->{ClipboardManager c=(ClipboardManager)getSystemService(CLIPBOARD_SERVICE);if(c.hasPrimaryClip())input.setText(c.getPrimaryClip().getItemAt(0).coerceToText(this));});
 button(root,"Mở video / xem trước",()->{String u=url();if(u!=null)web.loadUrl(u);});
 button(root,"Tải MP4 trực tiếp",()->{String u=url();if(u!=null){String p=Uri.parse(u).getPath();if(p!=null&&p.toLowerCase(java.util.Locale.ROOT).endsWith(".mp4"))confirm(u);else toast("Cần link MP4 trực tiếp hoặc nút tải chính thức trên trang.");}});
 button(root,"Mở danh sách tải xuống",()->{try{startActivity(new Intent(DownloadManager.ACTION_VIEW_DOWNLOADS));}catch(Exception e){toast("Không mở được danh sách tải.");}});
 web=new WebView(this);web.getSettings().setJavaScriptEnabled(true);web.getSettings().setDomStorageEnabled(true);web.getSettings().setAllowFileAccess(false);web.getSettings().setAllowContentAccess(false);web.setWebViewClient(new WebViewClient(){public boolean shouldOverrideUrlLoading(WebView v,WebResourceRequest r){return !"https".equalsIgnoreCase(r.getUrl().getScheme());}});web.setDownloadListener((u,a,c,m,l)->{if("video/mp4".equalsIgnoreCase(m))confirm(u);else toast("Chỉ hỗ trợ tải video MP4.");});root.addView(web,new LinearLayout.LayoutParams(-1,0,1)); }
 void button(LinearLayout r,String s,Runnable a){Button b=new Button(this);b.setText(s);b.setOnClickListener(v->a.run());r.addView(b);}
 String url(){Matcher m=Pattern.compile("https://[^\\s<>]+",Pattern.CASE_INSENSITIVE).matcher(input.getText());if(m.find())return m.group();toast("Hãy nhập liên kết HTTPS hợp lệ.");return null;}
 void confirm(String u){if(!"https".equalsIgnoreCase(Uri.parse(u).getScheme()))return;new AlertDialog.Builder(this).setTitle("Tải video").setMessage("Chỉ tải video bạn sở hữu hoặc được phép tải xuống.").setNegativeButton("Hủy",null).setPositiveButton("Tải",(d,w)->{try{DownloadManager.Request r=new DownloadManager.Request(Uri.parse(u));r.setMimeType("video/mp4");r.setTitle("Video Douyin");r.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);r.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS,"Douyin_"+System.currentTimeMillis()+".mp4");((DownloadManager)getSystemService(DOWNLOAD_SERVICE)).enqueue(r);toast("Đã đưa vào hàng đợi tải xuống.");}catch(Exception e){toast("Không thể bắt đầu tải.");}}).show();}
 void toast(String s){Toast.makeText(this,s,Toast.LENGTH_LONG).show();}
}
