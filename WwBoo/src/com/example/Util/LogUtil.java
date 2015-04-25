package com.example.Util;

public class LogUtil {
	public static void Log(String Tag,Object obj){
		android.util.Log.e(Tag, obj+"");
	}
}
