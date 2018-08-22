package com.lyp.citypartner;

import java.text.Collator;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import com.lyp.adapter.MyCustomerAdapter;
import com.lyp.base.BaseActivity;
import com.lyp.contactsort.ContactSortModel;
import com.lyp.contactsort.PinyinComparator;
import com.lyp.contactsort.PinyinUtils;
import com.lyp.contactsort.SideBar;
import com.lyp.database.CustomerDao;
import com.lyp.net.Errors;
import com.lyp.net.MessageContants;
import com.lyp.net.NetProxyManager;
import com.lyp.utils.Constant;
import com.lyp.utils.DateUtil;
import com.lyp.utils.LogUtils;
import com.lyp.utils.SystemStatusManager;
import com.lyp.utils.ToastUtil;
import com.lyp.view.CustomPopupWindow;
import com.lyp.view.dialog.WaitDialog;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.view.WindowManager.LayoutParams;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class MyCustomerActivity extends BaseActivity {

	private final static int ADD_CUSTOMER_REQUEST_CODE = 0x1314;
	private final static int UPDATE_CUSTOMER_REQUEST_CODE = 0x1315;
	private ListView sortListView;
	private SideBar sideBar;
	private TextView dialog, mTvTitle;
	private MyCustomerAdapter adapter;
	// private EditTextWithDel mEtSearchName;
	private List<ContactSortModel> mCustomerList;
	private RelativeLayout select_mode;
	private CheckBox customer_select_all;
	private WaitDialog mWaitDialog;
	private CustomPopupWindow mPopWin;
	private SharedPreferences mSharedPreferences;
	private CustomerDao mCustomerDao;
	private int deletePosition = -1;

	private Handler mHandler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case MessageContants.MSG_GET_CUSTOMER_LIST: {
				parseCustomerList((String) msg.obj);
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
		setContentView(R.layout.my_customer_layout);
		initViews();
	}

	private void initViews() {
		// mEtSearchName = (EditTextWithDel) findViewById(R.id.et_search);
		sideBar = (SideBar) findViewById(R.id.sidrbar);
		dialog = (TextView) findViewById(R.id.dialog);
		mTvTitle = (TextView) findViewById(R.id.tv_title);
		sortListView = (ListView) findViewById(R.id.lv_contact);
		mTvTitle.setText(R.string.my_customer);
		select_mode = (RelativeLayout) findViewById(R.id.select_mode);
		select_mode.setVisibility(View.GONE);
		customer_select_all = (CheckBox) findViewById(R.id.customer_select_all);
		initDatas();
		initEvents();
		adapter = new MyCustomerAdapter(this, mCustomerList);
		sortListView.setAdapter(adapter);
		getCustomerData();
		if (mWaitDialog == null) {
			mWaitDialog = new WaitDialog(MyCustomerActivity.this, R.string.loading_data);
		}
		mWaitDialog.show();
	}

	private void getCustomerData() {
		mSharedPreferences = getSharedPreferences(Constant.SHARED_PREFERENCE, MODE_PRIVATE);
		String tokenid = mSharedPreferences.getString(Constant.TOKEN_ID, "");
		String id = mSharedPreferences.getString(Constant.ID, "");
		NetProxyManager.getInstance().toGetCustomerList(mHandler, tokenid, id);
	}

	private void parseCustomerList(String result) {
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
			LogUtils.d("" + json);
			if (!success) {
				String message = json.getString("message");
				ToastUtil.showShort(this, message);
				if (json.getString("code").equals(Constant.TOKEN_INVAILD) 
						|| json.getString("code").equals(Constant.DIFF_DEVICE)) {
					backLogin();
				}
				return;
			}
			JSONArray jsonArray = json.getJSONArray("data");
			mCustomerList.clear();
			for (int i = 0; i < jsonArray.length(); i++) {
				JSONObject jsonObject = jsonArray.getJSONObject(i);
				ContactSortModel contactSortModel = new ContactSortModel();
				contactSortModel.setId(jsonObject.getString("id"));
				if (jsonObject.has("birthday")) {
					String birthday = jsonObject.getString("birthday");
					contactSortModel.setBirthday(birthday);
				}
				if (jsonObject.has("regtime")) {
					contactSortModel.setRegtime(jsonObject.getString("regtime"));
				}
				if (jsonObject.has("cusname")) {
					contactSortModel.setName(jsonObject.getString("cusname"));
				}
				if (jsonObject.has("nickname")) {
					contactSortModel.setNickname(jsonObject.getString("nickname"));
				}
				if (jsonObject.has("sex")) {
					contactSortModel.setGender(jsonObject.getString("sex"));
				}
				if (jsonObject.has("fixedtelephone")) {
					contactSortModel.setFixedtelephone(jsonObject.getString("fixedtelephone"));
				}
				if (jsonObject.has("telephone")) {
					contactSortModel.setCphone(jsonObject.getString("telephone"));
				}
				if (jsonObject.has("logisticsaddress")) {
					contactSortModel.setCaddress(jsonObject.getString("logisticsaddress"));
				}
				if (jsonObject.has("email")) {
					contactSortModel.setCemail(jsonObject.getString("email"));
				}
				if (jsonObject.has("maritalStatus")) {
					contactSortModel.setMarry(jsonObject.getString("maritalStatus"));
				}
				if (jsonObject.has("district")) {
					contactSortModel.setDistrict(jsonObject.getString("district"));
				}
				if (jsonObject.has("cuspoint")) {
					contactSortModel.setCuspoint(jsonObject.getString("cuspoint"));
				}
				if (jsonObject.has("cusicon")) {
					contactSortModel.setAvater(jsonObject.getString("cusicon"));
				}
				if (jsonObject.has("unionshopsupid")) {
					contactSortModel.setUnionshopsupid(jsonObject.getString("unionshopsupid"));
				}
				if (jsonObject.has("zipcode")) {
					contactSortModel.setZipcode(jsonObject.getString("zipcode"));
				}
				if (jsonObject.has("codepic")) {
					contactSortModel.setCodepic(jsonObject.getString("codepic"));
				}
				if (jsonObject.has("idcardno")) {
					contactSortModel.setIccardno(jsonObject.getString("idcardno"));
				}
				mCustomerList.add(contactSortModel);
			}
			contactsSort();
			mCustomerDao.saveContactList(mCustomerList);
		} catch (Exception ex) {
			LogUtils.e(ex.getMessage());
			ToastUtil.showLongMessage("服务器返回信息有问题！");
			return;
		} finally {
			if (mWaitDialog != null && mWaitDialog.isShowing()) {
				mWaitDialog.dismiss();
			}
		}
	}

	private void parseDeleteCustomer(String result) {
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
			LogUtils.d("" + json);
			if (!success) {
				String message = json.getString("message");
				ToastUtil.showShort(this, message);
				return;
			}
			mCustomerDao.deleteContact(mCustomerList.get(deletePosition).getId());
			mCustomerList.remove(deletePosition);
			adapter.notifyDataSetChanged();
		} catch (Exception ex) {
			LogUtils.e(ex.getMessage());
			return;
		}
	}

	public void addCustomer(View view) {
//		Intent intent = new Intent();
//		intent.setClass(this, AddCustomerActivity.class);
//		startActivityForResult(intent, ADD_CUSTOMER_REQUEST_CODE);
	}

	public void backMyCustomer(View view) {
		back();
	}

	private void back() {
//		if (adapter.isMulChoice()) {
//			mTvTitle.setText(R.string.my_customer);
//			adapter.setMulChoice(false);
//			adapter.getSelectContact().clear();
//			adapter.isCheckNum = 0;
//			select_mode.setVisibility(View.GONE);
//			customer_select_all.setChecked(false);
//			for (int i = 0; i < mCustomerList.size(); i++) {
//				adapter.visiblecheck.put(i, CheckBox.INVISIBLE);
//				adapter.ischeck.put(i, false);
//			}
//			adapter.notifyDataSetChanged();
//		} else {
			finish();
//		}
	}

	private void contactsSort() {
		ArrayList<String> indexString = new ArrayList<String>();

		for (int i = 0; i < mCustomerList.size(); i++) {
			ContactSortModel sortModel = mCustomerList.get(i);
			String name = sortModel.getName();
			LogUtils.d("Sort name: " + name);
			if (name == null || name.trim().length() <= 0 || Character.isDigit(name.charAt(0))) {
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
			} else {
				sortModel.setSortLetters("@");
				if (!indexString.contains("@")) {
					indexString.add("@");
				}
			}
		}
		LogUtils.d("Start Sort contacts");
		Comparator<Object> com = Collator.getInstance(java.util.Locale.CHINA);
		Collections.sort(mCustomerList, new PinyinComparator());
		Collections.sort(indexString, com);
		// sideBar.setIndexText(indexString);
		LogUtils.d("End Sort contacts");
		adapter.notifyDataSetChanged();
	}

	private void initEvents() {
		// 设置右侧触摸监听
		sideBar.setOnTouchingLetterChangedListener(new SideBar.OnTouchingLetterChangedListener() {
			@Override
			public void onTouchingLetterChanged(String s) {
				// 该字母首次出现的位置
				int position = adapter.getPositionForSection(s.charAt(0));
				if (position != -1) {
					sortListView.setSelection(position + 1);
				}
			}
		});

		// ListView的点击事件
		sortListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				ContactSortModel contactSortModel = mCustomerList.get(position);
				Intent intent = new Intent();
				intent.setClass(MyCustomerActivity.this, CustomerDetailActivity.class);
				intent.putExtra("id", contactSortModel.getId());
				startActivityForResult(intent, UPDATE_CUSTOMER_REQUEST_CODE);
				// showClickEvent(view, position);

				// if (adapter.isMulChoice()) {
				// CheckBox checkBox = (CheckBox)
				// view.findViewById(R.id.customer_select);
				// if (checkBox.isChecked()) {
				// customer_select_all.setChecked(false);
				// checkBox.setChecked(false);
				// adapter.ischeck.put(position, false);
				// adapter.isCheckNum--;
				// adapter.getSelectContact().remove(mCustomerList.get(position));
				// } else {
				// checkBox.setChecked(true);
				// adapter.ischeck.put(position, true);
				// adapter.isCheckNum++;
				// if (adapter.isCheckNum == adapter.getCount()) {
				// customer_select_all.setChecked(true);
				// }
				// adapter.getSelectContact().add(mCustomerList.get(position));
				// }
				// adapter.notifyDataSetChanged();
				// ToastUtil.showShort(MyCustomerActivity.this,
				// position + " " + adapter.isCheckNum + " " +
				// mCustomerList.get(position).getName());
				// } else {
				// ToastUtil.showShort(MyCustomerActivity.this,
				// position + " " + mCustomerList.get(position).getName());
				// }
			}
		});

		sortListView.setOnItemLongClickListener(new Onlongclick());

		// 根据输入框输入值的改变来过滤搜索
		// mEtSearchName.addTextChangedListener(new TextWatcher() {
		// @Override
		// public void beforeTextChanged(CharSequence s, int start, int count,
		// int after) {
		//
		// }
		//
		// @Override
		// public void onTextChanged(CharSequence s, int start, int before, int
		// count) {
		// //当输入框里面的值为空，更新为原来的列表，否则为过滤数据列表
		// filterData(s.toString());
		// }
		//
		// @Override
		// public void afterTextChanged(Editable s) {
		//
		// }
		// });
	}

	private void showClickEvent(View view, final int position) {
//		final ContactSortModel contactSortModel = mCustomerList.get(position);
//		View inflate = getLayoutInflater().inflate(R.layout.customer_info_popupwindow, null);
//		mPopWin = new CustomPopupWindow(inflate, LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT,
//				MyCustomerActivity.this);
//		inflate.findViewById(R.id.dial_phone).setOnClickListener(new OnClickListener() {
//			@Override
//			public void onClick(View v) {
//				Intent intent = new Intent(Intent.ACTION_DIAL);
//				Uri uri = Uri.parse("tel:" + contactSortModel.getCphone());
//				intent.setData(uri);
//				// intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//				startActivity(intent);
//				mPopWin.dismiss();
//			}
//		});
//		inflate.findViewById(R.id.send_sms).setOnClickListener(new OnClickListener() {
//			@Override
//			public void onClick(View v) {
//				Uri uri = Uri.parse("smsto:" + contactSortModel.getCphone());
//				Intent intentMessage = new Intent(Intent.ACTION_VIEW, uri);
//				startActivity(intentMessage);
//				mPopWin.dismiss();
//			}
//		});
//		inflate.findViewById(R.id.delet_customer).setOnClickListener(new OnClickListener() {
//			@Override
//			public void onClick(View v) {
//				if (mSharedPreferences == null) {
//					mSharedPreferences = getSharedPreferences(Constant.SHARED_PREFERENCE, MODE_PRIVATE);
//				}
//				String tokenid = mSharedPreferences.getString(Constant.TOKEN_ID, "");
//				String saleId = mSharedPreferences.getString(Constant.ID, "");
//				deletePosition = position;
//				NetProxyManager.getInstance().toDeleteCustomer(mHandler, tokenid, saleId, contactSortModel.getId());
//				mPopWin.dismiss();
//			}
//		});
//
//		mPopWin.showAtLocation(view, Gravity.CENTER, 0, 0);
	}

	private void initDatas() {
		sideBar.setTextView(dialog);
		mCustomerDao = new CustomerDao(this.getApplicationContext());
		mCustomerList = mCustomerDao.getContactList();
		if (mCustomerList == null)
			mCustomerList = new ArrayList<ContactSortModel>();
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

	class Onlongclick implements AdapterView.OnItemLongClickListener {

		@Override
		public boolean onItemLongClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
			showClickEvent(arg1, arg2);
			// if (adapter.isMulChoice()) {
			// return false;
			// }
			// mTvTitle.setText(R.string.select_customer);
			// adapter.setMulChoice(true);
			// // adapter.getSelectContact().clear();
			// select_mode.setVisibility(View.VISIBLE);
			// customer_select_all.setChecked(false);
			// for (int i = 0; i < mCustomerList.size(); i++) {
			// adapter.visiblecheck.put(i, CheckBox.VISIBLE);
			// // adapter.ischeck.put(i, false);
			// }
			// adapter.notifyDataSetChanged();
			// ToastUtil.showShort(MyCustomerActivity.this, "onItemLongClick");
			return true;
		}
	}

	public void selectAll(View view) {
		CheckBox checkbox = (CheckBox) view;
		if (checkbox.isChecked()) {
			for (int i = 0; i < mCustomerList.size(); i++) {
				adapter.ischeck.put(i, true);
			}
			adapter.isCheckNum = adapter.getCount();
		} else {
			for (int i = 0; i < mCustomerList.size(); i++) {
				adapter.ischeck.put(i, false);
			}
			adapter.isCheckNum = 0;
		}
		adapter.notifyDataSetChanged();
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			back();
			return false;
		}
		return super.onKeyDown(keyCode, event);
	}

	@Override
	protected void backLogin() {
		super.backLogin();
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode == RESULT_OK) {
			switch (requestCode) {
			case ADD_CUSTOMER_REQUEST_CODE:
				getCustomerData();
				break;
			case UPDATE_CUSTOMER_REQUEST_CODE:
				getCustomerData();
			default:
				break;
			}
			return;
		}
		super.onActivityResult(requestCode, resultCode, data);
	}
}
