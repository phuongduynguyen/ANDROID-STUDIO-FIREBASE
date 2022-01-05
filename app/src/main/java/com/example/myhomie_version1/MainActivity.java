package com.example.myhomie_version1;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import com.facebook.FacebookSdk;
import com.facebook.appevents.AppEventsLogger;

public class MainActivity extends AppCompatActivity {

    DatabaseReference mData;
    TextView forgotPassword;
    Button  btnDangky, btnDangNhap;
    EditText edtEmail, edtPassword;
    private FirebaseAuth mAuth;
    CallbackManager mCallbackManager;
    LoginButton loginButton;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        anhxa();

        AppEventsLogger.activateApp(this);
        mAuth         = FirebaseAuth.getInstance();
        mData         = FirebaseDatabase.getInstance().getReference();
        //Initialise Facebook SDK
        FacebookSdk.sdkInitialize(MainActivity.this);

        // Initialize Facebook Login button
        mCallbackManager    = CallbackManager.Factory.create();
        loginButton         = findViewById(R.id.login_button);
        loginButton.setReadPermissions(Arrays.asList("public_profile", "email"));
        setLogin_Button();


//        btnGitHub.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent intent = new Intent(MainActivity.this, Welcome.class);
//                startActivity(intent);
//
//            }
//        });
        btnDangky.setOnClickListener(v -> DangKy());
        btnDangNhap.setOnClickListener(v -> DangNhap());
        forgotPassword.setOnClickListener(v -> {
            EditText resetMail = new EditText(v.getContext());
            AlertDialog.Builder passwordResetDialog = new AlertDialog.Builder(v.getContext());
            passwordResetDialog.setTitle("Reset PassWord ?");
            passwordResetDialog.setMessage("Enter Your Email to Received Reset Link. ");
            passwordResetDialog.setView(resetMail);


            passwordResetDialog.setNegativeButton("No", (dialog, which) -> {

            });

            passwordResetDialog.setPositiveButton("Yes", (dialog, which) -> {

                String mail = resetMail.getText().toString();
                mAuth.sendPasswordResetEmail(mail).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Toast.makeText(MainActivity.this, "Reset Link Sent to Your Email.", Toast.LENGTH_SHORT).show();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull @NotNull Exception e) {
                        Toast.makeText(MainActivity.this, "Error! Reset Link is Not Sent." + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });

            });


            passwordResetDialog.create().show();
        });


//        mData.child("HoTen").setValue("30");
//        mData.child("Status").setValue("ON");
//        SinhVien sv = new SinhVien("70", "90", "90");
//        mData.child("BMP180").setValue(sv, new DatabaseReference.CompletionListener() {
//            @Override
//            public void onComplete(@Nullable @org.jetbrains.annotations.Nullable DatabaseError error, @NonNull @NotNull DatabaseReference ref) {
//
//                if (error == null){
//                    Toast.makeText(MainActivity.this, "Luu thanh cong", Toast.LENGTH_SHORT).show();
//                }
//                else {
//                    Toast.makeText(MainActivity.this, "Luu that bai", Toast.LENGTH_SHORT).show();
//                }
//            }
//        });

        final RelativeLayout signInButton = findViewById(R.id.loginGg);
        GoogleSignInOptions googleSignInOptions = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();

        GoogleSignInClient googleSignInClient = GoogleSignIn.getClient(this, googleSignInOptions);
        GoogleSignInAccount googleSignInAccount = GoogleSignIn.getLastSignedInAccount(this);

        if (googleSignInAccount != null) {
            startActivity(new Intent(MainActivity.this, MainActivity3.class));
            finish();
        }

        ActivityResultLauncher<Intent> activityResultLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(), result -> {
                    Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(result.getData());
                    handleSigninTask(task);

                }
        );

        signInButton.setOnClickListener(v -> {
            Intent signInIntent = googleSignInClient.getSignInIntent();
            activityResultLauncher.launch(signInIntent);
        });

    }

    private void handleSigninTask(Task<GoogleSignInAccount> task) {
        try {
            GoogleSignInAccount account = task.getResult(ApiException.class);
            final String getFullName = account.getDisplayName();
            final String getEmail = account.getEmail();
            final Uri photo = account.getPhotoUrl();

            startActivity(new Intent(MainActivity.this, MainActivity3.class));
            finish();
        } catch (ApiException e) {
            e.printStackTrace();
        }
    }


    private void setLogin_Button() {
        loginButton.registerCallback(mCallbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                handleFacebookAccessToken(loginResult.getAccessToken());
                Intent intent = new Intent(MainActivity.this, MainActivity3.class);
                startActivity(intent);

            }

            @Override
            public void onCancel() {
            }

            @Override
            public void onError(FacebookException error) {
//                Intent intent = new Intent(MainActivity.this, MainActivity2.class);
//                startActivity(intent);
            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        updateUI(currentUser);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        mCallbackManager.onActivityResult(requestCode, resultCode, data);
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void handleFacebookAccessToken(AccessToken token) {
        AuthCredential credential = FacebookAuthProvider.getCredential(token.getToken());
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            FirebaseUser user = mAuth.getCurrentUser();
                            updateUI(user);

                        } else {
                            // If sign in fails, display a message to the user.

                            updateUI(null);
                        }
                    }
                });
    }

    private void updateUI(FirebaseUser user) {
        if (user != null){
            Intent intent = new Intent(MainActivity.this, MainActivity3.class);
            startActivity(intent);

        }else {

        }
    }


//        Map<String, Integer> myMap = new HashMap<String,Integer>();
//        myMap.put("XeMay", 2);
//        mData.child("PhuongTien").setValue(myMap);

//        SinhVien SV =  new SinhVien("Nguyen Nhat Huy", "Da Nang", 2004);
//        mData.child("HocVien").push().setValue(SV);

//        mData.child("Tester").setValue(1234, new DatabaseReference.CompletionListener() {
//            @Override
//            public void onComplete(@Nullable @org.jetbrains.annotations.Nullable DatabaseError error, @NonNull @org.jetbrains.annotations.NotNull DatabaseReference ref) {
//                if (error == null){
//                    Toast.makeText(MainActivity.this, "Luu thanh cong", Toast.LENGTH_SHORT).show();
//                }
//                else {
//                    Toast.makeText(MainActivity.this, "Luu that bai", Toast.LENGTH_SHORT).show();
//                }
//            }
//        });


//        mData.child("BMP180").addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
//                nhietDo.setText(snapshot.getValue().toString());
////              doAm.setText(snapshot.getValue().toString());
////              apSuat.setText(snapshot.getValue().toString());
//            }
//
//            @Override
//            public void onCancelled(@NonNull @NotNull DatabaseError error) {
//
//            }
//        });

//        mData.child("Status").addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
//                trangThai.setText(snapshot.getValue().toString());
//            }
//
//            @Override
//            public void onCancelled(@NonNull @NotNull DatabaseError error) {
//
//            }
//        });
//        btnOn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                mData.child("Status").setValue("ON");
//            }
//        });
//        btnOff.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                mData.child("Status").setValue("OFF");
//            }
//        });


    private void DangNhap() {
        String email    = edtEmail.getText().toString();
        String password = edtPassword.getText().toString();

        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()){
                            Toast.makeText(MainActivity.this, "Login Successled! (^^)", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(MainActivity.this, MainActivity3.class);
                            startActivity(intent);
                        }else {
                            Toast.makeText(MainActivity.this, "Login Fai!! (@@)", Toast.LENGTH_SHORT).show();
                        }

                    }
                });


    }

    private void DangKy(){
        String email    = edtEmail.getText().toString();
        String password = edtPassword.getText().toString();

        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()){
                            Toast.makeText(MainActivity.this, "Signup Success! (^^)", Toast.LENGTH_SHORT).show();
                        }else {
                            Toast.makeText(MainActivity.this, "Registration Failed!! (@@)", Toast.LENGTH_SHORT).show();
                        }
                    }
                });

    }

    private void anhxa() {

//        nhietDo         = (TextView) findViewById(R.id.tvTemp);
//        trangThai       = (TextView) findViewById(R.id.tvHienThi);
//        hienThi         = (TextView) findViewById(R.id.tvDangNhapHomeC);
//        btnOn           = (Button) findViewById(R.id.btnOn);
//        btnOff          = (Button) findViewById(R.id.btnOff);
        btnDangky       = (Button) findViewById(R.id.btnDangKy);
        btnDangNhap     = (Button) findViewById(R.id.btnDangNhap);
        edtEmail        = (EditText) findViewById(R.id.edtEmail);
        edtPassword     = (EditText) findViewById(R.id.edtPassword);
        forgotPassword  = (TextView) findViewById(R.id.forgotPassword);

    }
}
