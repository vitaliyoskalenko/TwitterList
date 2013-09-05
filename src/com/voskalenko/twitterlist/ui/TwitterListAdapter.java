package com.voskalenko.twitterlist.ui;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import com.voskalenko.twitterlist.R;
import com.voskalenko.twitterlist.db.DatabaseManager;
import com.voskalenko.twitterlist.model.TwitterObj;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

public class TwitterListAdapter extends BaseAdapter implements Filterable{

		private static final String TAG = TwitterListAdapter.class.getSimpleName(); 
		
	    private  List<TwitterObj> twitterLst;
	    private LayoutInflater mInflater;
	    private SimpleDateFormat formater;
	    
	    private final Filter filter = new Filter() {
	    	
	    	@Override
	    	protected FilterResults performFiltering(CharSequence condition) {
	    		FilterResults results = new FilterResults();
	    		DatabaseManager dbMng = DatabaseManager.getInstance();
	    		List<TwitterObj> obj;
	    		
	    		if(!TextUtils.isEmpty(condition))
	    			obj = dbMng.getTwitterHomeLines((String)condition);
	    		else 
	    			obj = dbMng.getAllTwitterHomeLines();	
	    		
	    		results.values = obj; 
	    		results.count = obj.size();
	    		
	    		return results;
	    	}
	    	
	    	@Override
	    	protected void publishResults(CharSequence condition,
	    			FilterResults results) {
	    		if(results != null) {
	    			twitterLst = (List<TwitterObj>) results.values;
	    			notifyDataSetChanged();
	    		}
	    	}
	    	
	    };
	    
	    public TwitterListAdapter(Context ctx, List<TwitterObj> lst)
	    {
	    	twitterLst = lst;
	        mInflater = LayoutInflater.from(ctx);
	        formater = new SimpleDateFormat("EEEE MMMM dd", Locale.US);
	    }
    	
	    @Override
	    public Filter getFilter() {
	    	return filter;
	    }
	    
	    public int getCount()
	    {
	        return twitterLst == null ? 0 : twitterLst.size();
	    }

	    public Object getItem(int position)
	    {
	        return twitterLst == null ? null : twitterLst.get(position);
	    }

	    public long getItemId(int position)
	    {
	        return position;
	    }

	    public View getView(int position, View convertView, ViewGroup parent)
	    {
	        UiHolder holder;

	        if(convertView == null)
	        {
	            convertView = mInflater.inflate(R.layout.list_item, null);
	            
	            holder = new UiHolder();
	            holder.imgPhoto = (ImageView) convertView.findViewById(R.id.img_photo);
	            holder.txtUser = (TextView) convertView.findViewById(R.id.txt_user);
	            holder.txtCreatedAt = (TextView) convertView.findViewById(R.id.txt_created_at);
	            holder.txtDescription = (TextView) convertView.findViewById(R.id.txt_description);
	            holder.txtHeader = (TextView) convertView.findViewById(R.id.txt_header);

	            convertView.setTag(holder);
	        } else {
	            holder = (UiHolder) convertView.getTag();
	        }
	        
	        String url = twitterLst.get(position).getUser().getProfileImageURL();
	        new DownloadPhotoTask(holder.imgPhoto).execute(url); 
	        
	        holder.txtUser.setText(twitterLst.get(position).getUser().getName());
	        holder.txtCreatedAt.setText(twitterLst.get(position).getCreatedAt().getTime().toString());
	        holder.txtDescription.setText(twitterLst.get(position).getText());
	       
	        
	        Calendar prevDate = twitterLst.get(position - 1 < 0 ? 0 : position - 1).getCreatedAt();
	        Calendar thisDate = twitterLst.get(position).getCreatedAt();
	        
	        if(prevDate.get(Calendar.DAY_OF_MONTH) ==  thisDate.get(Calendar.DAY_OF_MONTH) && position > 0)
	        	holder.txtHeader.setVisibility(View.GONE);  
	        else {
	        		holder.txtHeader.setText(formater.format(thisDate.getTime()));
	        		holder.txtHeader.setVisibility(View.VISIBLE);
	        	}
	        
	        return convertView;
	    }

	    static class UiHolder
	    {
	        ImageView imgPhoto;
	        TextView txtUser;
	        TextView txtCreatedAt;
	        TextView txtDescription;
	        TextView txtHeader;
	    }
	    
	    public class DownloadPhotoTask extends AsyncTask<String, Bitmap, Bitmap> {
	    	
	    	private final WeakReference<ImageView> imgPhotoRef;

	    	public DownloadPhotoTask(ImageView imgPhoto) {
				imgPhotoRef = new WeakReference<ImageView>(imgPhoto);
			}
	    	
	    	@Override
	    	protected void onPreExecute() {
	    		setPhoto(null);
	    		super.onPreExecute();
	    	}
	    	
	        @Override
	        protected Bitmap doInBackground(String... url) {
	        	URL urlPhoto = null;
	        	Bitmap bmp = null;
				try {
					urlPhoto = new URL(url[0]);
					bmp = BitmapFactory.decodeStream(urlPhoto.openConnection().getInputStream());
				} catch (MalformedURLException e) {
					Log.e(TAG, "Failed to get photo");
				} catch (IOException e) {
					Log.e(TAG, "Failed to get photo");
				}	
				return bmp;
	        }

	        @Override
	        protected void onPostExecute(Bitmap bmp) {
	        	setPhoto(bmp);
	            
	        }
	        
	       private void setPhoto(Bitmap bmp) {
	                final ImageView imgPhoto = imgPhotoRef.get();
	                if (imgPhoto != null) {
	                	imgPhoto.setImageBitmap(bmp);
	            }
	       }
	    }

	}
