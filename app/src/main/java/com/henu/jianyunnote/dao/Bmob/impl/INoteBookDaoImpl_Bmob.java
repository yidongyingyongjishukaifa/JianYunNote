package com.henu.jianyunnote.dao.Bmob.impl;

import com.henu.jianyunnote.dao.Bmob.INoteBookDao_Bmob;
import com.henu.jianyunnote.model.Bmob.NoteBook_Bmob;

import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.SaveListener;

public class INoteBookDaoImpl_Bmob implements INoteBookDao_Bmob {

    @Override
    public NoteBook_Bmob insert2NoteBook(String notebook_name, String user_id) {
        final NoteBook_Bmob notebook = new NoteBook_Bmob();
        if (notebook_name != null) {
            if ("".equals(notebook_name)) {
                notebook.setNoteBookName("无标题笔记本");
            } else {
                notebook.setNoteBookName(notebook_name);
            }
        }
        notebook.setUserId(user_id);
        notebook.setIsDelete(0);
        notebook.save(new SaveListener<String>() {
            @Override
            public void done(String s, BmobException e) {

            }
        });
        return notebook;
    }
}