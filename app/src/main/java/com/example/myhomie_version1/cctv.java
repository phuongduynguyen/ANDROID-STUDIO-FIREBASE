package com.example.myhomie_version1;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.ClipData;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import android.content.ClipboardManager;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.marcoscg.ipcamview.IPCamView;

import org.jetbrains.annotations.NotNull;

public class cctv extends AppCompatActivity {
    private ClipboardManager clipboardManager;
    private String copy;
    Button start;
    TextView ipCam;
    FirebaseDatabase database = FirebaseDatabase.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cctv);
        final DatabaseReference ipCamera = database.getReference("IP");


        start = findViewById(R.id.button2);
        ipCam = findViewById(R.id.ipCam);

        ipCamera.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                copy = snapshot.getValue(String.class);
                ipCam.setText(copy);
            }
            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {
            }
        });

//        start.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent intent = getPackageManager().getLaunchIntentForPackage("com.android.chrome");
//                if (intent != null) {
//                    startActivity(intent);//null pointer check in case package name was not found
//                }
//            }
//        });
    }
    public void openApp(View view){

        clipboardManager = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
        android.content.ClipData clip = android.content.ClipData.newPlainText("copied", copy);
        clipboardManager.setPrimaryClip(clip);

        Intent intent = getPackageManager().getLaunchIntentForPackage("com.microsoft.emmx");
        if (intent != null) {
            startActivity(intent);//null pointer check in case package name was not found
        }else{
            Toast.makeText(cctv.this, "There is no package", Toast.LENGTH_SHORT).show();
        }

    }
}