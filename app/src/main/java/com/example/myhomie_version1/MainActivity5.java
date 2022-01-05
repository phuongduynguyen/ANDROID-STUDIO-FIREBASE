package com.example.myhomie_version1;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Build;
import android.os.Bundle;
import android.widget.TextView;

import com.goodiebag.protractorview.ProtractorView;

public class MainActivity5 extends AppCompatActivity {

    TextView textView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main5);
        textView = (TextView) findViewById(R.id.textView9);
        ProtractorView protractorView = (ProtractorView) findViewById(R.id.protractorview);


        protractorView.setTickIntervals(15);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            protractorView.setArcColor(getColor(R.color.blue));
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            protractorView.setProgressColor(getColor(R.color.red));
        }

        protractorView.setOnProtractorViewChangeListener(new ProtractorView.OnProtractorViewChangeListener() {
             @Override
             public void onProgressChanged(ProtractorView pv,
                                           int progress, boolean b) {
                 textView.setText(""+progress + "°C");
             }

             @Override
             public void onStartTrackingTouch(ProtractorView pv) {

             }

             @Override
             public void onStopTrackingTouch(ProtractorView pv) {

             }
                                                                 });

    }
}