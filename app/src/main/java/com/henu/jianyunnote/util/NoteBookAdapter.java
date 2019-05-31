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
import com.henu.jianyunnote.Dao.INoteBookDao_LitePal;
import com.henu.jianyunnote.Dao.INoteDao_LitePal;
import com.henu.jianyunnote.Dao.IUserDao_LitePal;
import com.henu.jianyunnote.Dao.impl.INoteBookDaoImpl_LitePal;
import com.henu.jianyunnote.Dao.impl.INoteDaoImpl_LitePal;
import com.henu.jianyunnote.Dao.impl.IUserDaoImpl_LitePal;
import com.henu.jianyunnote.controller.noteParttion.NoteParttionController;
import com.henu.jianyunnote.R;

import java.util.List;
import java.util.Map;

public class NoteBookAdapter extends BaseSwipeAdapter {
    private List<Map<String, Object>> mDatas;
    private Context mContext;
    private int pos;
    private IUserDao_LitePal userService = new IUserDaoImpl_LitePal();
    private INoteBookDao_LitePal noteBookService = new INoteBookDaoImpl_LitePal();
    private INoteDao_LitePal noteService = new INoteDaoImpl_LitePal();

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
        viewHolder.NOTE_MESSAGE = convertView.findViewById(R.id.note_message);
        viewHolder.NOTE_UPDATE_TIME = convertView.findViewById(R.id.note_update_time);
        //设置控件的数据
        viewHolder.NOTE_MESSAGE.setText(mDatas.get(position).get("NOTE_MESSAGE").toString());
        viewHolder.NOTE_UPDATE_TIME.setTextSize(13);
        viewHolder.NOTE_UPDATE_TIME.setText(mDatas.get(position).get("NOTE_UPDATE_TIME").toString());
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
                        int id;
                        if (p < NoteParttionController.notebooks_count) {
                            id = NoteParttionController.local_notebooks_id[p];
                            noteBookService.updateNoteBookById(id);
                            NoteParttionController.local_notebooks_id = ArrayUtil.deleteIdInArray(NoteParttionController.local_notebooks_id, p);
                            NoteParttionController.notebooks_count = NoteParttionController.local_notebooks_id.length;
                        } else {
                            id = NoteParttionController.local_notes_id[p - NoteParttionController.notebooks_count];
                            noteService.updateNoteById(id);
                            NoteParttionController.local_notes_id = ArrayUtil.deleteIdInArray(NoteParttionController.local_notes_id, p- NoteParttionController.notebooks_count);
                        }
                        userService.updateUserByUser(NoteParttionController.current_user);
                        notifyDataSetChanged();
                        Toast.makeText(MyApplication.getContext(), "删除成功", Toast.LENGTH_SHORT).show();
                        sl.close();
                    }
                });
                builder.setNegativeButton("否",null);
                AlertDialog alertDialog = builder.create();
                alertDialog.show();
            }
        });
    }

    @Override
    public View generateView(int position, ViewGroup arg1) {
        View v = LayoutInflater.from(mContext).inflate(R.layout.listview_items, null);
        pos = position;
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
        private TextView NOTE_MESSAGE;
        private TextView NOTE_UPDATE_TIME;
    }
}
