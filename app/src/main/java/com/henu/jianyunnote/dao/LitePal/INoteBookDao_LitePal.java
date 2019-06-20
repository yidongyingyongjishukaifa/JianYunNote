package com.henu.jianyunnote.dao.LitePal;

import com.henu.jianyunnote.model.LitePal.NoteBook_LitePal;

public interface INoteBookDao_LitePal {
    void setNoteBookIsDeleteById(Integer notebook_id);

    NoteBook_LitePal insert2NoteBook(String notebook_name, String bmob_objectid, Integer user_id, boolean isSync);

    NoteBook_LitePal updateNoteBookNameById(String notebook_name, Integer notebook_id, boolean isSync);

    NoteBook_LitePal updateNoteBookById(String notebook_id, boolean isSync);

    void updateNoteByNoteBook(NoteBook_LitePal notebook);
}
