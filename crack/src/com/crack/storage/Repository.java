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

public class Repository extends Observable {

	public ArrayList<Friend> friends;
	private static Repository instance;
	private String FILENAME = "friends.dat";
	private Context context;
	
	public static Repository getInstance(Context context) {
		if (instance == null)
			instance = new Repository(context);
		return instance;
	}

	public void addFriend(Friend newFriend) {
		int idx = -1;
		for (int i=0; i< friends.size(); i++) {
			if (friends.get(i).getEmail().equals(newFriend.getEmail())){
				idx = i;
				break;
			}
		}
		if (idx==-1) {
			newFriend.setStaleness(0);
			friends.add(newFriend);
		}
		else {
			Friend existingFriend = friends.get(idx);
			existingFriend.setImage(newFriend.getImage());
			existingFriend.setName(newFriend.getName());
			existingFriend.setStaleness(0);
		}
		save();
		setChanged();
		notifyObservers();
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
	
	private Repository(Context context) {
		this.context = context;
		update();
	}

	private void save() {
		try {			
			FileOutputStream fos = context.openFileOutput(FILENAME, Context.MODE_PRIVATE);		
			fos.write(serialize(friends).getBytes());
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

}
