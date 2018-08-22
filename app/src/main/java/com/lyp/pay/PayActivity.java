package com.lyp.pay;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.DecimalFormat;
import java.util.Map;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.json.JSONObject;

import com.alipay.sdk.app.PayTask;
import com.lyp.base.BaseActivity;
import com.lyp.bean.ShopPicBean;
import com.lyp.citypartner.R;
import com.lyp.manager.ImageManager;
import com.lyp.net.Errors;
import com.lyp.net.MessageContants;
import com.lyp.net.NetProxyManager;
import com.lyp.utils.Constant;
import com.lyp.utils.LogUtils;
import com.lyp.utils.SystemStatusManager;
import com.lyp.utils.ToastUtil;
import com.lyp.view.CircleImageView;
import com.lyp.view.dialog.CustomerAlertDialog;
import com.lyp.view.dialog.WaitDialog;
import com.tencent.mm.opensdk.modelbase.BaseResp;
import com.tencent.mm.opensdk.modelpay.PayReq;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;

import android.app.AlertDialog;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class PayActivity extends BaseActivity implements OnClickListener {
	/**
	 * 微信支付渠道
	 */
	private static final String CHANNEL_WECHAT = "wx";
	private static final int WECHAT = 0x02;

	/**
	 * 支付支付渠道
	 */
	private static final String CHANNEL_ALIPAY = "alipay";

	private static final int ALIPAY = 0x01;
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
	private TextView customer_need_price;
	private TextView customer_price;
	private CircleImageView civ;
	private TextView cname;
	private double cashCardPay;
	private double serviceCardPay;
	private DecimalFormat df;
	private Button payBtn;
	private LinearLayout pay_layout;
	private String cusamountpay;
	private String cusamountpaid;
	private String shopgetpoints;
	private String ordercode;
	private String shopamountpaid;
	private String shopamountpay;
	private String cuspointsded;
	private String ordermoney;
	private String shoppointsded;
	private String id;
	private String userid;
	private String cusicon;
	private String cusname;
	private String flag = null;
	private View pay_btn;
	private IWXAPI api;

	private Handler mainHandler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case MessageContants.MSG_GET_ORDER_DETAIL: {
				parsePayOrder((String) msg.obj);
				break;
			}
			case MessageContants.MSG_PAY_SIGN: {
				parseGetPaySign((String) msg.obj);
				wechat_pay.setOnClickListener(PayActivity.this);
				alipay.setOnClickListener(PayActivity.this);
				upmp.setOnClickListener(PayActivity.this);
				break;
			}
			case ALIPAY:
				PayResult payResult = new PayResult((Map<String, String>) msg.obj);
				/**
				 * 对于支付结果，请商户依赖服务端的异步通知结果。同步通知结果，仅作为支付结束的通知。
				 */
				String resultInfo = payResult.getResult();// 同步返回需要验证的信息
				String resultStatus = payResult.getResultStatus();
				// 判断resultStatus 为9000则代表支付成功
				if (TextUtils.equals(resultStatus, "9000")) {
					// 该笔订单是否真实支付成功，需要依赖服务端的异步通知。
					ToastUtil.showShort(PayActivity.this, "支付成功！");
					setResult(RESULT_OK);
					finish();
				} else {
					// 该笔订单真实的支付结果，需要依赖服务端的异步通知。
					// Toast.makeText(PayActivity.this, "支付失败",
					// Toast.LENGTH_SHORT).show();
					ToastUtil.showShort(PayActivity.this, "支付失败！");
					setResult(RESULT_OK);
					finish();
				}
				break;
			case MessageContants.MSG_WX_PAY_SIGN: {
				parseGetWXPaySign((String) msg.obj);
				wechat_pay.setOnClickListener(PayActivity.this);
				alipay.setOnClickListener(PayActivity.this);
				upmp.setOnClickListener(PayActivity.this);
				break;
			}
			case MessageContants.MSG_QUERY_WX_PAY_RESULT: {
				parseGetPayResult((String) msg.obj);
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
		api = WXAPIFactory.createWXAPI(this, Constant.APP_ID);
		EventBus.getDefault().register(this);
		cId = getIntent().getStringExtra("cId");
		cusamountpaid = getIntent().getStringExtra("cusamountpaid");
		cusamountpay = getIntent().getStringExtra("cusamountpay");
		shopgetpoints = getIntent().getStringExtra("shopgetpoints");
		ordercode = getIntent().getStringExtra("ordercode");
		shopamountpaid = getIntent().getStringExtra("shopamountpaid");
		shopamountpay = getIntent().getStringExtra("shopamountpay");
		cuspointsded = getIntent().getStringExtra("cuspointsded");
		ordermoney = getIntent().getStringExtra("ordermoney");
		shoppointsded = getIntent().getStringExtra("shoppointsded");
		id = getIntent().getStringExtra("id");
		userid = getIntent().getStringExtra("userid");
		cusname = getIntent().getStringExtra("cusname");
		cusicon = getIntent().getStringExtra("cusicon");
		setContentView(R.layout.pay_layout);
		initView();
	}

	private void initView() {
		df = new DecimalFormat("0.00");
		civ = (CircleImageView) findViewById(R.id.civ);
		ImageManager.loadDefaultImage(cusicon, civ);
		cname = (TextView) findViewById(R.id.cname);
		cname.setText(cusname);
		input_price = (EditText) findViewById(R.id.input_price);
		order_amount = (TextView) findViewById(R.id.order_amount);
		point_price = (TextView) findViewById(R.id.point_price);
		customer_need_price = (TextView) findViewById(R.id.customer_need_price);
		customer_need_price.setText("￥" + df.format(Double.valueOf(cusamountpay)));
		customer_price = (TextView) findViewById(R.id.customer_price);
		customer_price.setText("￥" + df.format(Double.valueOf(cusamountpaid)));
		shop_pay = (TextView) findViewById(R.id.shop_pay);
		shop_point_price = (TextView) findViewById(R.id.shop_point_price);
		shop_get_point = (TextView) findViewById(R.id.shop_get_point);
		pay_money = (TextView) findViewById(R.id.pay_money);
		order_amount.setText("￥" + df.format(Double.valueOf(ordermoney)));
		point_price.setText("￥" + df.format(Double.valueOf(cuspointsded)));
		shop_pay.setText("￥" + df.format(Double.valueOf(shopamountpay)));
		shop_point_price.setText("￥" + df.format(Double.valueOf(shoppointsded)));
		// shop_get_point.setText(shopgetpoints);
		Double payMoney = Double.valueOf(shopamountpaid);
		pay_money.setText("￥" + df.format(payMoney));

		pay_layout = (LinearLayout) findViewById(R.id.pay_layout);
		pay_btn = findViewById(R.id.pay_btn);
		if (payMoney <= 0) {
			pay_layout.setVisibility(View.GONE);
			pay_btn.setVisibility(View.VISIBLE);
		} else {
			pay_layout.setVisibility(View.VISIBLE);
			pay_btn.setVisibility(View.GONE);
		}
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
		ToastUtil.showShort(this, "支付成功！");
		setResult(RESULT_OK);
		finish();
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
	private void parseGetPaySign(String result) {
		if (Errors.ERROR_NET.equals(result) || Errors.ERROR_SERVER.equals(result)) {
			ToastUtil.showLong(this, R.string.network_error);
			if (mWaitDialog != null && mWaitDialog.isShowing()) {
				mWaitDialog.dismiss();
			}
			return;
		}
		// to parser json data
		try {
			LogUtils.d("lyp", result);
			JSONObject json = new JSONObject(result);
			boolean success = json.getBoolean("success");

			if (!success) {
				String message = json.getString("message");
				ToastUtil.showShort(this, message);
				return;
			}
			JSONObject job = json.getJSONObject("data");
			String sign = job.getString("sign");
			if (CHANNEL_ALIPAY.equals(flag)) {
				alipay(sign);
			} else if (CHANNEL_WECHAT.equals(flag)) {

			}

		} catch (Exception ex) {
			LogUtils.e(ex.getMessage());
		} finally {
			if (mWaitDialog != null && mWaitDialog.isShowing()) {
				mWaitDialog.dismiss();
			}
		}
	}

	/**
	 * to parse login infomations from network
	 */
	private void parseGetWXPaySign(String result) {
		if (Errors.ERROR_NET.equals(result) || Errors.ERROR_SERVER.equals(result)) {
			ToastUtil.showLong(this, R.string.network_error);
			if (mWaitDialog != null && mWaitDialog.isShowing()) {
				mWaitDialog.dismiss();
			}
			return;
		}
		// to parser json data
		try {
			LogUtils.d("lyp", result);
			JSONObject json = new JSONObject(result);
			boolean success = json.getBoolean("success");

			if (!success) {
				String message = json.getString("message");
				ToastUtil.showShort(this, message);
				return;
			}
			JSONObject job = json.getJSONObject("data");
			String partnerid = job.getString("partnerid");
			String prepayid = job.getString("prepayid");
			String packageStr = job.getString("package");
			String noncestr = job.getString("noncestr");
			String timestamp = job.getString("timestamp");
			String sign = job.getString("sign");
			// if (CHANNEL_ALIPAY.equals(flag)) {
			//
			// } else if (CHANNEL_WECHAT.equals(flag)) {
			//
			// }
			wxpay(partnerid, prepayid, packageStr, noncestr, timestamp, sign);
		} catch (Exception ex) {
			LogUtils.e(ex.getMessage());
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
		}
	}

	@Override
	public void onClick(View v) {
		// mSharedPreferences = getSharedPreferences(Constant.SHARED_PREFERENCE,
		// MODE_PRIVATE);
		// String tokenid = mSharedPreferences.getString(Constant.TOKEN_ID, "");
		// String saleId = mSharedPreferences.getString(Constant.ID, "");
		// 设置为ｎｕｌｌ，防止误操作
		wechat_pay.setOnClickListener(null);
		alipay.setOnClickListener(null);
		upmp.setOnClickListener(null);
		DecimalFormat paydf = new DecimalFormat("0");
		String payAmount = paydf.format(Double.valueOf(shopamountpaid) * 100);
		switch (v.getId()) {
		case R.id.wenxin_pay:
			// flag = CHANNEL_WECHAT;

			NetProxyManager.getInstance().toGetWXPaySign(mainHandler, ordercode, payAmount, "微信支付", "APP");

			// new PaymentTask().execute(new PaymentRequest(CHANNEL_WECHAT,
			// ordercode, payAmount));
			// NetProxyManager.getInstance().toPayOrder(mainHandler, tokenid,
			// saleId, orderId);
			break;
		case R.id.alipay:
			// ToastUtil.showShort(this, "支付成功！");
			// setResult(RESULT_OK);
			// finish();
			flag = CHANNEL_ALIPAY;
			NetProxyManager.getInstance().toGetPaySign(mainHandler, ordercode, payAmount, flag);
			break;
		case R.id.union_pay:
			// new PaymentTask().execute(new PaymentRequest(CHANNEL_UPACP,
			// ordercode, payAmount));
			// NetProxyManager.getInstance().toPayOrder(mainHandler, tokenid,
			// saleId, orderId);
			break;
		}
	}

	private void alipay(String sign) {
		final String orderInfo = sign;
		Runnable payRunnable = new Runnable() {

			@Override
			public void run() {
				PayTask alipay = new PayTask(PayActivity.this);
				Map<String, String> result = alipay.payV2(orderInfo, true);
				Log.i("msp", result.toString());

				Message msg = new Message();
				msg.what = ALIPAY;
				msg.obj = result;
				mainHandler.sendMessage(msg);
			}
		};

		Thread payThread = new Thread(payRunnable);
		payThread.start();
	}

	private void wxpay(final String partnerid, final String prepayid, final String packageStr, final String noncestr,
			final String timestamp, final String sign) {
		// Runnable payRunnable = new Runnable() {
		//
		// @Override
		// public void run() {
		PayReq req = new PayReq();
		req.appId = Constant.APP_ID;
		req.partnerId = partnerid;
		req.prepayId = prepayid;
		req.nonceStr = noncestr;
		req.timeStamp = timestamp;
		req.packageValue = packageStr;
		req.sign = sign;
		req.extData = ordercode; // optional
		// 在支付之前，如果应用没有注册到微信，应该先调用IWXMsg.registerApp将应用注册到微信
		api.registerApp(Constant.APP_ID);
		api.sendReq(req);
		// }
		// };
		//
		// Thread payThread = new Thread(payRunnable);
		// payThread.start();
	}

	/**
	 * 这里用到的了EventBus框架
	 * 
	 * @param weiXin
	 */
	@Subscribe
	public void onEventMainThread(BaseResp baseResp) {
		NetProxyManager.getInstance().toQueryWXorder(mainHandler, ordercode, "APP");
		switch (baseResp.errCode) {
		case BaseResp.ErrCode.ERR_OK:
			// 0支付成功：支付成功则去后台查询支付结果再展示用户实际支付结果
//			NetProxyManager.getInstance().toQueryWXorder(mainHandler, ordercode, "APP");
//			new CustomerAlertDialog(this).builder().setTitle("提示").setMsg("支付成功！")
//			.setPositiveButton("确定", new OnClickListener() {
//				@Override
//				public void onClick(View v) {
//
//				}
//			}).show();
			break;
		case BaseResp.ErrCode.ERR_COMM:
			// -1错误：
			// 可能的原因：签名错误、未注册APPID、项目设置APPID不正确、注册的APPID与设置的不匹配、其他异常等。
//			new CustomerAlertDialog(this).builder().setTitle("提示").setMsg("支付失败：" + baseResp.errCode)
//			.setPositiveButton("确定", new OnClickListener() {
//				@Override
//				public void onClick(View v) {
//
//				}
//			}).show();
			break;
		case BaseResp.ErrCode.ERR_USER_CANCEL:
			// -2用户取消支付
//			new CustomerAlertDialog(this).builder().setTitle("提示").setMsg("取消支付！")
//					.setPositiveButton("确定", new OnClickListener() {
//						@Override
//						public void onClick(View v) {
//                           finish();
//						}
//					}).show();
//			break;
		default:
			break;
		}
	}

	/**
	 * to parse login infomations from network
	 */
	private void parseGetPayResult(String result) {
		if (Errors.ERROR_NET.equals(result) || Errors.ERROR_SERVER.equals(result)) {
			ToastUtil.showLong(this, R.string.network_error);
			if (mWaitDialog != null && mWaitDialog.isShowing()) {
				mWaitDialog.dismiss();
			}
			return;
		}
		// to parser json data
		try {
			LogUtils.d("lyp", result);
			JSONObject json = new JSONObject(result);
			boolean success = json.getBoolean("success");

			if (!success) {
				String message = json.getString("message");
				ToastUtil.showShort(this, message);
				return;
			}
			
			String pay_result = json.getString("data");
			/*
			SUCCESS—支付成功 
			REFUND—转入退款 
			NOTPAY—未支付 
			CLOSED—已关闭 
			REVOKED—已撤销（刷卡支付） 
			USERPAYING--用户支付中 
			PAYERROR--支付失败(其他原因，如银行返回失败)
			*/
			String showString = "支付失败";
			switch (pay_result) 
			{
			case "SUCCESS":
				showString = "支付成功！";
				break;
			case "REFUND":
				showString = "转入退款！";
				break;
			case "NOTPAY":
				showString = "未支付！";
				break;
			case "CLOSED":
				showString = "已关闭！";
				break;
			case "REVOKED":
				showString = "已撤销！";
				break;
			case "USERPAYING":
				showString = "用户支付中 ...";
				break;
			case "PAYERROR":
				showString = "支付失败！";
				break;
			default:
				break;
			}
			ToastUtil.showShort(PayActivity.this, showString);
			setResult(RESULT_OK);
			finish();
		} catch (Exception ex) {
			LogUtils.e(ex.getMessage());
		} finally {
			if (mWaitDialog != null && mWaitDialog.isShowing()) {
				mWaitDialog.dismiss();
			}
		}
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		EventBus.getDefault().unregister(this);// 取消注册
	}
}
