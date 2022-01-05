package com.example.myhomie_version1;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;

public class Weather extends AppCompatActivity {

    EditText edtSeach;
    Button btnSeach, btnNextday;
    TextView tvName, tvCountry, tvTemp, tvHumid, tvStatus, tvWind, tvDay, tvCloudsmall;
    ImageView imgIcon;
    String City = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weather);
        anhxa();
        GetCurrentWeatherData("Danang");

        btnSeach.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String city = edtSeach.getText().toString();
                if (City.equals("")){
                    City = "Danang";
                    GetCurrentWeatherData(City);
                }else {
                    City = city;
                    GetCurrentWeatherData(City);
                }
            }
        });

        btnNextday.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String city = edtSeach.getText().toString();
                Intent intent = new Intent(Weather.this, Nextday.class);
                intent.putExtra("name", city);
                startActivity(intent);

            }
        });
    }

    public void GetCurrentWeatherData(String data){
        RequestQueue requestQueue = Volley.newRequestQueue(Weather.this);
        String url = "http://api.openweathermap.org/data/2.5/weather?q="+data+"&units=metric&appid=3ca18de60aae5c95c830000d31962c54";
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            String day = jsonObject.getString("dt");
                            String name = jsonObject.getString("name");
                            String name1 = "Turan";
                            if (name.equals(name1))
                            {
                                tvName.setText("Danang");
                                Toast.makeText(Weather.this, "Danang!", Toast.LENGTH_SHORT).show();
                            }else {
                                tvName.setText(name);
                                Toast.makeText(Weather.this, "Turan!", Toast.LENGTH_SHORT).show();
                            }


                            long l = Long.valueOf(day);
                            Date date = new Date(l*1000L);
                            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("EEEE yyyy-MM-dd HH:mm:ss");
                            String Day = simpleDateFormat.format(date);

                            tvDay.setText(Day);

                            JSONArray jsonArrayWeather = jsonObject.getJSONArray("weather");
                            JSONObject jsonObjectWeather = jsonArrayWeather.getJSONObject(0);
                            String status = jsonObjectWeather.getString("main");
                            String icon = jsonObjectWeather.getString("icon");

                            Picasso.with(Weather.this).load("http://openweathermap.org/img/wn/"+icon+"@2x.png").into(imgIcon);
                            tvStatus.setText(status);

                            JSONObject jsonObjectMain = jsonObject.getJSONObject("main");
                            String nhietdo = jsonObjectMain.getString("temp");
                            String doam = jsonObjectMain.getString("humidity");
                            Double a = Double.valueOf(nhietdo);
                            String Nhietdo = String.valueOf(a.intValue());
                            tvTemp.setText(Nhietdo+"°C");
                            tvHumid.setText(doam+"%");

                            JSONObject jsonObjectWind = jsonObject.getJSONObject("wind");
                            String gio = jsonObjectWind.getString("speed");
                            tvWind.setText(gio+"m/s");

                            JSONObject jsonObjecClouds = jsonObject.getJSONObject("clouds");
                            String may = jsonObjecClouds.getString("all");
                            tvCloudsmall.setText(may+"%");

                            JSONObject jsonObjectSys = jsonObject.getJSONObject("sys");
                            String  country = jsonObjectSys.getString("country");
                            tvCountry.setText(country);

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                    }
                });

        requestQueue.add(stringRequest);
    }

    private void anhxa() {
        edtSeach    = (EditText) findViewById(R.id.edtSeach);
        btnSeach    = (Button) findViewById(R.id.btnSeach);
        btnNextday  = (Button) findViewById(R.id.btnNextday);
        tvName      = (TextView) findViewById(R.id.tvName);
        tvCountry   = (TextView) findViewById(R.id.tvCountry);
        tvTemp      = (TextView) findViewById(R.id.tvTemperature);
        tvHumid     = (TextView) findViewById(R.id.tvHumidity);
        tvStatus    = (TextView) findViewById(R.id.tvStatus);
        tvWind      = (TextView) findViewById(R.id.tvSpeed);
        tvDay       = (TextView) findViewById(R.id.tvDay);
        tvCloudsmall= (TextView) findViewById(R.id.tvCloud);
        imgIcon     = (ImageView) findViewById(R.id.imageIcon);


    }
}