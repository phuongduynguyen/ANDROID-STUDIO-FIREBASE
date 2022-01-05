package com.example.myhomie_version1;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.TextView;
import android.view.WindowManager;
import android.content.Intent;
import android.widget.Toast;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.components.Legend;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Calendar;

public class Chart extends AppCompatActivity {

    private TextView mConnectionState;
    private double startTime = 0.0;
    private ArrayList<Entry> valuesTemperature = new ArrayList<>();
    private ArrayList<Entry> valuesPressure = new ArrayList<>();
    private ArrayList<Entry> valuesAltitude = new ArrayList<>();
    private int updatePeriod = 1000;
    private SeekBar seekBarUpdate, seekBarDataSet;
    private TextView textViewUpdate, textViewDataSet;
    private LineChart chartTemperature, chartPressure, chartAltitude;
    private Switch aSwitchRun;
    private Button buttonClear;
    private int maximumDataSet = 20;
    private String receiveBuffer = "";
    DatabaseReference mData;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.bmp);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        anhxa();
        mData         = FirebaseDatabase.getInstance().getReference();

        final Intent intent = getIntent();
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        initializeCharts();
       // messageHandler();
        setData();

        textViewDataSet.setText("Data set: " + maximumDataSet);

        seekBarUpdate.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                updatePeriod = (progress + 1) * 1000;
                if(progress != 0)
                    textViewUpdate.setText("Update period: " + (progress + 1) + " seconds");
                else
                    textViewUpdate.setText("Update period: 1 second");
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        seekBarDataSet.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                maximumDataSet = progress + 20;
                textViewDataSet.setText("Data set: " + (progress + 20));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        buttonClear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clearUI();
            }
        });
        aSwitchRun.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mData.child("Status").addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                        String value = snapshot.getValue(String.class);
                        if (value.equals("ON")){
                            mConnectionState.setText(value);
                        }else{
                            mConnectionState.setText("OFF");
                        }
                    }
                    @Override
                    public void onCancelled(@NonNull @NotNull DatabaseError error) {
                        Toast.makeText(Chart.this, "Cant connected to firebase", Toast.LENGTH_SHORT).show();
                    }
                });
                mData.child("ApSuat").addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                        String value = snapshot.getValue(String.class);
                        float i;
                        double currentTime;
                        try {
                            i = Float.parseFloat(value);
                        }catch (Exception e){
                            i = 0;
                        }

                        if (i!=0){
                            if(startTime == 0.0)
                            {
                                startTime = Calendar.getInstance().getTimeInMillis();
                                currentTime = startTime;
                            }else
                            {
                                currentTime = Calendar.getInstance().getTimeInMillis();
                            }

                            double time = (currentTime - startTime) / 1000.0;

                            valuesPressure.add(new Entry((float)time, i));

                            while(valuesPressure.size() > maximumDataSet)
                                valuesPressure.remove(0);

                            updateCharts();
                        }


                    }

                    @Override
                    public void onCancelled(@NonNull @NotNull DatabaseError error) {
                        Toast.makeText(Chart.this, "Fall get Pressure data from Firebase", Toast.LENGTH_SHORT).show();
                    }
                });
                mData.child("DoAm").addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                        String value = snapshot.getValue(String.class);
                        float i;
                        double currentTime;
                        try {
                            i = Float.parseFloat(value);
                        }catch (Exception e){
                            i = 0;
                        }

                        if (i!=0) {
                            if (startTime == 0.0) {
                                startTime = Calendar.getInstance().getTimeInMillis();
                                currentTime = startTime;
                            } else {
                                currentTime = Calendar.getInstance().getTimeInMillis();
                            }

                            double time = (currentTime - startTime) / 1000.0;

                            valuesAltitude.add(new Entry((float) time, i));

                            while (valuesAltitude.size() > maximumDataSet)
                                valuesAltitude.remove(0);

                            updateCharts();
                        }

                    }

                    @Override
                    public void onCancelled(@NonNull @NotNull DatabaseError error) {
                        Toast.makeText(Chart.this, "Fall get Humid data from Firebase", Toast.LENGTH_SHORT).show();

                    }
                });
                mData.child("NhietDo").addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                        String value = snapshot.getValue(String.class);
                        float i;
                        double currentTime;
                        try {
                            i = Float.parseFloat(value);
                        }catch (Exception e){
                            i = 0;
                        }

                        if (i!=0) {
                            if (startTime == 0.0) {
                                startTime = Calendar.getInstance().getTimeInMillis();
                                currentTime = startTime;
                            } else {
                                currentTime = Calendar.getInstance().getTimeInMillis();
                            }

                            double time = (currentTime - startTime) / 1000.0;

                            valuesTemperature.add(new Entry((float) time, i));

                            while (valuesTemperature.size() > maximumDataSet)
                                valuesTemperature.remove(0);

                            updateCharts();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull @NotNull DatabaseError error) {
                        Toast.makeText(Chart.this, "Fall get Temperature data from Firebase", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });

    }

    private void anhxa() {
        mConnectionState    = findViewById(R.id.connection_state);
        aSwitchRun          = findViewById(R.id.switchRun);
        textViewUpdate      = findViewById(R.id.textViewUpdate);
        textViewDataSet     = findViewById(R.id.textViewDataSet);
        seekBarUpdate       = findViewById(R.id.seekBarUpdate);
        seekBarDataSet      = findViewById(R.id.seekBarDataSet);
        buttonClear         = findViewById(R.id.buttonClear);

    }

    private void initializeCharts()
    {
        chartTemperature = findViewById(R.id.chartTemperature);
        chartTemperature.setDrawGridBackground(true);

        // no description text
        chartTemperature.getDescription().setEnabled(false);

        // enable touch gestures
        chartTemperature.setTouchEnabled(false);

        // enable scaling and dragging
        chartTemperature.setDragEnabled(false);
        chartTemperature.setScaleEnabled(false);
        chartTemperature.setScaleY(1.0f);

        // if disabled, scaling can be done on x- and y-axis separately
        chartTemperature.setPinchZoom(false);

        chartTemperature.getAxisLeft().setDrawGridLines(false);
        chartTemperature.getAxisRight().setEnabled(false);
        chartTemperature.getXAxis().setDrawGridLines(false);
        chartTemperature.getXAxis().setDrawAxisLine(false);
        chartTemperature.animateY(2000);
        chartTemperature.animateX(2000);



        chartPressure = findViewById(R.id.chartPressure);
        chartPressure.setDrawGridBackground(true);

        // no description text
        chartPressure.getDescription().setEnabled(false);

        // enable touch gestures
        chartPressure.setTouchEnabled(false);

        // enable scaling and dragging
        chartPressure.setDragEnabled(false);
        chartPressure.setScaleEnabled(false);
        chartPressure.setScaleY(1.0f);

        // if disabled, scaling can be done on x- and y-axis separately
        chartPressure.setPinchZoom(false);

        chartPressure.getAxisLeft().setDrawGridLines(false);
        chartPressure.getAxisRight().setEnabled(false);
        chartPressure.getXAxis().setDrawGridLines(false);
        chartPressure.getXAxis().setDrawAxisLine(false);



        chartAltitude = findViewById(R.id.chartAltitude);
        chartAltitude.setDrawGridBackground(true);

        // no description text
        chartAltitude.getDescription().setEnabled(false);

        // enable touch gestures
        chartAltitude.setTouchEnabled(false);

        // enable scaling and dragging
        chartAltitude.setDragEnabled(false);
        chartAltitude.setScaleEnabled(false);
        chartAltitude.setScaleY(1.0f);

        // if disabled, scaling can be done on x- and y-axis separately
        chartAltitude.setPinchZoom(false);

        chartAltitude.getAxisLeft().setDrawGridLines(false);
        chartAltitude.getAxisRight().setEnabled(false);
        chartAltitude.getXAxis().setDrawGridLines(false);
        chartAltitude.getXAxis().setDrawAxisLine(false);
    }

    private void updateCharts()
    {
        chartTemperature.resetTracking();
        chartPressure.resetTracking();
        chartAltitude.resetTracking();

        setData();
        // redraw
        chartTemperature.invalidate();
        chartPressure.invalidate();
        chartAltitude.invalidate();
    }

    private void setData() {
        // create a dataset and give it a type
        LineDataSet set1 = new LineDataSet(valuesTemperature, "Temperature (°C)");

        set1.setColor(Color.RED);
        set1.setLineWidth(1.0f);
        set1.setDrawValues(true);
        set1.setDrawCircles(true);
        set1.setMode(LineDataSet.Mode.LINEAR);
        set1.setDrawFilled(false);

        // create a data object with the data sets
        LineData data = new LineData(set1);

        // set data
        chartTemperature.setData(data);

        // get the legend (only possible after setting data)
        Legend l = chartTemperature.getLegend();
        l.setEnabled(true);


        // create a dataset and give it a type
        set1 = new LineDataSet(valuesPressure, "Pressure (Pa)");

        set1.setColor(Color.GREEN);
        set1.setLineWidth(1.0f);
        set1.setDrawValues(true);
        set1.setDrawCircles(true);
        set1.setMode(LineDataSet.Mode.LINEAR);
        set1.setDrawFilled(false);

        // create a data object with the data sets
        data = new LineData(set1);

        // set data
        chartPressure.setData(data);

        // get the legend (only possible after setting data)
        l = chartPressure.getLegend();
        l.setEnabled(true);



        // create a dataset and give it a type
        set1 = new LineDataSet(valuesAltitude, "Humid (%)");
        set1.setColor(Color.BLUE);
        set1.setLineWidth(1.0f);
        set1.setDrawValues(true);
        set1.setDrawCircles(true);
        set1.setMode(LineDataSet.Mode.LINEAR);
        set1.setDrawFilled(false);

        // create a data object with the data sets
        data = new LineData(set1);

        // set data
        chartAltitude.setData(data);

        // get the legend (only possible after setting data)
        l = chartAltitude.getLegend();
        l.setEnabled(true);
    }

    private void clearUI() {
        receiveBuffer="";
        valuesTemperature.clear();
        valuesPressure.clear();
        valuesAltitude.clear();
        startTime = 0.0;
        updateCharts();
    }

    private void messageHandler() {
        if (receiveBuffer != null) {
            double currentTime;
            float temperature = -999.0f, pressure = -999.0f, altitude = -999.0f;

            try
            {
                temperature = (float) 16.5;
                pressure = (float) 11011;
                altitude = 100101;
            }catch (Exception e)
            {
                temperature = -999.0f;
                pressure = -999.0f;
                altitude = -999.0f;
            }

            if(temperature != -999.0 || pressure != -999.0 || altitude != -999.0)
            {
                if(startTime == 0.0)
                {
                    startTime = Calendar.getInstance().getTimeInMillis();
                    currentTime = startTime;
                }else
                {
                    currentTime = Calendar.getInstance().getTimeInMillis();
                }

                double time = (currentTime - startTime) / 1000.0;

                valuesTemperature.add(new Entry((float)time, temperature));
                //valuesPressure.add(new Entry((float)time, pressure));
                valuesAltitude.add(new Entry((float)time, altitude));

                while(valuesTemperature.size() > maximumDataSet)
                    valuesTemperature.remove(0);

                while(valuesPressure.size() > maximumDataSet)
                    valuesPressure.remove(0);

                while(valuesAltitude.size() > maximumDataSet)
                    valuesAltitude.remove(0);

                updateCharts();
            }

        }
    }
}