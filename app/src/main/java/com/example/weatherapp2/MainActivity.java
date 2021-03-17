package com.example.weatherapp2;

import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

//import android.support.v4.app.ActivityCompat;
//import android.support.v4.content.ContextCompat;
//import android.support.v7.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    TextView cityName;
    Button searchButton;
    TextView result;

    class Weather extends AsyncTask<String,Void,String>{  //First String means URL is in String, Void mean nothing, Third String means Return type will be String

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
                while (data != -1){
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

    public void search(View view){
        cityName = findViewById(R.id.cityName);
        searchButton = findViewById(R.id.searchButton);
        result = findViewById(R.id.result);

        String cName = cityName.getText().toString();


        String content;
        Weather weather = new Weather();
        try {
            content = weather.execute("https://api.openweathermap.org/data/2.5/weather?q=" +
                    cName+"&appid=bbb798a864678b778293bf8ea8b4190c").get();
            //First we will check data is retrieve successfully or not
            Log.i("contentData",content);

            //JSON
            JSONObject jsonObject = new JSONObject(content);
            String weatherData = jsonObject.getString("weather");
            String mainTemperature = jsonObject.getString("main"); //this main is not part of weather array, it's seperate variable like weather
            String windData = jsonObject.getString("wind");
            double visibility;
//            Log.i("weatherData",weatherData);
            //weather data is in Array
            JSONArray array = new JSONArray(weatherData);

            String main = "";
            String description = "";
            String temperature = "";
            String humidity = "";
            String pressure = "";
            String windSpeed = "";
            String windDirection = "";

            for(int i=0; i<array.length(); i++){
                JSONObject weatherPart = array.getJSONObject(i);
                main = weatherPart.getString("main");
                description = weatherPart.getString("description");
            }

            JSONObject mainPart = new JSONObject(mainTemperature);
            JSONObject windPart = new JSONObject(windData);
            temperature = mainPart.getString("temp");
            humidity = mainPart.getString("humidity");
            pressure = mainPart.getString("pressure");
            windSpeed =  windPart.getString("speed");
//            windDirection = windPart.getString("deg" + " degrees");


            visibility = Double.parseDouble(jsonObject.getString("visibility"));
            //By default visibility is in meter
            int visibiltyInKilometer = (int) visibility/1000;
            double windSpeedInKilometers = Double.parseDouble(windPart.getString("speed"));
            //By default visibility is in meter



            Log.i("Temperature",temperature);

            /*Log.i("main",main);
            Log.i("description",description);*/

            String resultText = "Main :                     " + main +
                    "\nDescription :          " + description +
                    "\nTemperature :        " + String.format("%.2f",kelvinTooFahrenheit(Float.parseFloat(temperature))) + "*F" +
                    "\nVisibility :               " + String.format("%.2f",visibiltyInKilometer/1.609) + "Mi" +
                    "\nHumidity :              " + humidity +"g.kg-1"+
                    "\nPressure :              " + pressure + "Hg" +
                    "\nWind Speed :         " +String.format("%.2f",(windSpeedInKilometers/1000)/1.609) + "Mi";
//                    "\nWind Direction :        " + windDirection;


            //Now we will show this result on screen
            result.setText(resultText);

            // Get the ImageView showing the weather icon
            ImageView weatherIconImageView = (ImageView) findViewById(R.id.weatherImageView);

            // show the correct weather icon
            if(main.equalsIgnoreCase("clouds")) {
                weatherIconImageView.setImageResource(R.drawable.cloudy_icon);
            } else if(main.equalsIgnoreCase("clear")) {
                weatherIconImageView.setImageResource(R.drawable.clear_icon);
            }


        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Set the city name to the default location
        ((TextView) findViewById(R.id.cityName)).setText("Palo Alto");

        // Click on the Search button
        findViewById(R.id.searchButton).performClick();

    }
    private double kelvinTooFahrenheit(double kelvin){
        return (kelvin - 273.15) * (9/5) + 32;
    }

    private double kelvinToCelsius(double kelvin){
        return kelvin - 273.15;
    }


}
