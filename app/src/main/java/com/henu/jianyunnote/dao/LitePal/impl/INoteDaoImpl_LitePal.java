package com.henu.jianyunnote.dao.LitePal.impl;

import com.henu.jianyunnote.model.LitePal.NoteBook_LitePal;
import com.henu.jianyunnote.model.LitePal.Note_LitePal;
import com.henu.jianyunnote.dao.LitePal.INoteDao_LitePal;

import org.litepal.LitePal;

import java.util.Date;
import java.util.List;

public class INoteDaoImpl_LitePal implements INoteDao_LitePal {
    @Override
    public void setNoteIsDeleteById(Integer id) {
        List<Note_LitePal> noteList = LitePal.where("id = ?", String.valueOf(id)).find(Note_LitePal.class);
        for (Note_LitePal note : noteList) {
            note.setIsDelete(1);
            note.setUpdateTime(new Date());
            note.save();
        }
    }

    @Override
    public Note_LitePal insert2Note(String note_title, String note_content, Integer notebook_id, Integer user_id, boolean isSync) {
        Note_LitePal note = new Note_LitePal();
        if (note_title != null) {
            if ("".equals(note_title)) {
                note.setTitle("无标题笔记");
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
        if (isSync) {
            note.setIsSync(1);
        }
        note.setCreateTime(new Date());
        note.setUpdateTime(new Date());
        note.setIsDelete(0);
        note.save();
        return note;
    }

    @Override
    public Note_LitePal updateNoteTitleById(String note_title, Integer note_id, boolean isSync) {
        List<Note_LitePal> noteList = LitePal.where("id = ?", String.valueOf(note_id)).find(Note_LitePal.class);
        Note_LitePal note = new Note_LitePal();
        for (Note_LitePal _note : noteList) {
            note = _note;
            if (note_title != null) {
                if ("".equals(note_title)) {
                    note.setTitle("无标题笔记");
                } else {
                    note.setTitle(note_title);
                }
            }
            if (isSync) {
                note.setIsSync(1);
            }
            _note.setUpdateTime(new Date());
            _note.save();
        }
        updateNoteBookByNote(note);
        return note;
    }

    @Override
    public void updateNoteBookByNote(Note_LitePal note) {
        List<NoteBook_LitePal> noteBookList = LitePal.where("id = ?", String.valueOf(note.getNoteBookId())).find(NoteBook_LitePal.class);
        for(NoteBook_LitePal noteBook_litePal : noteBookList){
            noteBook_litePal.setUpdateTime(note.getUpdateTime());
            noteBook_litePal.save();
        }
    }
}
