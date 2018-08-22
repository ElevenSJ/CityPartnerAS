package com.lyp.citypartner;

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
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.view.WindowManager;
import android.widget.CheckBox;
import android.widget.EditText;

public class AddOrderActivity extends BaseActivity {
	private WaitDialog mWaitDialog;
	private SharedPreferences mSharedPreferences;
	private String cId;
	private EditText input_price;
	private String shopid;
	private CheckBox isded;

	private Handler mainHandler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case MessageContants.MSG_ADD_ORDER: {
				parseAddOrder((String) msg.obj);
				break;
			}
			case MessageContants.MSG_GET_CUSTOMER_NEED_PAY: {
				parseCustomerNeedPay((String) msg.obj);
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
		setContentView(R.layout.add_order_layout);
		initView();
	}

	private void initView() {
		input_price = (EditText) findViewById(R.id.input_price);
		setPricePoint(input_price);
		isded = (CheckBox) findViewById(R.id.isded);
	}

    private void setPricePoint(final EditText editText) {  
        editText.addTextChangedListener(new TextWatcher() {  
  
            @Override  
            public void onTextChanged(CharSequence s, int start, int before,  
                    int count) {  
                if (s.toString().contains(".")) {  
                    if (s.length() - 1 - s.toString().indexOf(".") > 2) {  
                        s = s.toString().subSequence(0,  
                                s.toString().indexOf(".") + 3);  
                        editText.setText(s);  
                        editText.setSelection(s.length());  
                    }  
                }  
                if (s.toString().trim().substring(0).equals(".")) {  
                    s = "0" + s;  
                    editText.setText(s);  
                    editText.setSelection(2);  
                }  
  
                if (s.toString().startsWith("0")  
                        && s.toString().trim().length() > 1) {  
                    if (!s.toString().substring(1, 2).equals(".")) {  
                        editText.setText(s.subSequence(0, 1));  
                        editText.setSelection(1);  
                        return;  
                    }  
                }  
            }  
  
            @Override  
            public void beforeTextChanged(CharSequence s, int start, int count,  
                    int after) {  
  
            }  
  
            @Override  
            public void afterTextChanged(Editable s) {  
                // TODO Auto-generated method stub  
                  
            }  
  
        });  
  
    }  



	public void payBtn(View view) {
		Editable text = input_price.getText();
		if (TextUtils.isEmpty(text)) {
			ToastUtil.showMessage("请输入金额！");
			return;
		}
		String inputmoney = text.toString();
		if (TextUtils.isEmpty(inputmoney)) {
			ToastUtil.showMessage("请输入金额！");
			return;
		}
		if (Double.valueOf(inputmoney) < 1) {
			ToastUtil.showMessage("输入金额必须大于1才有效！");
			return;
		}
		if (mWaitDialog == null) {
			mWaitDialog = new WaitDialog(this, R.string.loading_data);
		}
		mWaitDialog.show();
		mSharedPreferences = getSharedPreferences(Constant.SHARED_PREFERENCE, MODE_PRIVATE);
		String tokenid = mSharedPreferences.getString(Constant.TOKEN_ID, "");
		String saleId = mSharedPreferences.getString(Constant.ID, "");
		shopid = saleId;
//			NetProxyManager.getInstance().toAddOrder(mainHandler, saleId, cId, 
//					text.toString());
		NetProxyManager.getInstance().toGetCustomerNeedPay(mainHandler,tokenid, saleId, cId, 
				inputmoney, isded.isChecked() == true ? "1" : "0");
		
	}

	/**
	 * to parse login infomations from network
	 */
	private void parseAddOrder(String result) {
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
			String cusamountpaid = job.getString("cusamountpay");
			String shopgetpoints = job.getString("shopgetpoints");
			String ordercode = job.getString("ordercode");
			String shopamountpaid = job.getString("shopamountpaid");
			String shopamountpay = job.getString("shopamountpay");
			String cuspointsded = job.getString("cuspointsded");
			String ordermoney = job.getString("ordermoney");
			String shoppointsded = job.getString("shoppointsded");
			String id = job.getString("id");
			String userid = job.getString("userid");
			Intent i = new Intent();
			i.setClass(this, PayActivity.class);
			i.putExtra("ordermoney", ordermoney);
			i.putExtra("cuspointsded", cuspointsded);
			i.putExtra("shopamountpay", shopamountpay);
			i.putExtra("shoppointsded", shoppointsded);
			i.putExtra("shopgetpoints", shopgetpoints);
			i.putExtra("shopamountpaid", shopamountpaid);
			i.putExtra("ordercode", ordercode);
			startActivity(i);
			finish();
		} catch (Exception ex) {
			ToastUtil.showLongMessage("请检查客户二维码是否正确！");
			LogUtils.e(ex.getMessage());
			return;
		} finally {
			if (mWaitDialog != null && mWaitDialog.isShowing()) {
				mWaitDialog.dismiss();
			}
		}
	}
	
	private void parseCustomerNeedPay(String result) {
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
			String cusname = job.getString("cusname");//客户名称
			String cusicon = job.getString("cusicon");//客户头像
			String cusamountpay = job.getString("cusamountpay");//客户应付金额
			String shopgetpoints = null;
			if (job.has("shopgetpoints")) {
			     shopgetpoints = job.getString("shopgetpoints");
			}
			String ordercode = job.getString("ordercode");//订单编号
			String cusamountpaid = job.getString("cusamountpaid");//客户实付金额
			String shopamountpaid = job.getString("shopamountpaid");//商户实付金额
			String shopamountpay = job.getString("shopamountpay");//商户应付金额
			String cuspointsded = job.getString("cuspointsded");//客户积分抵扣
			String ordermoney = job.getString("ordermoney");//订单金额
			String shoppointsded = job.getString("shoppointsded");//商户积分抵扣
			if (job.has("shopid")) {
				shopid = job.getString("shopid");
			}
			String userid = job.getString("userid");
			Intent i = new Intent();
//			i.setClass(this, ConfirmCustomerPayActivity.class);
			i.setClass(this, PayActivity.class);
			i.putExtra("ordermoney", ordermoney);
			i.putExtra("ordercode", ordercode);
			i.putExtra("cusamountpay", cusamountpay);
			i.putExtra("cuspointsded", cuspointsded);
			i.putExtra("shopamountpay", shopamountpay);
			i.putExtra("shoppointsded", shoppointsded);
			i.putExtra("shopgetpoints", shopgetpoints);
			i.putExtra("shopid", shopid);
			i.putExtra("cusname", cusname);
			i.putExtra("cusicon", cusicon);
			i.putExtra("shopamountpaid", shopamountpaid);
			i.putExtra("cusamountpaid", cusamountpaid);
			startActivity(i);
			finish();
		} catch (Exception ex) {
			ToastUtil.showLongMessage("请检查客户二维码是否正确！");
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
