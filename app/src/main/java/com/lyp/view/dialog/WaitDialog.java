package com.lyp.view.dialog;


import com.lyp.citypartner.R;

import android.app.Dialog;
import android.content.Context;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

public class WaitDialog {
	private Context mContext;
	private String mShowMsg;
	private Dialog mWaitDialog;

	public WaitDialog(Context context) {
		super();
		mContext = context;
		initDialog();
	}

	public WaitDialog(Context context, String showMsg) {
		super();
		mContext = context;
		mShowMsg = showMsg;
		initDialog();
	}

	public WaitDialog(Context context, int resid) {
		super();
		mContext = context;
		mShowMsg = context.getResources().getString(resid);
		initDialog();
	}

	private void initDialog() {
		mWaitDialog = new Dialog(mContext, R.style.waitDialogTheme);
		mWaitDialog.setContentView(R.layout.loading_layout);
		TextView showMsgView = (TextView) mWaitDialog
				.findViewById(R.id.load_tv);
		if (mShowMsg != null && mShowMsg.trim().length() > 0) {
			showMsgView.setVisibility(View.VISIBLE);
			showMsgView.setText(mShowMsg);
		} else {
			showMsgView.setVisibility(View.GONE);
		}
		/*
		 * 获取窗口对象及参数对象以修改对话框的布局设置, 可以直接调用getWindow(),表示获得这个Activity的Window
		 * 对象,这样这可以以同样的方式改变这个Activity的属性.
		 */
		Window dialogWindow = mWaitDialog.getWindow();
		dialogWindow.setGravity(Gravity.CENTER);
		WindowManager.LayoutParams lp = dialogWindow.getAttributes();

		// 将对话框的大小按屏幕大小的百分比设置
		DisplayMetrics dm = mContext.getResources().getDisplayMetrics();
		lp.height = (int) (dm.widthPixels * 0.5); // 宽度设置为屏幕的0.5
		lp.width = (int) (dm.widthPixels * 0.5); // 宽度设置为屏幕的0.5
		// 当Window的Attributes改变时系统会调用此函数,可以直接调用以应用上面对窗口参数的更改,也可以用setAttributes
		// dialog.onWindowAttributesChanged(lp);
		dialogWindow.setAttributes(lp);
	}

	public void show() {
		if (mWaitDialog == null) {
			initDialog();
		}
		mWaitDialog.show();
	}

	public boolean isShowing() {
		return mWaitDialog == null ? false : mWaitDialog.isShowing();
	}

	public void dismiss() {
		if (mWaitDialog != null && mWaitDialog.isShowing()) {
			mWaitDialog.dismiss();
		}
	}
}
