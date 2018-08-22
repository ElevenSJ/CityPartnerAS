package com.lyp.citypartner;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshBase.Mode;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener2;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.lyp.adapter.QueryOrderAdapter;
import com.lyp.base.BaseActivity;
import com.lyp.bean.OrderBean;
import com.lyp.net.Errors;
import com.lyp.net.MessageContants;
import com.lyp.net.NetProxyManager;
import com.lyp.utils.Constant;
import com.lyp.utils.DateUtil;
import com.lyp.utils.DisplayUtil;
import com.lyp.utils.LogUtils;
import com.lyp.utils.SystemStatusManager;
import com.lyp.utils.ToastUtil;
import com.lyp.view.CustomPopupWindow;
import com.lyp.view.dialog.WaitDialog;

import android.app.DatePickerDialog;
import android.app.DatePickerDialog.OnDateSetListener;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.DatePicker;
import android.widget.ListView;
import android.widget.TextView;

public class QueryOrderByIdActivity extends BaseActivity implements OnRefreshListener2<ListView> {

	private PullToRefreshListView mPullRefreshListView;
	private ListView lv_order;
	private WaitDialog mWaitDialog;
	private CustomPopupWindow mPopWin;
	private SharedPreferences mSharedPreferences;
	private List<OrderBean> orderList;
	private QueryOrderAdapter mOrderAdapter;
	private TextView start_time;
	private TextView end_time;
	private String userId;
	private String mCurrentIndex = "-1";
	private String nextindex = null;
	private String mRow = "20";

	private Handler mHandler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case MessageContants.MSG_GET_ORDER_LIST_BY_ID: {
				parseOrderList((String) msg.obj);
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
		setContentView(R.layout.query_order_by_id_layout);
		userId = getIntent().getStringExtra("userId");
		initView();
		initData();
	}

	private void initView() {
		mPullRefreshListView = (PullToRefreshListView) findViewById(R.id.pull_refresh_listview);
		lv_order = mPullRefreshListView.getRefreshableView();
		lv_order.setDividerHeight(new DisplayUtil(this).dipToPx(1));
		lv_order.setSelector(android.R.color.transparent);
		mPullRefreshListView.setMode(Mode.DISABLED);
		mPullRefreshListView.setOnRefreshListener(this);
		orderList = new ArrayList<OrderBean>();
		mOrderAdapter = new QueryOrderAdapter(orderList, this);
		lv_order.setAdapter(mOrderAdapter);
		lv_order.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> adapterView, View view, int position, long arg3) {
				// Intent intent = new Intent();
				// intent.setClass(OrderActivity.this,
				// OrderDetailActivity.class);
				// // intent.putExtra("id", contactSortModel.getId());
				// startActivity(intent);
			}
		});
	}

	private void initData() {
		if (mWaitDialog == null) {
			mWaitDialog = new WaitDialog(this, R.string.loading_data);
		}
		mWaitDialog.show();
		getData();
	}

	private void getData() {
		mSharedPreferences = getSharedPreferences(Constant.SHARED_PREFERENCE, MODE_PRIVATE);
		String tokenid = mSharedPreferences.getString(Constant.TOKEN_ID, "");
		String saleId = mSharedPreferences.getString(Constant.ID, "");
		NetProxyManager.getInstance().toGetOrderListById(mHandler, tokenid,
				saleId, userId,
				 mCurrentIndex, mRow);
//		NetProxyManager.getInstance().toGetOrderListById(mHandler, saleId, userId);
	}
	
	public void onBack(View view) {
		finish();
	}

	private void parseOrderList(String result) {
		if (Errors.ERROR_NET.equals(result) || Errors.ERROR_SERVER.equals(result)) {
			ToastUtil.showLong(this, R.string.network_error);
			if (mWaitDialog != null && mWaitDialog.isShowing()) {
				mWaitDialog.dismiss();
			}
			return;
		}
		// to parser json data
		try {
			LogUtils.d("----" + result);
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
			JSONObject jsonObject = json.getJSONObject("data");
			JSONArray jsonArray = jsonObject.getJSONArray("dataList");
//			orderList.clear();
			for (int i = 0; i < jsonArray.length(); i++) {
//				double orderPrice = 0d;
				JSONObject jb = jsonArray.getJSONObject(i);
				OrderBean orderBean = new OrderBean();
				orderBean.setOrderId(jb.getString("id"));
				orderBean.setOrdertime(DateUtil.formatDateStr(jb.getString("ordertime")));
				orderBean.setOrdercode(jb.getString("ordercode"));
				orderBean.setShopname(jb.getString("shopname"));
				orderBean.setShopamountpaid(jb.getString("shopamountpaid"));
				orderBean.setCusicon(jb.getString("cusicon"));
				orderBean.setCusname(jb.getString("cusname"));
				orderList.add(orderBean);
			}
			nextindex = null;
			if (jsonObject.has("nextFirstIndex") && !jsonObject.isNull("nextFirstIndex")) {
				nextindex = jsonObject.getString("nextFirstIndex");
				if (nextindex.length() > 0) {
					mCurrentIndex = nextindex;
				}
			}
			if (nextindex == null) {
				if (orderList.size() <=0) {
					ToastUtil.showLongMessage("未查到相关订单!");
				}
			} else {
				mPullRefreshListView.setMode(Mode.PULL_UP_TO_REFRESH);
			}
			mPullRefreshListView.onRefreshComplete();
			mOrderAdapter.notifyDataSetChanged();
			if (mWaitDialog != null && mWaitDialog.isShowing()) {
				mWaitDialog.dismiss();
			}
		} catch (Exception ex) {
			ToastUtil.showLongMessage("服务器返回有问题，请检查服务器：" + ex.getMessage());
			LogUtils.e(ex.getMessage());
			if (mWaitDialog != null && mWaitDialog.isShowing()) {
				mWaitDialog.dismiss();
			}
			return;
		}
	}

	private void setTranslucentStatus() {
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {

			getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
			getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
		}
		SystemStatusManager tintManager = new SystemStatusManager(this);
		tintManager.setStatusBarTintEnabled(true);
		tintManager.setStatusBarTintResource(R.color.main_bg_color);
		getWindow().getDecorView().setFitsSystemWindows(true);
	}

	@Override
	public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
		mHandler.postDelayed(mRefresCompleteRunnable, 1000);
	}

	@Override
	public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
		if (nextindex == null) {
			ToastUtil.showShort(this, R.string.no_more_data_loading);
			mHandler.postDelayed(mRefresCompleteRunnable, 1000);
		} else {
			getData();
		}
	}

	private Runnable mRefresCompleteRunnable = new Runnable() {

		@Override
		public void run() {
			mPullRefreshListView.onRefreshComplete();
		}
	};

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode == RESULT_OK && requestCode == 0x1314) {
			getData();
		}
		super.onActivityResult(requestCode, resultCode, data);
	}
}
