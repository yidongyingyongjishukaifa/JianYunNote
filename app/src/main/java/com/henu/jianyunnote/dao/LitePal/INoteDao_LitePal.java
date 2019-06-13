package com.henu.jianyunnote.dao.LitePal;

import com.henu.jianyunnote.model.LitePal.Note_LitePal;

public interface INoteDao_LitePal {
    void setNoteIsDeleteById(Integer id);

    Note_LitePal insert2Note(String note_title, String note_content, Integer notebook_id, Integer user_id, boolean isSync);

    Note_LitePal updateNoteTitleById(String note_title, Integer note_id, boolean isSync);

    void updateNoteBookByNote(Note_LitePal note);
}
