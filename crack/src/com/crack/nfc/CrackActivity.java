package com.crack.nfc;

import java.io.IOException;
import java.nio.charset.Charset;

import android.app.Activity;
import android.content.Intent;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.nfc.NfcAdapter.CreateNdefMessageCallback;
import android.nfc.NfcAdapter.OnNdefPushCompleteCallback;
import android.nfc.NfcEvent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Parcelable;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Toast;

import com.crack.storage.Friend;
import com.crack.storage.Repository;

public class CrackActivity extends Activity implements CreateNdefMessageCallback, OnNdefPushCompleteCallback {
	private final static String textLump = "Twas brillig, and the slithy toves Did gyre and gimble in the wabe: All mimsy were the borogoves, And the mome raths outgrabe.  Beware the Jabberwock, my son!  The jaws that bite, the claws that catch!  Beware the Jubjub bird, and shun The frumious Bandersnatch! He took his vorpal sword in hand: Long time the manxome foe he sought -- So rested he by the Tumtum tree, And stood awhile in thought.  And, as in uffish thought he stood, The Jabberwock, with eyes of flame, Came whiffling through the tulgey wood, And burbled as it came!  One, two! One, two! And through and through The vorpal blade went snicker-snack!  He left it dead, and with its head He went galumphing back.  And, has thou slain the Jabberwock?  Come to my arms, my beamish boy!  O frabjous day! Callooh! Callay!' He chortled in his joy.  `Twas brillig, and the slithy toves Did gyre and gimble in the wabe; All mimsy were the borogoves, And the mome raths outgrabe.";
    
	private Repository repo;
	
	protected static boolean authenticated = false;
	
	NfcAdapter mNfcAdapter;
    private static final int MESSAGE_SENT = 1;
    String contactLog = "";

	
	/** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        repo = Repository.getInstance(this);
        if (!authenticated) {
        	Intent profileScreen = new Intent(this,AnonymousActivity.class);
        	startActivity(profileScreen);
        	this.finish();
        	return;
        }
        
        for (int i=0; i<10; i++){
        	Friend f = new Friend();
        	f.setEmail("anton" + i + "@gmail.com");
        	f.setName("anton" + i);
        	f.setImageUrl("http://www.corbijn.co.uk/images/photo_selfim_anton_h_2.jpg");
        	repo.addFriend(f);
        }
        
        setContentView(R.layout.profile);
    	
    	ListView lv = (ListView)findViewById(R.id.listView1);
    	lv.setAdapter(new FriendsListAdapter(this));
        // Temp image button for launching friend canvas
        ImageButton ib = (ImageButton) findViewById(R.id.imageButton1);
        
        ib.setOnClickListener(new View.OnClickListener() {
			
			public void onClick(View arg0) {
	        	Intent friendCanvas = new Intent(CrackActivity.this,FriendCanvasActivity.class);
	        	startActivity(friendCanvas);				
			}
			
		});
        // END temp code
        
        //mInfoText = (TextView) findViewById(R.id.textView);
        
        mNfcAdapter = NfcAdapter.getDefaultAdapter(this);
        if (mNfcAdapter == null) {
            Toast.makeText(getApplicationContext(), "NFC is not available on this device.", Toast.LENGTH_LONG).show();
        }
        // Register callback to set NDEF message
        mNfcAdapter.setNdefPushMessageCallback(this, this);
        // Register callback to listen for message-sent success
        mNfcAdapter.setOnNdefPushCompleteCallback(this, this);

    }

    /**
     * Implementation for the CreateNdefMessageCallback interface
     */
//    @Override
    public NdefMessage createNdefMessage(NfcEvent event) {
        String text = messageToBeSent();
        NdefMessage msg = new NdefMessage(
                new NdefRecord[] { createMimeRecord(
                        "application/com.crack.nfc", text.getBytes())
         /**
          * The Android Application Record (AAR) is commented out. When a device
          * receives a push with an AAR in it, the application specified in the AAR
          * is guaranteed to run. The AAR overrides the tag dispatch system.
          * You can add it back in to guarantee that this
          * activity starts when receiving a beamed message. For now, this code
          * uses the tag dispatch system.
          */
          //,NdefRecord.createApplicationRecord("com.example.android.beam")
        });
        return msg;
    }

    /**
     * Implementation for the OnNdefPushCompleteCallback interface
     */
//    @Override
    public void onNdefPushComplete(NfcEvent arg0) {
        // A handler is needed to send messages to the activity when this
        // callback occurs, because it happens from a binder thread
        mHandler.obtainMessage(MESSAGE_SENT).sendToTarget();
    }

    /** This handler receives a message from onNdefPushComplete */
    private final Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
            case MESSAGE_SENT:
                Toast.makeText(getApplicationContext(), "Message sent!", Toast.LENGTH_LONG).show();
                break;
            }
        }
    };
    
    /**
     * Creates a custom MIME type encapsulated in an NDEF record
     *
     * @param mimeType
     */
    public NdefRecord createMimeRecord(String mimeType, byte[] payload) {
        byte[] mimeBytes = mimeType.getBytes(Charset.forName("US-ASCII"));
        NdefRecord mimeRecord = new NdefRecord(
                NdefRecord.TNF_MIME_MEDIA, mimeBytes, new byte[0], payload);
        return mimeRecord;
    }


    @Override
    public void onResume() {
        super.onResume();
        // Check to see that the Activity started due to an Android Beam
        if (NfcAdapter.ACTION_NDEF_DISCOVERED.equals(getIntent().getAction())) {
            processIntent(getIntent());
        }
    }

    @Override
    public void onNewIntent(Intent intent) {
    	super.onResume();
        //mNfcAdapter.enableForegroundDispatch(this, pendingIntent, intentFiltersArray, techListsArray);

        // onResume gets called after this to handle the intent
        setIntent(intent);
//        Tag tagFromIntent = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);

    }

    /**
     * Parses the NDEF Message from the intent and prints to the TextView
     */
    void processIntent(Intent intent) {
        Parcelable[] rawMsgs = intent.getParcelableArrayExtra(
                NfcAdapter.EXTRA_NDEF_MESSAGES);
        // only one message sent during the beam
        NdefMessage msg = (NdefMessage) rawMsgs[0];
        // record 0 contains the MIME type, record 1 is the AAR, if present
        String message = new String(msg.getRecords()[0].getPayload());
        handleMessageReceived(message);
    }
    
    // ----------------------------------------------
    // Nathan/Oren implement here
    
    // Called when NFC message is received
	private void handleMessageReceived(String message) {
		
		try {
			Friend f = (Friend)Repository.deserialize(message);
			repo.addFriend(f);
			Toast.makeText(getApplicationContext(), "Friend received: " +  f.getName(), Toast.LENGTH_LONG).show();
			return;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        Toast.makeText(getApplicationContext(), "Received " +  message, Toast.LENGTH_LONG).show();
	}        
	
	// Called to get NFC message for sending
	// Maximum message size is 32K
	private String messageToBeSent() {		
		try {
			return Repository.serialize(repo.getMe());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

}