package com.bingo.chinese_name;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;

public class Utils {
	
	private static final String LOG_TAG = "Utils";
	
	public static final boolean DEBUG = false;
	
	private static final int URL_CONNECTION_TIMEOUT = 20000;
	
	public static InputStream getInputStream(String link) {
		try {
			if (DEBUG) {
				Log.d(LOG_TAG, "Link: " + link);
			}
			URL url = new URL(link);
	        URLConnection c = url.openConnection();
	        if (!(c instanceof HttpURLConnection)) {
	        	if (DEBUG) {
	        		Log.e(LOG_TAG, "Not http");
	        	}
	            return null;
	        }
	
	        HttpURLConnection connection = (HttpURLConnection) c;
	        connection.setConnectTimeout(URL_CONNECTION_TIMEOUT);
	
	        int response = connection.getResponseCode();
	        if (response != HttpURLConnection.HTTP_OK && 
	        		response != HttpURLConnection.HTTP_PARTIAL) {
	        	if (DEBUG) {
	        		Log.e(LOG_TAG, "Response not ok, error code: " + response);
	        		return null;
	        	}
	        }
	        return connection.getInputStream();
		} catch (Exception e) {
			if (DEBUG) {
				Log.e(LOG_TAG, e.toString());
	            e.printStackTrace();
			}
            return null;
		}    
	}
	
	public static void closeInputStream(InputStream in) {
        try {
        	in.close();
        } catch (IOException e) {
        	if (DEBUG) {
        		Log.e(LOG_TAG, "InputStream close error");
        		e.printStackTrace();
        	}
        }
	}
	
	private static char toChar(String str) {
		return (char) Integer.valueOf(str).intValue();
	}
	
	public static String unescapeXmlString(String xmlString) {
		while (true) {
			if (xmlString.contains("&#")) {
				int start = xmlString.indexOf("&#");
				int end;
				char c;
				int length = xmlString.length();
				for (end = start + 2; end < length; end ++) {
					c = xmlString.charAt(end);
					if (c > '9' || c < '0') {
						break;
					}
				}
				if (start + 2 < end) {
					String digits = xmlString.substring(start + 2, end);
					if (end < length && xmlString.charAt(end) == ';') {
						end ++;
					}
					String toReplace = xmlString.substring(start, end);
					char[] chars = {toChar(digits)};
					xmlString = xmlString.replace(toReplace, new String(chars));
				}
			} else {
				return xmlString;
			}
		}
	}
	
	public static void showMessage(Context context, int resId) {
		Toast.makeText(context, resId, Toast.LENGTH_LONG).show();
	}
	
	public static void showMessage(Context context, String text) {
		Toast.makeText(context, text, Toast.LENGTH_LONG).show();
	}
	
	public static String getImageSavePath(String name, int size, boolean orient) {
		String path = Environment.getExternalStorageDirectory().toString();
		File file = new File(path, name + "_" + (orient ? "h_" : "v_") + size + ".png");
		return file.getAbsolutePath();
	}

	public static void save(Context context, String name, int size, 
			boolean orient, Bitmap bitmap, boolean showToast) {
        File file = new File(getImageSavePath(name, size, orient));
        try {
	        OutputStream out = new FileOutputStream(file);
	
	        bitmap.compress(Bitmap.CompressFormat.PNG , 100, out);
	        out.flush();
	        out.close();
        } catch (IOException e) {
        	showMessage(context, R.string.save_failed);
        	if (DEBUG) {
        		Log.e(LOG_TAG, e.toString());
	            e.printStackTrace();
        	}
        }
        if (showToast) {
        	showMessage(context, context.getString(R.string.saved_to) + file.getAbsolutePath());
        }
        if (DEBUG) {
        	Log.d(LOG_TAG, "Saved to " + file.getAbsolutePath());
        }
	}
	
	public static Bitmap adjustOpacity(Bitmap src)
	{
	    int width = src.getWidth();
	    int height = src.getHeight();
	    Bitmap dest = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
	    int[] pixels = new int[width * height];
	    src.getPixels(pixels, 0, width, 0, 0, width, height);
		for (int i = 0; i < width * height; i ++) {
			if ((pixels[i] & 0xff000000) != 0xff000000) {
				pixels[i] = 0xffffffff;
			}
		}
	    dest.setPixels(pixels, 0, width, 0, 0, width, height);
	    src.recycle();
	    return dest;
	}
}
