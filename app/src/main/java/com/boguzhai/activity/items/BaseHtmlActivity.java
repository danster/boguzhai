package com.boguzhai.activity.items;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.boguzhai.R;


public class BaseHtmlActivity extends Activity implements View.OnClickListener {
    WebView myWebView;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.item_html);
        myWebView = (WebView) findViewById(R.id.webview);
        myWebView.setWebViewClient(new WebViewClient());

        // refer to http://developer.android.com/guide/webapps/webview.html
        WebSettings webSettings = myWebView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webSettings.setSupportZoom(true);
        webSettings.setDisplayZoomControls(true);
        webSettings.setBuiltInZoomControls(true);

        myWebView.loadUrl(getIntent().getStringExtra("url"));
        //myWebView.loadUrl("https://ibsbjstar.ccb.com.cn/app/ccbMain?MERCHANTID=105290073990660&POSID=998003916&BRANCHID=310000000&ORDERID=56193&PAYMENT=1999.74&CURCODE=01&TXCODE=520100&REMARK1=canpai&REMARK2=144_0_0.26_9877&TYPE=1&GATEWAY=&CLIENTIP=&REGINFO=&PROINFO=&REFERER=&MAC=b3b01523035f4d9f088f22a92ebc205f");
    }

    @Override
    public void onClick(View v){
        if ((v.getId() == R.id.title_left) && myWebView.canGoBack()) {
            myWebView.goBack();
        } else if((v.getId() == R.id.title_left) && ! myWebView.canGoBack()){
            finish();
        }
    };
}


