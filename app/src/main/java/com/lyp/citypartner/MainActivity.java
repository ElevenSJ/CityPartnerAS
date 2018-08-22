package com.lyp.citypartner;

import java.text.Collator;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import com.google.zxing.client.android.CaptureActivity;
import com.lyp.base.BaseActivity;
import com.lyp.contactsort.ContactSortModel;
import com.lyp.contactsort.PinyinComparator;
import com.lyp.contactsort.PinyinUtils;
import com.lyp.database.CustomerDao;
import com.lyp.net.MessageContants;
import com.lyp.permission.PermissionUtils;
import com.lyp.permission.PermissionsActivity;
import com.lyp.permission.PermissionsChecker;
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
import android.os.Handler;
import android.view.View;
import android.view.WindowManager;
import android.widget.RelativeLayout;

public class MainActivity extends BaseActivity {

	private SharedPreferences mSharedPreferences;
	private CustomerDao mCustomerDao;
	private List<ContactSortModel> mCustomerList;
	private WaitDialog mWaitDialog;
	private PermissionsChecker mPermissionsChecker; // 权限检测器
	private static final int REQUEST_PERMISSION = 1006; // 权限请求
	private RelativeLayout new_guide_rl;

	private Handler mHandler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case MessageContants.MSG_GET_CUSTOMER_LIST: {
				// parseCustomerList((String) msg.obj);
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
		setContentView(R.layout.main_layout);
		mSharedPreferences = getSharedPreferences(Constant.SHARED_PREFERENCE, MODE_PRIVATE);
		boolean IS_GUIDE = mSharedPreferences.getBoolean(Constant.IS_GUIDE, true);
		new_guide_rl = (RelativeLayout) findViewById(R.id.new_guide_rl);
		// if (IS_GUIDE) {
		// new_guide_rl.setVisibility(View.VISIBLE);
		// } else {
		new_guide_rl.setVisibility(View.GONE);
		// }
		mPermissionsChecker = new PermissionsChecker(this);
		mCustomerDao = new CustomerDao(this.getApplicationContext());
		mCustomerList = mCustomerDao.getContactList();
		if (mCustomerList == null)
			mCustomerList = new ArrayList<ContactSortModel>();
		mSharedPreferences = getSharedPreferences(Constant.SHARED_PREFERENCE, MODE_PRIVATE);
		// String tokenid = mSharedPreferences.getString(Constant.TOKEN_ID, "");
		String id = mSharedPreferences.getString(Constant.ID, "");
		if (mWaitDialog == null) {
			mWaitDialog = new WaitDialog(this, R.string.loading_data);
		}
		// NetProxyManager.getInstance().toGetCustomerList(mHandler, id);

		PermissionUtils.requestMultiPermissions(this, mPermissionGrant);
	}

	public void searchContent(View view) {
		ToastUtil.showShort(this, "searchContent");
	}

	public void myCustomer(View view) {
		Intent intent = new Intent();
		intent.setClass(this, MyCustomerActivity.class);
		startActivity(intent);
	}

	public void myOrderList(View view) {
		Intent intent = new Intent();
		intent.setClass(this, OrderActivity.class);
		startActivity(intent);
	}

	public void payOrder(View view) {
//		ToastUtil.showShort(this, "正在开发中，尽请期待！");
		Intent i = new Intent();
		i.setClass(this, CaptureActivity.class);
		startActivity(i);
	}

	public void personalCenter(View view) {
		Intent intent = new Intent();
		intent.setClass(this, PersonalCenterActivity.class);
		startActivity(intent);
	}

	private void startPermissionsActivity() {
		PermissionsActivity.startActivityForResult(this, REQUEST_PERMISSION, PermissionUtils.requestPermissions);
	}

	private PermissionUtils.PermissionGrant mPermissionGrant = new PermissionUtils.PermissionGrant() {
		@Override
		public void onPermissionGranted(int requestCode) {
			switch (requestCode) {
			case PermissionUtils.CODE_MULTI_PERMISSION:
				break;
			default:
				break;
			}
		}
	};

	public void knowNewGuide(View view) {
		new_guide_rl.setVisibility(View.GONE);
		Editor editor = mSharedPreferences.edit();
		editor.putBoolean(Constant.IS_GUIDE, false);
		editor.commit();
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
		tintManager.setStatusBarTintResource(android.R.color.white);// 状态栏无背景
		getWindow().getDecorView().setFitsSystemWindows(true);
	}

	private void contactsSort() {
		ArrayList<String> indexString = new ArrayList<String>();

		for (int i = 0; i < mCustomerList.size(); i++) {
			ContactSortModel sortModel = mCustomerList.get(i);
			String name = sortModel.getName();
			if (Character.isDigit(name.charAt(0))) {
				sortModel.setSortLetters("#");
				if (!indexString.contains("#")) {
					indexString.add("#");
				}
				continue;
			}
			String pinyin = PinyinUtils.getPingYin(name);
			String sortString = pinyin.substring(0, 1).toUpperCase();
			LogUtils.d("Sort contacts: " + sortString);
			if (sortString.matches("[A-Z]")) {
				sortModel.setSortLetters(sortString.toUpperCase());
				LogUtils.d("contacts Letters: " + sortString.toUpperCase());
				if (!indexString.contains(sortString)) {
					indexString.add(sortString);
				}
			}
		}
		Comparator<Object> com = Collator.getInstance(java.util.Locale.CHINA);
		Collections.sort(mCustomerList, new PinyinComparator());
		Collections.sort(indexString, com);
		// sideBar.setIndexText(indexString);
	}

	@Override
	protected void onResume() {
		super.onResume();
		// Intent ecInitIntent =new Intent();
		// ecInitIntent.setAction("com.yuntongxun.ecdemo.inited");
		// sendBroadcast(ecInitIntent);
		// if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
		// if
		// (mPermissionsChecker.lacksPermissions(PermissionUtils.requestPermissions))
		// {
		// startPermissionsActivity();
		// return;
		// }
		// }

	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// if (requestCode == 0x1314) {
		// if (!Settings.System.canWrite(this)) {
		// Toast.makeText(this, "权限授予失败，", Toast.LENGTH_SHORT).show();
		// }
		// }
		super.onActivityResult(requestCode, resultCode, data);
	}

	@Override
	public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
		super.onRequestPermissionsResult(requestCode, permissions, grantResults);
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
	}

	@Override
	protected void onNewIntent(Intent intent) {
		super.onNewIntent(intent);
	}
}
