package com.example.smarthouse.hotel;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

import com.example.smarthouse.R;

/**
 * Created by maibenben on 2016/4/25.
 */
public class MyAdapter extends BaseAdapter{

	List<String>mList_zi;
    List<String>mList_ying;
    Context mcontext;
    public MyAdapter(List<String>mList_zi,List<String>mList_ying, Context context){
        this.mList_zi = mList_zi;
        this.mList_ying =mList_ying;
        mcontext = context;
    }

    @Override
    public int getCount() {
        return mList_zi.size();
    }

    @Override
    public Object getItem(int position) {
        return mList_zi.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        ViewHolder viewHolder;
        if (convertView == null){
            convertView = LayoutInflater.from(mcontext).inflate(R.layout.air_gridview_item,null);
        	
            viewHolder = new ViewHolder();
            viewHolder.tv1 = (TextView)convertView.findViewById(R.id.item_textview);
            viewHolder.tv2 = (TextView)convertView.findViewById(R.id.item_textview_ying);
            
            convertView.setTag(viewHolder);
        }else{
            viewHolder = (ViewHolder) convertView.getTag();
        }

        viewHolder.tv1.setText(mList_zi.get(position));
        viewHolder.tv2.setText(mList_ying.get(position));
        return convertView;
    }
    class ViewHolder {
        TextView tv1,tv2;
    }
}
