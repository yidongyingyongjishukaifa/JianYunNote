package com.henu.jianyunnote.Page;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.henu.jianyunnote.R;

import java.util.List;

public class PageAdapter extends ArrayAdapter<Page> {
    private int resouseId;


    public PageAdapter(Context context, int textViewResourceId, List<Page> objects) {
        super( context, textViewResourceId, objects );
        resouseId = textViewResourceId;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent){
        Page page = getItem( position );
        View view;
        PageAdapter.ViewHolder viewHolder;
        if(convertView == null){
            view = LayoutInflater.from( getContext() ).inflate( resouseId,parent,false );
            viewHolder = new PageAdapter.ViewHolder();
            viewHolder.pageTitle = view.findViewById( R.id.page_title );
            view.setTag( viewHolder );
        }
        else {
            view = convertView;
            viewHolder = (PageAdapter.ViewHolder) view.getTag();
        }
        viewHolder.pageTitle.setText( page.getpageTitle() );

        return view;
    }

    class ViewHolder{
        TextView pageTitle;
    }
}
