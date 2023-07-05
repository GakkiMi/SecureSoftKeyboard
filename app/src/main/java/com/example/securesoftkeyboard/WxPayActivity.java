package com.example.securesoftkeyboard;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.webkit.SslErrorHandler;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import java.util.HashMap;
import java.util.Map;

public class WxPayActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wx_pay);
        WebView webView = ((WebView) findViewById(R.id.webviewtest));

        WebSettings settings = webView.getSettings();
        settings.setJavaScriptEnabled(true);
        settings.setDefaultTextEncodingName("UTF-8");
        webView.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);
        webView.setWebChromeClient(new WebChromeClient());
        WebViewClient webViewClient = new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                // 如下方案可在非微信内部WebView的H5页面中调出微信支付
                if (url.startsWith("weixin://wap/pay?")) {
                    Intent intent = new Intent();
                    intent.setAction(Intent.ACTION_VIEW);
                    intent.setData(Uri.parse(url));
                    startActivity(intent);
                    return true;
                } else {
                    Map<String, String> extraHeaders = new HashMap<String, String>();
                    extraHeaders.put("Referer", "http://wxpay.wxutil.com");
                    view.loadUrl(url, extraHeaders);
                }
                return true;
            }

            @Override
            public void onReceivedSslError(WebView view, SslErrorHandler handler, android.net.http.SslError error) { // 重写此方法可以让webview处理https请求
                handler.proceed();
            }
        };
        webView.setWebViewClient(webViewClient);
        webView.loadUrl("https://wxpay.wxutil.com/mch/pay/h5.v2.php");
//        webView.loadUrl("http://wxpay.wxutil.com/mch/pay/h5.v2.php");
    }
}