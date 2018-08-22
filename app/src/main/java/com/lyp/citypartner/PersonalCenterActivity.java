package com.lyp.citypartner;

import java.io.File;
import java.util.ArrayList;

import org.json.JSONException;
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
import com.qiniu.android.http.ResponseInfo;
import com.qiniu.android.storage.UpCompletionHandler;
import com.qiniu.android.storage.UpProgressHandler;
import com.qiniu.android.storage.UploadManager;
import com.qiniu.android.storage.UploadOptions;

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

public class PersonalCenterActivity extends BaseActivity {

	private WaitDialog mWaitDialog;
	private SharedPreferences mSharedPreferences;
	private TextView tv_username;
	private TextView tv_shop_point;
	private CircleImageView user_avater;
	private String id;
	private static final int REQUEST_IMAGE = 2;
	/** 设置头像 */
	private static final int REQUESTCODE_CUTTING = 3;
	private ArrayList<String> mSelectPath;
	private WaitDialog mUploadDialog;
	private String avaterIcon;

	private Handler mainHandler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case MessageContants.MSG_GET_PERSON_INFO: {
				parseGetPersonInfo((String) msg.obj);
				break;
			}
			case MessageContants.MSG_EXCHANGE_SHOP_POINT: {
				parseExchangeShopPointInfo((String) msg.obj);
				break;
			}
			case MessageContants.MSG_GET_TOKEN: {
				parseGetToken((String) msg.obj);
				break;
			}
			case MessageContants.MSG_SAVE_SHOP_ICON: {
				parseUploadPic((String) msg.obj);
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
		setContentView(R.layout.personal_center_layout);
		initView();
		initData();
	}

	private void initView() {
		tv_username = (TextView) findViewById(R.id.tv_username);
		user_avater = (CircleImageView) findViewById(R.id.user_avater);
		tv_shop_point = (TextView) findViewById(R.id.tv_shop_point);
	}

	private void initData() {
		mSharedPreferences = getSharedPreferences(Constant.SHARED_PREFERENCE, MODE_PRIVATE);
		String tokenid = mSharedPreferences.getString(Constant.TOKEN_ID, "");
		String saleId = mSharedPreferences.getString(Constant.ID, "");
		if (mWaitDialog == null) {
			mWaitDialog = new WaitDialog(this, R.string.loading_data);
		}
		if (!mWaitDialog.isShowing()) {
			mWaitDialog.show();
		}
		NetProxyManager.getInstance().toGetPersonInfo(mainHandler, tokenid, saleId);
	}

	public void onBack(View view) {
		finish();
	}

	public void exchangeShopPoint(View view) {
		mSharedPreferences = getSharedPreferences(Constant.SHARED_PREFERENCE, MODE_PRIVATE);
		String tokenid = mSharedPreferences.getString(Constant.TOKEN_ID, "");
		String saleId = mSharedPreferences.getString(Constant.ID, "");
		if (mWaitDialog == null) {
			mWaitDialog = new WaitDialog(this, R.string.loading_data);
		}
		if (!mWaitDialog.isShowing()) {
			mWaitDialog.show();
		}
		NetProxyManager.getInstance().toExchangeShopPoint(mainHandler, tokenid, saleId);
	}

	public void queryShopPic(View view) {
		// ToastUtil.showShort(this, "正在开发中，尽请期待！");
		Intent intent = new Intent();
		intent.setClass(this, ShopPicActivity.class);
		startActivity(intent);
	}

	public void shopInfo(View view) {
		Intent intent = new Intent();
		intent.setClass(this, ShopInfoActivity.class);
		startActivity(intent);
	}

	public void shopIncome(View view) {
		Intent intent = new Intent();
		intent.setClass(this, IncomeHistoryActivity.class);
		startActivity(intent);
	}

	public void MyQRCode(View view) {
		Intent intent = new Intent();
		intent.setClass(this, MyQRCodeActivity.class);
		startActivity(intent);
	}

	public void exitApp(View view) {
		BaseApplication.getInstance().finishAllActivity();
		Editor editor = mSharedPreferences.edit();
		editor.clear().commit();
		Intent i = new Intent();
		i.setClass(this, LoginActivity.class);
		startActivity(i);
		finish();
	}

	public void settings(View view) {
		Intent intent = new Intent();
		intent.setClass(this, SettingsActivity.class);
		startActivity(intent);
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
				if (json.getString("code").equals(Constant.TOKEN_INVAILD)
						|| json.getString("code").equals(Constant.DIFF_DEVICE)) {
					backLogin();
				}
				return;
			}
			JSONObject obj = json.getJSONObject("data");
			id = obj.getString("id");
			String shopname = obj.getString("shopname");
			tv_username.setText(shopname);
			if (obj.has("shoppoint")) {
				tv_shop_point.setText(obj.getString("shoppoint"));
			} else {
				tv_shop_point.setText("0");
			}
			if (obj.has("shopicon")) {
				ImageManager.loadImage(obj.getString("shopicon"), user_avater, R.drawable.avater_login);
			}
		} catch (Exception ex) {
			LogUtils.e(ex.getMessage());
			return;
		} finally {
			if (mWaitDialog != null && mWaitDialog.isShowing()) {
				mWaitDialog.dismiss();
			}
		}
	}

	private void parseExchangeShopPointInfo(String result) {
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
			LogUtils.d("lyp" + json);
			boolean success = json.getBoolean("success");
			if (!success) {
				String message = json.getString("message");
				ToastUtil.showShort(this, message);
				if (mWaitDialog != null && mWaitDialog.isShowing()) {
					mWaitDialog.dismiss();
				}
				if (json.getString("code").equals(Constant.TOKEN_INVAILD)
						|| json.getString("code").equals(Constant.DIFF_DEVICE)) {
					backLogin();
				}
				return;
			}
			ToastUtil.showLongMessage("添加商户兑换现金请求成功!");
			initData();
		} catch (Exception ex) {
			LogUtils.e(ex.getMessage());
			if (mWaitDialog != null && mWaitDialog.isShowing()) {
				mWaitDialog.dismiss();
			}
			return;
		} finally {

		}
	}
	
	private void parseGetToken(String result) {
		if (Errors.ERROR_NET.equals(result) || Errors.ERROR_SERVER.equals(result)) {
			ToastUtil.showLong(this, R.string.network_error);
			if (mUploadDialog != null && mUploadDialog.isShowing()) {
				mUploadDialog.dismiss();
			}
			return;
		}
		// to parser json data
		try {
			JSONObject json = new JSONObject(result);
			boolean success = json.getBoolean("success");
			if (!success) {
				if (mUploadDialog != null && mUploadDialog.isShowing()) {
					mUploadDialog.dismiss();
				}
				String message = json.getString("message");
				ToastUtil.showShort(this, message);
				if (json.getString("code").equals(Constant.TOKEN_INVAILD) 
						|| json.getString("code").equals(Constant.DIFF_DEVICE)) {
					backLogin();
				}
				return;
			}
			JSONObject job = json.getJSONObject("data");
			String uptoken = job.getString("uptoken");
			uploadQiNiu(uptoken);
		} catch (Exception ex) {
			if (mWaitDialog != null && mWaitDialog.isShowing()) {
				mWaitDialog.dismiss();
			}
			LogUtils.e(ex.getMessage());
			return;
		}
	}
	
	private String domain = "https://public.app-storage-node.com/";
	private void uploadQiNiu(final String uploadToken) {
//		File uploadFile = new File(mSelectPath.get(0));
		File uploadFile = tempFile;
		UploadOptions uploadOptions = new UploadOptions(null, null, false, new UpProgressHandler() {
			@Override
			public void progress(String key, double percent) {
//				updateStatus(percent);//更新上传进度
			}
		}, null);
		
		UploadManager uploadManager = new UploadManager();
		String fileKey = "prj-sss/" + System.currentTimeMillis() + "_" + uploadFile.getName();
		LogUtils.i("qiniu", "fileKey = " + fileKey);
		uploadManager.put(uploadFile, fileKey, uploadToken, new UpCompletionHandler() {
			@Override
			public void complete(String key, ResponseInfo info, JSONObject job) {
				// res包含hash、key等信息，具体字段取决于上传策略的设置
				if (info.isOK()) {
					 try {
						LogUtils.i("qiniu", "Upload Success: " + job.toString());
						String fileKey = job.getString("key");
//						final String persistentId = job.getString("persistentId");

	                    final String picUrl = domain + fileKey;
	                    LogUtils.i("qiniu", "picUrl=" + picUrl);
	                    uploadPicUrl(picUrl);
					} catch (JSONException e) {
						if (mUploadDialog != null && mUploadDialog.isShowing()) {
							mUploadDialog.dismiss();
						}
						LogUtils.e("qiniu", "Upload Success error： " + e.getMessage());
						e.printStackTrace();
					}
                     
				} else {
					if (mUploadDialog != null && mUploadDialog.isShowing()) {
						mUploadDialog.dismiss();
					}
					ToastUtil.showLongMessage("上传失败!" + info.error);
					LogUtils.i("qiniu", "Upload Fail: " + info.error);
					// 如果失败，这里可以把info信息上报自己的服务器，便于后面分析上传错误原因
				}
				LogUtils.i("qiniu", "key=" + key + "\r\n " + " info = " + info);
			}
		}, uploadOptions);
	}
	
	private void uploadPicUrl(String picUrl) {
		mSharedPreferences = getSharedPreferences(Constant.SHARED_PREFERENCE, MODE_PRIVATE);
		String tokenid = mSharedPreferences.getString(Constant.TOKEN_ID, "");
		String saleId = mSharedPreferences.getString(Constant.ID, "");
		NetProxyManager.getInstance().toUploadAvaterPic(mainHandler, tokenid, saleId, picUrl);
		avaterIcon = picUrl;
	}
	
	private void parseUploadPic(String result) {
		if (Errors.ERROR_NET.equals(result) || Errors.ERROR_SERVER.equals(result)) {
			ToastUtil.showLong(this, R.string.network_error);
			if (mUploadDialog != null && mUploadDialog.isShowing()) {
				mUploadDialog.dismiss();
			}
			return;
		}
		// to parser json data
		try {
			JSONObject json = new JSONObject(result);
			boolean success = json.getBoolean("success");
			if (!success) {
				if (mUploadDialog != null && mUploadDialog.isShowing()) {
					mUploadDialog.dismiss();
				}
				String message = json.getString("message");
				ToastUtil.showShort(this, message);
				if (json.getString("code").equals(Constant.TOKEN_INVAILD) 
						|| json.getString("code").equals(Constant.DIFF_DEVICE)) {
					backLogin();
				}
				return;
			}
			ToastUtil.showMessage("上传商户头像成功！");
			ImageManager.loadImage(avaterIcon, user_avater, R.drawable.avater_login);
//			getData();
		} catch (Exception ex) {
			LogUtils.e(ex.getMessage());
			return;
		} finally {
			if (mUploadDialog != null && mUploadDialog.isShowing()) {
				mUploadDialog.dismiss();
			}
		}
	}

	File tempFile = null;
	Uri cropImageUri = null;

	/** 设置可编辑头像 */
	public void startPhotoZoom(Uri uri) {
		tempFile = new FileUtils().createCropFile();
		// TODO:outputUri不需要ContentUri,否则失败
		cropImageUri = Uri.fromFile(tempFile);
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
					imageUri = FileProvider.getUriForFile(this, "com.lyp.citypartner.fileprovider",
							new File(mSelectPath.get(0)));// 通过FileProvider创建一个content类型的Uri
				}
				startPhotoZoom(imageUri);
			}
		} else if (requestCode == REQUESTCODE_CUTTING) {
			if (resultCode == RESULT_OK) {
				// if (data != null) {
				// LogUtils.d("lyp", "!!!" + data.getDataString());
				// } else {
				LogUtils.d("lyp", tempFile.getAbsolutePath());
				// }

				if (mUploadDialog == null) {
					mUploadDialog = new WaitDialog(this, "头像正在上传中......");
				}
				mUploadDialog.show();
				NetProxyManager.getInstance().toGetToken(mainHandler);
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
