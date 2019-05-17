package com.henu.jianyunnote.Util;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.henu.jianyunnote.R;

import java.util.List;
import java.util.Map;

public class MyAdapter extends BaseAdapter {
    private List<Map<String, Object>> data;
    private Context context;

    public MyAdapter(Context context, List<Map<String, Object>> data) {
        this.context = context;
        this.data = data;
    }

    @Override
    public int getCount() {
        return data.size();
    }

    @Override
    public Object getItem(int position) {
        return data.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        //如果view未被实例化过，缓存池中没有对应的缓存
        if (convertView == null) {
            viewHolder = new ViewHolder();
            convertView = LayoutInflater.from(context).inflate(R.layout.simple_item,parent,false);

            //对viewHolder的属性进行赋值
            viewHolder.NOTE_MESSAGE = convertView.findViewById(R.id.note_message);
            viewHolder.NOTE_UPDATE_TIME = convertView.findViewById(R.id.note_update_time);

            //通过setTag将convertView与viewHolder关联
            convertView.setTag(viewHolder);
        } else {//如果缓存池中有对应的view缓存，则直接通过getTag取出viewHolder
            viewHolder = (ViewHolder) convertView.getTag();
        }
        // 设置控件的数据
        viewHolder.NOTE_MESSAGE.setText(data.get(position).get("NOTE_MESSAGE").toString());
        viewHolder.NOTE_UPDATE_TIME.setText(data.get(position).get("NOTE_UPDATE_TIME").toString());

        return convertView;
    }

    private class ViewHolder {
        private TextView NOTE_MESSAGE;
        private TextView NOTE_UPDATE_TIME;
    }
}
