package com.voskalenko.twitterlist.model;


import java.util.Calendar;

import com.j256.ormlite.field.DataType;
import com.j256.ormlite.field.DatabaseField;

import twitter4j.HashtagEntity;
import twitter4j.UserMentionEntity;

public class TwitterObj {
	
	@DatabaseField(id=true)
	private long id;
	@DatabaseField(dataType = DataType.SERIALIZABLE)
	private Calendar createdAt; 
	@DatabaseField(dataType = DataType.SERIALIZABLE)
	private HashtagEntity[] hashtagEntities; 
	@DatabaseField(dataType = DataType.SERIALIZABLE)
	private UserMentionEntity[] userMantionEntities;
	@DatabaseField
	private String place;
	@DatabaseField
	private String text; 
	@DatabaseField(foreign=true,foreignAutoRefresh=true)
	private UserObj user;
	
	public TwitterObj() {
	}
	
	public TwitterObj(long id, Calendar createdAt, HashtagEntity[] hashtagEntities, UserMentionEntity[] userMantionEntities,
			String place, String text, UserObj user) {
		this.id = id;
		this.createdAt = createdAt;
		this.hashtagEntities = hashtagEntities;
		this.userMantionEntities = userMantionEntities;
		this.place = place;
		this.text = text;
		this.user = user;
	}
	
	public Calendar getCreatedAt() {
		return createdAt;
	}
	public HashtagEntity[] getHashtagEntities() {
		return hashtagEntities;
	}
	
	public UserMentionEntity[] getUserMantionEntities() {
		return userMantionEntities;
	}
	
	public long getId() {
		return id;
	}
	
	public String getPlace() {
		return place;
	}
	
	public String getText() {
		return text;
	}
	
	public UserObj getUser() {
		return user;
	}
	
}
