package com.crack.storage;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Observable;

import android.content.Context;
import android.graphics.Bitmap;
/**
 * @author Anton
 * 
 * Persists friends across application launches. This class is observable, allowing
 * observers to be notified when there is a change in friends.
 */
public class Repository extends Observable {
	
	private Friend me;
	private String ME_FILENAME = "me.dat";
	private String MY_IMAGE_FILENAME = "myimage.dat";
	
	private ArrayList<Friend> friends;
	private String FILENAME = "friends.dat";
	
	private static Repository instance;
	private Context context;
	private Bitmap myImage;
	
	public static Repository getInstance(Context context) {
		if (instance == null)
			instance = new Repository(context);
		return instance;
	}

	private Repository(Context context) {
		this.context = context;
		update();
		updateMe();
//		updateMyImage();
	}
	
	/**
	 * Adds a friend object to the repo. If this friends 
	 * already exists in the repository (matched on e-mail) 
	 * it is updated and staleness set to 0.
	 * 
	 * Observers are notified.
	 * 
	 * @param newFriend a friend object to be added to the repo
	 */	
	public void addFriend(Friend newFriend) {
		int idx = -1;
		for (int i=0; i< friends.size(); i++) {
			if (friends.get(i).getEmail().equals(newFriend.getEmail())){
				idx = i;
				break;
			}
		}
		if (idx==-1) {
			friends.add(newFriend);
		}
		else {
			Friend existingFriend = friends.get(idx);
			existingFriend.setImageUrl(newFriend.getImageUrl());
			existingFriend.setName(newFriend.getName());
			existingFriend.setStaleness();
		}
		save();
	}
	
	@SuppressWarnings("unchecked")
	private void update() {
		try {
			FileInputStream fis = context.openFileInput(FILENAME);
			byte[] b = new byte[fis.available()];
			fis.read(b);
			String s = new String(b);
			if ("".equals(s)) {
				friends = new ArrayList<Friend>();
			}
			else {
				friends = (ArrayList<Friend>)deserialize(s);
			}
		} catch (FileNotFoundException ffe) {
			friends = new ArrayList<Friend>();
			save();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private void updateMe() {
		try {
			FileInputStream fis = context.openFileInput(ME_FILENAME);
			byte[] b = new byte[fis.available()];
			fis.read(b);
			String s = new String(b);
			if ("".equals(s)) {
				me = null;
			}
			else {
				me = (Friend)deserialize(s);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private void updateMyImage() {
		try {
			FileInputStream fis = context.openFileInput(MY_IMAGE_FILENAME);
			byte[] b = new byte[fis.available()];
			fis.read(b);
			String s = new String(b);
			if ("".equals(s)) {
				myImage = null;
			}
			else {
				myImage = (Bitmap)deserialize(s);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private void save() {
		try {			
			FileOutputStream fos = context.openFileOutput(FILENAME, Context.MODE_PRIVATE);		
			fos.write(serialize(friends).getBytes());
			fos.close();
			setChanged();
			notifyObservers();
		} catch (Exception e) {
			e.printStackTrace();
		}		
	}
	
	public void saveMyImage(Bitmap bitmap) {
		myImage = bitmap;
//		try {			
//			FileOutputStream fos = context.openFileOutput(MY_IMAGE_FILENAME, Context.MODE_PRIVATE);		
//			fos.write(serialize(bitmap).getBytes());
//			fos.close();
//			setChanged();
//			notifyObservers();
//		} catch (Exception e) {
//			e.printStackTrace();
//		}		
	}
	
	
	private void saveMe() {
		try {			
			FileOutputStream fos = context.openFileOutput(ME_FILENAME, Context.MODE_PRIVATE);		
			fos.write(serialize(me).getBytes());
			fos.close();
		} catch (Exception e) {	}		
	}

	public static String serialize(Object o) throws IOException {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		ObjectOutputStream oos = new ObjectOutputStream(baos);
		oos.writeObject(o);
		oos.close();
		byte[] bytes = baos.toByteArray();
		char[] chars = new char[(bytes.length + 1) / 2];
		for (int ci = 0, bi = 0; ci < chars.length; ci++) {
			chars[ci] = (char) (bytes[bi++] << 8);
			if (bi < bytes.length) {
				chars[ci] |= (char) (bytes[bi++] & 0xff);
			}
		}
		return new String(chars);
	}

	public static Object deserialize(String s) throws IOException, ClassNotFoundException {
		byte[] bytes = new byte[s.length() * 2];
		for (int ci = 0, bi = 0; ci < s.length(); ci++) {
			bytes[bi++] = (byte) (s.charAt(ci) >> 8);
			bytes[bi++] = (byte) s.charAt(ci);
		}
		return new ObjectInputStream(new ByteArrayInputStream(bytes)).readObject();
	}
	
	public ArrayList<Friend> getFriends() {
		return friends;
	}
	
	public Bitmap getMyImage() {
		return myImage;
	}
	
	public void setMe(Friend me) {
		this.me = me;
		saveMe();
	}
	
	public Friend getMe() {
		return me;
	}

}
