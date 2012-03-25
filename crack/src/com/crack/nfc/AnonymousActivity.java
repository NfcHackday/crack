package com.crack.nfc;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.test.suitebuilder.annotation.Smoke;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.facebook.android.DialogError;
import com.facebook.android.Facebook;
import com.facebook.android.Facebook.DialogListener;
import com.facebook.android.FacebookError;

public class AnonymousActivity extends Activity {

	Facebook facebook = new Facebook("362045603840068");
	String FILENAME = "AndroidSSO_data";
    private SharedPreferences mPrefs;
	
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mPrefs = getPreferences(MODE_PRIVATE);
        String access_token = mPrefs.getString("access_token", null);
        long expires = mPrefs.getLong("access_expires", 0);
        if(access_token != null) {
            facebook.setAccessToken(access_token);
        }
        if(expires != 0) {
            facebook.setAccessExpires(expires);
        }
        if(facebook.isSessionValid()) {
        	smokeCrack();
        	finish();
        }
        else{
        	setContentView(R.layout.anon);
        }
    }	
	
	private void smokeCrack() {
		Intent profileScreen = new Intent(AnonymousActivity.this,CrackActivity.class);
		startActivity(profileScreen);
	}
	
	public void fbLogin(View v){
		//Create the FB login	

        facebook.authorize(this, new String[] {"user_about_me", "user_photos", "email", "user_birthday"}, new DialogListener() {
            //@Override
            public void onComplete(Bundle values) {
                SharedPreferences.Editor editor = mPrefs.edit();
                editor.putString("access_token", facebook.getAccessToken());
                editor.putLong("access_expires", facebook.getAccessExpires());
                editor.commit();
                smokeCrack();
                
                AnonymousActivity.this.finish();
            }


            //@Override
            public void onFacebookError(FacebookError error) {
            	Toast.makeText(AnonymousActivity.this, "onFacebookError", Toast.LENGTH_LONG);
            }

            //@Override
            public void onError(DialogError e) {
            	Toast.makeText(AnonymousActivity.this, "onError", Toast.LENGTH_LONG);
            	
            }

            //@Override
            public void onCancel() {}
        });
    	
	}
	
	@Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        facebook.authorizeCallback(requestCode, resultCode, data);
    }
	
}
