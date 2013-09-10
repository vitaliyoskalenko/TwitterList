package com.voskalenko.twitterlist.ui;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import twitter4j.HashtagEntity;
import twitter4j.Paging;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.User;
import twitter4j.UserMentionEntity;
import twitter4j.auth.AccessToken;

import com.voskalenko.twitterlist.Constants;
import com.voskalenko.twitterlist.R;
import com.voskalenko.twitterlist.db.DatabaseManager;
import com.voskalenko.twitterlist.model.TwitterObj;
import com.voskalenko.twitterlist.model.UserObj;
import com.voskalenko.twitterlist.ui.HashTagHightLighter.Entity;

import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Activity;
import android.app.ProgressDialog;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;
import android.widget.AbsListView;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.TextView;

public class ListActivity extends Activity implements AbsListView.OnScrollListener{

	private static final String TAG = ListActivity.class.getSimpleName();

	private int curPage = 0;
	
	private ListView timeLinesLstView;
	private TextView userName;
	private ProgressDialog progress;
	private UserObj userObj;
	private TwitterListAdapter adapter;
	
	private Twitter twitter;
	private DatabaseManager dbMng;
	private List<TwitterObj> twitterLst;
	private AsyncTask task = new GetHomeTimelineTask();
	private Calendar createdAt = Calendar.getInstance();


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getWindow().requestFeature(Window.FEATURE_ACTION_BAR);
		setContentView(R.layout.activity_list);
		
		DatabaseManager.init(this);
		dbMng = DatabaseManager.getInstance();
		
		progress = new ProgressDialog(this);
		progress.setTitle(R.string.wait_dialog);

		timeLinesLstView = (ListView) findViewById(R.id.lst_timelines);
		userName = (TextView) findViewById(R.id.txt_user_name);

		twitterLst = dbMng.getAllTwitterHomeLines();
		if(twitterLst == null)
			twitterLst = new ArrayList<TwitterObj>();
		userObj = dbMng.getUser(0);
		
		adapter = new TwitterListAdapter(ListActivity.this, twitterLst);
		timeLinesLstView.setAdapter(adapter);
		timeLinesLstView.setOnScrollListener(this);

		userName.setText(getString(R.string.lines_owner, userObj == null ? "" : userObj.getName()));
		
		//gets Twitter instance with appTimelines credentials
		AccessToken accessToken = new AccessToken(
				Constants.OAUTH_ACCESS_TOKEN,
				Constants.OAUTH_ACCESS_TOKEN_SECRET);
		twitter = new TwitterFactory().getInstance();
		twitter.setOAuthConsumer(Constants.OAUTH_CONSUMER_KEY,
				Constants.OAUTH_CONSUMER_SECRET);
		twitter.setOAuthAccessToken(accessToken);
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);

		getMenuInflater().inflate(R.menu.activity_list_menu, menu);
		MenuItem searchItem = menu.findItem(R.id.action_search);
		SearchView searchView = (SearchView)searchItem.getActionView();
		searchView.setOnCloseListener(new SearchView.OnCloseListener() {

			@Override
			public boolean onClose() {
				adapter.getFilter().filter(null);
				return false;
			}
			
		});
		
		searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {

			@Override
			public boolean onQueryTextSubmit(String query) {
				return false;
			}

			@Override
			public boolean onQueryTextChange(String query) {
				if(query.length() > 2)
					adapter.getFilter().filter(query);
				return false;
			}
		});
		
		return true;
	}
	
	@Override
	public void onScrollStateChanged(AbsListView view, int scrollState) {
	}

	@Override
	public void onScroll(AbsListView view, int firstVisible,
			int visibleCount, int totalCount) {
			boolean loadMore = firstVisible + visibleCount >= totalCount;
			if (loadMore)
				if(task.getStatus() != AsyncTask.Status.RUNNING) {
					task =  new GetHomeTimelineTask().execute(++curPage);
					Log.i(TAG, "Home lines are loading " + curPage);
				}
	}
	
	class GetHomeTimelineTask extends AsyncTask<Integer, List<TwitterObj>, List<TwitterObj>> {
		
		@Override
		protected void onPreExecute() {
			progress.show();
		}
			
		@Override
		protected List<TwitterObj> doInBackground(Integer... page) {
			try {
				
				if(userObj != null) {
					User user = twitter.verifyCredentials();
					userObj = new UserObj(0, user.getName(), null);
				}
				
				Paging pg = new Paging(page[0]);
				List<twitter4j.Status> statuses = twitter.getHomeTimeline(pg);
				for (twitter4j.Status status : statuses) { 
					UserObj userObjChild = new UserObj(
						status.getUser().getId(), status.getUser().getName(), 
						status.getUser().getProfileImageURL());
					createdAt.setTime(status.getCreatedAt());
					
					HashTagHightLighter.Entity[] hashTagEntities = new HashTagHightLighter.Entity[status.getHashtagEntities().length];
					HashTagHightLighter.Entity[] userMentionEntities = new HashTagHightLighter.Entity[status.getUserMentionEntities().length];;
					
					int i = 0;
					for(HashtagEntity entity : status.getHashtagEntities())
						hashTagEntities[i++] = new Entity(entity.getText(), entity.getStart(), entity.getEnd()); 
					
					i = 0;
					for(UserMentionEntity entity : status.getUserMentionEntities())
						userMentionEntities[i++] = new HashTagHightLighter.Entity(entity.getScreenName(), entity.getStart(), entity.getEnd());
					
					TwitterObj twitterObj = new TwitterObj(status.getId(), createdAt, hashTagEntities, userMentionEntities,
							status.getPlace() == null ? "unknown" : status.getPlace().getName(), status.getText(), userObjChild);
					
					twitterLst.add(twitterObj);
				}
			} catch (TwitterException e) {
				Log.e(TAG, "Failed to get timeline: " + e.getMessage());
			}
			return twitterLst;
		}

		@Override
		protected void onPostExecute(List<TwitterObj> twitterLst) {
			if (!twitterLst.isEmpty()) { 
				for(TwitterObj obj : twitterLst)
					dbMng.addOrUpdUser(obj.getUser());
				dbMng.addOrUpdTwitterHomeLines(twitterLst);
				dbMng.addOrUpdUser(userObj);
				adapter.notifyDataSetChanged();
			}
			progress.dismiss();
		}
	}
	
	@Override
	protected void onDestroy() {
		if(!task.isCancelled())
			task.cancel(true);
		super.onDestroy();
	}
	
}
