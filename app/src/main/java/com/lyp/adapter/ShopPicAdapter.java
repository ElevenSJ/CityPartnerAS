
package com.lyp.adapter;

import java.util.List;

import com.lyp.bean.ShopPicBean;
import com.lyp.citypartner.R;
import com.lyp.manager.ImageManager;
import com.lyp.net.API;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class ShopPicAdapter extends BaseAdapter {

    private Context mContext;

    private List<ShopPicBean> list;

    public ShopPicAdapter(Context context,
            List<ShopPicBean> list) {
        super();
        this.mContext = context;
        this.list = list;
    }

    @Override
    public int getCount() {
        if (list == null)
            return 0;
        return list.size();
    }

    @Override
    public Object getItem(int arg0) {
        if (list == null)
            return null;
        return list.get(arg0);
    }

    @Override
    public long getItemId(int arg0) {
        return arg0;
    }

    @Override
    public View getView(int arg0, View contentView, ViewGroup arg2) {
        ViewHolder viewHolder = null;
        if (null == contentView) {
            viewHolder = new ViewHolder();
            contentView = LayoutInflater.from(mContext).inflate(
                    R.layout.shop_item_pic, null);
            viewHolder.image = (ImageView) contentView
                    .findViewById(R.id.image);
            contentView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) contentView.getTag();
        }

        ShopPicBean bean = list.get(arg0
                % list.size());
        ImageManager.loadImage(bean.getUrl(),
                viewHolder.image, R.drawable.default_image);
        return contentView;
    }

    private class ViewHolder {
        private ImageView image;
    }
}
