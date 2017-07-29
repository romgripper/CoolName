package com.bingo.chinese_name;

import android.content.Context;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Gallery;
import android.widget.ImageView;

public class FontImageAdapter extends BaseAdapter {
	
	private static int POLICE[] = { 100, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13 };
	
	private Context context;
	private DisplayMetrics metrics;

    private Integer[] mImageIds = {
            R.drawable.font0,
            R.drawable.font1,
            R.drawable.font2,
            R.drawable.font3,
            R.drawable.font4,
            R.drawable.font5,
            R.drawable.font6,
            R.drawable.font7,
            R.drawable.font8,
            R.drawable.font9,
            R.drawable.font10,
            R.drawable.font11,
            R.drawable.font12,
            R.drawable.font13
    };

    public FontImageAdapter(Context context, DisplayMetrics metrics) {
    	this.context = context;
    	this.metrics = metrics;
    }

    public int getCount() {
        return mImageIds.length;
    }

    public Object getItem(int position) {
        return POLICE[position];
    }

    public long getItemId(int position) {
        return position;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        ImageView i = new ImageView(context);

        i.setImageResource(mImageIds[position]);
        
        int width = (int)(metrics.density * 50);
        i.setLayoutParams(new Gallery.LayoutParams(width, width));
        i.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
        i.setBackgroundResource(R.drawable.blank);
        
        return i;
    }

}