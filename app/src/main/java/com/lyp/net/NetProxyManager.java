package com.lyp.net;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;

public class NetProxyManager {

	private ExecutorService executorService;
	private static NetProxyManager netProxyManager;
	private static final String TAG = NetProxyManager.class.getSimpleName();

	private NetProxyManager() {
		executorService = Executors.newSingleThreadExecutor();
	};

	public static NetProxyManager getInstance() {
		if (netProxyManager == null) {
			netProxyManager = new NetProxyManager();
		}
		return netProxyManager;
	}

	/**
	 * 获取验证码
	 * 
	 * @param handler
	 * @param phone
	 * @param what
	 */
	public void toGetSMSAuthCode(final Handler handler, final String telephone) {
		new Thread(new Runnable() {
			@Override
			public void run() {
				StringBuffer sb = new StringBuffer();
				sb.append(API.API_GET_SMS_AUTH_CODE);
				sb.append("?");
				sb.append("telephone=");
				sb.append(telephone);
				String result = HttpManager.httpGet(sb.toString());
				Message msg = new Message();
				msg.what = MessageContants.MSG_GET_SMS_AUTH_CODE;
				msg.obj = result;
				handler.sendMessage(msg);
			}
		}).start();
	}
	
	/**
	 * 验证码登录接口
	 * 
	 * @param handler
	 * @param phone
	 * @param authcode
	 * @param what
	 */
	public void toRegist(final Handler handler, final String telephone, final String msmcode, 
			final String shopname) {
		new Thread(new Runnable() {
			@Override
			public void run() {
				Map<String, String> params = new HashMap<String, String>();
				params.put("telephone", telephone);
				params.put("msmcode", msmcode);
				params.put("shopname", shopname);
				String result = "error";
				try {
					result = HttpSession.getRequestResult(API.API_REGIST, params);
				} catch (Exception ex) {
					result = "error";
					Log.e(TAG, ex.getMessage());
				} finally {
					Message msg = new Message();
					msg.what = MessageContants.MSG_REGIST;
					msg.obj = result;
					handler.sendMessage(msg);
				}
			}
		}).start();
	}

	/**
	 * 验证码登录接口
	 * 
	 * @param handler
	 * @param phone
	 * @param authcode
	 * @param what
	 */
	public void toLogin(final Handler handler, final String telephone, 
			final String checkcode, final String deviceId) {
		new Thread(new Runnable() {
			@Override
			public void run() {
				StringBuffer sb = new StringBuffer();
				sb.append(API.API_LOGIN);
				sb.append("?");
				sb.append("telephone=");
				sb.append(telephone);
				sb.append("&checkcode=");
				sb.append(checkcode);
				
				sb.append("&deviceId=");
				if (deviceId == null) {
					sb.append("0123456789ABCDEF");
				} else {
				    sb.append(deviceId);
				}
				String result = HttpManager.httpGet(sb.toString());
				Message msg = new Message();
				msg.what = MessageContants.MSG_LOGIN;
				msg.obj = result;
				handler.sendMessage(msg);
			}
		}).start();
	}
	
	/**
	 * 密码登录接口
	 * 
	 * @param handler
	 * @param phone
	 * @param authcode
	 * @param what
	 */
	public void toLoginByPWD(final Handler handler, final String telephone, final String password) {
		new Thread(new Runnable() {
			@Override
			public void run() {
				StringBuffer sb = new StringBuffer();
				sb.append(API.API_LOGIN_BY_PWD);
				sb.append("?");
				sb.append("telephone=");
				sb.append(telephone);
				sb.append("&password=");
				sb.append(password);
				String result = HttpManager.httpGet(sb.toString());
				Message msg = new Message();
				msg.what = MessageContants.MSG_LOGIN;
				msg.obj = result;
				handler.sendMessage(msg);
			}
		}).start();
	}

	/**
	 * 获取客户列表
	 * 
	 * @param handler
	 * @param tokenid
	 * @param saleId
	 * @param cname
	 * @param cphone
	 * @param pageNum
	 */
	public void toGetCustomerList(final Handler handler, final String token,
			final String shopid) {
		new Thread(new Runnable() {

			@Override
			public void run() {
				StringBuffer sb = new StringBuffer();
				sb.append(API.API_GET_CUSTOMER_LIST);
				sb.append("?");
				sb.append("token=");
				sb.append(token);
				sb.append("&shopid=");
				sb.append(shopid);

				String result = HttpManager.httpGet(sb.toString());
				Message msg = new Message();
				msg.what = MessageContants.MSG_GET_CUSTOMER_LIST;
				msg.obj = result;
				handler.sendMessage(msg);
			}
		}).start();
	}
	
	/**
	 * 获取客户详情
	 * 
	 * @param handler
	 * @param tokenid
	 * @param saleId
	 * @param cname
	 * @param cphone
	 * @param pageNum
	 */
	public void toGetCustomerDetail(final Handler handler, final String token,
			final String id) {
		new Thread(new Runnable() {

			@Override
			public void run() {
				StringBuffer sb = new StringBuffer();
				sb.append(API.API_GET_CUSTOMER_DETAIL);
				sb.append("?");
				sb.append("token=");
				sb.append(token);
				sb.append("&id=");
				sb.append(id);

				String result = HttpManager.httpGet(sb.toString());
				Message msg = new Message();
				msg.what = MessageContants.MSG_GET_CUSTOMER_DETAIL;
				msg.obj = result;
				handler.sendMessage(msg);
			}
		}).start();
	}


	public void toGetMyQRCode(final Handler handler, final String token,
			final String id) {
		new Thread(new Runnable() {
			@Override
			public void run() {
				StringBuffer sb = new StringBuffer();
				sb.append(API.API_MY_QRCODE);
				sb.append("?");
				sb.append("token=");
				sb.append(token);
				sb.append("&id=");
				sb.append(id);
				String result = HttpManager.httpGet(sb.toString());
				Message msg = new Message();
				msg.what = MessageContants.MSG_MY_QRCODE;
				msg.obj = result;
				handler.sendMessage(msg);
			}
		}).start();
	}
	

	public void toGetPersonInfo(final Handler handler, final String token,
			final String id) {
		new Thread(new Runnable() {

			@Override
			public void run() {
				StringBuffer sb = new StringBuffer();
				sb.append(API.API_GET_PERSON_INFO);
				sb.append("?");
				sb.append("token=");
				sb.append(token);
				sb.append("&id=");
				sb.append(id);

				String result = HttpManager.httpGet(sb.toString());
				Message msg = new Message();
				msg.what = MessageContants.MSG_GET_PERSON_INFO;
				msg.obj = result;
				handler.sendMessage(msg);
			}
		}).start();
	}
	
	public void toGetShopInfo(final Handler handler, final String token,
			final String id) {
		new Thread(new Runnable() {

			@Override
			public void run() {
				StringBuffer sb = new StringBuffer();
				sb.append(API.API_GET_SHOP_INFO);
				sb.append("?");
				sb.append("token=");
				sb.append(token);
				sb.append("&id=");
				sb.append(id);

				String result = HttpManager.httpGet(sb.toString());
				Message msg = new Message();
				msg.what = MessageContants.MSG_GET_SHOP_INFO;
				msg.obj = result;
				handler.sendMessage(msg);
			}
		}).start();
	}
	
	public void toGetOrderList(final Handler handler, final String token,
			final String shopid, 
			final String firstIndex, final String pageNum) {
		new Thread(new Runnable() {

			@Override
			public void run() {
				StringBuffer sb = new StringBuffer();
				sb.append(API.API_GET_ORDER_LIST);
				sb.append("?");
				sb.append("token=");
				sb.append(token);
				sb.append("&shopid=");
				sb.append(shopid);
				sb.append("&firstIndex=");
				sb.append(firstIndex);
				sb.append("&pageNum=");
				sb.append(pageNum);

				String result = HttpManager.httpGet(sb.toString());
				Message msg = new Message();
				msg.what = MessageContants.MSG_GET_ORDER_LIST;
				msg.obj = result;
				handler.sendMessage(msg);
			}
		}).start();
	}
	
	public void toGetOrderListByDate(final Handler handler, final String token,
			final String shopid,
			final String begintime, final String endtime,
			final String firstIndex, final String pageNum) {
		new Thread(new Runnable() {

			@Override
			public void run() {
				StringBuffer sb = new StringBuffer();
				sb.append(API.API_GET_ORDER_LIST_BY_DATE);
				sb.append("?");
				sb.append("token=");
				sb.append(token);
				sb.append("&shopid=");
				sb.append(shopid);
				sb.append("&begintime=");
				sb.append(begintime);
				sb.append("&endtime=");
				sb.append(endtime);
				sb.append("&firstIndex=");
				sb.append(firstIndex);
				sb.append("&pageNum=");
				sb.append(pageNum);

				String result = HttpManager.httpGet(sb.toString());
				Message msg = new Message();
				msg.what = MessageContants.MSG_GET_ORDER_LIST_BY_DATE;
				msg.obj = result;
				handler.sendMessage(msg);
			}
		}).start();
	}
	
	public void toGetOrderListById(final Handler handler, final String token,
			final String shopid, final String userid,
			final String firstIndex, final String pageNum) {
		new Thread(new Runnable() {

			@Override
			public void run() {
				StringBuffer sb = new StringBuffer();
				sb.append(API.API_GET_ORDER_LIST_BY_ID);
				sb.append("?");
				sb.append("token=");
				sb.append(token);
				sb.append("&userid=");
				sb.append(userid);
				sb.append("&shopid=");
				sb.append(shopid);
				sb.append("&firstIndex=");
				sb.append(firstIndex);
				sb.append("&pageNum=");
				sb.append(pageNum);

				String result = HttpManager.httpGet(sb.toString());
				Message msg = new Message();
				msg.what = MessageContants.MSG_GET_ORDER_LIST_BY_ID;
				msg.obj = result;
				handler.sendMessage(msg);
			}
		}).start();
	}
	
	public void toGetOrderDetail(final Handler handler, final String token,
			final String id) {
		new Thread(new Runnable() {

			@Override
			public void run() {
				StringBuffer sb = new StringBuffer();
				sb.append(API.API_GET_ORDER_DETAIL);
				sb.append("?");
				sb.append("token=");
				sb.append(token);
				sb.append("&id=");
				sb.append(id);

				String result = HttpManager.httpGet(sb.toString());
				Message msg = new Message();
				msg.what = MessageContants.MSG_GET_ORDER_DETAIL;
				msg.obj = result;
				handler.sendMessage(msg);
			}
		}).start();
	}
	
	public void toModifyShopInfo(final Handler handler, final String token,
			final String id, final String shopname,
			final String fixedtelephone, final String telephone, final String address, 
			final String email, final String zipcode, final String describtion,
			final String prov, final String provCode, final String city, 
			final String cityCode, final String county, final String countyCode) {
		new Thread(new Runnable() {

			@Override
			public void run() {
				Map<String, String> params = new HashMap<String, String>();
				params.put("token", token);
				params.put("id", id);
				if (!TextUtils.isEmpty(shopname)) {
					params.put("shopname", shopname);
				}
				if (!TextUtils.isEmpty(fixedtelephone)) {
					params.put("fixedtelephone", fixedtelephone);
				}
				if (!TextUtils.isEmpty(telephone)) {
					params.put("telephone", telephone);
				}
				if (!TextUtils.isEmpty(address)) {
					params.put("address", address);
				}
				if (!TextUtils.isEmpty(email)) {
					params.put("email", email);
				}
				if (!TextUtils.isEmpty(zipcode)) {
					params.put("zipcode", zipcode);
				}
				if (!TextUtils.isEmpty(describtion)) {
					params.put("describtion", describtion);
				}
				if (!TextUtils.isEmpty(prov)) {
					params.put("prov", prov);
				}
				if (!TextUtils.isEmpty(provCode)) {
					params.put("provCode", provCode);
				}
				if (!TextUtils.isEmpty(city)) {
					params.put("city", city);
				}
				if (!TextUtils.isEmpty(cityCode)) {
					params.put("cityCode", cityCode);
				}
				if (!TextUtils.isEmpty(county)) {
					params.put("county", county);
				}
				if (!TextUtils.isEmpty(countyCode)) {
					params.put("countyCode", countyCode);
				}
				String result = "error";
				try {
					result = HttpSession.getRequestResult(API.API_MODIFY_SHOP_INFO, params);
				} catch (Exception ex) {
					result = "error";
					Log.e(TAG, ex.getMessage());
				} finally {
					Message msg = new Message();
					msg.what = MessageContants.MSG_MODIFY_SHOP_INFO;
					msg.obj = result;
					handler.sendMessage(msg);
				}
			}
		}).start();
	}
	
	public void toGetShopPic(final Handler handler, final String token,
			final String shopid) {
		new Thread(new Runnable() {

			@Override
			public void run() {
				StringBuffer sb = new StringBuffer();
				sb.append(API.API_GET_SHOP_PIC);
				sb.append("?");
				sb.append("token=");
				sb.append(token);
				sb.append("&shopid=");
				sb.append(shopid);

				String result = HttpManager.httpGet(sb.toString());
				Message msg = new Message();
				msg.what = MessageContants.MSG_GET_SHOP_PIC;
				msg.obj = result;
				handler.sendMessage(msg);
			}
		}).start();
	}
	
	public void toAddOrder(final Handler handler, final String shopid, 
			final String userid, final String ordermoney) {
		new Thread(new Runnable() {

			@Override
			public void run() {
				StringBuffer sb = new StringBuffer();
				sb.append(API.API_ADD_ORDER);
				sb.append("?");
				sb.append("shopid=");
				sb.append(shopid);
				sb.append("&userid=");
				sb.append(userid);
				sb.append("&ordermoney=");
				sb.append(ordermoney);

				String result = HttpManager.httpGet(sb.toString());
				Message msg = new Message();
				msg.what = MessageContants.MSG_ADD_ORDER;
				msg.obj = result;
				handler.sendMessage(msg);
			}
		}).start();
	}
	
	public void toGetToken(final Handler handler) {
		new Thread(new Runnable() {

			@Override
			public void run() {
				StringBuffer sb = new StringBuffer();
				sb.append(API.API_GET_TOKEN);
//				sb.append("?");
//				sb.append("shopid=");
//				sb.append(shopid);

				String result = HttpManager.httpGet(sb.toString());
				Message msg = new Message();
				msg.what = MessageContants.MSG_GET_TOKEN;
				msg.obj = result;
				handler.sendMessage(msg);
			}
		}).start();
	}
	
	public void toUploadShopPic(final Handler handler, final String token,
			final String shopid,
			final String url) {
		new Thread(new Runnable() {

			@Override
			public void run() {
				Map<String, String> params = new HashMap<String, String>();
				params.put("token", token);
				params.put("shopid", shopid);
				if (!TextUtils.isEmpty(url)) {
					params.put("url", url);
				}
				String result = "error";
				try {
					result = HttpSession.getRequestResult(API.API_SAVE_SHOPPIC, params);
				} catch (Exception ex) {
					result = "error";
					Log.e(TAG, ex.getMessage());
				} finally {
					Message msg = new Message();
					msg.what = MessageContants.MSG_SAVE_SHOP_PIC;
					msg.obj = result;
					handler.sendMessage(msg);
				}
			}
		}).start();
	}
	
	public void toGetIncomeList(final Handler handler, final String token,
			final String shopid, 
			final String firstIndex, final String pageNum) {
		new Thread(new Runnable() {

			@Override
			public void run() {
				StringBuffer sb = new StringBuffer();
				sb.append(API.API_GET_INCOME_LIST);
				sb.append("?");
				sb.append("token=");
				sb.append(token);
				sb.append("&id=");
				sb.append(shopid);
				sb.append("&firstIndex=");
				sb.append(firstIndex);
				sb.append("&pageNum=");
				sb.append(pageNum);

				String result = HttpManager.httpGet(sb.toString());
				Message msg = new Message();
				msg.what = MessageContants.MSG_GET_INCOME_LIST;
				msg.obj = result;
				handler.sendMessage(msg);
			}
		}).start();
	}
	
	public void toGetIncomeByDate(final Handler handler, final String token,
			final String shopid,
			final String begintime, final String endtime,
			final String firstIndex, final String pageNum) {
		new Thread(new Runnable() {

			@Override
			public void run() {
				StringBuffer sb = new StringBuffer();
				sb.append(API.API_GET_INCOME_BY_DATE);
				sb.append("?");
				sb.append("token=");
				sb.append(token);
				sb.append("&shopid=");
				sb.append(shopid);
				sb.append("&time=");
				sb.append(begintime);
//				sb.append("&endtime=");
//				sb.append(endtime);
				sb.append("&firstIndex=");
				sb.append(firstIndex);
				sb.append("&pageNum=");
				sb.append(pageNum);

				String result = HttpManager.httpGet(sb.toString());
				Message msg = new Message();
				msg.what = MessageContants.MSG_GET_INCOME_BY_DATE;
				msg.obj = result;
				handler.sendMessage(msg);
			}
		}).start();
	}
	
	public void toGetCustomerNeedPay(final Handler handler, final String token,
			final String shopid, 
			final String userid, final String ordermoney, final String isded) {
		new Thread(new Runnable() {

			@Override
			public void run() {
				StringBuffer sb = new StringBuffer();
				sb.append(API.API_GET_CUSTOMER_NEED_PAY );
				sb.append("?");
				sb.append("token=");
				sb.append(token);
				sb.append("&shopid=");
				sb.append(shopid);
				sb.append("&userid=");
				sb.append(userid);
				sb.append("&ordermoney=");
				sb.append(ordermoney);
				sb.append("&isded=");
				sb.append(isded);

				String result = HttpManager.httpGet(sb.toString());
				Message msg = new Message();
				msg.what = MessageContants.MSG_GET_CUSTOMER_NEED_PAY;
				msg.obj = result;
				handler.sendMessage(msg);
			}
		}).start();
	}
	
	public void toConfirmCustomerPay(final Handler handler, final String shopid, 
			final String ordercode) {
		new Thread(new Runnable() {

			@Override
			public void run() {
				StringBuffer sb = new StringBuffer();
				sb.append(API.API_AFTER_CUSTOMER_PAY );
				sb.append("?");
				sb.append("shopid=");
				sb.append(shopid);
				sb.append("&ordercode=");
				sb.append(ordercode);

				String result = HttpManager.httpGet(sb.toString());
				Message msg = new Message();
				msg.what = MessageContants.MSG_AFTER_CUSTOMER_PAY;
				msg.obj = result;
				handler.sendMessage(msg);
			}
		}).start();
	}
	
	public void toGetPaySign(final Handler handler, final String orderId, 
			final String totalFee, final String channel) {
		new Thread(new Runnable() {

			@Override
			public void run() {
				StringBuffer sb = new StringBuffer();
				sb.append(API.API_PAY_SIGN );
				sb.append("?");
				sb.append("orderId=");
				sb.append(orderId);
				sb.append("&totalFee=");
				sb.append(totalFee);
				sb.append("&channel=");
				sb.append(channel);//（wx表示微信；alipay表示支付宝）

				String result = HttpManager.httpGet(sb.toString());
				Message msg = new Message();
				msg.what = MessageContants.MSG_PAY_SIGN;
				msg.obj = result;
				handler.sendMessage(msg);
			}
		}).start();
	}
	
	public void toExchangeShopPoint(final Handler handler, final String token,
			final String shopid) {
		new Thread(new Runnable() {

			@Override
			public void run() {
				StringBuffer sb = new StringBuffer();
				sb.append(API.API_EXCHANGE_SHOP_POINT );
				sb.append("?");
				sb.append("token=");
				sb.append(token);
				sb.append("&shopid=");
				sb.append(shopid);

				String result = HttpManager.httpGet(sb.toString());
				Message msg = new Message();
				msg.what = MessageContants.MSG_EXCHANGE_SHOP_POINT;
				msg.obj = result;
				handler.sendMessage(msg);
			}
		}).start();
	}
	
	public void toGetProvinceInfo(final Handler handler, final String code,
			final String type) {
		new Thread(new Runnable() {

			@Override
			public void run() {
				StringBuffer sb = new StringBuffer();
				sb.append(API.API_GET_CITY_INFO);
				sb.append("?");
				sb.append("type=");
			    sb.append(type);
				if (code != null) {
				    sb.append("&code=");
				    sb.append(code);
				}

				String result = HttpManager.httpGet(sb.toString());
				Message msg = new Message();
				msg.what = MessageContants.MSG_GET_PROVINCE_INFO;
				msg.obj = result;
				handler.sendMessage(msg);
			}
		}).start();
	}
	
	public void toGetCityInfo(final Handler handler, final String code,
			final String type) {
		new Thread(new Runnable() {

			@Override
			public void run() {
				StringBuffer sb = new StringBuffer();
				sb.append(API.API_GET_CITY_INFO);
				sb.append("?");
				sb.append("type=");
			    sb.append(type);
				if (code != null) {
				    sb.append("&code=");
				    sb.append(code);
				}

				String result = HttpManager.httpGet(sb.toString());
				Message msg = new Message();
				msg.what = MessageContants.MSG_GET_CITY_INFO;
				msg.obj = result;
				handler.sendMessage(msg);
			}
		}).start();
	}
	
	public void toGetDistrictInfo(final Handler handler, final String code,
			final String type) {
		new Thread(new Runnable() {

			@Override
			public void run() {
				StringBuffer sb = new StringBuffer();
				sb.append(API.API_GET_CITY_INFO);
				sb.append("?");
				sb.append("type=");
			    sb.append(type);
				if (code != null) {
				    sb.append("&code=");
				    sb.append(code);
				}

				String result = HttpManager.httpGet(sb.toString());
				Message msg = new Message();
				msg.what = MessageContants.MSG_GET_DISTRICT_INFO;
				msg.obj = result;
				handler.sendMessage(msg);
			}
		}).start();
	}
	
	public void toDelShopPic(final Handler handler, final String token,
			final String shopid, 
			final String picid) {
		new Thread(new Runnable() {

			@Override
			public void run() {
				StringBuffer sb = new StringBuffer();
				sb.append(API.API_DEL_SHOP_PIC);
				sb.append("?");
				sb.append("token=");
				sb.append(token);
				sb.append("&shopid=");
				sb.append(shopid);
				sb.append("&picid=");
				sb.append(picid);

				String result = HttpManager.httpGet(sb.toString());
				Message msg = new Message();
				msg.what = MessageContants.MSG_DEL_SHOP_PIC;
				msg.obj = result;
				handler.sendMessage(msg);
			}
		}).start();
	}
	
	public void toUploadAvaterPic(final Handler handler, final String token,
			final String shopid,
			final String url) {
		new Thread(new Runnable() {

			@Override
			public void run() {
				Map<String, String> params = new HashMap<String, String>();
				params.put("token", token);
				params.put("shopid", shopid);
				if (!TextUtils.isEmpty(url)) {
					params.put("url", url);
				}
				String result = "error";
				try {
					result = HttpSession.getRequestResult(API.API_SAVE_SHOP_ICON, params);
				} catch (Exception ex) {
					result = "error";
					Log.e(TAG, ex.getMessage());
				} finally {
					Message msg = new Message();
					msg.what = MessageContants.MSG_SAVE_SHOP_ICON;
					msg.obj = result;
					handler.sendMessage(msg);
				}
			}
		}).start();
	}
	
	
	public void toGetWXPaySign(final Handler handler, final String orderId, 
			final String totalFee, final String body, final String trade_type) {
		new Thread(new Runnable() {

			@Override
			public void run() {
				StringBuffer sb = new StringBuffer();
				sb.append(API.API_WX_PAY_SIGN );
				sb.append("?");
				sb.append("orderId=");
				sb.append(orderId);
				sb.append("&totalFee=");
				sb.append(totalFee);
				sb.append("&body=");
				sb.append(body);
				sb.append("&trade_type=");
				sb.append(trade_type);//（支付类型固定为APP）

				String result = HttpManager.httpGet(sb.toString());
				Message msg = new Message();
				msg.what = MessageContants.MSG_WX_PAY_SIGN;
				msg.obj = result;
				handler.sendMessage(msg);
			}
		}).start();
	}
	
	public void toQueryWXorder(final Handler handler, final String orderId, 
			final String trade_type) {
		new Thread(new Runnable() {

			@Override
			public void run() {
				StringBuffer sb = new StringBuffer();
				sb.append(API.API_QUERY_WX_PAY_RESULT );
				sb.append("?");
				sb.append("orderId=");
				sb.append(orderId);
				sb.append("&trade_type=");
				sb.append(trade_type);//（支付类型固定为APP）

				String result = HttpManager.httpGet(sb.toString());
				Message msg = new Message();
				msg.what = MessageContants.MSG_QUERY_WX_PAY_RESULT;
				msg.obj = result;
				handler.sendMessage(msg);
			}
		}).start();
	}
}
