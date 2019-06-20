package com.henu.jianyunnote.dao.LitePal.impl;

import com.henu.jianyunnote.model.LitePal.NoteBook_LitePal;
import com.henu.jianyunnote.model.LitePal.Note_LitePal;
import com.henu.jianyunnote.dao.LitePal.INoteBookDao_LitePal;
import com.henu.jianyunnote.util.Const;

import org.litepal.LitePal;

import java.util.Date;
import java.util.List;

public class INoteBookDaoImpl_LitePal implements INoteBookDao_LitePal {
    @Override
    public void setNoteBookIsDeleteById(Integer notebook_id) {
        List<Note_LitePal> noteList = LitePal.where("noteBookId = ?", String.valueOf(notebook_id)).find(Note_LitePal.class);
        for (Note_LitePal note : noteList) {
            note.setUpdateTime(new Date());
            note.setIsChange(Integer.parseInt(Const.ISCHANGE));
            note.setIsDelete(Integer.parseInt(Const.ISDELETE));
            note.save();
        }
        List<NoteBook_LitePal> noteBookList = LitePal.where("id = ?", String.valueOf(notebook_id)).find(NoteBook_LitePal.class);
        for (NoteBook_LitePal noteBook : noteBookList) {
            noteBook.setIsDelete(Integer.parseInt(Const.ISDELETE));
            noteBook.setIsChange(Integer.parseInt(Const.ISCHANGE));
            noteBook.setUpdateTime(new Date());
            noteBook.save();
        }
    }

    @Override
    public NoteBook_LitePal insert2NoteBook(String notebook_name, String bmob_objectid, Integer user_id, boolean isSync) {
        NoteBook_LitePal notebook = new NoteBook_LitePal();
        if (notebook_name != null) {
            notebook.setUserId(user_id);
            if ("".equals(notebook_name)) {
                notebook.setNoteBookName("无标题笔记本");
            } else {
                notebook.setNoteBookName(notebook_name);
            }
        }
        if (isSync) {
            notebook.setIsSync(Integer.parseInt(Const.NEEDSYNC));
        }
        if (bmob_objectid != null) {
            notebook.setBmob_notebook_id(bmob_objectid);
        }
        notebook.setIsDownload(Integer.parseInt(Const.ISDOWNLOAD));
        notebook.setIsChange(Integer.parseInt(Const.ISCHANGE));
        notebook.setCreateTime(new Date());
        notebook.setUpdateTime(new Date());
        notebook.setIsDelete(Integer.parseInt(Const.NOTDELETE));
        notebook.setNoteNumber(0);
        notebook.save();
        return notebook;
    }

    @Override
    public NoteBook_LitePal updateNoteBookNameById(String notebook_name, Integer notebook_id, boolean isSync) {
        List<NoteBook_LitePal> noteBookList = LitePal.where("id = ?", String.valueOf(notebook_id)).find(NoteBook_LitePal.class);
        NoteBook_LitePal notebook = new NoteBook_LitePal();
        for (NoteBook_LitePal noteBook : noteBookList) {
            notebook = noteBook;
            if (notebook_name != null) {
                if ("".equals(notebook_name)) {
                    notebook.setNoteBookName("无标题笔记本");
                } else {
                    notebook.setNoteBookName(notebook_name);
                }
            }
            if (isSync) {
                notebook.setIsSync(Integer.parseInt(Const.NEEDSYNC));
            }
            notebook.setIsChange(Integer.parseInt(Const.ISCHANGE));
            notebook.setUpdateTime(new Date());
            noteBook.save();
        }
        updateNoteByNoteBook(notebook);
        return notebook;
    }

    @Override
    public NoteBook_LitePal updateNoteBookById(String notebook_id, boolean isSync) {
        List<NoteBook_LitePal> noteBookList = LitePal.where("id = ?", notebook_id).find(NoteBook_LitePal.class);
        List<Note_LitePal> noteList = LitePal.where("noteBookId = ? and isDelete = ?", notebook_id, "0").find(Note_LitePal.class);
        int num = noteList.size();
        List<Note_LitePal> noteList2 = LitePal.where("noteBookId = ? and isDelete = ?", notebook_id, "0").order("updateTime desc").limit(1).find(Note_LitePal.class);
        Note_LitePal note_litePal = new Note_LitePal();
        for (Note_LitePal note : noteList2) {
            note_litePal = note;
        }
        NoteBook_LitePal notebook = new NoteBook_LitePal();
        for (NoteBook_LitePal noteBook : noteBookList) {
            notebook = noteBook;
            noteBook.setUpdateTime(note_litePal.getUpdateTime());
            noteBook.setNoteNumber(num);
            if (isSync) {
                notebook.setIsSync(Integer.parseInt(Const.NEEDSYNC));
            }
            notebook.setIsChange(Integer.parseInt(Const.ISCHANGE));
            noteBook.save();
        }
        return notebook;
    }

    @Override
    public void updateNoteByNoteBook(NoteBook_LitePal notebook) {
        List<Note_LitePal> noteList = LitePal.where("noteBookId = ? and isDelete = ?", String.valueOf(notebook.getId()), "0").find(Note_LitePal.class);
        for (Note_LitePal note : noteList) {
            note.setUpdateTime(notebook.getUpdateTime());
            note.setIsChange(Integer.parseInt(Const.ISCHANGE));
            note.save();
        }
    }
}
