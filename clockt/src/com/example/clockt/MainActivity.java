package com.example.clockt;

/*
 * This app is an analog clock to show the time by a drawing custom view. It's changes color every minute just for the fun
 * 
 * Author:Shimrit Or 02/08/2014
 */


import android.app.Activity;
import android.content.res.Configuration;
import android.os.Bundle;
import android.widget.Toast;

public class MainActivity extends Activity {
	
	

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
			
		
	}
	
	  @Override
	    public void onConfigurationChanged(Configuration newConfig) {
	        super.onConfigurationChanged(newConfig);
	 
	        // Checks the orientation of the screen for landscape and portrait
	        if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
	            Toast.makeText(this, "landscape", Toast.LENGTH_SHORT).show();
	        } else if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT){
	            Toast.makeText(this, "portrait", Toast.LENGTH_SHORT).show();
	        }
}
	
	 
}