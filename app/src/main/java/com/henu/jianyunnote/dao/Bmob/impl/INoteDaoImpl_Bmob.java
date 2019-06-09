package com.henu.jianyunnote.dao.Bmob.impl;

import com.henu.jianyunnote.model.Bmob.Note_Bmob;
import com.henu.jianyunnote.dao.Bmob.INoteDao_Bmob;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.CountListener;
import cn.bmob.v3.listener.SaveListener;
import cn.bmob.v3.listener.UpdateListener;

public class INoteDaoImpl_Bmob implements INoteDao_Bmob{

    @Override
    public void deleteNoteById(String objectId) {
        Note_Bmob note = new Note_Bmob();
        note.setIsDelete(1);
        note.update(objectId, new UpdateListener() {
            @Override
            public void done(BmobException e) {

            }
        });
    }

    @Override
    public Note_Bmob insert2Note(String note_title, String note_content, String notebook_id, String user_id) {
        final Note_Bmob note = new Note_Bmob();
        if (note_title != null) {
            if ("".equals(note_title)) {
                note.setTitle("未命名笔记");
            } else {
                note.setTitle(note_title);
            }
        }
        BmobQuery<Note_Bmob> query = new BmobQuery<>();
        query.count(Note_Bmob.class, new CountListener() {
            @Override
            public void done(Integer count, BmobException e) {
                if (e == null) {
                    note.setNote_id(String.valueOf(count + 1));
                }
            }
        });
        if (note_content != null) {
            note.setContent(note_content);
        }
        if (user_id != null) {
            note.setUserId(user_id);
        }
        if (notebook_id != null) {
            note.setNoteBookId(notebook_id);
        }
        note.setIsDelete(0);
        note.save(new SaveListener<String>() {
            @Override
            public void done(String s, BmobException e) {

            }
        });
        return  note;
    }
}

