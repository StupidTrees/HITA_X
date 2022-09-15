//package com.stupidtree.hitax.ui.news;
//
//import static com.stupidtree.hita.HITAApplication.HContext;
//
//import android.annotation.SuppressLint;
//import android.content.Intent;
//import android.net.Uri;
//import android.os.Bundle;
//import android.text.Html;
//import android.view.Menu;
//import android.view.MenuItem;
//import android.view.View;
//import android.webkit.JavascriptInterface;
//import android.webkit.WebChromeClient;
//import android.webkit.WebSettings;
//import android.webkit.WebView;
//import android.webkit.WebViewClient;
//import android.widget.TextView;
//
//import androidx.appcompat.widget.Toolbar;
//
//import com.google.gson.JsonObject;
//import com.google.gson.JsonParser;
//import com.stupidtree.hita.HITAApplication;
//import com.stupidtree.hita.R;
//import com.stupidtree.hita.fragments.BaseOperationTask;
//import com.stupidtree.hita.util.ActivityUtils;
//import com.stupidtree.hita.util.JsonUtils;
//import com.stupidtree.hitax.utils.ActivityUtils;
//import com.stupidtree.style.base.BaseActivity;
//
//import org.jsoup.Connection;
//import org.jsoup.Jsoup;
//import org.jsoup.nodes.Document;
//
//import java.util.ArrayList;
//import java.util.HashMap;
//import java.util.List;
//import java.util.Map;
//import java.util.Objects;
//import java.util.regex.Matcher;
//import java.util.regex.Pattern;
//
//
//public class ActivityNewsDetail extends BaseActivity {
//    private static final int MODE_HITSZ_NEWS = 929;
//    String link;
//    TextView title, time;
//    WebView wv;
//    int mode;
//    List<String> imagesOnPage;
//
//
//
//    @SuppressLint("SetJavaScriptEnabled")
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setWindowParams(true, true, false);
//        initToolbar();
//        String mS = getIntent().getStringExtra("mode");
//        if (mS.equals("hitsz_news")) mode = MODE_HITSZ_NEWS;
//        initLink();
//        initViews();
//
//
//    }
//
//    @SuppressLint("SetJavaScriptEnabled")
//    void initViews() {
//        imagesOnPage = new ArrayList<>();
//
//        //wv.setBackgroundColor(0);
//        title.setText(getIntent().getStringExtra("title"));
//        //支持javascript
//        wv.setWebChromeClient(new WebChromeClient());
//        wv.getSettings().setMixedContentMode(WebSettings.MIXED_CONTENT_COMPATIBILITY_MODE);
//        wv.getSettings().setJavaScriptEnabled(true);
//        //支持自适应
//        wv.getSettings().setLayoutAlgorithm(WebSettings.LayoutAlgorithm.NORMAL);
//        wv.getSettings().setLoadWithOverviewMode(true);
////java回调js代码，不要忘了@JavascriptInterface这个注解，不然点击事件不起作用
//        wv.addJavascriptInterface(new JsCallJavaObj() {
//            @JavascriptInterface
//            @Override
//            public void showBigImg(String url) {
//                //String url =
//                ActivityUtils.s
////                ActivityUtils.showOneImage(getThis(), url);
//                //   ActivityUtils.startPhotoDetailActivity(ActivityNewsDetail.this,url);
//            }
//        }, "jsCallJavaObj");
//        wv.setWebViewClient(new WebViewClient() {
//            @Override
//            public void onPageFinished(WebView view, String url) {
//                super.onPageFinished(view, url);
//                setWebImageClick(view);
//            }
//        });
//
//    }
//
//
//
//    @Override
//    public void onOperationStart(String id, Boolean[] params) {
//
//    }
//
//    @Override
//    public void onOperationDone(String id, BaseOperationTask task, Boolean[] params, Object o) {
//        if (o != null) {
//            Map m = (Map) o;
//            Document d = new Document("");
//            if (m.get("time") != null) time.setText(Html.fromHtml(String.valueOf(m.get("time"))));
//            if (m.get("text") != null) d = Jsoup.parse(String.valueOf(m.get("text")));
//            // d.removeClass("download_file");
//            String js = "<script type=\"text/javascript\">" +
//                    "var imgs = document.getElementsByTagName('img');" + // 找到img标签
//                    "for(var i = 0; i<imgs.length; i++){" +  // 逐个改变
//                    "imgs[i].style.width = '100%';" +  // 宽度改为100%
//                    "imgs[i].style.height = 'auto';" +
//                    "}" +
//                    "</script>";
//            if (mode == MODE_HITSZ_NEWS) {
//                wv.loadUrl("http://www.hitsz.edu.cn" + getIntent().getStringExtra("link"));
//
//            } else {
//                wv.loadData(d.toString() + js, "text/html; charset=UTF-8", null);
//            }
//            //
//        }
//    }
//
//    /**
//     * Js調用Java接口
//     */
//    private interface JsCallJavaObj {
//        void showBigImg(String url);
//    }
//
//    void initToolbar() {
//        Toolbar toolbar = findViewById(R.id.toolbar);
//        toolbar.setTitle("");
//        toolbar.inflateMenu(R.menu.toolbar_news_detail);
//        setSupportActionBar(toolbar);
//        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);//左侧添加一个默认的返回图标
//        getSupportActionBar().setHomeButtonEnabled(true); //设置返回键可用
//        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                onBackPressed();
//            }
//        });
//        toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
//            @Override
//            public boolean onMenuItemClick(MenuItem item) {
//                if (item.getItemId() == R.id.action_website) {
//                    Uri uri = Uri.parse(link);
//                    Intent intent = new Intent(Intent.ACTION_VIEW, uri);
//                    ActivityNewsDetail.this.startActivity(intent);
//                }
//                return false;
//            }
//        });
//    }
//
//    void initLink() {
//        if (mode == MODE_HITSZ_NEWS)
//            link = "http://www.hitsz.edu.cn" + getIntent().getStringExtra("link");
//        else if (mode == MODE_ZSW_NEWS)
//            link = "http://zsb.hitsz.edu.cn/zs_common/bkzn/zswz/zsjzxq?id=" + getIntent().getStringExtra("link");
//    }
//
//    /**
//     * 設置網頁中圖片的點擊事件
//     *
//     * @param view
//     */
//    private void setWebImageClick(WebView view) {
//        String jsCode = "javascript:(function(){" +
//                "var imgs=document.getElementsByTagName(\"img\");" +
//                "for(var i=0;i<imgs.length;i++){" +
//                "imgs[i].onclick=function(){" +
//                "window.jsCallJavaObj.showBigImg(this.src);" +
//                "}}})()";
//        view.loadUrl(jsCode);
//    }
//
//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        getMenuInflater().inflate(R.menu.toolbar_news_detail, menu);
//        return super.onCreateOptionsMenu(menu);
//    }
//
//    @Override
//    protected void onResume() {
//        super.onResume();
//        new LoadTask(this, mode, getIntent().getStringExtra("link")).executeOnExecutor(HITAApplication.TPE);
//    }
//
//
//}
