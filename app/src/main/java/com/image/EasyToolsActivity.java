package com.image;


import com.lyp.citypartner.R;

import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.drawable.ColorDrawable;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.TextView;

public abstract class EasyToolsActivity extends FragmentActivity implements OnClickListener {
	/** 标题 */
	public ActionBar actionBar;
	/** 标题View */
	public  View titleTabView;
	/** 左侧文字 */
	public TextView TiTLeft;
	/** 左侧图片 */
	public ImageView ImgLeft;
	/** 中间标题 */
	public TextView TiTCenter;
	/** 右边文字 */
	public TextView TiTRight;
	/** 右边标题 */
	public ImageView ImgRight;

	private IntentFilter mFilter = new IntentFilter();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		initActionBar();
	
	}

	
	/**
	 * 不需要标题栏时隐藏
	 */
	public void setActionBarVib() {
		//.getSupportActionBar().hide();
		actionBar.hide();
	}

	/**
	 * 标题
	 */
	@SuppressLint({ "NewApi", "InflateParams" })
	public void initActionBar() {
		if (getActionBar() != null) {
			ColorDrawable colorDrawable = new ColorDrawable(getResources().getColor(R.color.white));
			actionBar = getActionBar();
			actionBar.setTitle("");
			actionBar.setBackgroundDrawable(colorDrawable);
			actionBar.setDisplayShowHomeEnabled(false);
			actionBar.setDisplayShowTitleEnabled(true);
			actionBar.setDisplayHomeAsUpEnabled(false);
			actionBar.setLogo(null);
			actionBar.setHomeButtonEnabled(true);
			actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
			actionBar.setDisplayShowCustomEnabled(true);
			titleTabView = LayoutInflater.from(this).inflate(R.layout.head_title, null);
			actionBar.setCustomView(titleTabView);
			actionBar.show();
			TiTCenter = (TextView) titleTabView.findViewById(R.id.center_text);
			TiTLeft = (TextView) titleTabView.findViewById(R.id.left_text);
			ImgLeft = (ImageView) titleTabView.findViewById(R.id.left_img);
			TiTRight = (TextView) titleTabView.findViewById(R.id.right_text);
			ImgRight = (ImageView) titleTabView.findViewById(R.id.right_img);
		}
	}

	/**
	 * 网络监控
	 * 
	 * @author Administrator
	 *         <RelativeLayout android:id="@+id/title_view" android:layout_width
	 *         ="match_parent" android:layout_height="@dimen/title_view_height"
	 *         android:layout_marginTop="10dp" android:background="@color/blue"
	 *         >
	 * 
	 *         <TextView android:id="@+id/title_text" android:layout_width=
	 *         "wrap_content" android:layout_height="wrap_content"
	 *         android:layout_centerInParent="true" android:text="这是干货 这是港货" />
	 * 
	 *         <ImageView android:id="@+id/title_img" android:layout_width=
	 *         "wrap_content" android:layout_height="wrap_content"
	 *         android:layout_alignParentRight="true"
	 *         android:layout_centerInParent="true" android:paddingRight="10dp"
	 *         android:src="@drawable/close" /> </RelativeLayout>
	 */

	/**
	 * 设置头部样式是否显示
	 * 
	 * @param Tl
	 *            左侧文本
	 * @param Il
	 *            左侧图片
	 * @param Cr
	 *            中间标题
	 * @param Tr
	 *            右边文本
	 * @param Ir
	 *            右边标题
	 */
	public void TitleVisib(int Tl, int Il, int Cr, int Tr, int Ir) {
		if (Tl == 1)
			TiTLeft.setVisibility(View.VISIBLE);
		if (Il == 1)
			ImgLeft.setVisibility(View.VISIBLE);
		if (Cr == 1)
			TiTCenter.setVisibility(View.VISIBLE);
		if (Tr == 1)
			TiTRight.setVisibility(View.VISIBLE);
		if (Ir == 1)
			ImgRight.setVisibility(View.VISIBLE);
	}

	/**
	 * 设置点击事件
	 */
	public void TitleClick(int Tl, int Il, int Cr, int Tr, int Ir) {
		if (Tl == 1)
			TiTLeft.setOnClickListener(this);
		if (Il == 1)
			ImgLeft.setOnClickListener(this);
		if (Cr == 1)
			TiTCenter.setOnClickListener(this);
		if (Tr == 1)
			TiTRight.setOnClickListener(this);
		if (Ir == 1)
			ImgRight.setOnClickListener(this);
	}

	/**
	 * 设置标题样式
	 * 
	 * @param TlText
	 *            左侧文字
	 * @param IlImage
	 *            左侧图片
	 * @param CText
	 *            中间文字
	 * @param TrText
	 *            右边文字
	 * @param IrImage
	 *            右边图片
	 */
	public void TitleStyle(String TlText, int IlImage, String CText, String TrText, int IrImage) {
		if (isVisib(TiTLeft))
			TiTLeft.setText(TlText);
		if (isVisib(ImgLeft))
			ImgLeft.setImageResource(IlImage);
		if (isVisib(TiTCenter))
			TiTCenter.setText(CText);
		if (isVisib(TiTRight))
			TiTRight.setText(TrText);
		if (isVisib(ImgRight))
			ImgRight.setImageResource(IrImage);

	}

	/**
	 * 验证是否为隐藏状态
	 * 
	 * @param v
	 * @return
	 */
	private Boolean isVisib(View v) {
		if (v.getVisibility() == View.INVISIBLE) {
			return false;
		}
		return true;
	}

	/**
	 * 切换Activity
	 * 
	 * @param c
	 *            需要切换到的Activity
	 */
	public void GoActivity(Class<?> c) {
		Intent intent = new Intent(this, c);
		this.startActivity(intent);
	}

	/**
	 * 切换Activity
	 * 
	 * @param c
	 *            需要切换到的Activity
	 * @param type
	 *            参数
	 */
	public void GoActivity(Class<?> c, String type) {
		Intent intent = new Intent(this, c);
		intent.putExtra("type", type);
		this.startActivity(intent);
	}

	@Override
	protected void onResume() {
		super.onResume();
	}

	@Override
	protected void onPause() {
		super.onPause();
	}
	


	@Override

	protected void onDestroy() {

	  
	    super.onDestroy();
	}

}