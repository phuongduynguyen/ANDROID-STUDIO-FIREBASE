package com.example.myhomie_version1;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;

public class WebBrowser extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web_browser);
        final EditText urlEdt = findViewById(R.id.urlEdt);

        urlEdt.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if(actionId == EditorInfo.IME_ACTION_SEARCH)
                {
                    final String urlTxt = urlEdt.getText().toString();
                    if(!urlTxt.isEmpty()){
                        Intent intent = new Intent(WebBrowser.this, BrowserView.class);
                        intent.putExtra("url", urlTxt);
                        startActivity(intent);
                    }
                }
                return false;
            }
        });
    }
}