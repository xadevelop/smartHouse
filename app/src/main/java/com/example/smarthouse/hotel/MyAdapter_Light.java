package com.example.smarthouse.hotel;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import com.example.smarthouse.R;
import com.pullmi.common.CommonUtils;
import com.pullmi.entity.WareLight;

/**
 * Created by maibenben on 2016/4/25.
 */
public class MyAdapter_Light extends BaseAdapter{

    List<String>mList;
    Context mcontext;
    public MyAdapter_Light(List<String>list, Context context){
        mList = list;
        mcontext = context;
    }

    @Override
    public int getCount() {
        return mList.size();
    }

    @Override
    public Object getItem(int position) {
        return mList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        ViewHolder viewHolder;

        if (convertView == null){
            convertView = LayoutInflater.from(mcontext).inflate(R.layout.hotel_light_gridview_item,null);

            viewHolder = new ViewHolder();
            viewHolder.tv = (TextView)convertView.findViewById(R.id.light_item_textview);
            viewHolder.iv = (ImageView)convertView.findViewById(R.id.light_image);

            convertView.setTag(viewHolder);
        }else{

            viewHolder = (ViewHolder) convertView.getTag();
        }

        //需要给imageview给资源(根据服务器的数据判断)；
        for (int i = 0; i < activityHotel.mLightDataset.size(); i++) {
			if (mList.get(position).equals(CommonUtils.getGBstr(activityHotel.mLightDataset.get(i).dev.devName))) {
				if (activityHotel.mLightDataset.get(i).bOnOff == 1) {
		        	viewHolder.iv.setImageResource(R.drawable.light_on);
				}else {
			        viewHolder.iv.setImageResource(R.drawable.light_off);
				}
			}
		}
        if (mList.get(position).equals("全开")) {
        	viewHolder.iv.setImageResource(R.drawable.dk);
		}
        if (mList.get(position).equals("全关")) {
        	viewHolder.iv.setImageResource(R.drawable.dqg);
		}
        
        viewHolder.tv.setText(mList.get(position));

        return convertView;
    }
    class ViewHolder {
        TextView tv;
        ImageView iv;
    }
}
