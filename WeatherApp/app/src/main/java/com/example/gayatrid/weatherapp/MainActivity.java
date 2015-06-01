package com.example.gayatrid.weatherapp;

import android.app.Activity;
import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import org.json.JSONException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;


public class MainActivity extends ActionBarActivity {

    ProgressBar progress;
    ArrayList<WeatherDetails> weatherDetails = new ArrayList<>();
    TextView textView;
    TextView textView2;
    private double latitude=34.0672280;
    private double longitude=-118.1667410;

    LocationManager locationManager;
    LocationListener locationListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        progress = (ProgressBar) findViewById(R.id.progressBar);
        textView2=(TextView)findViewById(R.id.textView2);
        textView=(TextView)findViewById(R.id.textView);



        getLocation();

        startLoadTask(MainActivity.this);


    }

    @Override
    protected void onPause(){
        super.onPause();
        if(locationManager != null && locationListener != null){
            locationManager.removeUpdates(locationListener);
        }
    }

    public void startLoadTask(Context c){
        if (isOnline()) {
            Log.d("start load task ","in online");
            Toast.makeText(this, "in online", Toast.LENGTH_LONG).show();

            textView.setText("in Online");
            LoadPhotos task = new LoadPhotos();
            task.execute();
        } else {
            Log.d("start load task ","in not online");
            progress.setVisibility(View.GONE);
            textView.setText("Not Online");
            Toast.makeText(this, "Not online", Toast.LENGTH_LONG).show();
        }
    }


    public void getLocation(){

        locationManager = (LocationManager)this.getSystemService(Context.LOCATION_SERVICE);
//        boolean isGPRProvider=locationManager.isProviderEnabled(locationManager.GPS_PROVIDER);
//        boolean isNetworkProvider=locationManager.isProviderEnabled(locationManager.NETWORK_PROVIDER);

        Log.d("get location ","after request location updates location listener");

        locationListener = new LocationListener() {


            @Override
            public void onLocationChanged(Location location) {
                Log.d("get location ","location listener");
                latitude = location.getLatitude();
                longitude = location.getLongitude();
                Toast.makeText(MainActivity.this, "latitude: " + latitude + "longitude: " + longitude, Toast.LENGTH_LONG).show();
                Log.d("get location ","before start load task");
               // startLoadTask(MainActivity.this);
                Log.d("get location ","after start load task");
            }



            @Override
            public void onStatusChanged(String s, int i, Bundle bundle) {}

            @Override
            public void onProviderEnabled(String s) {}

            @Override
            public void onProviderDisabled(String s) {}
        };

//        Log.d("latitude",latitude+"");
//        Log.d("longitude",longitude+"");
//        if(latitude==-1&&longitude==-1)
//        {
//            Location location=null;
//             if(isNetworkProvider)
//             location=locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
//            if(isGPRProvider)
//                location=locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
//            Log.d("is gps provider",isGPRProvider+"");
//            Log.d("is network provider",isNetworkProvider+"");
//
//            if(location!=null)
//            {
//                Log.d("location latitude",+location.getLatitude()+"");
//                Log.d("location longitude",+location.getLongitude()+"");
//                latitude=location.getLatitude();
//                longitude=location.getLongitude();
//            }
//
//        }

        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 1, locationListener);

    }

    public ArrayList<WeatherDetails> getWeatherDetails() {
        return weatherDetails;
    }

    public void setWeatherDetails(ArrayList<WeatherDetails> weatherDetails) {
        this.weatherDetails = weatherDetails;
    }



    public boolean isOnline()
    {
       ConnectivityManager connectivityManager=(ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo=connectivityManager.getActiveNetworkInfo();
        return (networkInfo!=null&&networkInfo.isConnected());
    }




    public void showList()
    {
        Log.d("in show list",weatherDetails.size()+"");
        FragmentManager fm=getSupportFragmentManager();
        FragmentTransaction ft=fm.beginTransaction();
        ft.add(R.id.container,new WeatherListFragment());
        ft.commit();
    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private class LoadPhotos extends AsyncTask<String, Long, Long> {
        HttpURLConnection connection = null;
        ArrayList<WeatherDetails> weatherReport;

        @Override
        protected void onPreExecute() {
            textView2.setText("Pre execute");
            progress.setVisibility(View.VISIBLE);
            super.onPreExecute();
        }

        @Override
        protected void onProgressUpdate(Long... values) {
            textView2.setText("Progress update");
            super.onProgressUpdate(values);
        }

        @Override
        protected Long doInBackground(String... strings) {
            textView2.setText("Before data string");
            String dataString = "http://api.openweathermap.org/data/2.5/forecast/daily?lat="+latitude+"&lon="+longitude+"&cnt=10&mode=json";

            //    Log.i(Constants.TAG, dataString);
            try {
                URL dataUrl = new URL(dataString);
                textView2.setText("after data url");
                connection = (HttpURLConnection) dataUrl.openConnection();
//                textView2.setText("after connection");
                connection.connect();
                //textView2.setText("after connection connect");
                int status = connection.getResponseCode();
             //   Toast.makeText(getApplicationContext(), "after response code", Toast.LENGTH_LONG).show();
               // textView2.setText("after response code");
                Log.d("TAG", "status " + status);
                //if it is successful
                if (status == 200) {
                    InputStream is = connection.getInputStream();
               //     textView2.setText("after input stream");
                    BufferedReader reader = new BufferedReader(new InputStreamReader(is));
                    String responseString;
                    StringBuilder sb = new StringBuilder();

                    while ((responseString = reader.readLine()) != null) {
                  //      textView2.setText(responseString);
                        sb = sb.append(responseString);
                    //    Toast.makeText(getApplicationContext(), sb, Toast.LENGTH_LONG).show();

                    }
                    String weatherData = sb.toString();
                    weatherReport = WeatherDetails.makeWeatherReport(weatherData);
                    for(int i=0;i<weatherReport.size();i++) {
                        Log.d("created objects", weatherReport.get(i).toString());
                    }

                    return 0l;
                } else {
                    return 1l;
                }
            } catch (MalformedURLException e) {
                //  Log.i(Constants.TAG, "Malformed Url");
                e.printStackTrace();
                return 1l;
            } catch (IOException e) {
                e.printStackTrace();
                return 1l;
            } catch (JSONException e) {
                e.printStackTrace();
                return 1l;
            } finally {
                if (connection != null)
                    connection.disconnect();
            }

        }


        @Override
        protected void onPostExecute(Long result) {
            if (result != 1l) {
                textView2.setText("In post execute");
                DataBaseHelper dbHelper = new DataBaseHelper(getApplicationContext());
                dbHelper.clearTable();
                dbHelper.addRows(weatherReport);
                dbHelper.close();
//                if(locationManager != null && locationListener != null){
//                    locationManager.removeUpdates(locationListener);
//                }
                textView.setText(weatherReport.get(0).city);
                textView2.setText("City "+weatherReport.get(0).city+"\n Country "+weatherReport.get(0).country);
               // setWeatherDetails(weatherReport);
                showList();
            } else {
                Toast.makeText(getApplicationContext(), "AsyncTask didn't complete", Toast.LENGTH_LONG).show();
            }
            progress.setVisibility(View.GONE);

        }

    }

    }




