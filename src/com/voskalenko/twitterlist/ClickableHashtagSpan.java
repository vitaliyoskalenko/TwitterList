package com.voskalenko.twitterlist;

import android.content.Context;
import android.view.View;
import android.widget.Toast;

public class ClickableHashtagSpan extends ClickableSpanEx {

	public ClickableHashtagSpan(Context ctx, CharSequence text) {
		super(ctx, text);
	}
	
	@Override
	public void onClick(View view) {
		Toast.makeText(ctx, text, Toast.LENGTH_SHORT).show();
	}

}
