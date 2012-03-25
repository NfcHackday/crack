package com.crack.nfc;

import java.util.Vector;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

public class FriendCanvasActivity extends Activity {
	
	//static Friend f;
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    
        
//        f = new Friend();
//        f.lastCracked = 100;
//        f.photo = BitmapFactory.decodeResource(getResources(), R.drawable.anton);
//        f.name = "anton";
        
        
        setContentView(new FriendCanvas(getApplicationContext()));
      
    }

    public static class Friend {
    	public int lastCracked;
    	public int photoId;
    	public String name;
    	
    }
    
    public static class FriendProvider {
    	Context context;
    	public FriendProvider(Context c) {
    		context = c;
    	}
    	
    	public Vector<Friend> getFriends() {
    		Vector<Friend> friends = new Vector<Friend>();
    		
    		for (int i=0; i<20; i++) {
    			
    			Friend f = new Friend();
    	        f.lastCracked = i;
    	        f.photoId = R.drawable.anton;
    	        f.name = "anton";
    			friends.add(f);	
    		}
    		return friends;    		
    	}
    	
    }
    
    public class FriendCanvas extends View {
    	Vector<Friend> friends;
    	
		public FriendCanvas(Context context) {
			super(context);
			FriendProvider fp = new FriendProvider(context);
			friends = fp.getFriends();
			
			this.setOnTouchListener(new View.OnTouchListener() {
				
				public boolean onTouch(View view, MotionEvent touchEvent) {

					int imageSize = view.getWidth()/5;
					
					int touchRow = (int) Math.ceil(touchEvent.getY()/imageSize);
					int touchColumn = (int) Math.ceil(touchEvent.getX()/imageSize);

					Toast.makeText(getApplicationContext(), "Touched item "+touchRow*touchColumn, Toast.LENGTH_SHORT).show();
					
					return false;
				}
			});
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
				
				int i = (255 * f.lastCracked) / 100 ;
				p.setAlpha(i);
				
				/* From bottom
				currentX -= imageSize;
				
				if (currentX < 0) {
					currentX = this.getWidth();
					currentY -= imageSize ;
				}
				*/
				
				Rect r = new Rect( currentX,currentY, currentX+imageSize,currentY+ imageSize);
				
				
				//canvas.drawBitmap(f.photo, currentX, currentY, p);
				canvas.drawBitmap(BitmapFactory.decodeResource(getResources(), f.photoId), null, r,  p);
				
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