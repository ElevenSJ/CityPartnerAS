package com.lyp.utils;

import android.content.Context;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListAdapter;
import android.widget.ListView;

public class DisplayUtil {
	private Context context;

	private int width;
	private int height;
	private float density;
	private int densityDpi;
	private float fontScale;

	/**
	 * @param activity
	 */
	public DisplayUtil(Context context) {
		super();
		this.context = context;
		DisplayMetrics dm = this.context.getResources().getDisplayMetrics();
		this.width = dm.widthPixels;
		;
		this.height = dm.heightPixels;
		this.density = dm.density;
		this.densityDpi = dm.densityDpi;
		this.fontScale = dm.scaledDensity;
	}

	/**
	 * get screen width
	 * 
	 * @return
	 */
	public int getScreenWidth() {
		return width;
	}

	/**
	 * get screen height
	 * 
	 * @return
	 */
	public int getScreenHeight() {
		return height;
	}

	/**
	 * get screen density
	 * 
	 * @return
	 */
	public float getScreenDensity() {
		return density;
	}

	/**
	 * get screen DensityDpi
	 * 
	 * @return
	 */
	public int getScreenDensityDpi() {
		return densityDpi;
	}

	/**
	 * get screen scaledDensity
	 * 
	 * @return
	 */
	public float getScreenScaledDensity() {
		return fontScale;
	}

	/**
	 * change px to dip or dp
	 * 
	 * @param pxValue
	 * @param scale
	 * @return
	 */
	public int pxToDip(float pxValue) {
		return (int) (pxValue / density + 0.5f);
	}

	/**
	 * change dip or dp to px
	 * 
	 * @param dipValue
	 * @param scale
	 * @return
	 */
	public int dipToPx(float dipValue) {
		return (int) (dipValue * density + 0.5f);
	}

	/**
	 * change px to sp
	 * 
	 * @param pxValue
	 * @param fontScale
	 * @return
	 */
	public int pxToSp(float pxValue) {
		return (int) (pxValue / fontScale + 0.5f);
	}

	/**
	 * change sp to px
	 * 
	 * @param spValue
	 * @param fontScale
	 * @return
	 */
	public int spToPx(float spValue) {
		return (int) (spValue * fontScale + 0.5f);
	}
	
	public static void setListViewHeightBasedOnChildren(ListView listView) {  
        ListAdapter listAdapter = listView.getAdapter();   
        if (listAdapter == null) {  
            // pre-condition  
            return;  
        }  
  
        int totalHeight = 0;  
        for (int i = 0; i < listAdapter.getCount(); i++) {  
            View listItem = listAdapter.getView(i, null, listView);  
            listItem.measure(0, 0);  
            totalHeight += listItem.getMeasuredHeight();  
        }  
  
        ViewGroup.LayoutParams params = listView.getLayoutParams();  
        params.height = totalHeight + (listView.getDividerHeight() * (listAdapter.getCount() - 1));  
        listView.setLayoutParams(params);  
    }  
}
