package com.bingo.chinese_name;

import java.util.Random;

import android.content.Context;
import android.util.Log;

public class Settings {
	
	private static final String LOG_TAG =
			Settings.class.getSimpleName();
	
	public static final boolean MOBCLIX_ADS_TEST = false;
		
	private static final String appDescription = "chinese,name";
    
    private static Random random = new Random();
	
    private static final String[] adKeywords = {
    	"china+news",
    	"china+weather",
	    "chinese+calendar",
	    "currency+converter",
	    "chinese+name",
	    "real+chinese+name",
	    "chinese+tattoo",
	    "chinese+dictionary",
	    "chinese+translator",
	    "chinese+text+to+speech",
	    "romanization+converter",
	    "chinese+annoted+news",
	    "chinese+annotator",
	    "chinese+number+conversion",
	    "chinese+unit+converter",
	    "chinese+stroke+order",
	    "chinese+practice+sheet",
	    "handwriting+recognition",
	    "chinese+to+pinyin",
	    "Pinyin+To+Chinese",
	    "Chinese+Synonyms+Thesaurus",	
	    "Chinese+Annoted+Bible",
	    "Chinese+Dictionary+of+Exemples",
	    "Chinese+Family+Titles",
	    "Make+Chinese+Calligraphy",
	    "Traditional+Simplified Chinese",
	    "Chinese+Unicode+To+HTML",
	    "HTML To Chinese Unicode",
	    "Pinyin+To+HTML",
	    "Pinyin+Virtual+Keyboard",
	    "write+chinese"
	};
	private static String[] getAdKeywords() {
		return adKeywords;
	}	
	
	public static String getNextMobclixKeyword() {
		return getNextKeyword().replace("+", ",");
	}
    
    private static String getNextKeyword() {
    	String[] keywords = getAdKeywords();
    	int index = random.nextInt(keywords.length);
    	if (Utils.DEBUG) {
    		Log.d(LOG_TAG, "getNextKeyword: " + keywords[index]);
    	}
    	return keywords[index];
    }
    
    public static String getAppDescription() {
		return appDescription;
	}

}