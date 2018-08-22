package com.lyp.citypartner;

import java.io.File;
import java.util.ArrayList;

import org.json.JSONObject;

import com.image.FileUtils;
import com.image.select.MultiImageSelectorActivity;
import com.lyp.base.BaseActivity;
import com.lyp.base.BaseApplication;
import com.lyp.manager.ImageManager;
import com.lyp.net.Errors;
import com.lyp.net.MessageContants;
import com.lyp.net.NetProxyManager;
import com.lyp.utils.Constant;
import com.lyp.utils.LogUtils;
import com.lyp.utils.SystemStatusManager;
import com.lyp.utils.ToastUtil;
import com.lyp.view.CircleImageView;
import com.lyp.view.dialog.WaitDialog;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.v4.content.FileProvider;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;

public class SettingsActivity extends BaseActivity {

	private WaitDialog mWaitDialog;
	private SharedPreferences mSharedPreferences;
	private String id;
	private static final int REQUEST_IMAGE = 2;
	/** 设置头像 */
	private static final int REQUESTCODE_CUTTING = 3;
	private ArrayList<String> mSelectPath;

	private Handler mainHandler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case MessageContants.MSG_GET_PERSON_INFO: {
				parseGetPersonInfo((String) msg.obj);
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
		setContentView(R.layout.setting_layout);
		initView();
//		initData();
	}

	private void initView() {
	}

	private void initData() {
		mSharedPreferences = getSharedPreferences(Constant.SHARED_PREFERENCE, MODE_PRIVATE);
		String tokenid = mSharedPreferences.getString(Constant.TOKEN_ID, "");
		String saleId = mSharedPreferences.getString(Constant.ID, "");
		if (mWaitDialog == null) {
			mWaitDialog = new WaitDialog(this, R.string.loading_data);
		}
		mWaitDialog.show();
//		NetProxyManager.getInstance().toGetPersonInfo(mainHandler, tokenid, saleId);
	}


	public void onBack(View view) {
		finish();
	}

	public void exitApp(View view) {
		BaseApplication.getInstance().finishAllActivity();
		mSharedPreferences = getSharedPreferences(Constant.SHARED_PREFERENCE, MODE_PRIVATE);
		Editor editor = mSharedPreferences.edit();
		editor.clear().commit();
		Intent i = new Intent();
		i.setClass(this, LoginActivity.class);
		startActivity(i);
		finish();
	}
	
	public void uploadAvater(View view) {
		Intent intent = new Intent(this, MultiImageSelectorActivity.class);
		// 是否显示拍摄图片
		intent.putExtra(MultiImageSelectorActivity.EXTRA_SHOW_CAMERA, true);
		// 最大可选择图片数量
		intent.putExtra(MultiImageSelectorActivity.EXTRA_SELECT_COUNT, 9);
		// 选择模式
		intent.putExtra(MultiImageSelectorActivity.EXTRA_SELECT_MODE, MultiImageSelectorActivity.MODE_SINGLE);
		// 默认选择
		if (mSelectPath != null && mSelectPath.size() > 0) {
			intent.putExtra(MultiImageSelectorActivity.EXTRA_DEFAULT_SELECTED_LIST, mSelectPath);
		}
		startActivityForResult(intent, REQUEST_IMAGE);
	}

	/**
	 * to parse person infomations from network
	 */
	private void parseGetPersonInfo(String result) {
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
			LogUtils.d("Json: " + json);
			boolean success = json.getBoolean("success");
			if (!success) {
				String message = json.getString("message");
				ToastUtil.showShort(this, message);
				if (json.getString("resCode").equals(Constant.RELOGIN)) {
					backLogin();
				}
				return;
			}
			JSONObject obj = json.getJSONObject("data");
		} catch (Exception ex) {
			LogUtils.e(ex.getMessage());
			return;
		} finally {
			if (mWaitDialog != null && mWaitDialog.isShowing()) {
				mWaitDialog.dismiss();
			}
		}
	}
	
	File tempFile = null;
	/** 设置可编辑头像 */
	public void startPhotoZoom(Uri uri) {
		tempFile = new FileUtils().createCropFile();
		// TODO:outputUri不需要ContentUri,否则失败
		Uri cropImageUri = Uri.fromFile(tempFile);
		Intent intent = new Intent("com.android.camera.action.CROP");
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
			// cropImageUri = FileProvider.getUriForFile(activity,
			// "com.lyp.fileprovider", file);//通过FileProvider创建一个content类型的Uri
			intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
		}
		intent.setDataAndType(uri, "image/*");
		intent.putExtra("crop", true);
		intent.putExtra("aspectX", 1);
		intent.putExtra("aspectY", 1);
		intent.putExtra("outputX", 300);
		intent.putExtra("outputY", 300);
		intent.putExtra("return-data", true);
		intent.putExtra("noFaceDetection", true);
		intent.putExtra("scale", true);
		intent.putExtra(MediaStore.EXTRA_OUTPUT, cropImageUri);
		intent.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString());
		startActivityForResult(intent, REQUESTCODE_CUTTING);
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (requestCode == REQUEST_IMAGE) {
			if (resultCode == RESULT_OK) {
				mSelectPath = data.getStringArrayListExtra(MultiImageSelectorActivity.EXTRA_RESULT);
				if (mSelectPath == null || mSelectPath.size() <= 0) {
					ToastUtil.showLongMessage("设置头像失败，请重新设置！");
					return;
				}
				for (int i = 0; i < mSelectPath.size(); i++) {
					LogUtils.d("lyp", mSelectPath.get(i));
				}
				Uri imageUri = Uri.parse("file://" + mSelectPath.get(0));
	            // 设置系统相机拍照后的输出路径
	        	if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
	    			imageUri = FileProvider.getUriForFile(this, "com.lyp.citypartner.fileprovider", new File(mSelectPath.get(0)));// 通过FileProvider创建一个content类型的Uri
	    		}
				startPhotoZoom(imageUri);
			}
		} else if (requestCode == REQUESTCODE_CUTTING) {
			if (resultCode == RESULT_OK) {
//				if (data != null) {
//					LogUtils.d("lyp", "!!!" + data.getDataString());
//				} else {
					LogUtils.d("lyp", tempFile.getAbsolutePath());
//				}
			} else {
				ToastUtil.showLongMessage("设置头像失败，请重新设置！");
			}
			
		}
	}

	/**
	 * 设置状态栏背景状态
	 */
	private void setTranslucentStatus() {
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {

			// 透明状态栏
			getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
			// 透明导航栏
			getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
		}
		SystemStatusManager tintManager = new SystemStatusManager(this);
		tintManager.setStatusBarTintEnabled(true);
		tintManager.setStatusBarTintResource(R.color.main_bg_color);// 状态栏背景
		getWindow().getDecorView().setFitsSystemWindows(true);
	}

	@Override
	protected void onResume() {
		super.onResume();
	}
}
