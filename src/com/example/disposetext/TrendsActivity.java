package com.example.disposetext;

import com.example.Util.Defs;

import android.app.Activity;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;

public class TrendsActivity extends Activity {
	private static final String TAG ="TrendsActivity";
	private static final Uri PROFILE_URI = Uri.parse(Defs.TRENDS_SCHEMA);

	private String uid;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);

		extractUidFromUri();
		setTitle("Profile1:Hello, "+ uid);
	}


	private void extractUidFromUri() {
		Uri uri = getIntent().getData();
		if (uri !=null&& PROFILE_URI.getScheme().equals(uri.getScheme())) {
			uid = uri.getQueryParameter(Defs.PARAM_UID);
			Log.d(TAG, "uid from url: "+ uid);
		}
	}
}