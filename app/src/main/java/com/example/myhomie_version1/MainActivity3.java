package com.example.myhomie_version1;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.os.Vibrator;
import android.util.Log;
import android.view.ContextMenu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.login.LoginManager;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.jetbrains.annotations.NotNull;
import org.opencv.android.OpenCVLoader;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Date;

public class MainActivity3 extends AppCompatActivity {

    private static final int NOTIFICATION_ID = 1;
    private static final int JOB_ID = 123;
    private MediaPlayer mediaPlayer;
    private BroadcastReceiver broadcastReceiver;

    static {
        if (OpenCVLoader.initDebug()){
            Log.d("MainActivity: ", "OpenCV loaded");
        }
        else {
            Log.d("MainActivity: ", "OpenCV not loaded");
        }
    }

    private static final int NOTIFY_ID = 2020;

    ImageView   bgapp, cloud, infor, sensor, weather, cctv, wfi;
    TextView    status;
    //Animation bganim, cloudnim;
    LinearLayout textsplash, texthome, menus;
    Animation    frombottom;
    FirebaseAuth mAth;
    DatabaseReference mData;
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    Vibrator vibrator;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main3);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[] {Manifest.permission.CAMERA}, 1);
            }
        }


        GoogleSignInOptions googleSignInOptions = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();

        GoogleSignInClient googleSignInClient = GoogleSignIn.getClient(this, googleSignInOptions);
        GoogleSignInAccount googleSignInAccount = GoogleSignIn.getLastSignedInAccount(this);

        vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);
        final DatabaseReference wifi = database.getReference("Status");
        final DatabaseReference alert = database.getReference("Alert");
        final long[] pattern = {2000, 1000};

        mAth          = FirebaseAuth.getInstance();
        mData         = FirebaseDatabase.getInstance().getReference();
        frombottom    = AnimationUtils.loadAnimation(this, R.anim.frombottom);

        wfi     = (ImageView) findViewById(R.id.status_connect);
        status  = (TextView) findViewById(R.id.connect);
        bgapp   = (ImageView) findViewById(R.id.bgapp);
        cloud   = (ImageView) findViewById(R.id.cloud);
        infor   = (ImageView) findViewById(R.id.menuInfor);
        sensor  = (ImageView) findViewById(R.id.sensor);
        weather = (ImageView) findViewById(R.id.imgWeather);
        cctv    = (ImageView) findViewById(R.id.cctv);

        texthome    = (LinearLayout) findViewById(R.id.textHome);
        textsplash  = (LinearLayout) findViewById(R.id.textSplash);
        menus       = (LinearLayout) findViewById(R.id.menus);

        bgapp.animate().translationY(-1600).setDuration(800).setStartDelay(300);
        cloud.animate().alpha(0).setDuration(800).setStartDelay(600);
        textsplash.animate().translationY(140).alpha(0).setDuration(800).setStartDelay(300);
        texthome.startAnimation(frombottom);

        broadcastReceiver = new BroadcastReceiver();

        infor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                myStopService();
                mAth.signOut();
                googleSignInClient.signOut();
                LoginManager.getInstance().logOut();
                Intent intent = new Intent(MainActivity3.this, MainActivity.class);
                startActivity(intent);
                finish();
            }
        });

        sensor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Intent intent = new Intent(MainActivity3.this, Device.class);
//                startActivity(intent);
                Showmenu();
            }
        });

        weather.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity3.this, cctv.class);
                startActivity(intent);
            }
        });

        cctv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity3.this, CameraActivity.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP));
            }
        });

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[] {android.Manifest.permission.CAMERA}, 50);
        }

        registerForContextMenu(sensor);

        wifi.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                String value = snapshot.getValue(String.class);
                if (value.equals("ON")) {
                    wfi.setImageResource(R.drawable.onnn);
                    status.setText("Connected");
                    status.setTextColor(Color.parseColor("#FFB649C8"));
                    Toast.makeText(MainActivity3.this, "Connected", Toast.LENGTH_SHORT).show();
                } else if(value.equals("OFF")){
                    wfi.setImageResource(R.drawable.offf);
                    status.setText("Disconnected");
                    status.setTextColor(Color.parseColor("#000000"));
                    Toast.makeText(MainActivity3.this,"Disconnected",Toast.LENGTH_SHORT).show();
                }
            }
            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {
                wfi.setImageResource(R.drawable.offf);
                status.setText("Disconnected");
                status.setTextColor(Color.parseColor("#000000"));
                Toast.makeText(MainActivity3.this,"Disconnected",Toast.LENGTH_SHORT).show();
            }
        });

        myStartService();
        alert.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                String value = snapshot.getValue(String.class);

                if (value.equals("ON")) {
                    Song song = new Song(R.raw.iphone);
                    sendNotification();
                    vibrator.vibrate(pattern,0);
                    Toast.makeText(MainActivity3.this,"you have Wanning",Toast.LENGTH_SHORT).show();
                    startMusic(song);
                }else {
                    vibrator.cancel();
                    if (mediaPlayer != null) {
                        mediaPlayer.release();
                        mediaPlayer = null;
                    }
                    Toast.makeText(MainActivity3.this,"no notify",Toast.LENGTH_SHORT).show();
                }

            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {
            }
        });

    }

    private void startMusic(Song song) {
        if (mediaPlayer == null) {
            mediaPlayer = MediaPlayer.create(getApplicationContext(), song.getResource());
        }
        mediaPlayer.start();
    }

    private void myStartService() {
        Song song = new Song(R.raw.iphone);
        Intent intent = new Intent(this, MyService.class);
        Bundle bundle= new Bundle();
        bundle.putSerializable("song", song );
        intent.putExtras(bundle);
        startService(intent);
    }

    private void myStopService() {
        Intent intent = new Intent(this, MyService.class);
        stopService(intent);
    }


    private void sendNotification() {


        Bitmap bitmap = BitmapFactory.decodeResource(getResources(),R.drawable.fire);

        // Create an Intent for the activity you want to start
        Intent resultIntent = new Intent(this, BrowserView.class);
        // Create the TaskStackBuilder and add the intent, which inflates the back stack
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
        stackBuilder.addNextIntentWithParentStack(resultIntent);
        // Get the PendingIntent containing the entire back stack
        PendingIntent resultPendingIntent = stackBuilder.getPendingIntent(getNotificationId(), PendingIntent.FLAG_UPDATE_CURRENT);

        Notification notification = new NotificationCompat.Builder(this, com.example.myhomie_version1.Notification.CHANNEL_ID)
                .setContentTitle("FIRE Warning!!")
                .setContentText("Abnormally high concentration of CO2 detected ")
                .setSmallIcon(R.drawable.notificationn)
                .setLargeIcon(bitmap)
                .setShowWhen(true)
                .setContentIntent(resultPendingIntent)
                .build();

        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        if (notificationManager != null) {
            notificationManager.notify(getNotificationId(), notification );
        }

    }

    private int getNotificationId () {
        return (int) new Date().getTime();
    }


    private void Showmenu(){
        PopupMenu popupMenu = new PopupMenu(this, sensor);
        popupMenu.getMenuInflater().inflate(R.menu.menu_device, popupMenu.getMenu());
        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {

                switch (item.getItemId()){
                    case R.id.phongKhach:
                        Intent intent = new Intent(MainActivity3.this, Custom_Device.class);
                        startActivity(intent);
                        break;
                    case R.id.phongBep:
                        Intent intent4 = new Intent(MainActivity3.this, Custom_Device.class);
                        startActivity(intent4);
                        break;
                    case R.id.monitor:
                        Intent intent2 = new Intent(MainActivity3.this, MainActivity4.class);
                        startActivity(intent2);
                        break;
                    case R.id.weather:
                        Intent intent1 = new Intent(MainActivity3.this, Weather.class);
                        startActivity(intent1);
                        break;
                }
                return false;
            }
        });
        popupMenu.show();
    }



    @Override
    protected void onStart() {
        super.onStart();
        IntentFilter intentFilter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
        registerReceiver(broadcastReceiver, intentFilter);
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(broadcastReceiver);
    }
}
