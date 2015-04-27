package com.example.Util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.text.util.Linkify;
import android.text.util.Linkify.MatchFilter;
import android.text.util.Linkify.TransformFilter;
import android.widget.TextView;

public class StringUtil {
	/**
	 * 微博字符串处理
	 * @param v
	 */
	public static void extractMention2Link(TextView TextView) {
		TextView.setAutoLinkMask(0);//@(\\w+?\\-*?)(?=\\W|$)(.)
		Pattern mentionsPattern = Pattern.compile("@[\\u4e00-\\u9fa5\\w\\-]+");
		String mentionsScheme = String.format("%s/?%s=", Defs.MENTIONS_SCHEMA, Defs.PARAM_UID);
		Linkify.addLinks(TextView, mentionsPattern, mentionsScheme, new MatchFilter() {

			@Override
			public boolean acceptMatch(CharSequence s, int start, int end) {
				return s.charAt(end-1) !='.';
			}

		}, new TransformFilter() {
			@Override
			public String transformUrl(Matcher match, String url) {
				return match.group(0); 
			}
		});

		Pattern trendsPattern = Pattern.compile("#(\\w+?)#");
		String trendsScheme = String.format("%s/?%s=", Defs.TRENDS_SCHEMA, Defs.PARAM_UID);
		Linkify.addLinks(TextView, trendsPattern, trendsScheme, null, new TransformFilter() {
			@Override
			public String transformUrl(Matcher match, String url) {
				//Log.d(TAG, match.group(1));
				return match.group(1); 
			}
		});

	}
}
