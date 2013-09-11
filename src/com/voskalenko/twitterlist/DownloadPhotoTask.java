package com.voskalenko.twitterlist;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.net.MalformedURLException;
import java.net.URL;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.ImageView;

public class DownloadPhotoTask extends AsyncTask<String, Bitmap, Bitmap> {
	
	private final String TAG = DownloadPhotoTask.class.getSimpleName();
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