package com.voskalenko.twitterlist.ui;

import com.voskalenko.twitterlist.DownloadPhotoTask;
import com.voskalenko.twitterlist.R;
import com.voskalenko.twitterlist.db.DatabaseManager;
import com.voskalenko.twitterlist.model.UserObj;

import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

public class ProfileActivity extends Activity {

	private ImageView imgUserPhoto;
	private TextView txtUserName;
	private TextView txtUserHashtag;
	private TextView txtUserEntity;
	private TextView txtUserDescription;
	private ListView lstItems;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_profile);
		
		imgUserPhoto = (ImageView) findViewById(R.id.img_user_photo);
		txtUserName = (TextView) findViewById(R.id.txt_user_name);
		txtUserHashtag = (TextView) findViewById(R.id.txt_user_hashtag);
		txtUserEntity = (TextView) findViewById(R.id.txt_user_entity);
		txtUserDescription = (TextView) findViewById(R.id.txt_user_description);
		lstItems = (ListView) findViewById(R.id.lst_items);
		
		DatabaseManager dbMng = DatabaseManager.getInstance();
		int userID = (int) getIntent().getLongExtra(ListActivity.USER_ID, 0);
		
		UserObj userObj = dbMng.getUser(userID);
		
		String url = userObj.getProfileImageURL();
		new DownloadPhotoTask(imgUserPhoto).execute(url);
		
		txtUserName.setText(userObj.getName());
		txtUserEntity.setText(userObj.getUrlEntity());
		txtUserHashtag.setText("@" + userObj.getName());
		txtUserDescription.setText(userObj.getDescription());
		
		ProfileItemsAdapter adapter = new ProfileItemsAdapter(this, userObj);
		lstItems.setAdapter(adapter);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_profile, menu);
		return true;
	}

}
