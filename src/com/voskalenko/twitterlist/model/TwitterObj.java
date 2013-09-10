package com.voskalenko.twitterlist.model;


import java.util.Calendar;

import com.j256.ormlite.field.DataType;
import com.j256.ormlite.field.DatabaseField;
import com.voskalenko.twitterlist.ui.HashTagHightLighter;

public class TwitterObj {
	
	@DatabaseField(id=true)
	private long id;
	@DatabaseField(dataType = DataType.SERIALIZABLE)
	private Calendar createdAt; 
	@DatabaseField(dataType = DataType.SERIALIZABLE)
	private HashTagHightLighter.Entity[] hashtagEntities; 
	@DatabaseField(dataType = DataType.SERIALIZABLE)
	private HashTagHightLighter.Entity[] userMantionEntities;
	@DatabaseField
	private String place;
	@DatabaseField
	private String text; 
	@DatabaseField(foreign=true,foreignAutoRefresh=true)
	private UserObj user;
	
	public TwitterObj() {
	}
	
	public TwitterObj(long id, Calendar createdAt, HashTagHightLighter.Entity[] hashtagEntities, HashTagHightLighter.Entity[] userMantionEntities,
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
	public HashTagHightLighter.Entity[] getHashtagEntities() {
		return hashtagEntities;
	}
	
	public HashTagHightLighter.Entity[] getUserMantionEntities() {
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
