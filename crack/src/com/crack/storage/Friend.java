package com.crack.storage;

import java.io.Serializable;

public class Friend implements Serializable {
	
	private static final long serialVersionUID = 5995064843255722178L;
	
	private String email;
	private String name;
	private String imageUrl;
	private int staleness;
	
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

	public void setStaleness(int staleness) {
		this.staleness = staleness;
	}

	public int getStaleness() {
		return staleness;
	}
	
}
