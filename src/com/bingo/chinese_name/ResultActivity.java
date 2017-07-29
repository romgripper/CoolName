package com.bingo.chinese_name;

import java.io.BufferedInputStream;
import java.io.InputStream;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.ClipboardManager;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.mobclix.android.sdk.Mobclix;
import com.mobclix.android.sdk.MobclixIABRectangleMAdView;
import com.mobclix.android.sdk.MobclixMMABannerXLAdView;

public class ResultActivity extends Activity {
	
	private static final String LOG_TAG = ResultActivity.class.getSimpleName();
	
	public static final String KEY_ENGLISH_NAME = "english name";
	public static final String KEY_CHINESE_NAME = "chinese name";
	public static final String KEY_PRONONCIATION = "prononciation";
	public static final String KEY_NAME_BITMAP = "name bitmap";
	public static final String KEY_FONT_SIZE = "font size";
	public static final String KEY_FONT_ORIENTATION = "font orientation";
		
	private ImageView nameImage;
	private Button saveButton;
	private Button shareButton;
	private Button copyButton;
	
	private String englishName;
	private String chineseName;
	private String prononciation;
	private Bitmap nameBitmap;
	private int fontSize;
	private boolean orientation;
	
	private LoadImageTask loadImageTask;	

	private MobclixMMABannerXLAdView bannerAdView;
    private MobclixIABRectangleMAdView rectAdView;
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.result);
        setTitle(R.string.your_chinese_name);
        init();
    }
    
    private void init() {
    	initAds();
    	Intent intent = getIntent();
    	englishName = intent.getStringExtra(KEY_ENGLISH_NAME);
    	if (englishName != null && englishName.length() > 0) {
    		englishName = englishName.substring(0, 1).toUpperCase() + englishName.substring(1);
    	}
    	TextView textView = (TextView) findViewById(R.id.english_name);
    	textView.setText(englishName);
    	chineseName = intent.getStringExtra(KEY_CHINESE_NAME);
    	textView = (TextView) findViewById(R.id.chinese_name);
    	textView.setText(chineseName);
    	prononciation = intent.getStringExtra(KEY_PRONONCIATION);
    	textView = (TextView) findViewById(R.id.prononciation);
    	textView.setText(prononciation);
    	fontSize = intent.getIntExtra(KEY_FONT_SIZE, 0);
    	orientation = intent.getBooleanExtra(KEY_FONT_ORIENTATION, true); 
    	String url = intent.getStringExtra(KEY_NAME_BITMAP);
    	nameImage = (ImageView) findViewById(R.id.name_image);
    	saveButton = (Button) findViewById(R.id.save_as);
    	saveButton.setText(getString(R.string.save_as) + " " + getImageSavePath());
    	saveButton.setOnClickListener(new View.OnClickListener() {
    		public void onClick(View v) {
    			save(true);
    		}
    	});
    	shareButton = (Button) findViewById(R.id.share);
    	shareButton.setOnClickListener(new View.OnClickListener() {
    		public void onClick(View v) {
    			share();
    		}
    	});
    	copyButton = (Button) findViewById(R.id.copy);
    	copyButton.setOnClickListener(new View.OnClickListener() {
    		public void onClick(View v) {
    			copy();
    		}
		});
    	if (chineseName != null && chineseName.length() != 0) {
    		copyButton.setVisibility(View.VISIBLE);
    	}
    	loadImage(url);
    }
    
	private void initAds() {    	
		Mobclix.onCreate(this);
		bannerAdView = (MobclixMMABannerXLAdView) findViewById(R.id.banner_adview);
		bannerAdView.setTestMode(Settings.MOBCLIX_ADS_TEST);
		bannerAdView.addMobclixAdViewListener(new MobclixAdsListener(this, 
				(FrameLayout) findViewById(R.id.ads_frame)));
		
		rectAdView = (MobclixIABRectangleMAdView) findViewById(R.id.rect_adview);
		rectAdView.setTestMode(Settings.MOBCLIX_ADS_TEST);
		rectAdView.addMobclixAdViewListener(new MobclixAdsListener(this, null));
	}
	
    private void loadImage(String url) {
    	if (loadImageTask != null || url == null || url.length() == 0) {
    		return;
    	}
    	loadImageTask = new LoadImageTask(url);
    	loadImageTask.execute();
    }
    
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
    	if (keyCode == KeyEvent.KEYCODE_BACK && loadImageTask != null) {
    		loadImageTask.cancel(true);
    	}
    	return super.onKeyDown(keyCode, event);    	
    }
    
    private void save(boolean showToast) {
    	Utils.save(this, englishName, fontSize, orientation, nameBitmap, showToast);
    }
    
    private String getImageSavePath() {
    	return Utils.getImageSavePath(englishName, fontSize, orientation);
    }
    
    @Override
    public void onPause() {
    	super.onPause();
    	bannerAdView.pause();
    	rectAdView.pause();
    }
    
    @Override
    public void onResume() {
    	super.onResume();
    	bannerAdView.resume();
    	rectAdView.resume();
    }
	
    @Override
	protected void onStop() {
    	super.onStop();
    	Mobclix.onStop(this);
	}

	@Override
    public void onDestroy() {
    	bannerAdView.cancelAd();
    	rectAdView.cancelAd();

    	super.onDestroy();
    }
	
    private void share() {
    	save(false);
    	
    	Intent share = new Intent(Intent.ACTION_SEND);
    	share.setType("image/png");
    	share.putExtra(Intent.EXTRA_STREAM,	Uri.parse("file://" + getImageSavePath()));
    	if (Utils.DEBUG) {
    		Log.d(LOG_TAG, "share file: " + getImageSavePath());
    	}
    	startActivity(Intent.createChooser(share, "Share Image"));
    }
    
	private void copy() {
		ClipboardManager clipboard = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
		clipboard.setText(chineseName);
		Utils.showMessage(this, "\"" + chineseName + "\" " + getString(R.string.is_copied));
	}
    
	private class LoadImageTask extends AsyncTask<Void, Void, Bitmap> {
    	
    	private String url;
    	
    	public LoadImageTask(String url) {
    		this.url = url;
    	}

		@Override
		protected Bitmap doInBackground(Void... arg0) {
			Bitmap bitmap = null;
			InputStream is = Utils.getInputStream(url);
			if (is == null) {
				return null;
			}
			BufferedInputStream bis = new BufferedInputStream(is);
			bitmap = BitmapFactory.decodeStream(bis);
			if (bitmap != null) {
				bitmap = Utils.adjustOpacity(bitmap);
			}
			Utils.closeInputStream(is);
			return bitmap; 
		}

		@Override
		protected void onPostExecute(Bitmap bitmap) {
			if (bitmap != null) {
				nameImage.setImageBitmap(bitmap);
				nameBitmap = bitmap;
				saveButton.setVisibility(View.VISIBLE);
				shareButton.setVisibility(View.VISIBLE);
			} else {
				Log.d(LOG_TAG, "bitmap is null");
			}
		}
		
    }
}
