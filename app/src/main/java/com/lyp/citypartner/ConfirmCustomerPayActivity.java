package com.lyp.citypartner;

import java.text.DecimalFormat;

import org.json.JSONObject;

import com.lyp.base.BaseActivity;
import com.lyp.net.Errors;
import com.lyp.net.MessageContants;
import com.lyp.net.NetProxyManager;
import com.lyp.pay.PayActivity;
import com.lyp.utils.Constant;
import com.lyp.utils.LogUtils;
import com.lyp.utils.SystemStatusManager;
import com.lyp.utils.ToastUtil;
import com.lyp.view.dialog.WaitDialog;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

public class ConfirmCustomerPayActivity extends BaseActivity {
	private WaitDialog mWaitDialog;
	private SharedPreferences mSharedPreferences;
	private String cId;
	private EditText input_price;
	private TextView order_amount;
	private TextView point_price;
	private TextView shop_pay;
	private TextView shop_point_price;
	private TextView shop_get_point;
	private TextView pay_money;
	private DecimalFormat df;
	private Button payBtn;
	private LinearLayout pay_layout;
	private String cusamountpay;
	private String shopgetpoints;
	private String ordercode;
//	private String shopamountpaid;
	private String shopamountpay;
	private String cuspointsded;
	private String ordermoney;
	private String shoppointsded;
	private String shopid;
	private String userid;
	private TextView customer_price;

	private Handler mainHandler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case MessageContants.MSG_AFTER_CUSTOMER_PAY: {
				parsePayOrder((String) msg.obj);
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
		cId = getIntent().getStringExtra("cId");
		cusamountpay = getIntent().getStringExtra("cusamountpay");
		shopgetpoints = getIntent().getStringExtra("shopgetpoints");
		ordercode = getIntent().getStringExtra("ordercode");
//		shopamountpaid = getIntent().getStringExtra("shopamountpaid");
		shopamountpay = getIntent().getStringExtra("shopamountpay");
		cuspointsded = getIntent().getStringExtra("cuspointsded");
		ordermoney = getIntent().getStringExtra("ordermoney");
		shoppointsded = getIntent().getStringExtra("shoppointsded");
		shopid = getIntent().getStringExtra("shopid");
		userid = getIntent().getStringExtra("userid");
		setContentView(R.layout.confirm_customer_pay_layout);
		initView();
	}

	private void initView() {
		df = new DecimalFormat("0.00");
//		Pingpp.DEBUG = true;
		input_price = (EditText) findViewById(R.id.input_price);
		order_amount = (TextView) findViewById(R.id.order_amount);
		customer_price = (TextView) findViewById(R.id.customer_price);
		point_price = (TextView) findViewById(R.id.point_price);
		shop_pay = (TextView) findViewById(R.id.shop_pay);
		shop_point_price = (TextView) findViewById(R.id.shop_point_price);
		shop_get_point = (TextView) findViewById(R.id.shop_get_point);
		pay_money = (TextView) findViewById(R.id.pay_money);
		order_amount.setText("￥" + df.format(Double.valueOf(ordermoney)));
		customer_price.setText("￥" + df.format(Double.valueOf(cusamountpay)));
		point_price.setText("￥" + df.format(Double.valueOf(cuspointsded)));
		shop_pay.setText("￥" + df.format(Double.valueOf(shopamountpay)));
		shop_point_price.setText("￥" + df.format(Double.valueOf(shoppointsded)));
		shop_get_point.setText(shopgetpoints);
		pay_money.setText("￥" + df.format(Double.valueOf(shopamountpay)));
	}

	private void initData() {
		if (mWaitDialog == null) {
			mWaitDialog = new WaitDialog(this, R.string.loading_data);
		}
		mWaitDialog.show();
		mSharedPreferences = getSharedPreferences(Constant.SHARED_PREFERENCE, MODE_PRIVATE);
		String tokenid = mSharedPreferences.getString(Constant.TOKEN_ID, "");
		String saleId = mSharedPreferences.getString(Constant.ID, "");
	}

	public void payBtn(View view) {
		if (mWaitDialog == null) {
			mWaitDialog = new WaitDialog(this, R.string.loading_data);
		}
		mWaitDialog.show();
		mSharedPreferences = getSharedPreferences(Constant.SHARED_PREFERENCE, MODE_PRIVATE);
		String tokenid = mSharedPreferences.getString(Constant.TOKEN_ID, "");
		NetProxyManager.getInstance().toConfirmCustomerPay(mainHandler, shopid, ordercode);

	}

	/**
	 * to parse login infomations from network
	 */
	private void parsePayOrder(String result) {
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
				return;
			}
			JSONObject job = json.getJSONObject("data");
			String shopamountpay = job.getString("shopamountpay");
			String ordercode = job.getString("ordercode");
//			ToastUtil.showShort(this, "客户支付成功！");
			Intent i = new Intent();
			i.setClass(this, PayActivity.class);
			i.putExtra("ordermoney", ordermoney);
			i.putExtra("cuspointsded", cuspointsded);
			i.putExtra("shopamountpay", shopamountpay);
			i.putExtra("shoppointsded", shoppointsded);
			i.putExtra("shopgetpoints", shopgetpoints);
//			i.putExtra("shopamountpaid", shopamountpaid);
			i.putExtra("ordercode", ordercode);
			startActivity(i);
			finish();
		} catch (Exception ex) {
			LogUtils.e(ex.getMessage());
			return;
		} finally {
			if (mWaitDialog != null && mWaitDialog.isShowing()) {
				mWaitDialog.dismiss();
			}
		}
	}

	/**
	 * to parse login infomations from network
	 */
	private void parseConfirmPayOrder(String result) {
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
			ToastUtil.showShort(this, "支付成功！");
			setResult(RESULT_OK);
			finish();
		} catch (Exception ex) {
			LogUtils.e(ex.getMessage());
			return;
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
}
