package com.crack.storage;

import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.net.HttpURLConnection;
import java.net.URL;

import com.crack.nfc.CrackActivity;

import android.graphics.Bitmap;

public class Friend implements Serializable {
	
	private static final long serialVersionUID = 5995064843255722178L;
	
	private String email;
	private String name;
	private String imageUrl;
	private String staleness;
	
	public Friend() {}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getImageUrl() {
		return imageUrl;
	}

	public void setImageUrl(String imageUrl) {
		this.imageUrl = imageUrl;		
	}

	public void setStaleness() {
		this.staleness = String.valueOf(System.currentTimeMillis());
	}

	public long getStaleness() {
		return Long.parseLong(staleness);
	}
	
}
