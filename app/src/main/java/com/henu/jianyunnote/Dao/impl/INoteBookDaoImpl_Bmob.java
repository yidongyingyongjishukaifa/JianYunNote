package com.henu.jianyunnote.Dao.impl;

import com.henu.jianyunnote.Dao.INoteBookDao_Bmob;
import com.henu.jianyunnote.Model.Bmob.NoteBook_Bmob;

import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.SaveListener;

public class INoteBookDaoImpl_Bmob implements INoteBookDao_Bmob {
    @Override
    public NoteBook_Bmob insert2NoteBook(String notebook_name, String user_id) {
        NoteBook_Bmob notebook = new NoteBook_Bmob();
        if (notebook_name != null) {
            if ("".equals(notebook_name)) {
                notebook.setNoteBookName("未命名笔记本");
            } else {
                notebook.setNoteBookName(notebook_name);
            }
        }
        notebook.setUserId(user_id);
        notebook.save(new SaveListener<String>() {
            @Override
            public void done(String s, BmobException e) {

            }
        });
        notebook.setIsDelete(0);
        return notebook;
    }
}
