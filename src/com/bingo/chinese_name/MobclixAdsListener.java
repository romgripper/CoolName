package com.bingo.chinese_name;

import android.app.Activity;
import android.text.Layout;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;

import com.google.ads.AdRequest;
import com.google.ads.AdSize;
import com.google.ads.AdView;
import com.mobclix.android.sdk.MobclixAdView;
import com.mobclix.android.sdk.MobclixAdViewListener;

public class MobclixAdsListener implements MobclixAdViewListener {
	
	private static final String LOG_TAG = "MobclixAdsListener ";
	private static final String ADMOB_ID = "a14e06020821cc0";
	
	private Activity activity;
	private FrameLayout layout;
	
	// If layout is not null, show admob ads in that layout
	public MobclixAdsListener(Activity activity, FrameLayout layout) {
		this.activity = activity;
		this.layout = layout;
	}

	public void onSuccessfulLoad(MobclixAdView view) {
		if (Utils.DEBUG) {
			Log.d(LOG_TAG, "The ad request was successful!");
		}
		view.setVisibility(View.VISIBLE);

	}

	public void onFailedLoad(MobclixAdView view, int errorCode) {
		if (Utils.DEBUG) {
			Log.d(LOG_TAG, "The ad request failed with error code: " + errorCode);
		}
		view.setVisibility(View.GONE);
		view.cancelAd();
	    // Create the adView
		
		if (layout != null) {
		    AdView adView = new AdView(activity, AdSize.BANNER, ADMOB_ID);
		    // Lookup your LinearLayout assuming it¡¯s been given
		    // the attribute android:id="@+id/mainLayout"
		    // Add the adView to it
		    layout.addView(adView);
		    // Initiate a generic request to load it with an ad
		    adView.loadAd(new AdRequest());
		}

	}

	public void onAdClick(MobclixAdView adView) {
		if (Utils.DEBUG) {
			Log.d(LOG_TAG, "Ad clicked!");
		}
	}

	public void onCustomAdTouchThrough(MobclixAdView adView, String string) {
		if (Utils.DEBUG) {
			Log.d(LOG_TAG, "The custom ad responded with '" + string + "' when touched!");
		}
	}

	public boolean onOpenAllocationLoad(MobclixAdView adView, int openAllocationCode) {
		if (Utils.DEBUG) {
			Log.d(LOG_TAG, "The ad request returned open allocation code: " + openAllocationCode);
		}
		return false;
	}
	
	public String keywords() {
		String description = Settings.getAppDescription();
		if (Utils.DEBUG) {
			Log.d(LOG_TAG, "keywords: " + description);
		}
		return description;
	}
	public String query() {
		String keyword = Settings.getNextMobclixKeyword();
		if (Utils.DEBUG) {
			Log.d(LOG_TAG, "query: " + keyword);
		}
		return keyword;
	}

}
