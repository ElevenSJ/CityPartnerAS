package com.lyp.citypartner;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshBase.Mode;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener2;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.lyp.adapter.IncomeHistoryAdapter;
import com.lyp.base.BaseActivity;
import com.lyp.bean.IncomeHistoryBean;
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

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

public class IncomeHistoryActivity extends BaseActivity implements OnRefreshListener2<ListView> {

	private PullToRefreshListView mPullRefreshListView;
	private ListView lv_order;
	private WaitDialog mWaitDialog;
	private CustomPopupWindow mPopWin;
	private SharedPreferences mSharedPreferences;
	private List<IncomeHistoryBean> list;
	private IncomeHistoryAdapter mAdapter;
	private String mCurrentIndex = "-1";
	private String nextindex = null;
	private String mRow = "20";

	private Handler mHandler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case MessageContants.MSG_GET_INCOME_LIST: {
				parseIncomeList((String) msg.obj);
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
		setContentView(R.layout.income_layout);
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
		list = new ArrayList<IncomeHistoryBean>();
		mAdapter = new IncomeHistoryAdapter(list, this);
		lv_order.setAdapter(mAdapter);
		lv_order.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> adapterView, View view, int position, long arg3) {
//				IncomeHistoryBean bean = list.get(position -1);
//				Intent i = new Intent();
//				i.setClass(IncomeHistoryActivity.this, QueryIncomeByDateActivity.class);
//				i.putExtra("id", bean.getId());
//				i.putExtra("time", bean.getDate());
//				startActivity(i);
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
		NetProxyManager.getInstance().toGetIncomeList(mHandler, tokenid,
				saleId, mCurrentIndex, mRow);
//		NetProxyManager.getInstance().toGetIncomeList(mHandler, saleId);
	}
	
	public void startQuery(View view){
		Intent intent =new Intent();
		intent.setClass(this, QueryOrderByDateActivity.class);
		startActivity(intent);
	}

	public void onBack(View view) {
		finish();
	}

	private void parseIncomeList(String result) {
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
				IncomeHistoryBean bean = new IncomeHistoryBean();
				bean.setId(jb.getString("id"));
				bean.setDate(jb.getString("time"));
				bean.setIncome(jb.getString("income"));
//				bean.setOrdertime(DateUtil.formatDateStr(jb.getString("ordertime")));
//				bean.setOrdercode(jb.getString("ordercode"));
//				bean.setShopamountpaid(jb.getString("shopamountpaid"));
//				JSONObject job = jb.getJSONObject("user");
//				orderBean.setCusicon(job.getString("cusicon"));
//				bean.setCusicon(jb.getString("cusicon"));
//				bean.setCusname(jb.getString("cusname"));
				list.add(bean);
			}
			nextindex = null;
			if (jsonObject.has("nextFirstIndex") && !jsonObject.isNull("nextFirstIndex")) {
				nextindex = jsonObject.getString("nextFirstIndex");
				if (nextindex.length() > 0) {
					mCurrentIndex = nextindex;
				}
			}
			if (nextindex == null) {
				if (list.size() <=0) {
					ToastUtil.showLongMessage("很遗憾，您沒有任何收益!");
				}
			} else {
				mPullRefreshListView.setMode(Mode.PULL_UP_TO_REFRESH);
			}
			mPullRefreshListView.onRefreshComplete();
			mAdapter.notifyDataSetChanged();
		} catch (Exception ex) {
			ToastUtil.showLongMessage("服务器返回有问题，请检查服务器!");
			LogUtils.e(ex.getMessage());
		} finally {
			if (mWaitDialog != null && mWaitDialog.isShowing()) {
				mWaitDialog.dismiss();
			}
		}
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
