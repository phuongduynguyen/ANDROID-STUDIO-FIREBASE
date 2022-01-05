package com.example.myhomie_version1;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Vibrator;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.jetbrains.annotations.NotNull;

public class BrowserView extends AppCompatActivity {

    FirebaseDatabase database = FirebaseDatabase.getInstance();

    private String url = "https://console.firebase.google.com/u/0/project/myhomie-72e49/database/myhomie-72e49-default-rtdb/data";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_browser_view);

        final WebView webView = findViewById(R.id.webView);
        final EditText urlET = findViewById(R.id.urlIET);
        final ImageView homeBtn = findViewById(R.id.homeIcon);
        final DatabaseReference alert = database.getReference("Alert");
//        url = getIntent().getStringExtra("url");
//        int endIndex = Math.min(4,url.length());
//        final String urlData = url.substring(0, endIndex );
//       // final String urlData = url.substring(0, 4);
//
//        if(!urlData.contains("www.")){
////            url = "www.google.com/search?q="+url;
//              url = "http://192.168.0.106/";
//        }

//        alert.setValue("OFF");
        urlET.setText(url);
        webView.setWebViewClient(new WebViewClient(){
            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
                urlET.setText(url);
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
            }
        });
//        urlET.setOnEditorActionListener(new TextView.OnEditorActionListener() {
//            @Override
//            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
//                if(actionId == EditorInfo.IME_ACTION_SEARCH)
//                {
//                    final String urlTxt = urlET.getText().toString();
//
//                    if(!urlTxt.isEmpty()){
//
//                        final String urlData = urlTxt.substring(0,4);
//
//                        if(!urlData.contains("www.")){
//                            url = "www.google.com/search?q="+url;
//                        }
//                        else {
//                            url = urlTxt;
//                        }
//                    }
//                }
//                return false;
//            }
//        });

        webView.getSettings().setJavaScriptEnabled(true);
        webView.loadUrl(url);
        homeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

    }
}