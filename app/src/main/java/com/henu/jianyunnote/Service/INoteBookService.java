package com.henu.jianyunnote.Service;

import com.henu.jianyunnote.Model.NoteBook_LitePal;

public interface INoteBookService {
    void updateNoteBookById(Integer id);

    NoteBook_LitePal insert2NoteBook(String notebook_name, Integer user_id);
}
