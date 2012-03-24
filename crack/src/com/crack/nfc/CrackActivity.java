package com.crack.nfc;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class CrackActivity extends Activity {
	
	private boolean authenticated = false;
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        if (this.authenticated) {
        	Intent profileScreen = new Intent(this,ProfileActivity.class);
        	startActivity(profileScreen);
        } else {
        	Intent anonScreen = new Intent(this,AnonymousActivity.class);
        	startActivity(anonScreen);
        }
        
    }
}