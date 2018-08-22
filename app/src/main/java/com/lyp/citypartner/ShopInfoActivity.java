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
import com.lyp.utils.ValidateUtils;
import com.lyp.view.dialog.CityDialog;
import com.lyp.view.dialog.CityDialog.InputListener;
import com.lyp.view.dialog.WaitDialog;

import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.TextView;

public class ShopInfoActivity extends BaseActivity {

	private WaitDialog mWaitDialog;
	private SharedPreferences mSharedPreferences;
	private EditText tv_name;
	private EditText tv_fixedphone;
	private EditText tv_mail;
	private TextView tv_area;
	private EditText tv_address;
	private EditText tv_phone;
	private EditText tv_zipcode;
	private EditText tv_describtion;
	private TextView tv_income;
	private String id;
	private CityDialog mCityDialog;
	private String prov;
	private String provCode;
	private String city;
	private String cityCode;
	private String county;
	private String countyCode;

	private Handler mainHandler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case MessageContants.MSG_GET_SHOP_INFO: {
				parseGetShopInfo((String) msg.obj);
				break;
			}
			case MessageContants.MSG_MODIFY_SHOP_INFO: {
				parseModifyShopInfo((String) msg.obj);
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
		setContentView(R.layout.shop_info_layout);
		initView();
		initData();
	}

	private void initView() {
		tv_name = (EditText) findViewById(R.id.tv_name);
		tv_phone = (EditText) findViewById(R.id.tv_phone);
		tv_zipcode = (EditText) findViewById(R.id.tv_zipcode);
		tv_fixedphone = (EditText) findViewById(R.id.tv_fixedphone);
		tv_mail = (EditText) findViewById(R.id.tv_mail);
		tv_area = (TextView) findViewById(R.id.tv_area);
		tv_address = (EditText) findViewById(R.id.tv_address);
		tv_describtion = (EditText) findViewById(R.id.tv_describtion);
		tv_income = (TextView) findViewById(R.id.tv_income);
	}
	
	public void setAddress(View view) {
		InputListener listener = new InputListener() {

			@Override
			public void getText(String province, String pCode, String c, 
					String cCode, String district,
					String districtCode) {
				prov = province;
				provCode = pCode;
				city = c;
				cityCode = cCode;
				county = district;
				countyCode = districtCode;
				tv_area.setText(prov + city + county);
			}
		};
		mCityDialog = new CityDialog(this, listener);
		mCityDialog.setTitle(R.string.district);
		mCityDialog.show();
	}

	private void initData() {
		mSharedPreferences = getSharedPreferences(Constant.SHARED_PREFERENCE, MODE_PRIVATE);
		String tokenid = mSharedPreferences.getString(Constant.TOKEN_ID, "");
		String saleId = mSharedPreferences.getString(Constant.ID, "");
		if (mWaitDialog == null) {
			mWaitDialog = new WaitDialog(this, R.string.loading_data);
		}
		mWaitDialog.show();
		NetProxyManager.getInstance().toGetShopInfo(mainHandler, tokenid, saleId);
	}
	
	public void modifyInfo(View view) {
		mSharedPreferences = getSharedPreferences(Constant.SHARED_PREFERENCE, MODE_PRIVATE);
		String tokenid = mSharedPreferences.getString(Constant.TOKEN_ID, "");
		String saleId = mSharedPreferences.getString(Constant.ID, "");
		
		Editable nameET = tv_name.getText();
		String shopname = null;
		if (nameET != null) {
			shopname = nameET.toString();
		}
		Editable phoneET = tv_phone.getText();
		String telephone = null;
		if (phoneET != null) {
			telephone = phoneET.toString();
			if (!ValidateUtils.isMobile(telephone)) {
				ToastUtil.showMessage("请输入合法的手机号！");
				return;
			}
		}
		Editable zipCodeET = tv_zipcode.getText();
		String zipcode = null;
		if (zipCodeET != null) {
			zipcode = zipCodeET.toString();
			if (!ValidateUtils.isPostCode(zipcode)) {
				ToastUtil.showMessage("请输入合法的邮编！");
				return;
			}
		}
		Editable fixedPhoneET = tv_fixedphone.getText();
		String fixedtelephone = null;
		if (fixedPhoneET != null) {
			fixedtelephone = fixedPhoneET.toString();
			if (!ValidateUtils.isFixedPhone(fixedtelephone)) {
				ToastUtil.showMessage("请输入合法的座机号！");
				return;
			}
		}
		Editable mailET = tv_mail.getText();
		String email = null;
		if (mailET != null) {
			email = mailET.toString();
			if (!ValidateUtils.isEmail(email)) {
				ToastUtil.showMessage("请输入合法的邮箱！");
				return;
			}
		}
		Editable addressET = tv_address.getText();
		String address = null;
		if (addressET != null && addressET.toString().trim().length() > 0) {
			address = addressET.toString();
		} else {
//			ToastUtil.showMessage("请输入地址");
//			return;
		}
		
		Editable describtionET = tv_describtion.getText();
		String describtion = null;
		if (describtionET != null) {
			describtion = describtionET.toString();
		}
		if (mWaitDialog == null) {
			mWaitDialog = new WaitDialog(this, R.string.loading_data);
		}
		mWaitDialog.show();
		NetProxyManager.getInstance().toModifyShopInfo(mainHandler, tokenid,
				id, shopname, 
				fixedtelephone, telephone, address, email, zipcode, describtion,
				prov, provCode, city, cityCode, county, countyCode);
	}

	/**
	 * to parse login infomations from network
	 */
	private void parseGetShopInfo(String result) {
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
			if (!success) {
				String message = json.getString("message");
				ToastUtil.showShort(this, message);
				if (json.getString("code").equals(Constant.TOKEN_INVAILD) 
						|| json.getString("code").equals(Constant.DIFF_DEVICE)) {
					backLogin();
				}
				return;
			}
			JSONObject job = json.getJSONObject("data");
			id = job.getString("id");
			tv_name.setText(job.getString("shopname"));
			tv_fixedphone.setText(job.getString("fixedtelephone"));
			tv_mail.setText(job.getString("email"));
			tv_phone.setText(job.getString("telephone"));
			tv_zipcode.setText(job.getString("zipcode"));
			tv_describtion.setText(job.getString("describtion"));
			String area = null;
			if (job.has("prov")) {
				 prov = job.getString("prov");
				 area = prov;
				provCode = job.getString("provCode");
			}
			if (job.has("city")) {
				city = job.getString("city");
				area = area + city;
				cityCode = job.getString("cityCode");
			}
			if (job.has("county")) {
				county = job.getString("county");
				area = area + county;
				countyCode = job.getString("countyCode");
			}
			tv_area.setText(area);
			if (job.has("income")) {
				tv_income.setText(job.getString("income"));
			}
			tv_address.setText(job.getString("address"));
		} catch (Exception ex) {
			LogUtils.e(ex.getMessage());
			return;
		} finally {
			if (mWaitDialog != null && mWaitDialog.isShowing()) {
				mWaitDialog.dismiss();
			}
		}
	}
	
	private void parseModifyShopInfo(String result) {
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
			if (!success) {
				String message = json.getString("message");
				ToastUtil.showShort(this, message);
				if (json.getString("code").equals(Constant.TOKEN_INVAILD) 
						|| json.getString("code").equals(Constant.DIFF_DEVICE)) {
					backLogin();
				}
				return;
			}
//			JSONObject job = json.getJSONObject("data");
//			id = job.getString("id");
//			tv_name.setText(job.getString("shopname"));
//			tv_fixedphone.setText(job.getString("fixedtelephone"));
//			tv_mail.setText(job.getString("email"));
//			tv_address.setText(job.getString("address"));
//			tv_phone.setText(job.getString("telephone"));
//			tv_zipcode.setText(job.getString("zipcode"));
//			tv_describtion.setText(job.getString("describtion"));
			ToastUtil.showLongMessage("修改商户信息成功");
		} catch (Exception ex) {
			LogUtils.e(ex.getMessage());
			return;
		} finally {
			if (mWaitDialog != null && mWaitDialog.isShowing()) {
				mWaitDialog.dismiss();
			}
		}
	}

	public void onBack(View view) {
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

}
