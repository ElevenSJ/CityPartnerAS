package com.lyp.citypartner;

import org.json.JSONObject;

import com.lyp.base.BaseActivity;
import com.lyp.net.Errors;
import com.lyp.net.MessageContants;
import com.lyp.net.NetProxyManager;
import com.lyp.utils.Constant;
import com.lyp.utils.LogUtils;
import com.lyp.utils.SystemStatusManager;
import com.lyp.utils.ToastUtil;
import com.lyp.view.dialog.WaitDialog;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.text.Editable;
import android.text.TextUtils;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.TextView;

public class LoginActivity extends BaseActivity {

	private WaitDialog mWaitDialog;
	private EditText phone_number_et;
	private EditText verify_code_et;
	private String phone;
	private SharedPreferences mSharedPreferences;
	private boolean isPause = false;
	private TextView get_verify_code_tv;
	private TimeCount timeCount = new TimeCount(60 * 1000, 1000);

	/***
	 * 获取验证码倒计时
	 */
	private class TimeCount extends CountDownTimer {

		public TimeCount(long millisInFuture, long countDownInterval) {
			super(millisInFuture, countDownInterval);
		}

		@Override
		public void onTick(long millisUntilFinished) {
			get_verify_code_tv.setClickable(false);
			get_verify_code_tv.setText(millisUntilFinished / 1000 + "秒");
		}

		@Override
		public void onFinish() {
			get_verify_code_tv.setText("获取验证码");
			get_verify_code_tv.setClickable(true);
		}
	}

	private Handler mainHandler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case MessageContants.MSG_LOGIN: {
				if (mWaitDialog == null) {
					mWaitDialog = new WaitDialog(LoginActivity.this, R.string.loading_data);
				}
				if (!isPause) {
					mWaitDialog.show();
					parseLoginInfo((String) msg.obj);
				}
				break;
			}
			case MessageContants.MSG_GET_SMS_AUTH_CODE: {
				parseSMSAuthCodeInfo((String) msg.obj);
				break;
			}
			default:
				break;
			}
		};
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setTranslucentStatus();
		setContentView(R.layout.login_layout);
		initView();
	}

	@Override
	protected void onResume() {
		super.onResume();
		isPause = false;
	}

	@Override
	protected void onPause() {
		super.onPause();
		isPause = true;
	}

	private void initView() {
		mSharedPreferences = getSharedPreferences(Constant.SHARED_PREFERENCE, MODE_PRIVATE);
		phone_number_et = (EditText) findViewById(R.id.phone_number_et);
		verify_code_et = (EditText) findViewById(R.id.verify_code_et);
		get_verify_code_tv = (TextView) findViewById(R.id.get_verify_code_tv);
	}

	public void getVerifyCode(View view) {
		Editable phoneET = phone_number_et.getText();
		if (phoneET == null) {
			ToastUtil.showLong(LoginActivity.this, "号码不能为空");
			return;
		}
		phone = phoneET.toString();
		if (phone.length() <= 0) {
			ToastUtil.showLong(LoginActivity.this, "号码不能为空");
			return;
		}
		// ToastUtil.showLong(LoginActivity.this, "接口开发中...");
		NetProxyManager.getInstance().toGetSMSAuthCode(mainHandler, phone);
		timeCount.start();
	}

	public void login(View view) {
		// loginSuccess();

		Editable verifyCodeET = verify_code_et.getText();
		if (TextUtils.isEmpty(verifyCodeET)) {
			ToastUtil.showLong(LoginActivity.this, "验证码不能为空");
			return;
		}
		Editable phoneET = phone_number_et.getText();
		if (phoneET == null) {
			ToastUtil.showLong(LoginActivity.this, "号码不能为空");
			return;
		}
		phone = phoneET.toString();
		if (phone.length() <= 0) {
			ToastUtil.showLong(LoginActivity.this, "号码不能为空");
			return;
		}
		String serialNumber = android.os.Build.SERIAL;
		NetProxyManager.getInstance().toLogin(mainHandler, phone, verifyCodeET.toString(), serialNumber);
		// NetProxyManager.getInstance().toLoginByPWD(mainHandler, phone,
		// verifyCodeET.toString());
	}

	public void toRegist(View view) {
		Intent intent = new Intent();
		intent.setClass(LoginActivity.this, RegistShopActivity.class);
		startActivity(intent);
	}

	private void loginSuccess() {
		Intent intent = new Intent();
		intent.setClass(LoginActivity.this, MainActivity.class);
		startActivity(intent);
		finish();
	}

	/**
	 * 设置状态栏背景状态
	 */
	private void setTranslucentStatus() {
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
			// Window win = getWindow();
			// WindowManager.LayoutParams winParams = win.getAttributes();
			// final int bits =
			// WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS;
			// winParams.flags |= bits;
			// win.setAttributes(winParams);

			// 透明状态栏
			getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
			// 透明导航栏
			getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
		}
		SystemStatusManager tintManager = new SystemStatusManager(this);
		tintManager.setStatusBarTintEnabled(true);
		tintManager.setStatusBarTintResource(android.R.color.white);// 状态栏无背景
		getWindow().getDecorView().setFitsSystemWindows(true);
	}

	/**
	 * to parse get sms auth code
	 */
	private void parseSMSAuthCodeInfo(String result) {
		if (Errors.ERROR_NET.equals(result) || Errors.ERROR_SERVER.equals(result)) {
			ToastUtil.showLong(this, R.string.network_error);
			if (mWaitDialog != null && mWaitDialog.isShowing()) {
				mWaitDialog.dismiss();
			}
			return;
		}
		// to parser json data
		try {
			JSONObject json = new JSONObject(result);
			boolean success = json.getBoolean("success");
			if (mWaitDialog != null && mWaitDialog.isShowing()) {
				mWaitDialog.dismiss();
			}
			LogUtils.d("" + json);
			if (!success) {
				String message = json.getString("message");
				ToastUtil.showShort(this, message);
				// timeCount.cancel();
				// timeCount.onFinish();
				return;
			}
			ToastUtil.showLong(LoginActivity.this, "短信验证码发送成功，请等待!");
		} catch (Exception ex) {
			LogUtils.e(ex.getMessage());
			return;
		}
	}

	/**
	 * to parse login infomations from network
	 */
	private void parseLoginInfo(String result) {
		if (Errors.ERROR_NET.equals(result) || Errors.ERROR_SERVER.equals(result)) {
			ToastUtil.showLong(this, R.string.network_error);
			if (mWaitDialog != null && mWaitDialog.isShowing()) {
				mWaitDialog.dismiss();
			}
			return;
		}
		// to parser json data
		try {
			JSONObject json = new JSONObject(result);
			boolean success = json.getBoolean("success");
			if (mWaitDialog != null && mWaitDialog.isShowing()) {
				mWaitDialog.dismiss();
			}
			if (!success) {
				String message = json.getString("message");
				ToastUtil.showShort(this, message);
				return;
			}
			JSONObject jot = json.getJSONObject("data");
			String id = jot.getString("id");
			if (jot.has("audittype")) {
				if ("0".equals(jot.getString("audittype"))) {
					ToastUtil.showLongMessage("暂未通过审核，请耐心等待！");
					return;
				}
			}
			// String name = jot.getString("name");
			// String token = jot.getString("token_id");
			Editor editor = mSharedPreferences.edit();
			editor.putBoolean(Constant.IS_FIRST, false);
			String token = jot.getString("tokenid");
			editor.putString(Constant.TOKEN_ID, token);
			editor.putString(Constant.ID, id);
			// editor.putString(Constant.USER_NAME, name);
			editor.commit();
			loginSuccess();
		} catch (Exception ex) {
			LogUtils.e(ex.getMessage());
			return;
		}
	}
}
