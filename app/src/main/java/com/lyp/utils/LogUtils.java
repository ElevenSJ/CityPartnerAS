package com.lyp.utils;

import android.util.Log;

/**
 * Log工具类
 * Created by lyp on 2017/10/12.
 */
public class LogUtils {
	private static final Boolean LOG_OUT = true;

	public static void d(String TAG, String msg) {
		if (LOG_OUT) {
			Log.d(TAG, msg);
		}
	}

	public static void i(String TAG, String msg) {
		if (LOG_OUT) {
			Log.i(TAG, msg);
		}
	}

	public static void v(String TAG, String msg) {
		if (LOG_OUT) {
			Log.v(TAG, msg);
		}
	}

	public static void w(String TAG, String msg) {
		if (LOG_OUT) {
			Log.w(TAG, msg);
		}
	}

	public static void e(String TAG, String msg) {
		if (LOG_OUT) {
			Log.e(TAG, msg);
		}
	}

	private static final String TAG = "lyp";

	private static final String TAG_CONTENT_PRINT = "%s:%s.%s:%d";

	private static StackTraceElement getCurrentStackTraceElement() {
		return Thread.currentThread().getStackTrace()[4];
	}

	private static String getContent(StackTraceElement trace, String tag) {
		return String.format(TAG_CONTENT_PRINT, tag, trace.getClassName(), trace.getMethodName(),
				trace.getLineNumber());
	}

	public static void v(String msg) {
		if (LOG_OUT)
			Log.v(TAG, getContent(getCurrentStackTraceElement(), TAG) + ">>" + msg);
	}

	public static void i(String msg) {
		if (LOG_OUT)
			Log.i(TAG, getContent(getCurrentStackTraceElement(), TAG) + ">>" + msg);
	}

	public static void d(String msg) {
		if (LOG_OUT)
			Log.d(TAG, getContent(getCurrentStackTraceElement(), TAG) + ">>" + msg);
	}

	public static void e(String msg) {
		if (LOG_OUT)
			Log.e(TAG, getContent(getCurrentStackTraceElement(), TAG) + ">>" + msg);
	}

	public static void w(String msg) {
		if (LOG_OUT)
			Log.w(TAG, getContent(getCurrentStackTraceElement(), TAG) + ">>" + msg);
	}
}
