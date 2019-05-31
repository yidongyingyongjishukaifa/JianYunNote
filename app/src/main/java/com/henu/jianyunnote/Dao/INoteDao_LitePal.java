package com.henu.jianyunnote.Dao;

import com.henu.jianyunnote.Model.LitePal.Note_LitePal;

public interface INoteDao_LitePal {
    void updateNoteById(Integer id);

    Note_LitePal insert2Note(String note_title, String note_content, Integer notebook_id, Integer user_id);

}
