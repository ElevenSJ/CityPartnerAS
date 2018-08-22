package com.lyp.citypartner;

import java.text.DecimalFormat;

import org.json.JSONObject;

import com.lyp.base.BaseActivity;
import com.lyp.manager.ImageManager;
import com.lyp.net.Errors;
import com.lyp.net.MessageContants;
import com.lyp.net.NetProxyManager;
import com.lyp.utils.Constant;
import com.lyp.utils.DateUtil;
import com.lyp.utils.LogUtils;
import com.lyp.utils.SystemStatusManager;
import com.lyp.utils.ToastUtil;
import com.lyp.view.CircleImageView;
import com.lyp.view.dialog.WaitDialog;

import android.content.SharedPreferences;
import android.content.res.Resources;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;

public class OrderDetailActivity extends BaseActivity  {
	/** 资源对象 */
	public Resources res;
	private WaitDialog mWaitDialog;
	private SharedPreferences mSharedPreferences;
	private String id;
	private TextView order_no;
	private TextView order_date;
	private TextView order_state;
	private CircleImageView civ;
	private TextView cname;
	private TextView order_price;
	private TextView customer_price;
	private TextView point_price;
	private TextView shop_pay;
	private TextView shop_point_price;
	private TextView shop_paid;
	private TextView shop_get_point;
	private TextView pay_money;
	private TextView customer_need_price;

	private Handler mainHandler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case  MessageContants.MSG_GET_ORDER_DETAIL: {
				parseGetOrderDetail((String) msg.obj);
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
		setContentView(R.layout.order_detail_layout);
		id = getIntent().getStringExtra("order_id");
		order_no = (TextView) findViewById(R.id.order_no);
		order_date = (TextView) findViewById(R.id.order_date);
		order_state = (TextView) findViewById(R.id.order_state);
		civ = (CircleImageView) findViewById(R.id.civ);
		cname = (TextView) findViewById(R.id.cname);
		customer_price = (TextView) findViewById(R.id.customer_price);
		order_price = (TextView) findViewById(R.id.order_price);
		point_price = (TextView) findViewById(R.id.point_price);
		shop_pay = (TextView) findViewById(R.id.shop_pay);
		shop_point_price = (TextView) findViewById(R.id.shop_point_price);
		shop_paid = (TextView) findViewById(R.id.shop_paid);
		shop_get_point = (TextView) findViewById(R.id.shop_get_point);
		pay_money = (TextView) findViewById(R.id.pay_money);
		customer_need_price = (TextView) findViewById(R.id.customer_need_price);
		
		mSharedPreferences = getSharedPreferences(Constant.SHARED_PREFERENCE, MODE_PRIVATE);
		String tokenid = mSharedPreferences.getString(Constant.TOKEN_ID, "");
//		String saleId = mSharedPreferences.getString(Constant.ID, "");
		if (mWaitDialog == null) {
			mWaitDialog = new WaitDialog(this, R.string.loading_data);
		}
		mWaitDialog.show();
		NetProxyManager.getInstance().toGetOrderDetail(mainHandler, tokenid, id);
	}

	public void newWedding(View view) {

	}


	/**
	 * to parse login infomations from network
	 */
	private void parseGetOrderDetail(String result) {
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
			JSONObject job = json.getJSONObject("data");
			DecimalFormat df = new DecimalFormat("0.00");
			order_no.setText("No." + job.getString("ordercode"));
			order_date.setText(DateUtil.formatDateStr(job.getString("ordertime")));
			String status = job.getString("status");
			if ("1".equals(status)) {
				order_state.setText("已付款");
			} else if ("2".equals(status)) {
				order_state.setText("未付款");
			} else {
				order_state.setText("未付款");
			}
			
			order_price.setText("￥" + df.format(job.getDouble("ordermoney")));
			if (job.has("cusamountpaid")) {
				customer_need_price.setText("￥" + df.format(job.getDouble("cusamountpaid")));
			}
			customer_price.setText("￥" + df.format(job.getDouble("cusamountpay")));
			point_price.setText("￥" + df.format(job.getDouble("cuspointsded")));
			shop_pay.setText("￥" + df.format(job.getDouble("shopamountpay")));
			shop_point_price.setText("￥" + df.format(job.getDouble("shoppointsded")));
			shop_paid.setText("￥" + df.format(job.getDouble("shopamountpay")));
			shop_get_point.setText(job.getString("shopgetpoints"));
			pay_money.setText("￥" + df.format(job.getDouble("shopamountpaid")));
//			pay_money.setText("￥" + df.format(job.getDouble("shopamountpay")));
			
			JSONObject jb = job.getJSONObject("user");
			if (jb.has("cusicon")) {
				ImageManager.loadImage(jb.getString("cusicon"), civ, R.drawable.personal);
			}
			cname.setText(jb.getString("cusname"));
		} catch (Exception ex) {
			LogUtils.e(ex.getMessage());
			ToastUtil.showLongMessage("服务器返回数据有问题！");
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
