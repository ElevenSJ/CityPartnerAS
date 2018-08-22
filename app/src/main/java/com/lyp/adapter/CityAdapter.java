package com.lyp.adapter;

import java.util.List;

import com.lyp.bean.MyListItem;
import com.lyp.citypartner.R;
import com.lyp.utils.DisplayUtil;

import android.content.Context;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

public class CityAdapter extends BaseAdapter {

	private Context context;
	private List<MyListItem> myList;

	public CityAdapter(Context context, List<MyListItem> myList) {
		this.context = context;
		this.myList = myList;
	}

	public int getCount() {
		return myList.size();
	}

	public Object getItem(int position) {
		return myList.get(position).getName();
	}

	public long getItemId(int position) {
		return position;
	}

	public View getView(int position, View convertView, ViewGroup parent) {
		MyListItem myListItem = myList.get(position);
		return new CityAdapterView(this.context, myListItem);
	}

	class CityAdapterView extends LinearLayout {
		public static final String LOG_TAG = "CityAdapterView";

		public CityAdapterView(Context context, MyListItem myListItem) {
			super(context);
			this.setOrientation(HORIZONTAL);

			DisplayUtil du = new DisplayUtil(context);
			LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
					LayoutParams.MATCH_PARENT, du.dipToPx(50));
			params.gravity = Gravity.CENTER_VERTICAL;

			TextView name = new TextView(context);
			name.setTextColor(context.getResources().getColor(R.color.black));
			name.setTextSize(18);
			name.setGravity(Gravity.CENTER_VERTICAL);
			name.setPadding(20, 0, 20, 0);
			name.setText(myListItem.getName());
			addView(name, params);

			LinearLayout.LayoutParams params2 = new LinearLayout.LayoutParams(
					LayoutParams.MATCH_PARENT, du.dipToPx(50));
			params2.gravity = Gravity.CENTER_VERTICAL;

			TextView pcode = new TextView(context);
			pcode.setTextColor(context.getResources().getColor(R.color.black));
			pcode.setTextSize(18);
			pcode.setText(myListItem.getPcode());
			pcode.setPadding(20, 0, 20, 0);
			pcode.setGravity(Gravity.CENTER_VERTICAL);
			addView(pcode, params2);
			pcode.setVisibility(GONE);

		}

	}

}