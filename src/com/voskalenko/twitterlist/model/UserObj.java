package com.voskalenko.twitterlist.model;

import com.j256.ormlite.field.DatabaseField;

public class UserObj {
	
	@DatabaseField(id=true)
	private long id;
	@DatabaseField
	private String name;
	@DatabaseField()
	private String profileImage;
	
	public UserObj() {
	}
	
	public UserObj(long id, String name, String profileImage) {
		this.id = id;
		this.name = name;
		this.profileImage = profileImage;
	}

	public long getId() {
		return id;
	}
	
	public String getName() {
		return name;
	}
	
	public String getProfileImageURL() {
		return profileImage;
	}
}
