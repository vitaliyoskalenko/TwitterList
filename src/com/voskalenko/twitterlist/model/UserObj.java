package com.voskalenko.twitterlist.model;

import twitter4j.URLEntity;

import com.j256.ormlite.field.DatabaseField;

public class UserObj {
	
	@DatabaseField(id=true)
	private long id;
	@DatabaseField
	private String name;
	@DatabaseField()
	private String profileImage;
	@DatabaseField
	private String description;
	@DatabaseField
	private String urlEntity;
	@DatabaseField()
	private int favourites;
	@DatabaseField()
	private int following;
	@DatabaseField()
	private int followers;
	@DatabaseField()
	private int listed;
	
	public UserObj() {
	}
	
	public UserObj(long id, String name, String profileImage, String description, 
			String urlEntity, int favourites, int following, int followers, int listed) {
		this.id = id;
		this.name = name;
		this.profileImage = profileImage;
		this.description = description;
		this.urlEntity = urlEntity;
		this.favourites = favourites;
		this.following = following;
		this.followers = followers;
		this.listed = listed;
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
	
	public String getDescription() {
		return description;
	}

	public String getUrlEntity() {
		return urlEntity;
	}

	public int getFavourites() {
		return favourites;
	}

	
	public int getFolloWing() {
		return following;
	}

	public int getFollowers() {
		return followers;
	}

	public int getListed() {
		return listed;
	}

}
