package com.henu.jianyunnote.dao;

import com.henu.jianyunnote.model.LitePal.Note_LitePal;

public interface INoteDao_LitePal {
    void updateNoteById(Integer id);

    Note_LitePal insert2Note(String note_title, String note_content, Integer notebook_id, Integer user_id);

}
