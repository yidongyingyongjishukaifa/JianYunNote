package com.henu.jianyunnote.dao.LitePal.impl;

import com.henu.jianyunnote.model.LitePal.NoteBook_LitePal;
import com.henu.jianyunnote.model.LitePal.Note_LitePal;
import com.henu.jianyunnote.dao.LitePal.INoteDao_LitePal;
import com.henu.jianyunnote.util.Const;

import org.litepal.LitePal;

import java.util.Date;
import java.util.List;

public class INoteDaoImpl_LitePal implements INoteDao_LitePal {

    @Override
    public void setNoteIsDeleteById(Integer id) {
        List<Note_LitePal> noteList = LitePal.where("id = ?", String.valueOf(id)).find(Note_LitePal.class);
        for (Note_LitePal note : noteList) {
            note.setIsDelete(Integer.parseInt(Const.ISDELETE));
            note.setIsChange(Integer.parseInt(Const.ISCHANGE));
            note.setUpdateTime(new Date());
            note.save();
        }
    }

    @Override
    public void setNoteUnDeleteById(Integer id) {
        List<Note_LitePal> noteList = LitePal.where("id = ?", String.valueOf(id)).find(Note_LitePal.class);
        for (Note_LitePal note : noteList) {
            int notebook_id = note.getNoteBookId();
            List<NoteBook_LitePal> noteBookList = LitePal.where("id = ?", String.valueOf(notebook_id)).find(NoteBook_LitePal.class);
            if (noteBookList == null || noteBookList.size() == 0) {
                NoteBook_LitePal notebook = new NoteBook_LitePal();
                notebook.setUserId(note.getUserId());
                notebook.setNoteBookName("无标题笔记本");
                notebook.setCreateTime(new Date());
                notebook.setUpdateTime(new Date());
                notebook.setIsDelete(Integer.parseInt(Const.NOTDELETE));
                notebook.setNoteNumber(1);
                notebook.save();
                note.setNoteBookId(notebook.getId());
            }
            note.setIsDelete(Integer.parseInt(Const.NOTDELETE));
            note.setIsChange(Integer.parseInt(Const.ISCHANGE));
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
            note.setIsSync(Integer.parseInt(Const.NEEDSYNC));
        }
        note.setIsDownload(Integer.parseInt(Const.ISDOWNLOAD));
        note.setIsChange(Integer.parseInt(Const.ISCHANGE));
        note.setCreateTime(new Date());
        note.setUpdateTime(new Date());
        note.setIsDelete(Integer.parseInt(Const.NOTDELETE));
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
                note.setIsSync(Integer.parseInt(Const.NEEDSYNC));
            }
            note.setIsChange(Integer.parseInt(Const.ISCHANGE));
            note.setUpdateTime(new Date());
            note.save();
        }
        updateNoteBookByNote(note);
        return note;
    }

    @Override
    public void updateNoteBookByNote(Note_LitePal note) {
        List<NoteBook_LitePal> noteBookList = LitePal.where("id = ?", String.valueOf(note.getNoteBookId())).find(NoteBook_LitePal.class);
        for (NoteBook_LitePal noteBook_litePal : noteBookList) {
            noteBook_litePal.setUpdateTime(note.getUpdateTime());
            noteBook_litePal.setIsChange(Integer.parseInt(Const.ISCHANGE));
            noteBook_litePal.save();
        }
    }
}
