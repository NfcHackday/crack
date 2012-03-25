package com.crack.nfc;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;

import com.crack.storage.Friend;
import com.crack.storage.Repository;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;

import android.os.AsyncTask;
import android.os.Handler;
import android.service.wallpaper.WallpaperService;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.View;

public class FriendWallpaperService extends WallpaperService {

    private final Handler mHandler = new Handler();


	private static HashMap<Friend,Bitmap> images = new HashMap<Friend, Bitmap>();
	private static HashMap<Friend,ImageDrawingData> imageData = new HashMap<Friend, ImageDrawingData>();
	private static ArrayList<Friend> friends;
	
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
		        //data[0].v.postInvalidate();
		        Log.d("Done", "Loading image");
		    } catch (IOException e) {
		        e.printStackTrace();
		        return null;
		    }
			return null;
	 	    
	     }

	 }
    
    @Override
    public void onCreate() {
        super.onCreate();
        
        friends = Repository.getInstance(getApplicationContext()).getFriends();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public Engine onCreateEngine() {
        return new CubeEngine();
    }

    class CubeEngine extends Engine {

        private final Paint mPaint = new Paint();

        private final Runnable mDrawCube = new Runnable() {
            public void run() {
                drawFrame();
            }
        };
        private boolean mVisible;
        
        @Override
        public void onCreate(SurfaceHolder surfaceHolder) {
            super.onCreate(surfaceHolder);

            // By default we don't get touch events, so enable them.
            setTouchEventsEnabled(true);
        }

        @Override
        public void onDestroy() {
            super.onDestroy();
            mHandler.removeCallbacks(mDrawCube);
        }

        @Override
        public void onVisibilityChanged(boolean visible) {
            mVisible = visible;
            if (visible) {
                drawFrame();
            } else {
                mHandler.removeCallbacks(mDrawCube);
            }
        }

        @Override
        public void onSurfaceChanged(SurfaceHolder holder, int format, int width, int height) {
            super.onSurfaceChanged(holder, format, width, height);

            drawFrame();
        }

        @Override
        public void onSurfaceCreated(SurfaceHolder holder) {
            super.onSurfaceCreated(holder);
        }

        @Override
        public void onSurfaceDestroyed(SurfaceHolder holder) {
            super.onSurfaceDestroyed(holder);
            mVisible = false;
            mHandler.removeCallbacks(mDrawCube);
        }

        @Override
        public void onOffsetsChanged(float xOffset, float yOffset,
                float xStep, float yStep, int xPixels, int yPixels) {
            drawFrame();
        }

        /*
         * Store the position of the touch event so we can use it for drawing later
         */
        @Override
        public void onTouchEvent(MotionEvent event) {
            if (event.getAction() == MotionEvent.ACTION_UP) {

            	

				int imageSize = this.getDesiredMinimumWidth()/5;
				
				int touchRow = (int) Math.ceil(event.getY()/imageSize);
				int touchColumn = (int) Math.ceil(event.getX()/imageSize);
				
				int touchItem = ((touchRow-1)*5)+touchColumn;
				
				if (touchItem <= friends.size()) {
					Friend touchedFriend = (Friend) friends.get(touchItem-1);
					
					Log.d("Touched Friend",String.valueOf(touchItem));
					/*
					Intent emailIntent = new Intent(android.content.Intent.ACTION_SEND);
					emailIntent.putExtra(android.content.Intent.EXTRA_EMAIL, new String[] {touchedFriend.getEmail()});
					emailIntent.setType("text/plain");
					emailIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
					startActivity(Intent.createChooser(emailIntent, "It's been too long "+touchedFriend.getName()+"! Let's meet up on Friday."));
					*/
				}
            }
            super.onTouchEvent(event);
        }

        /*
         * Draw one frame of the animation. This method gets called repeatedly
         * by posting a delayed Runnable. You can do any drawing you want in
         * here. This example draws a wireframe cube.
         */
        void drawFrame() {
            final SurfaceHolder holder = getSurfaceHolder();

            Canvas c = null;
            try {
                c = holder.lockCanvas();
                if (c != null) {
                    // draw something
                    drawFriends(c);
                }
            } finally {
                if (c != null) holder.unlockCanvasAndPost(c);
            }

            // Reschedule the next redraw
            mHandler.removeCallbacks(mDrawCube);
            if (mVisible) {
                mHandler.postDelayed(mDrawCube, 5000);
            }
        }

        void drawFriends(Canvas c) {
			int imageSize = this.getDesiredMinimumWidth()/5;
			
			// Start at top left
			int currentY = 0;
			int currentX = 0;
			
			
			
			for (Friend f : friends) {
				
				
				Rect r = new Rect( currentX,currentY, currentX+imageSize,currentY+ imageSize);
				
				ImageDrawingData data = new ImageDrawingData();
				
				data.friend = f;
				data.r = r;
				data.p = mPaint;
				data.c = c;
				//data.v = this;
				
				imageData.put(f, data);
				
				Bitmap fImage = images.get(f);
				if (fImage == null) {
					if (!images.containsKey(f)) {
						// Remember it's already requested....
						images.put(f,null);
						// Download asynch
						new DownloadImageTask().execute(data);
					}
				} else {
					// Calculate staleness over 1 hour
					int timeoutSeconds = 180;
					Long staleness = System.currentTimeMillis() - f.getStaleness();
					double staleSeconds = (staleness/1000) ;
					double transparency = 255-(255 * (staleSeconds/timeoutSeconds)) ;
					
					if (staleSeconds >= timeoutSeconds) {
						
						transparency = 0;
					}
					
					mPaint.setAlpha((int)transparency);
					
					c.drawBitmap(fImage, null, r, mPaint);
					Log.d("Drawing",transparency+", "+String.valueOf(mPaint.getAlpha()));
				}
				
				currentX += imageSize;
				if (currentX > (this.getDesiredMinimumWidth()-imageSize)) {
					currentY += imageSize;
					currentX = 0;
				}
				
			}
        }

    }
}
