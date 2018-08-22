package com.lyp.citypartner;

import org.json.JSONArray;
import org.json.JSONObject;

import com.lyp.base.BaseActivity;
import com.lyp.contactsort.ContactSortModel;
import com.lyp.database.CustomerDao;
import com.lyp.manager.ImageManager;
import com.lyp.net.Errors;
import com.lyp.net.MessageContants;
import com.lyp.net.NetProxyManager;
import com.lyp.utils.Constant;
import com.lyp.utils.LogUtils;
import com.lyp.utils.SystemStatusManager;
import com.lyp.utils.ToastUtil;
import com.lyp.view.CircleImageView;
import com.lyp.view.CustomPopupWindow;
import com.lyp.view.MyListView;
import com.lyp.view.dialog.WaitDialog;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;

public class CustomerDetailActivity extends BaseActivity {

	private final static int MODIFY_CUSTOMER_REQUEST_CODE = 0x1315;
	private WaitDialog mWaitDialog;
	private CustomerDao mCustomerDao;
	private TextView tv_title;
	private CircleImageView customer_avater;
	private TextView tv_name;
	private TextView tv_gener;
	private TextView tv_birthday;
	private TextView tv_fixedphone;
	private TextView tv_mail;
	private TextView tv_address;
	private TextView tv_phone;
	private TextView tv_district;
	private TextView tv_regtime;
	private TextView tv_profession;
	private String cusId;
	private CustomPopupWindow mPopWin;
	private boolean isUpdate = false;
	private SharedPreferences mSharedPreferences;
	private int deletePosition = -1;

	private Handler mHandler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case MessageContants.MSG_GET_CUSTOMER_DETAIL: {
				parseCustomerDetail((String) msg.obj);
				break;
			}
			default:
				break;
			}
		};
	};

	private Handler mainHandler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case 0: {
				parseGetProductType((String) msg.obj);
				break;
			}
			default:
				break;
			}
		};
	};
	private ContactSortModel contact;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setTranslucentStatus();
		setContentView(R.layout.customer_detail_layout);
		cusId = getIntent().getStringExtra("id");
		initView();
		initData();
	}

	private void initView() {
		tv_title = (TextView) findViewById(R.id.tv_title);
		customer_avater = (CircleImageView) findViewById(R.id.customer_avater);
		tv_name = (TextView) findViewById(R.id.tv_name);
		tv_gener = (TextView) findViewById(R.id.tv_gener);
		tv_phone = (TextView) findViewById(R.id.tv_phone);
		tv_birthday = (TextView) findViewById(R.id.tv_birthday);
		tv_fixedphone = (TextView) findViewById(R.id.tv_fixedphone);
		tv_mail = (TextView) findViewById(R.id.tv_mail);
		tv_address = (TextView) findViewById(R.id.tv_address);
		tv_regtime = (TextView) findViewById(R.id.tv_regtime);
		mCustomerDao = new CustomerDao(this.getApplicationContext());
	}

	private void initData() {
		
		mSharedPreferences = getSharedPreferences(Constant.SHARED_PREFERENCE, MODE_PRIVATE);
		String tokenid = mSharedPreferences.getString(Constant.TOKEN_ID, "");
//		String id = mSharedPreferences.getString(Constant.ID, "");
		NetProxyManager.getInstance().toGetCustomerDetail(mHandler, tokenid, cusId);
		
//		contact = mCustomerDao.getContact(id);
//		if (contact.getName() != null) {
//			tv_name.setText(contact.getName());
//		}
//
//		if (contact.getAvater() != null) {
//			ImageManager.loadImage(contact.getAvater(), customer_avater, R.drawable.customer_avater_dafault);
//		}
//
//		if (contact.getGender() != null) {
//			tv_gener.setText(contact.getGender().equals("0") ? "女" : "男");
//		}
//
//		if (contact.getCphone() != null) {
//			tv_phone.setText(contact.getCphone());
//		}
//
//		if (contact.getFixedtelephone() != null) {
//			tv_fixedphone.setText(contact.getFixedtelephone());
//		}
//
//		if (contact.getBirthday() != null) {
//			// tv_birthday.setText(DateUtil.stringToStr6(contact.getBirthday()));
//			tv_birthday.setText(contact.getBirthday());
//		}
//
//		if (contact.getRegtime() != null) {
//			tv_regtime.setText(contact.getRegtime());
//		}
//
//		if (contact.getCemail() != null) {
//			tv_mail.setText(contact.getCemail());
//		}
//		if (contact.getCaddress() != null) {
//			tv_address.setText(contact.getCaddress());
//		}
		
		// if (contact.getDistrict() != null) {
		// tv_district.setText(contact.getDistrict());
		// }
	}

	private void parseCustomerDetail(String result) {
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
			JSONObject jsonObject = json.getJSONObject("data");
			ContactSortModel contactSortModel = new ContactSortModel();
			contactSortModel.setId(jsonObject.getString("id"));
			if (jsonObject.has("birthday")) {
				String birthday = jsonObject.getString("birthday");
				contactSortModel.setBirthday(birthday);
				tv_birthday.setText(birthday);
			}
			if (jsonObject.has("regtime")) {
				String regtime = jsonObject.getString("regtime");
				contactSortModel.setRegtime(regtime);
				tv_regtime.setText(regtime);
			}
			if (jsonObject.has("cusname")) {
				String cusname = jsonObject.getString("cusname");
				contactSortModel.setName(cusname);
				tv_name.setText(cusname);
			}
			if (jsonObject.has("nickname")) {
				contactSortModel.setNickname(jsonObject.getString("nickname"));
			}
			if (jsonObject.has("sex")) {
				String sex = jsonObject.getString("sex");
				contactSortModel.setGender(sex);
				tv_gener.setText(sex.equals("0") ? "女" : "男");
			}
			if (jsonObject.has("fixedtelephone")) {
				String fixedtelephone = jsonObject.getString("fixedtelephone");
				contactSortModel.setFixedtelephone(fixedtelephone);
				tv_fixedphone.setText(fixedtelephone);
			}
			if (jsonObject.has("telephone")) {
				String telephone = jsonObject.getString("telephone");
				contactSortModel.setCphone(telephone);
				tv_phone.setText(telephone);
			}
			if (jsonObject.has("logisticsaddress")) {
				String logisticsaddress = jsonObject.getString("logisticsaddress");
				contactSortModel.setCaddress(logisticsaddress);
				tv_address.setText(logisticsaddress);
			}
			if (jsonObject.has("email")) {
				String email = jsonObject.getString("email");
				contactSortModel.setCemail(email);
				tv_mail.setText(email);
			}
			if (jsonObject.has("maritalStatus")) {
				contactSortModel.setMarry(jsonObject.getString("maritalStatus"));
			}
			if (jsonObject.has("district")) {
				String district = jsonObject.getString("district");
				contactSortModel.setDistrict(district);
				tv_address.setText(district);
			}
			if (jsonObject.has("cuspoint")) {
				contactSortModel.setCuspoint(jsonObject.getString("cuspoint"));
			}
			if (jsonObject.has("cusicon")) {
				String cusicon = jsonObject.getString("cusicon");
				contactSortModel.setAvater(cusicon);
				ImageManager.loadImage(cusicon, customer_avater, R.drawable.customer_avater_dafault);
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
//			mCustomerDao.saveContact(contactSortModel);
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

	public void modifyCustomer(View view) {
		// Intent intent = new Intent();
		// intent.setClass(this, ModifyCustomerActivity.class);
		// intent.putExtra("id", id);
		// startActivityForResult(intent, MODIFY_CUSTOMER_REQUEST_CODE);
	}

	public void callOrSMS(View view) {
		// View inflate =
		// getLayoutInflater().inflate(R.layout.customer_info_popupwindow,
		// null);
		// mPopWin = new CustomPopupWindow(inflate, LayoutParams.WRAP_CONTENT,
		// LayoutParams.WRAP_CONTENT,
		// CustomerDetailActivity.this);
		// inflate.findViewById(R.id.dial_phone).setOnClickListener(new
		// OnClickListener() {
		// @Override
		// public void onClick(View v) {
		// Intent intent = new Intent(Intent.ACTION_DIAL);
		// Uri uri = Uri.parse("tel:" + contact.getCphone());
		// intent.setData(uri);
		// // intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		// startActivity(intent);
		// mPopWin.dismiss();
		// }
		// });
		// inflate.findViewById(R.id.send_sms).setOnClickListener(new
		// OnClickListener() {
		// @Override
		// public void onClick(View v) {
		// Uri uri = Uri.parse("smsto:" + contact.getCphone());
		// Intent intentMessage = new Intent(Intent.ACTION_VIEW, uri);
		// startActivity(intentMessage);
		// mPopWin.dismiss();
		// }
		// });
		//
		// mPopWin.showAtLocation(view, Gravity.CENTER, 0, 0);
	}

	/**
	 * to parse login infomations from network
	 */
	private void parseGetProductType(String result) {
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
				if (json.getString("resCode").equals(Constant.RELOGIN)) {
					backLogin();
				}
				return;
			}
			JSONArray object = json.getJSONArray("object");
			// ToastUtil.showShort(this, "ok--" + object);
		} catch (Exception ex) {
			LogUtils.e(ex.getMessage());
			return;
		}
	}

	public void onBack(View view) {
		if (isUpdate) {
			setResult(RESULT_OK);
		}
		finish();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
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
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode == RESULT_OK && requestCode == MODIFY_CUSTOMER_REQUEST_CODE) {
			isUpdate = true;
			initData();
			return;
		}
		super.onActivityResult(requestCode, resultCode, data);
	}
}
