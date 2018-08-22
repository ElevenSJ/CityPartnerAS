package com.lyp.citypartner;

import com.lyp.base.BaseActivity;
import com.lyp.citypartner.R;
import com.lyp.net.MessageContants;
import com.lyp.utils.Constant;
import com.lyp.utils.ToastUtil;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.view.WindowManager;

public class SplashActivity extends BaseActivity {

	private static final String TAG = "SplashActivity";
	private long startTime;
	private SharedPreferences mSharedPreferences;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mSharedPreferences = getSharedPreferences(Constant.SHARED_PREFERENCE, MODE_PRIVATE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
		setContentView(R.layout.splash);
		ConnectivityManager connManger = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
		NetworkInfo[] infos = connManger.getAllNetworkInfo(); // 获取所有的连接对象信息
		NetworkInfo active_info = connManger.getActiveNetworkInfo(); // 获取可用的连接对象信息
		if (active_info == null) {
			ToastUtil.showMessage("请检查网络状态!");
			finish();
			return;
		}
		if (!active_info.isAvailable() || !active_info.isConnected()) {
			ToastUtil.showLong(this, R.string.network_error);
			Intent intent = new Intent();
			intent.setClass(SplashActivity.this, LoginActivity.class);
			startActivity(intent);
			finish();
			return;
		} else {
			final boolean isFirst = mSharedPreferences.getBoolean(Constant.IS_FIRST, true);
			// String username =
			// mSharedPreferences.getString(Constant.CURRENT_USER_NAME, "");
			// String password =
			// mSharedPreferences.getString(Constant.USER_PASSWORD, "");
			// if (password != null && password.length() > 0) {
			//
			// } else {
			mainHandler.postDelayed(new Runnable() {

				@Override
				public void run() {
					String tokenid = mSharedPreferences.getString(Constant.TOKEN_ID, "");
					String id = mSharedPreferences.getString(Constant.ID, "");
					Intent intent = new Intent();
					if (isFirst || tokenid == null) {
						intent.setClass(SplashActivity.this, LoginActivity.class);
					} else {
						intent.setClass(SplashActivity.this, MainActivity.class);
					}
					startActivity(intent);
					finish();
				}
			}, Constant.DELAY_TIME);
			// }

		}
	}
	
	@Override
	protected void onResume() {
		super.onResume();
	}

	@Override
	protected void onPause() {
		super.onPause();
		if (mainHandler != null) {
			mainHandler.removeCallbacksAndMessages(null);
		}
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
	}

	/* message handler */
	@SuppressLint("HandlerLeak")
	private Handler mainHandler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case MessageContants.MSG_LOGIN: {
				break;
			}
			default:
				break;
			}
		};
	};

	public void delayJump() {
		final Intent intent = new Intent();
		long time = System.currentTimeMillis() - startTime;
		if (time >= Constant.DELAY_TIME) {
			// intent.setClass(SplashActivity.this, MainTab.class);
			startActivity(intent);
			finish();

		} else {
			mainHandler.postDelayed(new Runnable() {

				@Override
				public void run() {
					// intent.setClass(SplashActivity.this, MainTab.class);
					startActivity(intent);
					finish();

				}
			}, (Constant.DELAY_TIME - time));
		}
	}

}
