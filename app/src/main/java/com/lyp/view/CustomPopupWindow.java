package com.lyp.view;


import com.lyp.utils.DisplayUtil;

import android.app.Activity;
import android.view.Gravity;
import android.view.View;
import android.widget.PopupWindow;

public class CustomPopupWindow {

    private PopupWindow mPopupWindow;

    private DisplayUtil mDisplayUtil;

    /**
     * @param contentView
     * @param width
     * @param height
     */
    public CustomPopupWindow(View contentView, int width, int height, Activity activity) {
        super();
        mPopupWindow = new PopupWindow(contentView, width, height);
        mPopupWindow.setOutsideTouchable(false);
        mPopupWindow.setFocusable(true);
        mPopupWindow.setBackgroundDrawable(activity.getResources().getDrawable(
                android.R.color.transparent));
        mPopupWindow.update();
        mDisplayUtil = new DisplayUtil(activity);
    }

    /**
     * getPopupWindow
     * 
     * @return popupwindow
     */
    public PopupWindow getPopupWindow() {
        return mPopupWindow;
    }

    /**
     * show popupwindow up or down as click view
     * 
     * @param anchor click view
     * @param popwindowHeiht popupwindow height(dip)
     */
    public void showByClickView(View anchor, int popwindowHeiht) {
        if (anchor.getBottom() <= mDisplayUtil.getScreenHeight() / 2) {
            showAsDropDown(anchor);
        } else {
            showAsDropUp(anchor, 0, 0, popwindowHeiht);
        }
    }

    /**
     * show popupwindow up or down as click view
     * 
     * @param anchor click view
     * @param xOff the popup's x location offset(px)
     * @param yOff the popup's y location offset(px)
     * @param popwindowHeiht popupwindow height(dip)
     */
    public void showByClickView(View anchor, int xOff, int yOff, int popwindowHeiht) {
        if (anchor.getBottom() <= mDisplayUtil.getScreenHeight() / 2) {
            showAsDropDown(anchor, xOff, yOff);
        } else {
            showAsDropUp(anchor, xOff, yOff, popwindowHeiht);
        }
    }

    /**
     * show popupwindow up as click view
     * 
     * @param anchor click view
     * @param xOff the popup's x location offset(px)
     * @param yOff the popup's y location offset(px)
     */
    public void showAsDropDown(View anchor, int xOff, int yOff) {
        if (mPopupWindow != null && !mPopupWindow.isShowing()) {
            mPopupWindow.showAsDropDown(anchor, xOff, yOff);
        }
    }

    /**
     * show popupwindow up as click view
     * 
     * @param anchor click view
     * @param xOff the popup's x location offset(px)
     * @param yOff the popup's y location offset(px)
     * @param popwindowHeiht popupwindow height(dip)
     */
    public void showAsDropUp(View anchor, int xOff, int yOff, int popwindowHeiht) {
        if (mPopupWindow != null && !mPopupWindow.isShowing()) {
            mPopupWindow.showAsDropDown(anchor, xOff,
                    yOff - (anchor.getHeight() + mDisplayUtil.dipToPx(popwindowHeiht)));
        }
    }

    /**
     * show popupwindow down as click view
     * 
     * @param anchor click view
     */
    public void showAsDropDown(View anchor) {
        if (mPopupWindow != null && !mPopupWindow.isShowing()) {
            mPopupWindow.showAsDropDown(anchor);
        }
    }

    /**
     * show popupwindow at location by x,y
     * 
     * @param parent parent a parent view
     * @param gravity the gravity which controls the placement of the popup
     *            window
     * @param x the popup's x location offset
     * @param y the popup's y location offset
     */
    public void showAtLocation(View parent, int gravity, int x, int y) {
        if (mPopupWindow != null && !mPopupWindow.isShowing()) {
            mPopupWindow.showAtLocation(parent, gravity, x, y);
        }
    }

    /**
     * show popupwindow at custom location
     * 
     * @param anchor anchor click view
     * @param popwindWidth popupwindow width
     */
    public void showCustomAsDropDown(View anchor) {
        if (mPopupWindow != null && !mPopupWindow.isShowing()) {
            int[] arrayOfInt = new int[2];
            anchor.getLocationInWindow(arrayOfInt);
            mPopupWindow.showAtLocation(anchor, Gravity.NO_GRAVITY, arrayOfInt[0], arrayOfInt[1]
                    + anchor.getHeight());
        }
    }

    /**
     * show popupwindow at custom location
     * 
     * @param anchor anchor click view
     * @param popwindWidth popupwindow width
     */
    public void showCustomAsDropCenterDown(View anchor, int popwindWidth) {
        if (mPopupWindow != null && !mPopupWindow.isShowing()) {
            int[] arrayOfInt = new int[2];
            anchor.getLocationInWindow(arrayOfInt);
            mPopupWindow.showAtLocation(anchor, Gravity.NO_GRAVITY,
                    arrayOfInt[0] + anchor.getWidth() / 2 - popwindWidth / 2, arrayOfInt[1]
                            + anchor.getHeight() + 2);
        }
    }

    /**
     * dismiss popupwindow
     */
    public void dismiss() {
        if (mPopupWindow != null && mPopupWindow.isShowing()) {
            mPopupWindow.dismiss();
        }
    }

    /**
     * judge popupwindow is showing
     * 
     * @return
     */
    public boolean isShowing() {
        if (mPopupWindow != null) {
            return mPopupWindow.isShowing();
        }

        return false;
    }
}
