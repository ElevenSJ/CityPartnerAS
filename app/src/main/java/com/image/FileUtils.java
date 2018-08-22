package com.image;

import android.content.Context;
import android.os.Environment;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * 文件操作类 Created by Nereo on 2015/4/8.
 */
public class FileUtils {
	private static final String PHOTO_DATE_FORMAT = "'AVATER'_yyyyMMdd_HHmmss";
	private File cropIconDir;
	private File iconDir;

	public FileUtils() {
        if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())) {
            File external = Environment.getExternalStorageDirectory();
            String rootDir = "/" + "CityPartner";
            cropIconDir = new File(external, rootDir);
            if (!cropIconDir.exists()) {
                cropIconDir.mkdirs();

            }
            iconDir = new File(external, rootDir);
            if (!iconDir.exists()) {
                iconDir.mkdirs();

            }
        }
    }

	public File createCropFile() {
		String fileName = "";
		if (cropIconDir != null) {
			fileName = generateTempPhotoFileName();
		}
		return new File(cropIconDir, fileName);
	}

	public File createTmpFile() {
		String fileName = "";
		if (iconDir != null) {
			fileName = generateTempPhotoFileName();
		}
		return new File(iconDir, fileName);
	}

	private String generateTempPhotoFileName() {
		Date date = new Date(System.currentTimeMillis());
		SimpleDateFormat dateFormat = new SimpleDateFormat(PHOTO_DATE_FORMAT);
		return dateFormat.format(date) + ".jpg";
	}

//	public static File createTmpFile(Context context) {
//
//		String state = Environment.getExternalStorageState();
//		if (state.equals(Environment.MEDIA_MOUNTED)) {
//			// 已挂载
//			File pic = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
//			String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.CHINA).format(new Date());
//			String fileName = "multi_image_" + timeStamp + "";
//			File tmpFile = new File(pic, fileName + ".jpg");
//			return tmpFile;
//		} else {
//			File cacheDir = context.getCacheDir();
//			String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.CHINA).format(new Date());
//			String fileName = "multi_image_" + timeStamp + "";
//			File tmpFile = new File(cacheDir, fileName + ".jpg");
//			return tmpFile;
//		}
//
//	}

}
