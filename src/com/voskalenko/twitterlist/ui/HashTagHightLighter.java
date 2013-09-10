package com.voskalenko.twitterlist.ui;

import java.io.Serializable;

import com.voskalenko.twitterlist.ClickableHashtagSpan;

import android.content.Context;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.Spanned;

public class HashTagHightLighter {
	
	public final static class Entity implements Serializable {
		
		private String text;
		private int posStart;
		private int posEnd;
		
		public Entity(String text, int posStart, int posEnd) {
			this.text = text;
			this.posStart = posStart;
			this.posEnd = posEnd;
		}
		
		public String getText() {
			return text;
		}

		public int getPosStart() {
			return posStart;
		}

		public int getPosEnd() {
			return posEnd;
		}

		
		
		
	}
	
	public static Spanned hightLightSpan(Context ctx, String text, HashTagHightLighter.Entity[]... entities) {
		
		SpannableString spanString =  new SpannableString(text);
		char[] tagIdentifiers = new char[]{'#','@'};
		String strTag;
		int posStart, posEnd;
		
		for(int i = 0; i < entities.length; i++)
			for(int j = 0; j < entities[i].length; j++) {
				strTag = tagIdentifiers[i] + entities[i][j].getText();
				posStart = entities[i][j].getPosStart();
				posEnd = entities[i][j].getPosEnd();
					
				ClickableHashtagSpan tagSpan= new ClickableHashtagSpan(ctx, strTag);
				spanString.setSpan(tagSpan, posStart, 
						posEnd, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
			}
		
		return spanString;
	}
}