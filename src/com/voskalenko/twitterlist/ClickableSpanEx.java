package com.voskalenko.twitterlist;

import android.content.Context;
import android.text.style.ClickableSpan;
import android.view.View;

public class ClickableSpanEx extends ClickableSpan {

	protected final Context ctx;
	protected final CharSequence text;

	public ClickableSpanEx(Context ctx, CharSequence text) {
		super();
		this.ctx = ctx;
		this.text = text;
	}

	@Override
	public void onClick(View view) {
	}
}