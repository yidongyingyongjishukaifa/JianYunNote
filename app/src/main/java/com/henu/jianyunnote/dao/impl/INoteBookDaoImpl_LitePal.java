package com.henu.jianyunnote.dao.impl;

import com.henu.jianyunnote.model.LitePal.NoteBook_LitePal;
import com.henu.jianyunnote.model.LitePal.Note_LitePal;
import com.henu.jianyunnote.dao.INoteBookDao_LitePal;

import org.litepal.LitePal;

import java.util.Date;
import java.util.List;

public class INoteBookDaoImpl_LitePal implements INoteBookDao_LitePal {
    @Override
    public void updateNoteBookById(Integer id) {
        List<Note_LitePal> noteList = LitePal.where("noteBookId = ?", String.valueOf(id)).find(Note_LitePal.class);
        for (Note_LitePal note : noteList) {
            note.setUpdateTime(new Date());
            note.setIsDelete(1);
            note.save();
        }
        List<NoteBook_LitePal> noteBookList = LitePal.where("id = ?", String.valueOf(id)).find(NoteBook_LitePal.class);
        for (NoteBook_LitePal noteBook : noteBookList) {
            noteBook.setIsDelete(1);
            noteBook.setUpdateTime(new Date());
            noteBook.save();
        }
    }

    @Override
    public NoteBook_LitePal insert2NoteBook(String notebook_name, Integer user_id) {
        NoteBook_LitePal notebook = new NoteBook_LitePal();
        if (notebook_name != null) {
            notebook.setUserId(user_id);
            if ("".equals(notebook_name)) {
                notebook.setNoteBookName("未命名笔记本");
            } else {
                notebook.setNoteBookName(notebook_name);
            }
        }
        notebook.setCreateTime(new Date());
        notebook.setUpdateTime(new Date());
        notebook.setIsDelete(0);
        notebook.save();
        return notebook;
    }
}
