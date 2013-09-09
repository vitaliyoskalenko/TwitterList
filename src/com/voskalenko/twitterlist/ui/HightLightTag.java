package com.voskalenko.twitterlist.ui;

import twitter4j.HashtagEntity;
import twitter4j.UserMentionEntity;
import android.content.Context;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ClickableSpan;
import android.view.View;
import android.widget.Toast;

public class HightLightTag {
	
	
	public static Spanned getSpanned(Context ctx, String text, Object[] entities) {
		
		SpannableString spanString =  new SpannableString(text);
		String strTag;
		int posStart, posEnd;
		
		
		for(int i = 0; i < entities.length; i++) {
			if(entities[i] instanceof HashtagEntity) {
				HashtagEntity hashtagEntity = ((HashtagEntity)entities[i]);
				strTag = "#" + hashtagEntity.getText();
				posStart = hashtagEntity.getStart();
				posEnd = hashtagEntity.getEnd();
			} else { 
				UserMentionEntity userMentionEntity = ((UserMentionEntity)entities[i]);
				strTag = "@" + userMentionEntity.getScreenName();
				posStart = userMentionEntity.getStart();
				posEnd = userMentionEntity.getEnd();
				
			}
			
			TagSpan tagSpan= new TagSpan(ctx, strTag);
			spanString.setSpan(tagSpan, posStart, 
					posEnd, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
		}
		
		return spanString;
	}
	

	private static class TagSpan extends ClickableSpan {

		private final Context ctx;
		private CharSequence hashTag;

		public TagSpan(Context ctx, CharSequence tag) {
			super();
			this.ctx = ctx;
			this.hashTag = tag;
		}

		@Override
		public void onClick(View view) {
			Toast.makeText(ctx, hashTag, Toast.LENGTH_SHORT).show();
		}
	}

}