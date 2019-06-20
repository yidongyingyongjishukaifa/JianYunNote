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
}

