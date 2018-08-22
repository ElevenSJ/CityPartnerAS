package com.lyp.base;

import java.util.Stack;

import com.lyp.utils.LogUtils;
import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;
import com.tencent.bugly.crashreport.CrashReport;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.Application;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;

public class BaseApplication extends Application {

	private static BaseApplication instance;
	public static Context applicationContext;
	// 存储某种情况下需要被干掉的activity
	private static Stack<Activity> activityStack;
	// 存储所有活着的activity
	private static Stack<Activity> allActivityStack;

	@Override
	public void onCreate() {
		// TODO Auto-generated method stub
		super.onCreate();
		instance = this;
		activityStack = new Stack<Activity>();
		allActivityStack = new Stack<Activity>();
		// 第三个参数为SDK调试模式开关，调试模式的行为特性如下：
		// 输出详细的Bugly SDK的Log
		// 每一条Crash都会被立即上报
		// 自定义日志将会在Logcat中输出
		// 建议在测试阶段建议设置成true，发布时设置为false。
		CrashReport.initCrashReport(getApplicationContext(), "ad82395393", true);
		initImageLoader(getApplicationContext());
//		SDKInitializer.initialize(instance);
	}

	public static BaseApplication getInstance() {
		return instance;
	}

	/**
	 * 添加Activity到堆栈
	 */
	public void addActivity(Activity activity) {
		LogUtils.d("add " + activity);
		activityStack.add(activity);
	}

	/**
	 * 移除Activity到堆栈
	 */
	public void removeActivity(Activity activity) {
		LogUtils.d("remove " + activity);
		activityStack.remove(activity);
	}

	/**
	 * 获取当前Activity（堆栈中最后一个压入的）
	 */
	public Activity currentActivity() {
		Activity activity = activityStack.lastElement();
		return activity;
	}

	/**
	 * 结束当前Activity（堆栈中最后一个压入的）
	 */
	public void finishActivity() {
		Activity activity = activityStack.lastElement();
		finishActivity(activity);
	}

	/**
	 * 结束指定的Activity
	 */
	public void finishActivity(Activity activity) {
		if (activity != null) {
			activityStack.remove(activity);
			activity.finish();
			activity = null;
		}
	}

	/**
	 * 结束指定类名的Activity
	 */
	public void finishActivity(Class<?> cls) {
		for (Activity activity : activityStack) {
			if (activity.getClass().equals(cls)) {
				finishActivity(activity);
			}
		}
	}

	/**
	 * 结束特殊路径Activity
	 */
	public void finishSpecialPathActivity() {
		for (int i = 0, size = activityStack.size(); i < size; i++) {
			if (null != activityStack.get(i)) {
				activityStack.get(i).finish();
			}
		}
		activityStack.clear();
	}

	/**
	 * 退出应用程序
	 */
	@SuppressWarnings("deprecation")
	public void AppExit(Context context) {
		try {
			finishAllActivity();
			ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
			activityManager.restartPackage(context.getPackageName());
			System.exit(0);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 添加Activity到allActivityStack堆栈
	 */
	public void addActivityToAll(Activity activity) {
		LogUtils.d("add " + activity);
		allActivityStack.add(activity);
	}

	/**
	 * 移除Activity到allActivityStack堆栈
	 */
	public void removeActivityFromAll(Activity activity) {
		LogUtils.d("remove " + activity);
		allActivityStack.remove(activity);
	}

	/**
	 * 结束所有Activity
	 */
	public void finishAllActivity() {
		for (int i = 0, size = allActivityStack.size(); i < size; i++) {
			if (null != allActivityStack.get(i)) {
				allActivityStack.get(i).finish();
			}
		}
		allActivityStack.clear();
	}

	@Override
	protected void attachBaseContext(Context base) {
		super.attachBaseContext(base);
	}

	public void initImageLoader(Context context) {
		// This configuration tuning is custom. You can tune every option, you
		// may tune some of them,
		// or you can create default configuration by
		// ImageLoaderConfiguration.createDefault(this);
		// method.
		ImageLoaderConfiguration.Builder config = new ImageLoaderConfiguration.Builder(context);
		config.threadPriority(Thread.NORM_PRIORITY - 2);
		config.denyCacheImageMultipleSizesInMemory();
		config.diskCacheFileNameGenerator(new Md5FileNameGenerator());
		config.diskCacheSize(50 * 1024 * 1024); // 50 MiB
		config.discCacheFileCount(100);// 缓存一百张图片
		config.tasksProcessingOrder(QueueProcessingType.LIFO);
		config.writeDebugLogs(); // Remove for release app

		// Initialize ImageLoader with configuration.
		ImageLoader.getInstance().init(config.build());
	}


	/**
	 * 返回配置文件的日志开关
	 * 
	 * @return
	 */
	public boolean getLoggingSwitch() {
		try {
			ApplicationInfo appInfo = getPackageManager().getApplicationInfo(getPackageName(),
					PackageManager.GET_META_DATA);
			boolean b = appInfo.metaData.getBoolean("LOGGING");
			LogUtils.w("[BaseApplication - getLogging] logging is: " + b);
			return b;
		} catch (PackageManager.NameNotFoundException e) {
			e.printStackTrace();
		}

		return false;
	}

	public boolean getAlphaSwitch() {
		try {
			ApplicationInfo appInfo = getPackageManager().getApplicationInfo(getPackageName(),
					PackageManager.GET_META_DATA);
			boolean b = appInfo.metaData.getBoolean("ALPHA");
			LogUtils.w("[BaseApplication - getAlpha] Alpha is: " + b);
			return b;
		} catch (PackageManager.NameNotFoundException e) {
			e.printStackTrace();
		}

		return false;
	}

	@Override
	public void onLowMemory() {
		super.onLowMemory();
	}
}
