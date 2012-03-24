package com.crack.nfc;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class AnonymousActivity extends Activity {

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    	setContentView(R.layout.anon);
    	
        Button buttonFacebookSignin = (Button) findViewById(R.id.facebookSignin);
        
        buttonFacebookSignin.setOnClickListener(new View.OnClickListener() {
			
			public void onClick(View arg0) {
				Toast.makeText(getApplicationContext(), "Sign in", Toast.LENGTH_SHORT).show();
				
				// Orene's code here....
				
				CrackActivity.authenticated = true;
				
	        	Intent profileScreen = new Intent(AnonymousActivity.this,CrackActivity.class);
	        	startActivity(profileScreen);
	        	
	        	AnonymousActivity.this.finish();
	        	
	        	return;
			}
		});
        
    }	
	
}
