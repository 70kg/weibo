package com.example.loadimage;

import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;

import android.graphics.Bitmap;
import android.util.Log;

public class MemoryCache {

	private static final String TAG = "MemoryCache";
	// 鏀惧叆缂撳瓨鏃舵槸涓悓姝ユ搷浣�
	// LinkedHashMap鏋勯�鏂规硶鐨勬渶鍚庝竴涓弬鏁皌rue浠ｈ〃杩欎釜map閲岀殑鍏冪礌灏嗘寜鐓ф渶杩戜娇鐢ㄦ鏁扮敱灏戝埌澶氭帓鍒楋紝鍗矻RU
	// 杩欐牱鐨勫ソ澶勬槸濡傛灉瑕佸皢缂撳瓨涓殑鍏冪礌鏇挎崲锛屽垯鍏堥亶鍘嗗嚭鏈�繎鏈�皯浣跨敤鐨勫厓绱犳潵鏇挎崲浠ユ彁楂樻晥鐜�
	private Map<String, Bitmap> cache = Collections
			.synchronizedMap(new LinkedHashMap<String, Bitmap>(10, 1.5f, true));
	// 缂撳瓨涓浘鐗囨墍鍗犵敤鐨勫瓧鑺傦紝鍒濆0锛屽皢閫氳繃姝ゅ彉閲忎弗鏍兼帶鍒剁紦瀛樻墍鍗犵敤鐨勫爢鍐呭瓨
	private long size = 0;// current allocated size
	// 缂撳瓨鍙兘鍗犵敤鐨勬渶澶у爢鍐呭瓨
	private long limit = 1000000;// max memory in bytes

	public MemoryCache() {
		// use 25% of available heap size
		setLimit(Runtime.getRuntime().maxMemory() / 10);
	}

	public void setLimit(long new_limit) {
		limit = new_limit;
		Log.i(TAG, "MemoryCache will use up to " + limit / 1024. / 1024. + "MB");
	}

	public Bitmap get(String id) {
		try {
			if (!cache.containsKey(id))
				return null;
			return cache.get(id);
		} catch (NullPointerException ex) {
			return null;
		}
	}

	public void put(String id, Bitmap bitmap) {
		try {
			if (cache.containsKey(id))
				size -= getSizeInBytes(cache.get(id));
			cache.put(id, bitmap);
			size += getSizeInBytes(bitmap);
			checkSize();
		} catch (Throwable th) {
			th.printStackTrace();
		}
	}

	/**
	 * 涓ユ牸鎺у埗鍫嗗唴瀛橈紝濡傛灉瓒呰繃灏嗛鍏堟浛鎹㈡渶杩戞渶灏戜娇鐢ㄧ殑閭ｄ釜鍥剧墖缂撳瓨
	 * 
	 */
	private void checkSize() {
		Log.i(TAG, "cache size=" + size + " length=" + cache.size());
		if (size > limit) {
			// 鍏堥亶鍘嗘渶杩戞渶灏戜娇鐢ㄧ殑鍏冪礌
			Iterator<Entry<String, Bitmap>> iter = cache.entrySet().iterator();
			while (iter.hasNext()) {
				Entry<String, Bitmap> entry = iter.next();
				size -= getSizeInBytes(entry.getValue());
				iter.remove();
				if (size <= limit)
					break;
			}
			Log.i(TAG, "Clean cache. New size " + cache.size());
		}
	}

	public void clear() {
		cache.clear();
	}

	/**
	 * 鍥剧墖鍗犵敤鐨勫唴瀛�
	 * 
	 * [url=home.php?mod=space&uid=2768922]@Param[/url] bitmap
	 * 
	 * @return
	 */
	long getSizeInBytes(Bitmap bitmap) {
		if (bitmap == null)
			return 0;
		return bitmap.getRowBytes() * bitmap.getHeight();
	}
}

