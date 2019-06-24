package com.henu.jianyunnote.util;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Handler;
import android.os.Message;
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
import com.henu.jianyunnote.R;
import com.henu.jianyunnote.activity.noteParttion.NoteParttionActivity;
import com.henu.jianyunnote.activity.noteTrash.NoteTrashActivity;
import com.henu.jianyunnote.dao.LitePal.INoteDao_LitePal;
import com.henu.jianyunnote.dao.LitePal.IUserDao_LitePal;
import com.henu.jianyunnote.dao.LitePal.impl.INoteDaoImpl_LitePal;
import com.henu.jianyunnote.dao.LitePal.impl.IUserDaoImpl_LitePal;
import com.henu.jianyunnote.model.Bmob.Note_Bmob;
import com.henu.jianyunnote.model.LitePal.Note_LitePal;

import org.litepal.LitePal;

import java.util.List;
import java.util.Map;

import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.UpdateListener;

public class NoteTrashAdapter extends BaseSwipeAdapter {
    private List<Map<String, Object>> mDatas;
    private Context mContext;
    private IUserDao_LitePal userService = new IUserDaoImpl_LitePal();
    private INoteDao_LitePal noteService = new INoteDaoImpl_LitePal();
    public Handler handler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            switch (msg.what) {
                case 0:

                    Toast.makeText(MyApplication.getContext(), "彻底删除成功", Toast.LENGTH_SHORT).show();
                    notifyDataSetChanged();
                    break;
                default:
                    //do something
                    break;
            }
            return false;
        }
    });

    public NoteTrashAdapter(Context context, List<Map<String, Object>> data) {
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
        final TextView delete = convertView.findViewById(R.id.note_delete);
        delete.setTag(position);
        ViewHolder viewHolder = new ViewHolder();
        //对viewHolder的属性进行赋值
        viewHolder.NOTE_MESSAGE = convertView.findViewById(R.id.note_message);
        viewHolder.NOTE_UPDATE_TIME = convertView.findViewById(R.id.note_update_time);
        viewHolder.IS_NOTE_SYNC = convertView.findViewById(R.id.is_note_sync);
        if (mDatas.get(position).get("IS_NOTE_SYNC") != null) {
            viewHolder.IS_NOTE_SYNC.setVisibility(View.VISIBLE);
            viewHolder.IS_NOTE_SYNC.setText("未同步");
        }
        //设置控件的数据
        viewHolder.NOTE_MESSAGE.setText(mDatas.get(position).get("NOTE_MESSAGE").toString());
        viewHolder.NOTE_UPDATE_TIME.setText(mDatas.get(position).get("NOTE_UPDATE_TIME").toString());
        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
                builder.setTitle("彻底删除");
                builder.setMessage("真的要删除吗？");
                builder.setPositiveButton("是", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        NoteTrashActivity.flag = true;
                        int p = (Integer) delete.getTag();
                        mDatas.remove(p);
                        int id = NoteTrashActivity.local_notes_id[p];
                        noteService.setNoteIsDeleteById(id);
                        userService.updateUserByUser(NoteParttionActivity.current_user);
                        String note_id = String.valueOf(NoteTrashActivity.local_notes_id[p]);
                        NoteTrashActivity.local_notes_id = ArrayUtil.deleteIdInArray(NoteTrashActivity.local_notes_id, p);
                        List<Note_LitePal> noteList = LitePal.where("id = ?", note_id).find(Note_LitePal.class);
                        for (final Note_LitePal note_litePal : noteList) {
                            if (note_litePal.getBmob_note_id() != null) {
                                String bmob_note_objectId = note_litePal.getBmob_note_id();
                                Note_Bmob note_bmob = new Note_Bmob();
                                note_bmob.setObjectId(bmob_note_objectId);
                                note_bmob.delete(new UpdateListener() {
                                    @Override
                                    public void done(BmobException e) {
                                        if (e == null) {
                                            LitePal.delete(Note_LitePal.class, note_litePal.getId());
                                            handler.sendEmptyMessage(0);
                                        }
                                    }
                                });
                            } else {
                                LitePal.delete(Note_LitePal.class, note_litePal.getId());
                                handler.sendEmptyMessage(0);
                            }
                        }
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
        View v = LayoutInflater.from(mContext).inflate(R.layout.note_trash_listview_items, null);
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
        private TextView IS_NOTE_SYNC;
    }
}
