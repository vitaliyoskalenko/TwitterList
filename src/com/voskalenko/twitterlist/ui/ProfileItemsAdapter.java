package com.voskalenko.twitterlist.ui;

import java.text.NumberFormat;

import com.voskalenko.twitterlist.R;
import com.voskalenko.twitterlist.model.UserObj;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class ProfileItemsAdapter extends BaseAdapter {
	
	private static final String TAG = TwitterListAdapter.class.getSimpleName();
	private String[] itemsArr;
	private int[] itemsBmpRes;
	private int[] itemsCounter;
	
	private LayoutInflater mInflater; 
	private NumberFormat nFormater;
	
	public ProfileItemsAdapter(Context ctx, UserObj userObj) {
		
		itemsArr = ctx.getResources().getStringArray(R.array.profile_items);
		itemsBmpRes = new int[]{R.drawable.ic_tweets, R.drawable.ic_favorities, 
				R.drawable.ic_following, R.drawable.ic_following, R.drawable.ic_listed};
		itemsCounter = new int[]{0, userObj.getFavourites(), userObj.getFolloWing(), userObj.getFollowers(), userObj.getListed()};
		mInflater = LayoutInflater.from(ctx);
		
		nFormater = NumberFormat.getInstance();
		nFormater.setMinimumFractionDigits(0);
		nFormater.setMaximumFractionDigits(3);
	}
	@Override
	public int getCount() {
		return itemsArr.length;
	}

	@Override
	public Object getItem(int position) {
		return itemsArr[position];
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		UiHolder holder;
		if(convertView == null) {
			holder = new UiHolder();
			convertView = mInflater.inflate(R.layout.profile_item, null);
			holder.imgItem = (ImageView) convertView.findViewById(R.id.img_item);
			holder.txtItem = (TextView) convertView.findViewById(R.id.txt_item);
			holder.txtItemCounter = (TextView) convertView.findViewById(R.id.txt_item_counter);
			
			convertView.setTag(holder);
		} else
			holder = (UiHolder) convertView.getTag();
		
		holder.imgItem.setImageResource(itemsBmpRes[position]);
		holder.txtItem.setText(itemsArr[position]);
		
		holder.txtItemCounter.setText(nFormater.format(itemsCounter[position]));
		
		return convertView;
	}
	
	static class UiHolder {
		public ImageView imgItem;
		public TextView txtItem;
		public TextView txtItemCounter;
	}
}
