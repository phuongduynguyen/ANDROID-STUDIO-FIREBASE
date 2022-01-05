package com.example.myhomie_version1;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class Nextday extends AppCompatActivity {

    String thanhpho = "";
    ImageView imgback;
    TextView txtName;
    ListView lv;
    CustomAdapter customAdapter;
    ArrayList<Thoitiet> mangthoitiet;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nextday);
        anhxa();
        Intent intent = getIntent();
        String city = intent.getStringExtra("name");
        Log.d("ketqua", "du lieu truyen qua: " + city);
        if (city.equals("")){
            thanhpho = "Danang";
            Get7DaysData(thanhpho);
        }else {
            thanhpho = city;
            Get7DaysData(thanhpho);
        }

        imgback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

    }

    private void anhxa() {
        imgback = (ImageView) findViewById(R.id.imageviewBack);
        txtName = (TextView) findViewById(R.id.tvCity);
        lv      = (ListView) findViewById(R.id.listview);
        mangthoitiet = new ArrayList<Thoitiet>();
        customAdapter = new CustomAdapter(Nextday.this, mangthoitiet);
        lv.setAdapter(customAdapter);
    }

    private void Get7DaysData(String data) {
        String url = "http://api.openweathermap.org/data/2.5/forecast/daily?q="+data+"&units=metric&cnt=7&appid=53fbf527d52d4d773e828243b90c1f8e";
        RequestQueue requestQueue = Volley.newRequestQueue(Nextday.this);
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                response -> {
                    try {
                        Log.d("ketqua","Json: " + response);
                        JSONObject jsonObject = new JSONObject(response);
                        JSONObject jsonObjectCity = jsonObject.getJSONObject("city");
                        String name = jsonObjectCity.getString("name");

                        txtName.setText(name);

                        JSONArray jsonArrayList = jsonObject.getJSONArray("list");

                        for (int i = 0; i < jsonArrayList.length(); i++){
                            JSONObject jsonObjectList = jsonArrayList.getJSONObject(i);
                            String ngay = jsonObjectList.getString("dt");

                            long l = Long.valueOf(ngay);
                            Date date = new Date(l*1000L);
                            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("EEEE yyyy-MM-dd HH:mm:ss");
                            String Day = simpleDateFormat.format(date);

                            JSONObject jsonObjectTemp = jsonObjectList.getJSONObject("temp");
                            String max = jsonObjectTemp.getString("max");
                            String min = jsonObjectTemp.getString("min");

                            Double a = Double.valueOf(max);
                            Double b = Double.valueOf(min);
                            String Nhietdomax = String.valueOf(a.intValue());
                            String Nhietdomin = String.valueOf(b.intValue());

                            JSONArray jsonArrayWeather  = jsonObjectList.getJSONArray("weather");
                            JSONObject jsonObjectWeather = jsonArrayWeather.getJSONObject(0);
                            String status = jsonObjectWeather.getString("description");
                            String icon   = jsonObjectWeather.getString("icon");

                            mangthoitiet.add(new Thoitiet(Day,status,icon,Nhietdomax,Nhietdomin));
                        }
                        customAdapter.notifyDataSetChanged();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }


                },new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                    }
        });
        requestQueue.add(stringRequest);
    }
}