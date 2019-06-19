package com.henu.jianyunnote.util;

import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.daimajia.swipe.SimpleSwipeListener;
import com.daimajia.swipe.SwipeLayout;
import com.daimajia.swipe.adapters.BaseSwipeAdapter;
import com.henu.jianyunnote.activity.noteParttion.NoteParttionActivity;
import com.henu.jianyunnote.dao.LitePal.INoteBookDao_LitePal;
import com.henu.jianyunnote.dao.LitePal.IUserDao_LitePal;
import com.henu.jianyunnote.dao.LitePal.impl.INoteBookDaoImpl_LitePal;
import com.henu.jianyunnote.dao.LitePal.impl.IUserDaoImpl_LitePal;
import com.henu.jianyunnote.R;

import java.util.List;
import java.util.Map;

public class NoteBookAdapter extends BaseSwipeAdapter {
    private List<Map<String, Object>> mDatas;
    private Context mContext;
    private IUserDao_LitePal userService = new IUserDaoImpl_LitePal();
    private INoteBookDao_LitePal noteBookService = new INoteBookDaoImpl_LitePal();

    public NoteBookAdapter(Context context, List<Map<String, Object>> data) {
        this.mContext = context;
        this.mDatas = data;
    }

    @Override
    public int getCount() {
        return mDatas.size();
    }

    @Override
    public Object getItem(int position) {
        return mDatas.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public void fillValues(int position, View convertView) {
        final SwipeLayout sl = convertView.findViewById(getSwipeLayoutResourceId(position));
        final TextView delete = convertView.findViewById(R.id.delete);
        delete.setTag(position);
        ViewHolder viewHolder = new ViewHolder();
        //对viewHolder的属性进行赋值
        viewHolder.NOTEBOOK_MESSAGE = convertView.findViewById(R.id.notebook_message);
        viewHolder.NOTEBOOK_UPDATE_TIME = convertView.findViewById(R.id.notebook_update_time);
        viewHolder.IS_NOTEBOOK_SYNC = convertView.findViewById(R.id.is_notebook_sync);
        if (mDatas.get(position).get("IS_NOTEBOOK_SYNC") != null) {
            viewHolder.IS_NOTEBOOK_SYNC.setVisibility(View.VISIBLE);
            viewHolder.IS_NOTEBOOK_SYNC.setText("未同步");
        }
        //设置控件的数据
        viewHolder.NOTEBOOK_MESSAGE.setText(mDatas.get(position).get("NOTEBOOK_MESSAGE").toString());
        viewHolder.NOTEBOOK_UPDATE_TIME.setText(mDatas.get(position).get("NOTEBOOK_UPDATE_TIME").toString());

        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
                builder.setTitle("删除");
                builder.setMessage("真的要删除吗？");
                builder.setPositiveButton("是", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        int p = (Integer) delete.getTag();
                        mDatas.remove(p);
                        int id = NoteParttionActivity.local_notebooks_id[p];
                        noteBookService.setNoteBookIsDeleteById(id);
                        NoteParttionActivity.local_notebooks_id = ArrayUtil.deleteIdInArray(NoteParttionActivity.local_notebooks_id, p);
                        userService.updateUserByUser(NoteParttionActivity.current_user);
                        notifyDataSetChanged();
                        Toast.makeText(MyApplication.getContext(), "删除成功", Toast.LENGTH_SHORT).show();
                        sl.close();
                    }
                });
                builder.setNegativeButton("否", null);
                AlertDialog alertDialog = builder.create();
                alertDialog.show();
            }
        });
    }

    @Override
    public View generateView(int position, ViewGroup arg1) {
        View v = LayoutInflater.from(mContext).inflate(R.layout.notebook_listview_items, null);
        final SwipeLayout swipeLayout = v.findViewById(R.id.swipe);

        swipeLayout.addSwipeListener(new SimpleSwipeListener() {
            @Override
            public void onOpen(SwipeLayout layout) {//当隐藏的删除menu被打开的时候的回调函数
                YoYo.with(Techniques.Tada).duration(500).delay(100).playOn(layout.findViewById(R.id.trash));
            }
        });

        swipeLayout.setOnDoubleClickListener(new SwipeLayout.DoubleClickListener() {
            @Override
            public void onDoubleClick(SwipeLayout layout,
                                      boolean surface) {
                Toast.makeText(mContext, "DoubleClick", Toast.LENGTH_SHORT).show();
            }
        });

        return v;
    }

    @Override
    public int getSwipeLayoutResourceId(int position) {
        return R.id.swipe;
    }

    private class ViewHolder {
        private TextView NOTEBOOK_MESSAGE;
        private TextView NOTEBOOK_UPDATE_TIME;
        private TextView IS_NOTEBOOK_SYNC;
    }
}
