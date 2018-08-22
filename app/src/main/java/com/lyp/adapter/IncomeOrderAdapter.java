package com.lyp.adapter;

import java.text.DecimalFormat;
import java.util.List;

import com.lyp.bean.OrderBean;
import com.lyp.citypartner.OrderDetailActivity;
import com.lyp.citypartner.R;
import com.lyp.manager.ImageManager;
import com.lyp.utils.DateUtil;

import android.app.Activity;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class IncomeOrderAdapter extends BaseAdapter {

	private List<OrderBean> orderList;
	private Activity activity;

	public IncomeOrderAdapter(List<OrderBean> orderList, Activity context) {
		super();
		this.orderList = orderList;
		this.activity = context;
	}

	@Override
	public int getCount() {
		if (orderList != null) {
			return orderList.size();
		}
		return 0;
	}

	@Override
	public Object getItem(int position) {
		if (orderList != null) {
			return orderList.get(position);
		}
		return null;
	}

	@Override
	public long getItemId(int position) {
		return 0;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		ViewHolder viewHolder = null;
		final OrderBean orderBean = orderList.get(position);
		if (convertView == null) {
			viewHolder = new ViewHolder();
			convertView = LayoutInflater.from(activity).inflate(R.layout.income_order_item, null);
			viewHolder.order_date = (TextView) convertView.findViewById(R.id.order_date);
			viewHolder.civ_1 = (ImageView) convertView.findViewById(R.id.civ_1);
			viewHolder.cname = (TextView) convertView.findViewById(R.id.cname);
			viewHolder.order_price = (TextView) convertView.findViewById(R.id.order_price);
//			viewHolder.order_detail.setOnClickListener(new OnClickListener() {
//
//				@Override
//				public void onClick(View v) {
//					Intent i = new Intent();
//					i.setClass(activity, OrderDetailActivity.class);
//					i.putExtra("order_id", orderBean.getOrderId());
//					activity.startActivity(i);
//				}
//			});
			convertView.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder) convertView.getTag();
		}
		viewHolder.order_date.setText(DateUtil.stringToStr(orderBean.getOrdertime()));
		viewHolder.cname.setText(orderBean.getCusname());
		if (orderBean.getCusicon() != null) {
			ImageManager.loadImage(orderBean.getCusicon(), viewHolder.civ_1, R.drawable.personal);
		}
		DecimalFormat df = new DecimalFormat("0.00");
		viewHolder.order_price.setText("￥" + df.format(Double.valueOf(orderBean.getShopamountpaid())));
		return convertView;
	}

	public class ViewHolder {
		public TextView order_date;
		public ImageView civ_1;
		public TextView order_price;
		public TextView cname;
	}
}
