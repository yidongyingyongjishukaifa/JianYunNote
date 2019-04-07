package com.henu.jianyunnote.Parttion;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.henu.jianyunnote.Parttion.Parttion;
import com.henu.jianyunnote.R;

import java.util.List;

public class ParttionAdapter extends ArrayAdapter<Parttion> {

    private int resouseId;


    public ParttionAdapter( Context context, int textViewResourceId, List<Parttion> objects) {
        super( context, textViewResourceId, objects );
        resouseId = textViewResourceId;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent){
        Parttion parttion = getItem( position );
        View view;
        ViewHolder viewHolder;
        if(convertView == null){
            view = LayoutInflater.from( getContext() ).inflate( resouseId,parent,false );
            viewHolder = new ViewHolder();
            viewHolder.parttionTitle = view.findViewById( R.id.parttion_title );
            view.setTag( viewHolder );
        }
        else {
            view = convertView;
            viewHolder = (ViewHolder) view.getTag();
        }
        viewHolder.parttionTitle.setText( parttion.getparttionTitle() );

        return view;
    }

    class ViewHolder{
        TextView parttionTitle;
    }
}
