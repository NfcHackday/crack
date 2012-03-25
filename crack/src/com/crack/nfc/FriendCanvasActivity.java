package com.crack.nfc;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Vector;

import com.crack.storage.Friend;
import com.crack.storage.Repository;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

public class FriendCanvasActivity extends Activity {

	private static HashMap<Friend,Bitmap> images = new HashMap<Friend, Bitmap>();
	private static HashMap<Friend,ImageDrawingData> imageData = new HashMap<Friend, FriendCanvasActivity.ImageDrawingData>();
	
	private class ImageDrawingData {
		
		private Friend friend;
		private Rect r;
		private Paint p;
		private Canvas c;
		private View v;
	}
	
	public class DownloadImageTask extends AsyncTask<ImageDrawingData, Integer, Long> {
	     
		 protected Long doInBackground(ImageDrawingData... data) {
	 	    try {
		        URL url = new URL(data[0].friend.getImageUrl());
		        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
		        connection.setDoInput(true);
		        connection.connect();
		        InputStream input = connection.getInputStream();
		        Bitmap bitmap = BitmapFactory.decodeStream(input);
		        images.put(data[0].friend, bitmap);
		        data[0].v.postInvalidate();
		        Log.d("Done", "Loading image");
		    } catch (IOException e) {
		        e.printStackTrace();
		        return null;
		    }
			return null;
	 	    
	     }

	 }
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(new FriendCanvas(getApplicationContext()));
      
    }
    
    public class FriendCanvas extends View {
    	ArrayList<Friend> friends;
    	
		public FriendCanvas(Context context) {
			super(context);
			friends = Repository.getInstance(context).getFriends();
			
			this.setOnTouchListener(new View.OnTouchListener() {
				
				public boolean onTouch(View view, MotionEvent touchEvent) {

					int imageSize = view.getWidth()/5;
					
					int touchRow = (int) Math.ceil(touchEvent.getY()/imageSize);
					int touchColumn = (int) Math.ceil(touchEvent.getX()/imageSize);
					
					Toast.makeText(getApplicationContext(), "Touched item "+touchRow*touchColumn, Toast.LENGTH_SHORT).show();
					
					return false;
				}
			});
			
			// Start the data loading
		}
    	
		@Override
		protected void onDraw(Canvas canvas) {
			// TODO Auto-generated method stub
			//super.onDraw(canvas);
			
			int imageSize = this.getWidth()/5;
			
			Paint p = new Paint();
			
			/* Start at bottom right
			int currentY = this.getHeight()-imageSize;
			int currentX = this.getWidth();
			*/
			
			// Start at top left
			int currentY = 0;
			int currentX = 0;
			
			for (Friend f : friends) {
				

				
				int i = (255 * f.getStaleness()) / 100 ;
				//p.setAlpha(i);
				
				/* From bottom
				currentX -= imageSize;
				
				if (currentX < 0) {
					currentX = this.getWidth();
					currentY -= imageSize ;
				}
				*/
				
				Rect r = new Rect( currentX,currentY, currentX+imageSize,currentY+ imageSize);
				
				ImageDrawingData data = new ImageDrawingData();
				
				data.friend = f;
				data.r = r;
				data.p = p;
				data.c = canvas;
				data.v = this;
				
				imageData.put(f, data);
				
				Bitmap fImage = images.get(f);
				if (fImage == null) {
					new DownloadImageTask().execute(data);
				} else {
					canvas.drawBitmap(fImage, null, r, p);
				}
				
				currentX += imageSize;
				if (currentX > (this.getWidth()-imageSize)) {
					currentY += imageSize;
					currentX = 0;
				}
				
			}
			Toast.makeText(getApplicationContext(), "done!", Toast.LENGTH_SHORT).show();
			
		}
		
    }
    
    
}