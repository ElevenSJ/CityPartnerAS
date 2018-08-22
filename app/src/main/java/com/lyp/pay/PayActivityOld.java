package com.lyp.pay;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import com.lyp.base.BaseActivity;
import com.lyp.citypartner.R;
import com.lyp.net.API;
import com.lyp.net.Errors;
import com.lyp.net.HttpSession;
import com.lyp.net.MessageContants;
import com.lyp.utils.Constant;
import com.lyp.utils.LogUtils;
import com.lyp.utils.SystemStatusManager;
import com.lyp.utils.ToastUtil;
import com.lyp.view.dialog.WaitDialog;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class PayActivityOld extends BaseActivity implements OnClickListener {
	/**
	 * 开发者需要填一个服务端URL 该URL是用来请求支付需要的charge。务必确保，URL能返回json格式的charge对象。
	 * 服务端生成charge 的方式可以参考ping++官方文档，地址
	 * https://pingxx.com/guidance/server/import
	 *
	 * 【 http://218.244.151.190/demo/charge 】是 ping++ 为了方便开发者体验 sdk 而提供的一个临时 url
	 * 。 该 url 仅能调用【模拟支付控件】，开发者需要改为自己服务端的 url 。
	 */
	private static String YOUR_URL = "http://218.244.151.190/demo/charge";
	public static final String CHARGE_URL = YOUR_URL;
	public static final boolean LIVEMODE = false;
	/**
	 * 银联支付渠道
	 */
	private static final String CHANNEL_UPACP = "upacp";
	/**
	 * 微信支付渠道
	 */
	private static final String CHANNEL_WECHAT = "wx";
	/**
	 * 支付支付渠道
	 */
	private static final String CHANNEL_ALIPAY = "alipay";
	private RelativeLayout wechat_pay;
	private RelativeLayout alipay;
	private RelativeLayout upmp;
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
	private double cashCardPay;
	private double serviceCardPay;
	private DecimalFormat df;
	private Button payBtn;
	private LinearLayout pay_layout;
	private String cusamountpaid;
	private String shopgetpoints;
	private String ordercode;
//	private String shopamountpaid;
	private String shopamountpay;
	private String cuspointsded;
	private String ordermoney;
	private String shoppointsded;
	private String id;
	private String userid;

	private Handler mainHandler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case MessageContants.MSG_GET_ORDER_DETAIL: {
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
		cusamountpaid = getIntent().getStringExtra("cusamountpaid");
		shopgetpoints = getIntent().getStringExtra("shopgetpoints");
		ordercode = getIntent().getStringExtra("ordercode");
//		shopamountpaid = getIntent().getStringExtra("shopamountpaid");
		shopamountpay = getIntent().getStringExtra("shopamountpay");
		cuspointsded = getIntent().getStringExtra("cuspointsded");
		ordermoney = getIntent().getStringExtra("ordermoney");
		shoppointsded = getIntent().getStringExtra("shoppointsded");
		id = getIntent().getStringExtra("id");
		userid = getIntent().getStringExtra("userid");
		setContentView(R.layout.pay_layout);
		initView();
	}

	private void initView() {
		df = new DecimalFormat("0.00");
//		Pingpp.DEBUG = true;
		input_price = (EditText) findViewById(R.id.input_price);
		order_amount = (TextView) findViewById(R.id.order_amount);
		point_price = (TextView) findViewById(R.id.point_price);
		shop_pay = (TextView) findViewById(R.id.shop_pay);
		shop_point_price = (TextView) findViewById(R.id.shop_point_price);
		shop_get_point = (TextView) findViewById(R.id.shop_get_point);
		pay_money = (TextView) findViewById(R.id.pay_money);
		order_amount.setText("￥" + df.format(Double.valueOf(ordermoney)));
		point_price.setText("￥" + df.format(Double.valueOf(cuspointsded)));
		shop_pay.setText("￥" + df.format(Double.valueOf(shopamountpay)));
		shop_point_price.setText("￥" + df.format(Double.valueOf(shoppointsded)));
		shop_get_point.setText(shopgetpoints);
		pay_money.setText("￥" + df.format(Double.valueOf(shopamountpay)));

		wechat_pay = (RelativeLayout) findViewById(R.id.wenxin_pay);
		wechat_pay.setOnClickListener(this);
		alipay = (RelativeLayout) findViewById(R.id.alipay);
		alipay.setOnClickListener(this);
		upmp = (RelativeLayout) findViewById(R.id.union_pay);
		upmp.setOnClickListener(this);
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
		// if (mWaitDialog == null) {
		// mWaitDialog = new WaitDialog(this, R.string.loading_data);
		// }
		// mWaitDialog.show();
		// mSharedPreferences = getSharedPreferences(Constant.SHARED_PREFERENCE,
		// MODE_PRIVATE);
		// String tokenid = mSharedPreferences.getString(Constant.TOKEN_ID, "");
		// NetProxyManager.getInstance().toConfirmPayOrder(mainHandler, tokenid,
		// orderId, df.format(cashCardPay * 100),
		// null);

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

	class PaymentTask extends AsyncTask<PaymentRequest, Void, String> {

		@Override
		protected void onPreExecute() {
			// 按键点击之后的禁用，防止重复点击
			wechat_pay.setOnClickListener(null);
			alipay.setOnClickListener(null);
			upmp.setOnClickListener(null);
		}

		@Override
		protected String doInBackground(PaymentRequest... pr) {

			PaymentRequest paymentRequest = pr[0];
			String data = null;
			try {
				JSONObject object = new JSONObject();
				mSharedPreferences = getSharedPreferences(Constant.SHARED_PREFERENCE, Activity.MODE_PRIVATE);
				String tokenid = mSharedPreferences.getString(Constant.TOKEN_ID, "");
				// object.put("token_id", tokenid);
//				 object.put("channel", paymentRequest.channel);
//				 object.put("amount", paymentRequest.amount);
//				 object.put("ordercode", paymentRequest.ordercode);
				// object.put("livemode", paymentRequest.livemode);
//				 String json = object.toString();
				// 向Your Ping++ Server SDK请求数据
				// data = postJson(CHARGE_URL, json);
//				 LogUtils.d("lyp", json);
//				 data = postJson(API.API_PING_PAY_ORDER, json);
				Map<String, String> params = new HashMap<String, String>();
				params.put("ordercode", paymentRequest.ordercode);
				params.put("channel", paymentRequest.channel);
				params.put("amount", paymentRequest.amount);
				data = HttpSession.getRequestResult(API.API_PING_PAY_ORDER, params);
			} catch (Exception e) {
				e.printStackTrace();
			}
			return data;
		}

		/**
		 * 获得服务端的charge，调用ping++ sdk。
		 */
		@Override
		protected void onPostExecute(String data) {
			if (null == data) {
				showMsg("请求出错", "请检查URL", "URL无法获取charge");
				return;
			}
			LogUtils.d("charge", data);

			// 除QQ钱包外，其他渠道调起支付方式：
			// 参数一：Activity 当前调起支付的Activity
			// 参数二：data 获取到的charge或order的JSON字符串
			// Pingpp.createPayment(PayActivity.this, data);
			String charge = data;
			try {
				JSONObject json = new JSONObject(data);
//				if (!json.getBoolean("success")) {
//					String message = json.getString("message");
//					showMsg("请求出错", "请检查服务器", message);
//					return;
//				}
//				if (json.has("obj")) {
					charge = json.getJSONObject("obj").toString();
//				}
			} catch (JSONException e) {
				charge = data;
				e.printStackTrace();
			}
			LogUtils.d("charge", charge);
//			Pingpp.createPayment(PayActivityOld.this, charge);

			// QQ钱包调用方式
			// 参数一：Activity 当前调起支付的Activity
			// 参数二：data 获取到的charge或order的JSON字符串
			// 参数三：“qwalletXXXXXXX”需与AndroidManifest.xml中的scheme值一致
			// Pingpp.createPayment(ClientSDKActivity.this, data,
			// "qwalletXXXXXXX");
		}

	}

	/**
	 * onActivityResult 获得支付结果，如果支付成功，服务器会收到ping++ 服务器发送的异步通知。 最终支付成功根据异步通知为准
	 */
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {

		wechat_pay.setOnClickListener(PayActivityOld.this);
		alipay.setOnClickListener(PayActivityOld.this);
		upmp.setOnClickListener(PayActivityOld.this);

		// 支付页面返回处理
//		if (requestCode == Pingpp.REQUEST_CODE_PAYMENT) {
//			if (resultCode == Activity.RESULT_OK) {
//				String result = data.getExtras().getString("pay_result");
//				/*
//				 * 处理返回值 "success" - payment succeed "fail" - payment failed
//				 * "cancel" - user canceld "invalid" - payment plugin not
//				 * installed
//				 */
//				if ("success".equals(result)) {
//					ToastUtil.showShort(this, "支付成功！");
//					setResult(RESULT_OK);
//					finish();
//				} else {
//					String errorMsg = data.getExtras().getString("error_msg"); // 错误信息
//					String extraMsg = data.getExtras().getString("extra_msg"); // 错误信息
//					showMsg(result, errorMsg, extraMsg);
//				}
//			}
//		}
	}

	public void showMsg(String title, String msg1, String msg2) {
		String str = title;
		if (null != msg1 && msg1.length() != 0) {
			str += "\n" + msg1;
		}
		if (null != msg2 && msg2.length() != 0) {
			str += "\n" + msg2;
		}
		AlertDialog.Builder builder = new Builder(PayActivityOld.this);
		builder.setMessage(str);
		builder.setTitle("提示");
		builder.setPositiveButton("OK", null);
		builder.create().show();
	}

	/**
	 * 获取charge
	 * 
	 * @param urlStr
	 *            charge_url
	 * @param json
	 *            获取charge的传参
	 * @return charge
	 * @throws IOException
	 */
	private static String postJson(String urlStr, String json) throws IOException {
		URL url = new URL(urlStr);
		HttpURLConnection conn = (HttpURLConnection) url.openConnection();
		conn.setConnectTimeout(8000);
		conn.setReadTimeout(8000);
		conn.setRequestMethod("POST");
		conn.setRequestProperty("connection", "keep-alive");
		conn.setRequestProperty("Charset", "UTF-8");
		conn.setRequestProperty("Content-Type", "application/json");
		conn.setDoOutput(true);
		conn.setDoInput(true);
		conn.getOutputStream().write(json.getBytes());
		LogUtils.d("lyp", "urlStr: " + urlStr);
		LogUtils.d("lyp", "json: " + json);
		LogUtils.d("lyp", "responseCode: " + conn.getResponseCode());
		if (conn.getResponseCode() == 200) {
			BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream(), "utf-8"));
			StringBuilder response = new StringBuilder();
			String line;
			while ((line = reader.readLine()) != null) {
				response.append(line);
			}
			return response.toString();
		}
		return null;
	}

	class PaymentRequest {
		String ordercode;
		String channel;
		String amount;
		boolean livemode;

		public PaymentRequest(String channel, String ordercode, String amount) {
			this.ordercode = ordercode;
			this.channel = channel;
			this.amount = amount;
			this.livemode = LIVEMODE;
		}
	}

	@Override
	public void onClick(View v) {
		// mSharedPreferences = getSharedPreferences(Constant.SHARED_PREFERENCE,
		// MODE_PRIVATE);
		// String tokenid = mSharedPreferences.getString(Constant.TOKEN_ID, "");
		// String saleId = mSharedPreferences.getString(Constant.ID, "");
		String payAmount = df.format(Double.valueOf(shopamountpay) * 100);
		switch (v.getId()) {
		case R.id.wenxin_pay:
			ToastUtil.showShort(this, "支付成功！");
			setResult(RESULT_OK);
			finish();
//			new PaymentTask().execute(new PaymentRequest(CHANNEL_WECHAT, ordercode, payAmount));
			// NetProxyManager.getInstance().toPayOrder(mainHandler, tokenid,
			// saleId, orderId);
			break;
		case R.id.alipay:
			ToastUtil.showShort(this, "支付成功！");
			setResult(RESULT_OK);
			finish();
//			new PaymentTask().execute(new PaymentRequest(CHANNEL_ALIPAY, ordercode, payAmount));
			// NetProxyManager.getInstance().toPayOrder(mainHandler, tokenid,
			// saleId, orderId);
			break;
		case R.id.union_pay:
			new PaymentTask().execute(new PaymentRequest(CHANNEL_UPACP, ordercode, payAmount));
			// NetProxyManager.getInstance().toPayOrder(mainHandler, tokenid,
			// saleId, orderId);
			break;
		}
	}

}
