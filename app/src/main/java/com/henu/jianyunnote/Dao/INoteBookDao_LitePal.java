package com.henu.jianyunnote.Dao;

import com.henu.jianyunnote.Model.LitePal.NoteBook_LitePal;

public interface INoteBookDao_LitePal {
    void updateNoteBookById(Integer id);

    NoteBook_LitePal insert2NoteBook(String notebook_name, Integer user_id);
}
