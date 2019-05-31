package com.henu.jianyunnote.Dao.impl;

import com.henu.jianyunnote.Model.LitePal.Note_LitePal;
import com.henu.jianyunnote.Dao.INoteDao_LitePal;

import org.litepal.LitePal;

import java.util.Date;
import java.util.List;

public class INoteDaoImpl_LitePal implements INoteDao_LitePal {
    @Override
    public void updateNoteById(Integer id) {
        List<Note_LitePal> noteList = LitePal.where("id = ?", String.valueOf(id)).find(Note_LitePal.class);
        for (Note_LitePal note : noteList) {
            note.setIsDelete(1);
            note.setUpdateTime(new Date());
            note.save();
        }
    }

    @Override
    public Note_LitePal insert2Note(String note_title, String note_content, Integer notebook_id, Integer user_id) {
        Note_LitePal note = new Note_LitePal();
        if (note_title != null) {
            if ("".equals(note_title)) {
                note.setTitle("未命名笔记");
            } else {
                note.setTitle(note_title);
            }
        }
        if (note_content != null) {
            note.setContent(note_content);
        }
        if (user_id != null) {
            note.setUserId(user_id);
        }
        if (notebook_id != null) {
            note.setNoteBookId(notebook_id);
        }
        note.setCreateTime(new Date());
        note.setUpdateTime(new Date());
        note.setIsDelete(0);
        note.save();
        return note;
    }

}
