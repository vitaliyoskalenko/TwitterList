package com.voskalenko.twitterlist.db;

import java.sql.SQLException;
import java.util.List;

import com.voskalenko.twitterlist.model.TwitterObj;
import com.voskalenko.twitterlist.model.UserObj;

import android.content.Context;
import android.util.Log;

public class DatabaseManager {

	private static final String TAG = DatabaseManager.class.getSimpleName();
	private static DatabaseManager instance;
	
	private DatabaseHelper helper;

    public static void init(Context ctx) {
        if (instance == null) {
            instance = new DatabaseManager(ctx);
        }
    }

    public static DatabaseManager getInstance() {
        return instance;
    }
    
    private DatabaseManager(Context ctx) {
        helper = new DatabaseHelper(ctx);
    }

    private DatabaseHelper getHelper() {
        return helper;
    }

    public List<TwitterObj> getAllTwitterHomeLines() {
        List<TwitterObj> twitterLst = null;
        try {
        	twitterLst = getHelper().getTwitterHomeLinesDao().queryForAll();
        } catch (SQLException e) {
        	Log.e(TAG, "Failed to get twitter home lines: " + e.getMessage());
        }
        return twitterLst;
    }
    
    public List<TwitterObj> getTwitterHomeLines(String condition) {
        List<TwitterObj> twitterLst = null;
        try {
        	twitterLst = getHelper().getTwitterHomeLinesDao().queryBuilder().where().like("text", "%" + condition + "%").query();
        } catch (SQLException e) {
        	Log.e(TAG, "Failed to get twitter home lines: " + e.getMessage());
        }
        return twitterLst;
    }
    
    
    public UserObj getUser(int id) {
    	UserObj user = null;
    	try {
    		user = getHelper().getUserDao().queryForId(id);
    	} catch (SQLException e) {
    		Log.e(TAG, "Failed to get user: " + e.getMessage());
        }
    	return user;
    }
    
    public void addOrUpdTwitterHomeLines(List<TwitterObj> lst) {
        try {
        	for(TwitterObj obj : lst)
        		getHelper().getTwitterHomeLinesDao().createOrUpdate(obj);
        } catch (SQLException e) {
        	Log.e(TAG, "Failed to add twitter home lines: " + e.getMessage());
        }
    }
    
    public void addOrUpdUser(UserObj user) {
    	try {
    		getHelper().getUserDao().createOrUpdate(user);
    	} catch (SQLException e) {
    		Log.e(TAG, "Failed to add user: " + e.getMessage());
        }
    }
}
