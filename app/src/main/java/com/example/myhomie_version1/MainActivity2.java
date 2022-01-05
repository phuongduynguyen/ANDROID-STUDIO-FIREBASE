package com.example.myhomie_version1;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.media.Image;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.facebook.AccessToken;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.Profile;
import com.facebook.login.LoginManager;
import com.facebook.login.widget.ProfilePictureView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import org.json.JSONException;
import org.json.JSONObject;

public class MainActivity2 extends AppCompatActivity {

    ProfilePictureView profilePictureView;
    Button btn_Sign_out;
    ImageView myPhoto;
    TextView myName, myEmail;
    String email, name;

    FirebaseAuth mAth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);

        mAth = FirebaseAuth.getInstance();
        final  FirebaseUser mUser = mAth.getCurrentUser();

        btn_Sign_out = (Button) findViewById(R.id.btnSignOut);
        myEmail = (TextView) findViewById(R.id.edtEmail);
        myName =  (TextView) findViewById(R.id.myName);
        profilePictureView = (ProfilePictureView) findViewById(R.id.imageProfile);


        btn_Sign_out.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mAth.signOut();
                LoginManager.getInstance().logOut();
                Intent intent = new Intent(MainActivity2.this, MainActivity.class);
                startActivity(intent);
                finish();
            }
        });


        GraphRequest graphRequest = GraphRequest.newMeRequest(AccessToken.getCurrentAccessToken(), new GraphRequest.GraphJSONObjectCallback() {
            @Override
            public void onCompleted(JSONObject object, GraphResponse response) {
                Log.d("JSON", response.getJSONObject().toString());
                try {
                    email = object.getString("email");
                    name = object.getString("name");
                    myName.setText(object.getString("email"));
                    profilePictureView.setProfileId(Profile.getCurrentProfile().getId());
               } catch (JSONException e){
                    e.printStackTrace();
                   }
            }
        });
        Bundle parameters = new Bundle();
        parameters.putString("fields", "name,email");
        graphRequest.setParameters(parameters);
        graphRequest.executeAsync();

    }

    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_demo, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.menuDevice:
                Toast.makeText(this, "you choose device", Toast.LENGTH_SHORT).show();
                break;
            case R.id.menuCamera:
                Toast.makeText(this, "you choose camera", Toast.LENGTH_SHORT).show();
                break;
            case R.id.menuSensor:
                Toast.makeText(this, "you choose sensor", Toast.LENGTH_SHORT).show();
                break;
            case R.id.menuWeather:
                Toast.makeText(this, "you choose weather", Toast.LENGTH_SHORT).show();
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}