package com.example.weatherapp2;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.os.AsyncTask;
import android.os.Build;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;


public class MainActivity extends AppCompatActivity {

    TextView result;
    Button locationButton;
//    FusedLocationProviderClient fusedLocationProviderClient;
//    List<Address> addresses;
LocationManager lm = (LocationManager)getSystemService(Context.LOCATION_SERVICE);

    double longitude = 0;
    double latitude = 0;
    class Weather extends AsyncTask<String, Void, String> {  //First String means URL is in String, Void mean nothing, Third String means Return type will be String

        @Override
        protected String doInBackground(String... address) {
            //String... means multiple address can be send. It acts as array
            try {
                URL url = new URL(address[0]);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                //Establish connection with address
                connection.connect();

                //retrieve data from url
                InputStream is = connection.getInputStream();
                InputStreamReader isr = new InputStreamReader(is);

                //Retrieve data and return it as String
                int data = isr.read();
                String content = "";
                char ch;
                while (data != -1) {
                    ch = (char) data;
                    content = content + ch;
                    data = isr.read();
                }
                return content;

            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

            return null;
        }
    }

    public void search(View view) {
        result = findViewById(R.id.result);

        if(checkAndRequestPermissions() == true){
            longitude = lm.getLastKnownLocation().getLatitude();
        }

        Weather weather = new Weather();
        try {
            content = weather.execute("https://api.openweathermap.org/data/2.5/weather?lat=" + longitude + "&lon=" + latitude +
                    "&appid=bbb798a864678b778293bf8ea8b4190c");
            //First we will check data is retrieve successfully or not
            Log.i("contentData", content);

            //JSON
            JSONObject jsonObject = new JSONObject(content);
            String weatherData = jsonObject.getString("weather");
            String mainTemperature = jsonObject.getString("main"); //this main is not part of weather array, it's seperate variable like weather

            double visibility;
//            Log.i("weatherData",weatherData);
            //weather data is in Array
            JSONArray array = new JSONArray(weatherData);

            String main = "";
            String description = "";
            String temperature = "";
            String humidity = "";
            String pressure = "";
            for (int i = 0; i < array.length(); i++) {
                JSONObject weatherPart = array.getJSONObject(i);
                main = weatherPart.getString("main");
                description = weatherPart.getString("description");
            }

            JSONObject mainPart = new JSONObject(mainTemperature);
            temperature = mainPart.getString("temp");
            humidity = mainPart.getString("humidity");
            pressure = mainPart.getString("pressure");

            visibility = Double.parseDouble(jsonObject.getString("visibility"));
            //By default visibility is in meter
            int visibiltyInKilometer = (int) visibility / 1000;

            Log.i("Temperature", temperature);
            Log.i("main", main);
            Log.i("description", description);
            Log.i("Humidity", humidity);
            Log.i("Pressure", pressure);

            String resultText = "Main :                     " + main +
                    "\nDescription :        " + description +
                    "\nTemperature :        " + temperature + "*C" +
                    "\nVisibility :              " + visibiltyInKilometer + " KM" +
                    "\nHumidity :        " + humidity +
                    "\nPressure :        " + pressure;

            result.setText(resultText);

            //Now we will show this result on screen

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

//    private void getLocation() {
//        if (androidx.core.app.ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && androidx.core.app.ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
//            // TODO: Consider calling
//            //    ActivityCompat#requestPermissions
//            // here to request the missing permissions, and then overriding
//            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
//            //                                          int[] grantResults)
//            // to handle the case where the user grants the permission. See the documentation
//            // for ActivityCompat#requestPermissions for more details.
//            return;
//        }
//        if (ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
//            fusedLocationProviderClient.getLastLocation()
//                    .addOnSuccessListener(this, new OnSuccessListener<Location>() {
//                        @Override
//                        public void onSuccess(Location location) {
//                            // Got last known location. In some rare situations this can be null.
//                            if (location != null) {
//                                // Logic to handle location object
//                            }
//                        }
//                    });
//        }
//
//    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        locationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

    }
    public boolean checkAndRequestPermissions() {
        int internet = ContextCompat.checkSelfPermission(this,
                Manifest.permission.INTERNET);
        int loc = ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_COARSE_LOCATION);
        int loc2 = ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION);
        List<String> listPermissionsNeeded = new ArrayList<>();

        if (internet != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.INTERNET);
        }
        if (loc != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.ACCESS_COARSE_LOCATION);
        }
        if (loc2 != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.ACCESS_FINE_LOCATION);
        }
        if (!listPermissionsNeeded.isEmpty()) {
            ActivityCompat.requestPermissions((Activity) mContext, listPermissionsNeeded.toArray
                    (new String[listPermissionsNeeded.size()]), 1);
            return false;
        }
        return true;
    }

}
