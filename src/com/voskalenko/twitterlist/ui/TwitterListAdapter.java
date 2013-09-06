package com.voskalenko.twitterlist.ui;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import org.xml.sax.XMLReader;

import com.voskalenko.twitterlist.R;
import com.voskalenko.twitterlist.db.DatabaseManager;
import com.voskalenko.twitterlist.model.TwitterObj;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.text.Editable;
import android.text.Html;
import android.text.Html.TagHandler;
import android.text.Spannable;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.text.style.StrikethroughSpan;
import android.text.util.Linkify;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class TwitterListAdapter extends BaseAdapter implements Filterable {

	private static final String TAG = TwitterListAdapter.class.getSimpleName();
	private static final int TYPE_GROUP_HEADER = 0;
	private static final int TYPE_ITEM = 1;
	private static final int ITEM_TYPE_COUNT = 2;

	private List<TwitterObj> twitterLst;
	private LayoutInflater mInflater;
	private SimpleDateFormat formater;
	private final Context ctx;

	private final TagHandler tagHandler = new TagHandler() {

		@Override
		public void handleTag(boolean opening, String tag,
				Editable output, XMLReader xmlReader) {
			if (!tag.equals("html") && !tag.equals("body"))
				processSpan(opening, output, new HashTagSpan(ctx, tag));
		}

		private void processSpan(boolean opening, Editable output, Object span) {
			int len = output.length();
			if (opening)
				output.setSpan(span, len, len,Spannable.SPAN_MARK_MARK);
			else {
				Object[] objs = output.getSpans(0, len, span.getClass());
				int where = len;
				if (objs.length > 0) {
					for (int i = objs.length - 1; i >= 0; --i) {
						if (output.getSpanFlags(objs[i]) == Spannable.SPAN_MARK_MARK) {
							where = output.getSpanStart(objs[i]);
							output.removeSpan(objs[i]);
							break;
						}
					}
				}

				if (where != len)
					output.setSpan(span, where, len, 
							Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
			}
		}
	};
	
	private final Filter filter = new Filter() {

		@Override
		protected FilterResults performFiltering(CharSequence condition) {
			FilterResults results = new FilterResults();
			DatabaseManager dbMng = DatabaseManager.getInstance();
			List<TwitterObj> obj;

			if (!TextUtils.isEmpty(condition))
				obj = dbMng.getTwitterHomeLines((String) condition);
			else
				obj = dbMng.getAllTwitterHomeLines();

			results.values = obj;
			results.count = obj.size();

			return results;
		}

		@Override
		protected void publishResults(CharSequence condition,
				FilterResults results) {
			if (results != null) {
				twitterLst = (List<TwitterObj>) results.values;
				notifyDataSetChanged();
			}
		}

	};

	public int getViewTypeCount() {
		return ITEM_TYPE_COUNT;
	};

	public int getItemViewType(int position) {
		Calendar prevDate = twitterLst.get(position - 1 < 0 ? 0 : position - 1)
				.getCreatedAt();
		Calendar thisDate = twitterLst.get(position).getCreatedAt();

		return prevDate.get(Calendar.DAY_OF_MONTH) == thisDate
				.get(Calendar.DAY_OF_MONTH) && position > 0 ? TYPE_ITEM
				: TYPE_GROUP_HEADER;

	};

	public TwitterListAdapter(Context ctx, List<TwitterObj> lst) {
		twitterLst = lst;
		this.ctx = ctx;
		mInflater = LayoutInflater.from(ctx);
		formater = new SimpleDateFormat("EEEE MMMM dd", Locale.US);
	}

	@Override
	public Filter getFilter() {
		return filter;
	}

	public int getCount() {
		return twitterLst == null ? 0 : twitterLst.size();
	}

	public Object getItem(int position) {
		return twitterLst == null ? null : twitterLst.get(position);
	}

	public long getItemId(int position) {
		return position;
	}

	public View getView(int position, View convertView, ViewGroup parent) {

		UiHolder holder;

		if (convertView == null) {
			holder = new UiHolder();
			switch (getItemViewType(position)) {
				case TYPE_ITEM:
					convertView = mInflater.inflate(R.layout.list_item, null);
					holder.imgPhoto = (ImageView) convertView.findViewById(R.id.img_photo);
					holder.txtUser = (TextView) convertView.findViewById(R.id.txt_user);
					holder.txtCreatedAt = (TextView) convertView.findViewById(R.id.txt_created_at);
					holder.txtDescription = (TextView) convertView.findViewById(R.id.txt_description);
					holder.txtDescription.setLinksClickable(true);
					holder.txtDescription.setMovementMethod(LinkMovementMethod.getInstance());
					break;
	
				case TYPE_GROUP_HEADER:
					convertView = mInflater.inflate(R.layout.list_header, null);
					holder.txtHeader = (TextView) convertView.findViewById(R.id.txt_header);
					break;
			}

			convertView.setTag(holder);

		} else {
			holder = (UiHolder) convertView.getTag();
		}

		switch (getItemViewType(position)) {
			case TYPE_ITEM:
				String url = twitterLst.get(position).getUser().getProfileImageURL();
				new DownloadPhotoTask(holder.imgPhoto).execute(url);
				holder.txtUser.setText(twitterLst.get(position).getUser().getName());
				holder.txtCreatedAt.setText(twitterLst.get(position).getCreatedAt().getTime().toString());
				String text = twitterLst.get(position).getText();
				holder.txtDescription.setText(Html.fromHtml(wrapWithHashTags(text), null, tagHandler));
				break;
	
			case TYPE_GROUP_HEADER:
				holder.txtHeader.setText(formater.format(twitterLst.get(position).getCreatedAt().getTime()));
				break;
		}

		return convertView;
	}
	
	private String wrapWithHashTags(String str) {
		int posStart;
		int posNext = 1;
		while ((posStart = str.indexOf("#", posNext)) != -1) {
			int posEnd = 0;
			
			if((posEnd = str.indexOf(" ", posStart)) == -1)
				if((posEnd = str.indexOf(":", posStart)) == -1)
					posEnd = str.length();
			
			String hashTag = str.substring(posStart, posEnd).replace(":", "");
			String linkHashTag = String.format("<%1$s>%1$s</%1$s>", hashTag);
			str = str.replace(hashTag, linkHashTag);
			posNext += posStart + linkHashTag.length();
		}
		
		return str;
	}

	private class HashTagSpan extends ClickableSpan {

		private final Context ctx;
		private String hashTag;

		public HashTagSpan(Context ctx, String hashTag) {
			super();
			this.ctx = ctx;
			this.hashTag = hashTag;
		}

		@Override
		public void onClick(View view) {
			Toast.makeText(ctx, hashTag, Toast.LENGTH_SHORT).show();
		}
	}

	static class UiHolder {
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
				bmp = BitmapFactory.decodeStream(urlPhoto.openConnection()
						.getInputStream());
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
