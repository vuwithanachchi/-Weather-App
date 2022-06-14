package com.example.weatherapp;


import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.textfield.TextInputEditText;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.jar.JarException;

public class MainActivity extends AppCompatActivity {

    private RelativeLayout homeReL;
    private ProgressBar loadingProB;
    private TextView LocationNameTV, TempTV, CondiTV;
    private TextInputEditText LocationEdit;
    private ImageView BacgroundIV, IconIV;
    private ImageButton SearchIV;
    private RecyclerView WeatherRV;
    private ArrayList<Weather_RVModal> weatherRvModalArrayList;
    private Weather_RVAdapter Weather_RVAdapter;
    private LocationManager locationManager;
    private int PERMISSION_CODE = 1;
    private String LocationName;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        setContentView(R.layout.activity_main);

        homeReL = findViewById(R.id.idRevHome);
        loadingProB = findViewById(R.id.idProgressLoading);
        LocationNameTV = findViewById(R.id.idTextLocationName);
        TempTV = findViewById(R.id.idTextVTemperature);
        CondiTV = findViewById(R.id.idTextVCondition);
        BacgroundIV = findViewById(R.id.idIBackground);
        IconIV = findViewById(R.id.idImageVcon);
        SearchIV = findViewById(R.id.idImageVSearch);
        WeatherRV = findViewById(R.id.idRV_Weather);
        weatherRvModalArrayList = new ArrayList<>();
        Weather_RVAdapter = new Weather_RVAdapter(this, weatherRvModalArrayList);
        WeatherRV.setAdapter(Weather_RVAdapter);

        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, PERMISSION_CODE);
            Location location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
            LocationName = getLocationName(location.getLongitude(), location.getLatitude());
            getWeatherInfo(LocationName);
            SearchIV.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    String location = LocationEdit.getText().toString();
                    if (location.isEmpty()) {
                        Toast.makeText(MainActivity.this, "Please enter the location", Toast.LENGTH_SHORT).show();
                    } else {
                        LocationNameTV.setText(LocationName);
                        getWeatherInfo(location);
                    }
                }
            });
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Permission granted..", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Please provide the permissions", Toast.LENGTH_SHORT).show();
                finish();
            }
        }
    }


    private String getLocationName(double longitude, double latitude) {
        String LocationName = "Not found";
        Geocoder gcd = new Geocoder(getBaseContext(), Locale.getDefault());
        try {
            List<Address> addresses = gcd.getFromLocation(latitude, longitude, 10);
            for (Address adr : addresses) {
                if (adr != null) {
                    String location = adr.getLocality();
                    if (location != null && !location.equals("")) {
                        LocationName = location;
                    } else {
                        Log.d("TAG", "Location not found");
                        Toast.makeText(this, "User location not found..", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return LocationName;
    }

    private void getWeatherInfo(String LocationName) {
        String url = "http://api.weatherapi.com/v1/forecast.json?key=c26f7aeadab941069c5150143223105&q=" + LocationName + "&days=1&aqi=yes&alerts=yes";
        LocationNameTV.setText(LocationName);
        RequestQueue requestedQueue = Volley.newRequestQueue(getApplicationContext());

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                loadingProB.setVisibility(View.GONE);
                homeReL.setVisibility(View.VISIBLE);
                weatherRvModalArrayList.clear();

                try {
                    String temperature = response.getJSONObject("current").getString("temp_c");
                    TempTV.setText(temperature + "°c");
                    int isDay = response.getJSONObject("current").getInt("is day");
                    String condition = response.getJSONObject("current").getJSONObject("condition").getString("text");
                    String conditionIcon = response.getJSONObject("current").getJSONObject("condition").getString("text");
                    Picasso.get().load("http:".concat(conditionIcon)).into(IconIV);
                    CondiTV.setText(condition);
                    if (isDay == 1) {

                        //morning
                        Picasso.get().load("https://i.pinimg.com/originals/0b/7a/09/0b7a090bee2409d9f6537773c7fcd671.jpg").into(BacgroundIV);
                    } else {
                        Picasso.get().load("https://i.pinimg.com/564x/86/3e/63/863e63985b8c5d225b9c7f083236066c.jpg").into(BacgroundIV);
                    }

                    JSONObject forecastObj = response.getJSONObject("forecast");
                    JSONObject forecastO = forecastObj.getJSONObject("forecastday");
                    JSONArray hourArray = forecastO.getJSONArray("hour");

                    for (int i = 0; i < hourArray.length(); i++) {
                        JSONObject hourObj = hourArray.getJSONObject(i);
                        String time = hourObj.getString("time");
                        String temper = hourObj.getString("temp_c");
                        String img = hourObj.getJSONObject("condition").getString("icon");
                        String wind = hourObj.getString("wind_kph");
                        weatherRvModalArrayList.add(new Weather_RVModal(time, temper, img, wind));

                    }
                    Weather_RVAdapter.notifyDataSetChanged();

                } catch (JSONException e) {
                    e.printStackTrace();
                }


            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(MainActivity.this, "Please enter valid location name..", Toast.LENGTH_SHORT).show();


            }
        });

        requestedQueue.add(jsonObjectRequest);
    }
}



