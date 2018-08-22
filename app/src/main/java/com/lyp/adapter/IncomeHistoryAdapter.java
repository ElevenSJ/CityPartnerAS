package com.lyp.adapter;

import java.util.List;

import com.lyp.bean.IncomeHistoryBean;
import com.lyp.citypartner.IncomeHistoryActivity;
import com.lyp.citypartner.QueryIncomeByDateActivity;
import com.lyp.citypartner.R;
import com.lyp.utils.DateUtil;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class IncomeHistoryAdapter extends BaseAdapter {

	private List<IncomeHistoryBean> list;
	private IncomeHistoryActivity activity;

	public IncomeHistoryAdapter(List<IncomeHistoryBean> orderList, IncomeHistoryActivity context) {
		super();
		this.list = orderList;
		this.activity = context;
	}

	@Override
	public int getCount() {
		if (list != null) {
			return list.size();
		}
		return 0;
	}

	@Override
	public Object getItem(int position) {
		if (list != null) {
			return list.get(position);
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
		final IncomeHistoryBean bean = list.get(position);
		if (convertView == null) {
			viewHolder = new ViewHolder();
			convertView = LayoutInflater.from(activity).inflate(R.layout.income_history_item, null);
//			viewHolder.no = (TextView) convertView.findViewById(R.id.no);
			viewHolder.date = (TextView) convertView.findViewById(R.id.date);
//			viewHolder.civ_1 = (ImageView) convertView.findViewById(R.id.civ_1);
			viewHolder.price = (TextView) convertView.findViewById(R.id.price);
//			viewHolder.detail = (TextView) convertView.findViewById(R.id.detail);
//			viewHolder.detail.setOnClickListener(new OnClickListener() {
//
//				@Override
//				public void onClick(View v) {
//					Intent i = new Intent();
//					i.setClass(activity, QueryIncomeByDateActivity.class);
//					i.putExtra("id", bean.getId());
//					i.putExtra("time", bean.getDate());
//					activity.startActivity(i);
//				}
//			});
			convertView.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder) convertView.getTag();
		}
		viewHolder.price.setText(bean.getIncome());
		viewHolder.date.setText(DateUtil.formatDateStr1(bean.getDate()));
//		viewHolder.no.setText("No." + bean.getId());
//		viewHolder.date.setText(DateUtil.stringToStr(bean.getOrdertime()));
//		if (bean.getCusicon() != null) {
//			ImageManager.loadImage(bean.getCusicon(), viewHolder.civ_1, R.drawable.personal);
//		}
//		DecimalFormat df = new DecimalFormat("0.00");
//		viewHolder.order_price.setText("共计 ￥" + df.format(Double.valueOf(bean.getShopamountpaid())));
		return convertView;
	}

	public class ViewHolder {
		public TextView no;
		public TextView date;
		public ImageView civ_1;
		public TextView price;
		public TextView detail;
	}
}
