package com.bingo.chinese_name;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.regex.MatchResult;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.Gallery;
import android.widget.RadioButton;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;

import com.mobclix.android.sdk.Mobclix;
import com.mobclix.android.sdk.MobclixIABRectangleMAdView;
import com.mobclix.android.sdk.MobclixMMABannerXLAdView;

public class ChineseNameActivity extends Activity {
	
	private static final String LOG_TAG = ChineseNameActivity.class.getSimpleName();
	
	private static final int MAX_FONT_SIZE = 120;
	private static final int MIN_FONT_SIZE = 10;
	private static final String NAME_PATTERN_STRING = 
			"<a href=\"[^\"]*/dictionnaire/\\?q=[^\"]*\"[^>]*>([^<]*)</a>";
	private static final Pattern NAME_PATTERN = Pattern.compile(NAME_PATTERN_STRING);
	
	private SeekBar fontSizeSeekBar;
	private TextView fontSizeText;
	private ProgressDialog waitingDialog;
	private EditText nameText;
	private RadioButton horizontalButton;
	private Gallery fontGallery;
	
	private GetNameTask getNameTask;
	
	private MobclixMMABannerXLAdView bannerAdView;
    private MobclixIABRectangleMAdView rectAdView;
    
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        setTitle(R.string.get_your_chinese_name);
        init();
    }
    
    private void init() {
    	initViews();
    }
    
    private void initViews() {
    	initAds();
    	nameText = (EditText) findViewById(R.id.name);
    	horizontalButton = (RadioButton) findViewById(R.id.horizontal);
    	fontSizeSeekBar = (SeekBar) findViewById(R.id.font_size_bar);
    	fontSizeSeekBar.setMax(MAX_FONT_SIZE - MIN_FONT_SIZE);
    	fontSizeSeekBar.setProgress(MAX_FONT_SIZE / 2 - MIN_FONT_SIZE);
    	fontSizeText = (TextView) findViewById(R.id.font_size);
    	fontSizeText.setText(getString(R.string.font_size) 
    			+ " " + (fontSizeSeekBar.getProgress() + MIN_FONT_SIZE));
    	fontSizeSeekBar.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {

			public void onProgressChanged(SeekBar seekBar, int progress,
					boolean fromUser) {
				progress += MIN_FONT_SIZE;
				fontSizeText.setText(getString(R.string.font_size) + " " + progress);			
			}

			public void onStartTrackingTouch(SeekBar seekBar) {
			}

			public void onStopTrackingTouch(SeekBar seekBar) {
			}
    		
    	});
    	
    	DisplayMetrics metrics = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(metrics);
    	fontGallery = (Gallery) findViewById(R.id.font);
    	fontGallery.setAdapter(new FontImageAdapter(this, metrics));
    	
    	Button button = (Button) findViewById(R.id.get_name);
    	button.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				getNameFromServer();
			}
    		
    	});
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
	
    private boolean isOnline() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        if (netInfo != null && netInfo.isConnectedOrConnecting()) {
            return true;
        }
        return false;
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
    
    private void getNameFromServer() {
		if (getNameTask != null) {
			return;
		}
		if (! isOnline()) {
			Utils.showMessage(this, R.string.no_internet_access);
			return;
		}
		String url;
		url = "http://www.chinesetools.eu/names/" +
			"?nom=" + nameText.getText().toString() +
			"&taille=" + (fontSizeSeekBar.getProgress() + MIN_FONT_SIZE) +
			"&dispo=" + (horizontalButton.isChecked() ? 1 : 2) +
			"&valid=Ok&valid=Ok" +
			"&couleur=%23000000" +
			"&police=" + fontGallery.getSelectedItemId() +
			"&Submit=Get+my+Chinese+Name+%21";
		getNameTask = new GetNameTask(url);
		getNameTask.execute();
    }
	
	private class GetNameTask
			extends AsyncTask<Void, Void, Void> {
		
		private String url;
		private String chineseName;
		private String nameBitmapUrl;
		private String prononciation;
		
		public GetNameTask(String url) {
			this.url = url;
		}    
		
		private void cancelJob() {
	    	if (getNameTask != null) {
	    		getNameTask.cancel(true);
	    		getNameTask = null;
	    	}
	    }
		
		@Override
		protected void onPreExecute() {
			waitingDialog = ProgressDialog.show(
					ChineseNameActivity.this, "", getString(R.string.loading), true,
					true, new OnCancelListener() {

						public void onCancel(DialogInterface dialog) {
							cancelJob();
						}
						
					});
		}

		@Override
		protected Void doInBackground(Void... params) {
			InputStream in = Utils.getInputStream(url);
			if (in != null) {
				try {
					parseStream(in);
					Utils.closeInputStream(in);
				} catch (Exception e) {
					if (Utils.DEBUG) {
						Log.d(LOG_TAG, e.toString());
				        e.printStackTrace();
					}
				}
			} else {
				if (Utils.DEBUG) {
					Log.d(LOG_TAG, "Connecting server failed");
				}
			}
			return null;
		}
		
		@Override
		protected void onProgressUpdate(Void... progress) {
		}
		
		@Override
		protected void onPostExecute(Void param) {
			getNameTask = null;
			waitingDialog.cancel();
			if (chineseName == null || chineseName.length() == 0) {
				Utils.showMessage(ChineseNameActivity.this, R.string.get_name_fail);
				return;
			}
			Intent intent = new Intent(ChineseNameActivity.this, ResultActivity.class);
			intent.putExtra(ResultActivity.KEY_ENGLISH_NAME, nameText.getText().toString());
			intent.putExtra(ResultActivity.KEY_CHINESE_NAME, chineseName);
			intent.putExtra(ResultActivity.KEY_PRONONCIATION, prononciation);
			intent.putExtra(ResultActivity.KEY_NAME_BITMAP, nameBitmapUrl);
			intent.putExtra(ResultActivity.KEY_FONT_SIZE, fontSizeSeekBar.getProgress() + MIN_FONT_SIZE);
			intent.putExtra(ResultActivity.KEY_FONT_ORIENTATION, horizontalButton.isChecked());
			startActivity(intent);
		}
		
		private void parseStream(InputStream in) {
			BufferedReader reader = new BufferedReader(new InputStreamReader(in));
			try {
				String line = reader.readLine();
				while(line != null) {
					if (line.contains("dictionnaire/?")) {
						chineseName = "";
						line = Utils.unescapeXmlString(line);
						Matcher m = NAME_PATTERN.matcher(line);
						while (m.find()) {
							MatchResult mr = m.toMatchResult();
						    chineseName += mr.group(1);
						    line = line.replaceFirst(NAME_PATTERN_STRING, "");
						    m = NAME_PATTERN.matcher(line);
						}
						if (Utils.DEBUG) {
							Log.d(LOG_TAG, "Chinese name: " + chineseName);
						}
						int start = line.indexOf("prononciation");
						int end = line.indexOf("<", start);
						start = line.indexOf(":", start);
						prononciation = line.substring(start + 1, end);
						prononciation = prononciation.trim();
						if (Utils.DEBUG) {
							Log.d(LOG_TAG, "Prononciation: " + prononciation);
						}
						start = line.indexOf("gen_boutons.php?");
						end = line.indexOf('"', start);
						nameBitmapUrl = "http://www.chinesetools.eu/names/" + line.substring(start, end);
						if (Utils.DEBUG) {
							Log.d(LOG_TAG, "Bitmap: " + nameBitmapUrl);
						}
						return;
					}
					line = reader.readLine();
				}
			} catch(IOException e) {
				if (Utils.DEBUG) {
					Log.e(LOG_TAG, "IO exception");
				}
			}
		}
	}
}