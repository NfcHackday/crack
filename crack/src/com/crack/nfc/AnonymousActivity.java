package com.crack.nfc;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.crack.storage.Friend;
import com.crack.storage.Repository;
import com.facebook.android.AsyncFacebookRunner;
import com.facebook.android.BaseRequestListener;
import com.facebook.android.DialogError;
import com.facebook.android.Facebook;
import com.facebook.android.Facebook.DialogListener;
import com.facebook.android.FacebookError;
import com.facebook.android.Util;

public class AnonymousActivity extends Activity {

	Facebook facebook = new Facebook("362045603840068");
	String FILENAME = "AndroidSSO_data";
    private SharedPreferences mPrefs;
    private static AsyncFacebookRunner mAsyncRunner;
	
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mAsyncRunner = new AsyncFacebookRunner(facebook);
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
        	if(Repository.getInstance(AnonymousActivity.this).getMe() == null){
        		getUserDetails();
        	}
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
                getUserDetails();
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
	
	protected void getUserDetails() {
        mAsyncRunner.request("me", new Bundle(), new graphApiRequestListener());
	}

	@Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        facebook.authorizeCallback(requestCode, resultCode, data);
    }
	
	/*
     * Callback after a given Graph API request is executed Get the response and
     * show it.
     */
    public class graphApiRequestListener extends BaseRequestListener {

        @Override
        public void onComplete(final String response, final Object state) {
            try {
                JSONObject json = Util.parseJson(response);
                Friend me = new Friend();
                me.setEmail(json.getString("email"));
                me.setImageUrl("http://graph.facebook.com/" + json.getString("id")+ "/picture?type=square");
                me.setName(json.getString("name"));
                Repository.getInstance(AnonymousActivity.this).setMe(me);
                Log.d("oren", String.valueOf(me));
                Log.d("oren", json.toString(2));
            } catch (JSONException e) {
            	Log.d("oren",e.getMessage());
                e.printStackTrace();
            } catch (FacebookError e) {
            	Log.d("oren",e.getMessage());
                e.printStackTrace();
            }
        }

        public void onFacebookError(FacebookError error) {
            Log.e("oren", "onFacebookError");
        }

    }
    
    
	
}
