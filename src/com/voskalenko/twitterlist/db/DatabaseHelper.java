package com.voskalenko.twitterlist.db;

import android.content.Context;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.j256.ormlite.android.apptools.OrmLiteSqliteOpenHelper;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;
import com.voskalenko.twitterlist.model.TwitterObj;
import com.voskalenko.twitterlist.model.UserObj;

public class DatabaseHelper extends OrmLiteSqliteOpenHelper {
	
	private static final String DATABASE_NAME = "twitter9.sqlite";

    private static final int DATABASE_VERSION = 9;
    private Dao<TwitterObj, Integer> twitterHomeLinesDao = null;
    private Dao<UserObj, Integer> userDao = null;
    
    public DatabaseHelper(Context ctx) {
    	 super(ctx, DATABASE_NAME, null, DATABASE_VERSION);
	}
    
	@Override
	public void onCreate(SQLiteDatabase db, ConnectionSource conn) {
		 try {
	            TableUtils.createTable(conn, TwitterObj.class);
	            TableUtils.createTable(conn, UserObj.class);
	        } catch (SQLException e) {
	            Log.e(DatabaseHelper.class.getName(), "Can't create database", e);
	            throw new RuntimeException(e);
	        } catch (java.sql.SQLException e) {
	            e.printStackTrace();
	        }
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, ConnectionSource conn, int oldVersion, int newVersion) {
		if(oldVersion < 2) {
			String sql = "alter table TwitterObj add column `isVisited` NUMERIC DEFAULT 0";
			db.execSQL(sql);
		}
	}

	public Dao<TwitterObj, Integer> getTwitterHomeLinesDao() {
        if (twitterHomeLinesDao == null) {
            try {
            	twitterHomeLinesDao = getDao(TwitterObj.class);
            }catch (java.sql.SQLException e) {
                e.printStackTrace();
            }
        }
        return twitterHomeLinesDao;
    }
	
	public Dao<UserObj, Integer> getUserDao() {
        if (userDao == null) {
            try {
            	userDao = getDao(UserObj.class);
            }catch (java.sql.SQLException e) {
                e.printStackTrace();
            }
        }
        return userDao;
    }
}
