package com.example.mapdemo;

import android.support.v7.app.ActionBarActivity;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.SupportMapFragment;
import android.location.*;
import android.content.*;
import android.util.Log;
import android.widget.Toast;
import android.view.View.OnClickListener;
import android.view.View;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.HttpStatus;
import org.apache.http.client.ResponseHandler;
import android.os.StrictMode;
import java.util.ArrayList;
import java.io.IOException;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;
import org.apache.http.entity.StringEntity;

public class MainActivity extends FragmentActivity implements LocationListener, OnClickListener {
	//六甲山：北緯34度46分41秒, 東経135度15分49秒
	private double mLatitude = 34.0d + 46.0d/60 + 41.0d/(60*60);
	private double mLongitude = 135.0d + 15.0d/60 + 49.0d/(60*60);
	private GoogleMap mMap = null;
	LocationManager locman;
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        View seekButton = findViewById(R.id.seek_button);
        seekButton.setOnClickListener(this);
        View setPinButton = findViewById(R.id.setpin_button);
        setPinButton.setOnClickListener(this);
		StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder().permitAll().build());
        
        locman = (LocationManager)getSystemService(Context.LOCATION_SERVICE);
        mMap = ((SupportMapFragment)getSupportFragmentManager().findFragmentById(R.id.map) ).getMap();    
        if (mMap != null) {
        	LatLng location = new LatLng(mLatitude, mLongitude);
        	CameraPosition cameraPos = new CameraPosition.Builder()
        	.target(location).zoom(10.0f)
        	.bearing(0).build();
        	mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPos));
        	// マーカー設定
        	MarkerOptions options = new MarkerOptions();
        	options.position(location);
        	mMap.addMarker(options);
        }
    }

    @Override public void onClick(View v) {
    	switch(v.getId()){ 
    		case R.id.seek_button:
    	        Toast.makeText(this, "Start Seeking", Toast.LENGTH_LONG).show();
    	        if (locman != null){
    	            locman.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0,0,this);          
    	        }
    	        break;
    		case R.id.setpin_button:
            	// マーカー設定
    	        Toast.makeText(this, "Add Marker!", Toast.LENGTH_LONG).show();
            	LatLng location = new LatLng(mLatitude, mLongitude);
            	MarkerOptions options = new MarkerOptions();
            	options.position(location);
            	mMap.addMarker(options);

            	String url="http://tanbozensen.herokuapp.com/api/tanbos/";
    			HttpClient httpClient = new DefaultHttpClient();
    			HttpPost post = new HttpPost(url);
    			     			
    			JSONObject jsonObject = new JSONObject();

    			HttpResponse res = null;

    			try{
//    				jsonObject.put("id", "5");
//    				jsonObject.put("latitude", "12.0290141");
//    				jsonObject.put("longitude", "129.0290141");
//    				jsonObject.put("phase", "1");
//    				jsonObject.put("rice_type", "1");
//    				jsonObject.put("done_date", "2015-04-29");
//    				StringEntity se = new StringEntity(jsonObject.toString());
//    				post.setEntity(se);
//    				post.setHeader("Accept", "application/json");
//    				post.setHeader("Content-Type", "application/json");
//    				res = httpClient.execute(post);

    				String strJson = "{ \"latitude\": 10.0290141, \"longitude\": 129.0290141, \"phase\": 1, \"rice_type\": 1, \"done_date\": \"2015-04-29\" }";
    				StringEntity body = new StringEntity(strJson);
    				post.addHeader("Content-type", "application/json");
    				post.setEntity(body);
    		        String result = httpClient.execute(post, new ResponseHandler<String>(){
    		            public String handleResponse(HttpResponse response) throws IOException{
    		                switch(response.getStatusLine().getStatusCode()){
    		                case HttpStatus.SC_OK:
    		                    return EntityUtils.toString(response.getEntity(), "UTF-8");
    		                case HttpStatus.SC_NOT_FOUND:
    		                    return "404";
    		                default:
//    		                    return "unknown";
    		                    return EntityUtils.toString(response.getEntity(), "UTF-8");
    		                }
    		            }
    		        });
    			    Toast.makeText(this, result, Toast.LENGTH_LONG).show();
//    				int status = res.getStatusLine().getStatusCode();
//    			    if ( status != HttpStatus.SC_OK ) {
//	    			    Toast.makeText(this, "POST SUCCESS!!!", Toast.LENGTH_LONG).show();
//    			    }
//    			    else {
//        			    Toast.makeText(this, "ErrorCode:" + String.valueOf(status), Toast.LENGTH_LONG).show();
//    			    }
    			} catch (Exception e) {
    			    e.printStackTrace();
    			    Toast.makeText(this, "POST FAILURE!", Toast.LENGTH_LONG).show();
    			}

    			break;
    	}
    }    
    
    @Override
    protected void onResume(){
        if (locman != null){
            locman.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0,0,this);          
        }
        super.onResume();
    }
     
    @Override
    protected void onPause(){
        if (locman != null){
            locman.removeUpdates(this);
        }
        super.onPause();
    }
    
    @Override
    public void onLocationChanged(Location location){
    	double tempLatitude, tempLongitude;
    	double Do, Fun, Byou;
    	LatLng Position;
    	
        Log.v("----------", "----------");
        Log.v("Latitude", String.valueOf(location.getLatitude()));
        Toast.makeText(this, String.valueOf(location.getLatitude()), Toast.LENGTH_LONG).show();
        Log.v("Longitude", String.valueOf(location.getLongitude()));
        Toast.makeText(this, String.valueOf(location.getLongitude()), Toast.LENGTH_LONG).show();
        Log.v("Accuracy", String.valueOf(location.getAccuracy()));
        Log.v("Altitude", String.valueOf(location.getAltitude()));
        Log.v("Time", String.valueOf(location.getTime()));
        Log.v("Speed", String.valueOf(location.getSpeed())); 
        Log.v("Bearing", String.valueOf(location.getBearing()));

        tempLatitude = location.getLatitude();
        tempLongitude = location.getLongitude();

        Do = (int)tempLatitude;
        tempLatitude = tempLatitude - Do;
        tempLatitude = tempLatitude * 100;
        Fun = (int)(tempLatitude);
        tempLatitude = tempLatitude - Fun;
        tempLatitude = tempLatitude * 100;
        Byou = tempLatitude;
//        Toast.makeText(this, String.valueOf(Do), Toast.LENGTH_LONG).show();
//        Toast.makeText(this, String.valueOf(Fun), Toast.LENGTH_LONG).show();
//        Toast.makeText(this, String.valueOf(Byou), Toast.LENGTH_LONG).show();
    	mLatitude = Do + Fun/60 + Byou/(60*60);

        Do = (int)tempLongitude;
        tempLongitude = tempLongitude - Do;
        tempLongitude = tempLongitude * 100;
        Fun = (int)(tempLongitude);
        tempLongitude = tempLongitude - Fun;
        tempLongitude = tempLongitude * 100;
        Byou = tempLongitude;
//      Toast.makeText(this, String.valueOf(Do), Toast.LENGTH_LONG).show();
//      Toast.makeText(this, String.valueOf(Fun), Toast.LENGTH_LONG).show();
//      Toast.makeText(this, String.valueOf(Byou), Toast.LENGTH_LONG).show();
    	mLongitude = Do + Fun/60 + Byou/(60*60);    	

    	//画面位置を取得先に移動する
    	Position = new LatLng(mLatitude, mLongitude);
    	CameraPosition cameraPos = new CameraPosition.Builder()
    	.target(Position).zoom(10.0f)
    	.bearing(0).build();
    	mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPos));
        
    	//listenerの削除
        if (locman != null){
            locman.removeUpdates(this);
        }
    }
     
    @Override
    public void onProviderDisabled(String provider){
     
    }
     
    @Override
    public void onProviderEnabled(String provider){
    }
     
    @Override
    public void onStatusChanged(String provider, int status, Bundle extras){
        switch(status){
        case LocationProvider.AVAILABLE:
            Log.v("Status","AVAILABLE");
            break;
        case LocationProvider.OUT_OF_SERVICE:
            Log.v("Status","OUT_OF_SERVICE");
            break;
        case  LocationProvider.TEMPORARILY_UNAVAILABLE:
            Log.v("Status","TEMPORARILY_UNAVAILABLE");
            break;
             
        }
    }
}
