package com.lyp.citypartner;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshBase.Mode;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener2;
import com.handmark.pulltorefresh.library.PullToRefreshGridView;
import com.image.preview.ImagePagerActivity;
import com.image.select.MultiImageSelectorActivity;
import com.lyp.adapter.ShopPicAdapter;
import com.lyp.base.BaseActivity;
import com.lyp.bean.ShopPicBean;
import com.lyp.net.Errors;
import com.lyp.net.MessageContants;
import com.lyp.net.NetProxyManager;
import com.lyp.utils.Constant;
import com.lyp.utils.LogUtils;
import com.lyp.utils.SystemStatusManager;
import com.lyp.utils.ToastUtil;
import com.lyp.view.CustomPopupWindow;
import com.lyp.view.dialog.CustomerAlertDialog;
import com.lyp.view.dialog.WaitDialog;
import com.qiniu.android.http.ResponseInfo;
import com.qiniu.android.storage.UpCompletionHandler;
import com.qiniu.android.storage.UpProgressHandler;
import com.qiniu.android.storage.UploadManager;
import com.qiniu.android.storage.UploadOptions;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.view.WindowManager.LayoutParams;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.GridView;

public class ShopPicActivity extends BaseActivity implements OnRefreshListener2<GridView> {
	/** 资源对象 */
	public Resources res;
	private WaitDialog mWaitDialog;
	private WaitDialog mUploadDialog;
	private SharedPreferences mSharedPreferences;
	private PullToRefreshGridView mPullRefreshGridView;
	private GridView mGridView;
	private ShopPicAdapter adapter;
	private List<ShopPicBean> list;
	private ArrayList<String> urlList;
	private CustomPopupWindow mPopWin;
	private int mPage = 1;
	private int mRow = 10;
	private int mTotal;
	
	private String domain = "https://public.app-storage-node.com/";

	private static final int REQUEST_IMAGE = 2;

	private ArrayList<String> mSelectPath;

	private Handler mHandler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case MessageContants.MSG_GET_SHOP_PIC: {
				parseGetShopPic((String) msg.obj);
				break;
			}
			case MessageContants.MSG_GET_TOKEN: {
				parseGetToken((String) msg.obj);
				break;
			}
			case MessageContants.MSG_SAVE_SHOP_PIC: {
				parseUploadPic((String) msg.obj);
				break;
			}
			case MessageContants.MSG_DEL_SHOP_PIC: {
				parseDelPic((String) msg.obj);
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
		setContentView(R.layout.shop_pic_layout);
		ToastUtil.showShort(this, "长按可以删除图片");
		initView();
		initData();
	}

	private void initView() {
		mPullRefreshGridView = (PullToRefreshGridView) findViewById(R.id.pull_refresh_grid);
		mPullRefreshGridView.setMode(Mode.DISABLED);
		mPullRefreshGridView.setOnRefreshListener(this);
		mGridView = mPullRefreshGridView.getRefreshableView();
		mGridView.setSelector(android.R.color.transparent);
		mGridView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				Intent intent=new Intent(ShopPicActivity.this, ImagePagerActivity.class);
				// 图片url,为了演示这里使用常量，一般从数据库中或网络中获取
				intent.putExtra(ImagePagerActivity.EXTRA_IMAGE_URLS, urlList);
				intent.putExtra(ImagePagerActivity.EXTRA_IMAGE_INDEX, position);
            	startActivity(intent);
			}
		});
		mGridView.setOnItemLongClickListener(new OnItemLongClickListener() {

			@Override
			public boolean onItemLongClick(AdapterView<?> arg0, View view, int index, long arg3) {
				showDelPop(view, index);
				return true;
			}
		});
		list = new ArrayList<ShopPicBean>();
		urlList = new ArrayList<>();
		adapter = new ShopPicAdapter(this, list);
		mGridView.setAdapter(adapter);
	}
	
	private void initData() {
		getData();
		if (mWaitDialog == null) {
			mWaitDialog = new WaitDialog(this, R.string.loading_data);
		}
		mWaitDialog.show();
	}

	private void getData() {
		mSharedPreferences = getSharedPreferences(Constant.SHARED_PREFERENCE, MODE_PRIVATE);
		String tokenid = mSharedPreferences.getString(Constant.TOKEN_ID, "");
		String saleId = mSharedPreferences.getString(Constant.ID, "");
		NetProxyManager.getInstance().toGetShopPic(mHandler, tokenid, saleId);
		// NetProxyManager.getInstance().toGetShopPic(mainHandler, saleId, mPage
		// + (mVideoBeans.size() / mRow), mRow);
	}
	
	public void showDelPop(View view, final int index) {
		String msg = "是否删除第" + (index + 1) + "张图片";
		new CustomerAlertDialog(this).builder().setTitle("提示").setMsg(msg)
		.setPositiveButton("删除", new OnClickListener() {
			@Override
			public void onClick(View v) {
				ShopPicBean shopPicBean = list.get(index);
				mSharedPreferences = getSharedPreferences(Constant.SHARED_PREFERENCE, MODE_PRIVATE);
				String tokenid = mSharedPreferences.getString(Constant.TOKEN_ID, "");
				String saleId = mSharedPreferences.getString(Constant.ID, "");
				NetProxyManager.getInstance().toDelShopPic(mHandler, tokenid, saleId, shopPicBean.getId());
			}
		}).setNegativeButton("取消", new OnClickListener() {
			@Override
			public void onClick(View v) {

			}
		}).show();
//		View inflate = getLayoutInflater().inflate(R.layout.dele_shop_pic_popupwindow, null);
//		mPopWin = new CustomPopupWindow(inflate, LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT,
//				this);
//		inflate.findViewById(R.id.shop_pic).setOnClickListener(new OnClickListener() {
//			@Override
//			public void onClick(View v) {
//				ShopPicBean shopPicBean = list.get(index);
//				mSharedPreferences = getSharedPreferences(Constant.SHARED_PREFERENCE, MODE_PRIVATE);
//				String tokenid = mSharedPreferences.getString(Constant.TOKEN_ID, "");
//				String saleId = mSharedPreferences.getString(Constant.ID, "");
//				NetProxyManager.getInstance().toDelShopPic(mHandler, tokenid, saleId, shopPicBean.getId());
//				mPopWin.dismiss();
//			}
//		});
//		mPopWin.showAtLocation(view, Gravity.CENTER, 0, 0);
	}

	public void uploadPic(View view) {
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
	 * to parse login infomations from network
	 */
	private void parseGetShopPic(String result) {
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
				if (json.getString("code").equals(Constant.TOKEN_INVAILD) 
						|| json.getString("code").equals(Constant.DIFF_DEVICE)) {
					backLogin();
				}
				return;
			}
			JSONArray array = json.getJSONArray("data");
			list.clear();
			urlList.clear();
			for (int i = 0; i < array.length(); i++) {
				JSONObject job = array.getJSONObject(i);
				ShopPicBean bean = new ShopPicBean();
				bean.setId(job.getString("id"));
				bean.setShopid(job.getString("shopid"));
				String url = job.getString("url");
				bean.setUrl(url);
				urlList.add(url);
				list.add(bean);
			}
			adapter.notifyDataSetChanged();
			// mPullRefreshGridView.onRefreshComplete();
			// if (mTotal <= 0) {
			// findViewById(R.id.no_data).setVisibility(View.VISIBLE);
			// return;
			// } else if (mTotal > 9) {
			// mPullRefreshGridView.setMode(Mode.BOTH);
			// }
			if (list.size() > 0)
				findViewById(R.id.no_data).setVisibility(View.GONE);
		} catch (Exception ex) {
			LogUtils.e(ex.getMessage());
			return;
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
			ToastUtil.showMessage("上传商户图片成功！");
			getData();
		} catch (Exception ex) {
			LogUtils.e(ex.getMessage());
			return;
		} finally {
			if (mUploadDialog != null && mUploadDialog.isShowing()) {
				mUploadDialog.dismiss();
			}
		}
	}
	
	private void parseDelPic(String result) {
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
			ToastUtil.showMessage("删除商户图片成功");
			getData();
		} catch (Exception ex) {
			LogUtils.e(ex.getMessage());
			return;
		} finally {
			if (mUploadDialog != null && mUploadDialog.isShowing()) {
				mUploadDialog.dismiss();
			}
		}
	}

	public void onBack(View view) {
		finish();
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
	public void onPullDownToRefresh(PullToRefreshBase<GridView> refreshView) {
		mHandler.postDelayed(mRefresCompleteRunnable, 1000);
	}

	@Override
	public void onPullUpToRefresh(PullToRefreshBase<GridView> refreshView) {
		if (mTotal == list.size()) {
			ToastUtil.showShort(this, R.string.no_more_data_loading);
			mHandler.postDelayed(mRefresCompleteRunnable, 1000);
		} else {
			getData();
		}
	}

	private Runnable mRefresCompleteRunnable = new Runnable() {

		@Override
		public void run() {
			mPullRefreshGridView.onRefreshComplete();
		}
	};

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (requestCode == REQUEST_IMAGE) {
			if (resultCode == RESULT_OK) {
				mSelectPath = data.getStringArrayListExtra(MultiImageSelectorActivity.EXTRA_RESULT);
				if (mSelectPath == null || mSelectPath.size() <= 0) {
					return;
				}
				if (mUploadDialog == null) {
					mUploadDialog = new WaitDialog(this, "图片正在上传中......");
				}
				mUploadDialog.show();
				NetProxyManager.getInstance().toGetToken(mHandler);
				for (int i = 0; i < mSelectPath.size(); i++) {
					LogUtils.d("lyp", mSelectPath.get(i));
				}

			}
		}
	}

	private void uploadQiNiu(final String uploadToken) {
		File uploadFile = new File(mSelectPath.get(0));
		UploadOptions uploadOptions = new UploadOptions(null, null, false, new UpProgressHandler() {
			@Override
			public void progress(String key, double percent) {
//				updateStatus(percent);//更新上传进度
			}
		}, null);
		
		//指定zone的具体区域 
		//FixedZone.zone0   华东机房
		//FixedZone.zone1   华北机房
		//FixedZone.zone2   华南机房
		//FixedZone.zoneNa0 北美机房
		//自动识别上传区域 
		//AutoZone.autoZone
		//Configuration config = new Configuration.Builder()
		//.zone(Zone.autoZone)
		//.build();
//		UploadManager uploadManager = new UploadManager(config);
//		data = <File对象、或 文件路径、或 字节数组>
//		String key = <指定七牛服务上的文件名，或 null>;
//		String token = <从服务端SDK获取>;
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
		NetProxyManager.getInstance().toUploadShopPic(mHandler, tokenid, saleId, picUrl);
	}

}
