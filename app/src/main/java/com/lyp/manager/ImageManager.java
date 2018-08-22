package com.lyp.manager;

import com.lyp.citypartner.R;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.imageaware.ImageViewAware;

import android.graphics.Bitmap;
import android.widget.ImageView;

public class ImageManager {
	private static DisplayImageOptions defaultOpts = new DisplayImageOptions.Builder()
			.showImageOnLoading(R.drawable.ic_launcher)
			.showImageForEmptyUri(R.drawable.ic_launcher)
			.showImageOnFail(R.drawable.ic_launcher).cacheInMemory(true)
			.cacheOnDisc(true).imageScaleType(ImageScaleType.EXACTLY)
			// .memoryCache(new WeakMemoryCache())

			// .displayer(new RoundedBitmapDisplayer(20))//
			// 是否设置为圆角，弧度为多少
			.bitmapConfig(Bitmap.Config.RGB_565).build();

	/**
	 * @param url
	 * @param view
	 *            加载的图片所
	 */
	public static void loadImage(String url, ImageView imageView) {
		ImageLoader loader = ImageLoader.getInstance();
		loader.displayImage(url, new ImageViewAware(imageView), defaultOpts);

	}

	/**
	 * @param url
	 * @param view
	 *            加载的图片所
	 */
	public static void loadImage(String url, ImageView imageView, int defaultRes) {
		ImageLoader loader = ImageLoader.getInstance();
		DisplayImageOptions opts = new DisplayImageOptions.Builder()
				.showImageOnLoading(defaultRes)
				.showImageForEmptyUri(defaultRes).showImageOnFail(defaultRes)
				.cacheInMemory(true).cacheOnDisc(true)
				.imageScaleType(ImageScaleType.EXACTLY)
				// .memoryCache(new WeakMemoryCache())

				// .displayer(new RoundedBitmapDisplayer(20))//
				// 是否设置为圆角，弧度为多少
				.bitmapConfig(Bitmap.Config.RGB_565).build();
		loader.displayImage(url, new ImageViewAware(imageView), opts);

	}
	
	/**
	 * @param url
	 * @param view
	 *            加载的图片所
	 */
	public static void loadDefaultImage(String url, ImageView imageView) {
		ImageLoader loader = ImageLoader.getInstance();
		loader.displayImage(url, new ImageViewAware(imageView));

	}

}
