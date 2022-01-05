package com.example.myhomie_version1
        ;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.jetbrains.annotations.NotNull;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;

import soup.neumorphism.NeumorphFloatingActionButton;

public class Custom_Device extends AppCompatActivity {

    ImageView imgAir, imgFan, imgLight, imgDoor, imgLevel, img_btn_pw_ac, img_btn_fan_ac, img_btn_cold_ac ;
    Switch swLight, swFan, swDoor;
    NeumorphFloatingActionButton neu_power;
    TextView tvTemp;
    SeekBar seekBar_Temp;
    Boolean stateAir, stateLight, stateFan, stateDoor;
    Boolean stateCold = false;
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    private int count = 0;
    private String simpleFileName = "note.txt";
    static Boolean stateAll = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.custom_device);
        anhxa();

        final DatabaseReference mayLanh     = database.getReference("MayLanh");
        final DatabaseReference quat        = database.getReference("Quat");
        final DatabaseReference den         = database.getReference("Den");
        final DatabaseReference cua         = database.getReference("Cua");
        final DatabaseReference seekBarTemp = database.getReference("SeekBarTemp");
        final DatabaseReference fanLevel    = database.getReference("FanLevel");
        final DatabaseReference cold        = database.getReference("Cold");

        mayLanh.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                String value = snapshot.getValue(String.class);
                if (value.equals("ON")) {
                    stateAir = true;
                    imgAir.setImageResource(R.drawable.neu_air_conditioner_on);
                    img_btn_pw_ac.setImageResource(R.drawable.power_onn);
                    Toast.makeText(Custom_Device.this,"Turn on Air-Conditioner", Toast.LENGTH_SHORT).show();
                }else if(value.equals("OFF")) {
                    stateAir = false;
                    imgAir.setImageResource(R.drawable.air_conditioner_neu);
                    img_btn_pw_ac.setImageResource(R.drawable.power_off);
                    Toast.makeText(Custom_Device.this,"Turn off the Air-Conditioner",Toast.LENGTH_SHORT).show();
                }
            }
            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {

            }
        });

        img_btn_pw_ac.setOnClickListener(v -> {
            stateAir =! stateAir;
            if (stateAir) {
                mayLanh.setValue("ON");
                imgAir.setImageResource(R.drawable.neu_air_conditioner_on);
                img_btn_pw_ac.setImageResource(R.drawable.power_onn);
                Toast.makeText(Custom_Device.this,"Air-Conditioner: ON", Toast.LENGTH_SHORT).show();
            }else {
                mayLanh.setValue("OFF");
                imgAir.setImageResource(R.drawable.air_conditioner_neu);
                img_btn_pw_ac.setImageResource(R.drawable.power_off);
                Toast.makeText(Custom_Device.this,"Air-Conditioner: OFF", Toast.LENGTH_SHORT).show();
            }
        });

        img_btn_fan_ac.setOnClickListener(v -> {
            count++;
            if (count == 1) {
                imgLevel.setImageResource(R.drawable.level1);
                fanLevel.setValue("1");
            }

            if (count == 2) {
                imgLevel.setImageResource(R.drawable.level2);
                fanLevel.setValue("2");
            }

            if (count == 3) {
                count = 0;
                imgLevel.setImageResource(R.drawable.level3);
                fanLevel.setValue("3");
            }
        });

        img_btn_cold_ac.setOnClickListener(v -> {
            stateCold =! stateCold;
            if (stateCold) {
                cold.setValue("ON");
                img_btn_cold_ac.setImageResource(R.drawable.cold_on);

            } else {
                cold.setValue("OFF");
                img_btn_cold_ac.setImageResource(R.drawable.cold_off);
            }
        });

        seekBar_Temp.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
//                maximumDataSet = progress + 16;
                tvTemp.setText((progress + 16) + "°C");
                String value = String.valueOf((progress + 16));
                seekBarTemp.setValue(value);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });

        quat.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                String value = snapshot.getValue(String.class);
                if (value.equals("ON")) {
                    stateFan = true;
                    imgFan.setImageResource(R.drawable.fan_on);
                    swFan.setChecked(true);
                    Toast.makeText(Custom_Device.this,"Fan: ON", Toast.LENGTH_SHORT).show();
                } else if(value.equals("OFF")) {
                    stateFan = false;
                    imgFan.setImageResource(R.drawable.fan_off);
                    swFan.setChecked(false);
                    Toast.makeText(Custom_Device.this,"Fan: OFF", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {

            }
        });

        swFan.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    quat.setValue("ON");
                }else{
                    quat.setValue("OFF");
                }
            }
        });

        den.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                String value = snapshot.getValue(String.class);
                if (value.equals("ON")) {
                    stateLight = true;
                    imgLight.setImageResource(R.drawable.lamp_on);
                    swLight.setChecked(true);
                    Toast.makeText(Custom_Device.this,"Light: ON", Toast.LENGTH_SHORT).show();
                } else if(value.equals("OFF")) {
                    stateLight = false;
                    imgLight.setImageResource(R.drawable.lamp_off);
                    swLight.setChecked(false);
                    Toast.makeText(Custom_Device.this,"Light: OFF", Toast.LENGTH_SHORT).show();
                }

            }
            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {
            }
        });

        swLight.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    den.setValue("ON");
                }else{
                    den.setValue("OFF");
                }
            }
        });

        cua.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                String value = snapshot.getValue(String.class);
                if (value.equals("ON")) {
                    stateDoor = true;
                    imgDoor.setImageResource(R.drawable.door_open);
                    swDoor.setChecked(true);
                    Toast.makeText(Custom_Device.this,"Door: ON", Toast.LENGTH_SHORT).show();
                } else if(value.equals("OFF")) {
                    stateDoor = false;
                    imgDoor.setImageResource(R.drawable.door_close);
                    swDoor.setChecked(false);
                    Toast.makeText(Custom_Device.this,"Door: OFF", Toast.LENGTH_SHORT).show();
                }

            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {

            }
        });

        swDoor.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    cua.setValue("ON");
                }else{
                    cua.setValue("OFF");
                }
            }
        });

        neu_power.setOnClickListener(v -> {
            stateAll =! stateAll;
            if (stateAll) {
                den.setValue("ON");
                quat.setValue("ON");
                mayLanh.setValue("ON");
            } else {
                den.setValue("OFF");
                quat.setValue("OFF");
                mayLanh.setValue("OFF");
            }
        });



    }


        private void anhxa() {

        imgAir              = (ImageView) findViewById(R.id.ptAir);
        imgFan              = (ImageView) findViewById(R.id.ptFan);
        imgLight            = (ImageView) findViewById(R.id.ptLight);
        imgDoor             = (ImageView) findViewById(R.id.ptdoor);
        imgLevel            = (ImageView) findViewById(R.id.img_level);
        img_btn_pw_ac       = (ImageView) findViewById(R.id.img_btn_power_AC);
        img_btn_fan_ac      = (ImageView) findViewById(R.id.img_btn_fan_AC);
        img_btn_cold_ac     = (ImageView) findViewById(R.id.img_btn_cold_AC);

        swLight             = (Switch)  findViewById(R.id.swLight);
        swFan               = (Switch)  findViewById(R.id.swFan);
        swDoor              = (Switch)  findViewById(R.id.swdoor);

        neu_power           = (NeumorphFloatingActionButton) findViewById(R.id.neu_power_btn);

        tvTemp              = (TextView) findViewById(R.id.tvTemp);

        seekBar_Temp        = (SeekBar) findViewById(R.id.seekBarTemp);


    }
}