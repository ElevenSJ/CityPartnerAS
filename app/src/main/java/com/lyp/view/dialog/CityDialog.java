package com.lyp.view.dialog;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import com.lyp.adapter.CityAdapter;
import com.lyp.bean.MyListItem;
import com.lyp.citypartner.R;
import com.lyp.net.Errors;
import com.lyp.net.MessageContants;
import com.lyp.net.NetProxyManager;
import com.lyp.utils.LogUtils;
import com.lyp.utils.ToastUtil;
import com.lyp.view.MySpinner;
import com.lyp.view.MySpinner.OnClickListener;

import android.app.Dialog;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;

public class CityDialog extends Dialog implements OnClickListener {

	private static final String TAG = "CityDialog";
	private WaitDialog mWaitDialog;
	CityAdapter provinceAdapter;
	CityAdapter cityAdapter;
	CityAdapter districtAdapter;
	List<MyListItem> provinceList = new ArrayList<MyListItem>();
	List<MyListItem> cityList = new ArrayList<MyListItem>();
	List<MyListItem> districtList = new ArrayList<MyListItem>();
	HashMap<String, List<MyListItem>> list = new HashMap<String, List<MyListItem>>();

	private Handler mHandler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case MessageContants.MSG_GET_PROVINCE_INFO: {
				parseProviceList((String) msg.obj);
				break;
			}
			case MessageContants.MSG_GET_CITY_INFO: {
				parseCityList((String) msg.obj);
				break;
			}
			case MessageContants.MSG_GET_DISTRICT_INFO: {
				parseDistrictList((String) msg.obj);
				break;
			}
			default:
				break;
			}
		};
	};

	private void parseProviceList(String result) {
		if (Errors.ERROR_NET.equals(result) || Errors.ERROR_SERVER.equals(result)) {
			ToastUtil.showLong(context, R.string.network_error);
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
				ToastUtil.showShort(context, message);
				return;
			}
			JSONArray job = json.getJSONArray("data");
			provinceList.clear();
			for (int i = 0; i < job.length(); i++) {
				MyListItem item = new MyListItem();
				JSONObject jb = job.getJSONObject(i);
				String name = jb.getString("pName");
				String id = jb.getString("pId");
				item.setName(name);
				// item.setPcode(pcode);
				item.setId(id);
				provinceList.add(item);
				// list.put(id, null);
			}
			provinceAdapter.notifyDataSetChanged();
			spinner1.show();
		} catch (Exception ex) {
			ToastUtil.showLongMessage("服务器返回有问题，请检查服务器：" + ex.getMessage());
			LogUtils.e(ex.getMessage());
			return;
		} finally {
			if (mWaitDialog != null && mWaitDialog.isShowing()) {
				mWaitDialog.dismiss();
			}
		}
	}

	private void parseCityList(String result) {
		if (Errors.ERROR_NET.equals(result) || Errors.ERROR_SERVER.equals(result)) {
			ToastUtil.showLong(context, R.string.network_error);
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
				ToastUtil.showShort(context, message);
				return;
			}
			JSONArray job = json.getJSONArray("data");
			cityList.clear();
			String parentId = null;
			for (int i = 0; i < job.length(); i++) {
				MyListItem item = new MyListItem();
				JSONObject jb = job.getJSONObject(i);
				String name = jb.getString("cName");
				String id = jb.getString("cId");
				if (jb.has("parentId")) {
					parentId = jb.getString("parentId");
				}
				item.setName(name);
				// item.setPcode(pcode);
				item.setId(id);
				cityList.add(item);
			}
			if (parentId != null) {
				list.put(parentId, cityList);
			}
			if (cityList.size() <= 0) {
				ToastUtil.showLongMessage("无可选城市！");
				return;
			}
			cityAdapter.notifyDataSetChanged();
			spinner2.show();
		} catch (Exception ex) {
			ToastUtil.showLongMessage("服务器返回有问题，请检查服务器：" + ex.getMessage());
			LogUtils.e(ex.getMessage());
			return;
		} finally {
			if (mWaitDialog != null && mWaitDialog.isShowing()) {
				mWaitDialog.dismiss();
			}
		}
	}

	private void parseDistrictList(String result) {
		if (Errors.ERROR_NET.equals(result) || Errors.ERROR_SERVER.equals(result)) {
			ToastUtil.showLong(context, R.string.network_error);
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
				ToastUtil.showShort(context, message);
				return;
			}
			JSONArray job = json.getJSONArray("data");
			districtList.clear();
			String parentId = null;
			for (int i = 0; i < job.length(); i++) {
				MyListItem item = new MyListItem();
				JSONObject jb = job.getJSONObject(i);
				String name = jb.getString("cName");
				String id = jb.getString("cId");
				if (jb.has("parentId")) {
					parentId = jb.getString("parentId");
				}
				item.setName(name);
				// item.setPcode(pcode);
				item.setId(id);
				districtList.add(item);
			}
			if (parentId != null) {
				list.put(parentId, districtList);
			}
			if (districtList.size() <= 0) {
				ToastUtil.showLongMessage("无可选县区！");
				return;
			}
			districtAdapter.notifyDataSetChanged();
			spinner3.show();
		} catch (Exception ex) {
			ToastUtil.showLongMessage("服务器返回有问题，请检查服务器：" + ex.getMessage());
			LogUtils.e(ex.getMessage());
			return;
		} finally {
			if (mWaitDialog != null && mWaitDialog.isShowing()) {
				mWaitDialog.dismiss();
			}
		}
	}

	// private CityDBManager dbm;
	private SQLiteDatabase db;
	private MySpinner spinner1 = null;
	private MySpinner spinner2 = null;
	private MySpinner spinner3 = null;
	private Button bt_ok;
	private Button bt_cancel;
	private String district = null;
	private String dId = null;
	private String province = null;
	private String pId = null;
	private String city = null;
	private String cId = null;
	private Context context;
	private InputListener IListener;

	public CityDialog(Context context) {
		super(context, android.R.style.Theme_Holo_Light_Dialog_MinWidth);
		this.context = context;
	}

	public CityDialog(Context context, InputListener inputListener) {
		super(context, android.R.style.Theme_Holo_Light_Dialog_MinWidth);
		this.context = context;
		IListener = inputListener;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.city_spinner_dialog);
		spinner1 = (MySpinner) findViewById(R.id.spinner1);
		provinceAdapter = new CityAdapter(context, provinceList);
		spinner1.setAdapter(provinceAdapter);
		spinner1.setOnItemClickListener(new SpinnerOnItemClickListener1());
		spinner1.setOnClickListener(this);
		spinner2 = (MySpinner) findViewById(R.id.spinner2);
		cityAdapter = new CityAdapter(context, cityList);
		spinner2.setAdapter(cityAdapter);
		spinner2.setOnItemClickListener(new SpinnerOnItemClickListener2());
		spinner2.setOnClickListener(this);
		spinner3 = (MySpinner) findViewById(R.id.spinner3);
		districtAdapter = new CityAdapter(context, districtList);
		spinner3.setAdapter(districtAdapter);
		spinner3.setOnItemClickListener(new SpinnerOnItemClickListener3());
		spinner3.setOnClickListener(this);
		bt_ok = (Button) findViewById(R.id.bt_ok);
		bt_cancel = (Button) findViewById(R.id.bt_cancel);
		bt_ok.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
//				String address = province;
//				if (!TextUtils.isEmpty(address)) {
//					address.trim();
//				}
//				if (!TextUtils.isEmpty(city)) {
//				    address = address + "" + city.trim();
//				}
//				if (!TextUtils.isEmpty(district)) {
//					address = address + "" + district.trim();
//				}
				IListener.getText(province, pId, city, cId, district, dId);
				dismiss();
			}
		});
		bt_cancel.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				dismiss();
			}
		});
		spinner1.setHint("请选择省份");
		spinner2.setHint("请选择城市");
		spinner3.setHint("请选择县区");
		// spinner1.setPrompt(context.getResources().getString(R.string.province));
		// spinner2.setPrompt(context.getResources().getString(R.string.city));
		// spinner3.setPrompt(context.getResources().getString(R.string.district));
		// initSpinner1();
	}

	// 定义回调接口
	public interface InputListener {
		void getText(String prov, String provCode, String city, String cityCode,
				String district, String districtCode);
	}

	public void initSpinner1() {
		if (provinceList.size() > 0) {
			spinner1.show();
		} else {
		    NetProxyManager.getInstance().toGetProvinceInfo(mHandler, null, "0");
		}
	}

	public void initSpinner2() {
		if (pId == null) {
			ToastUtil.showMessage("请先选择省份！");
			return;
		}
		if (list.containsKey(pId)) {
			List<MyListItem> cList = list.get(pId);
			cityList.clear();
			cityList.addAll(cList);
			cityAdapter.notifyDataSetChanged();
			spinner2.show();
		} else {
			NetProxyManager.getInstance().toGetCityInfo(mHandler, pId, "1");
		}
		// CityAdapter myAdapter = new CityAdapter(context, list);
		// spinner2.setAdapter(myAdapter);
		// spinner2.setOnItemSelectedListener(new SpinnerOnSelectedListener2());
	}

	public void initSpinner3() {
		if (cId == null) {
			ToastUtil.showMessage("请先选择城市！");
			return;
		}
		if (list.containsKey(cId)) {
			List<MyListItem> dList = list.get(cId);
			districtList.clear();
			districtList.addAll(dList);
			districtAdapter.notifyDataSetChanged();
			spinner3.show();
		} else {
			NetProxyManager.getInstance().toGetDistrictInfo(mHandler, cId, "2");
		}
		// CityAdapter myAdapter = new CityAdapter(context, list);
		// spinner3.setAdapter(myAdapter);
		// spinner3.setOnItemSelectedListener(new SpinnerOnSelectedListener3());
	}

	class SpinnerOnItemClickListener1 implements OnItemClickListener {

		@Override
		public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
			MyListItem item = provinceList.get(position);
			province = item.getName();
			if (pId != null && !pId.equals(item.getId())) {
                cId = null;
                cityList.clear();
                spinner2.setText(null);
                city = null;
                dId = null;
                districtList.clear();
                spinner3.setText(null);
                district = null;
			}
			pId = item.getId();
		}
	}

	class SpinnerOnItemClickListener2 implements OnItemClickListener {

		@Override
		public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
			MyListItem item = cityList.get(position);
			city = item.getName();
			if (cId != null && !cId.equals(item.getId())) {
                dId = null;
                districtList.clear();
                spinner3.setText(null);
                district = null;
			}
			cId = item.getId();
		}
	}

	class SpinnerOnItemClickListener3 implements OnItemClickListener {

		@Override
		public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
			MyListItem item = districtList.get(position);
			district = item.getName();
			dId = item.getId();
		}

	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.spinner1:
			initSpinner1();
			break;
		case R.id.spinner2:
			initSpinner2();
			break;
		case R.id.spinner3:
			initSpinner3();
			break;

		}
	}
}
