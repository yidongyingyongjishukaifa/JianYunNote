package com.henu.jianyunnote.Service;

import com.henu.jianyunnote.Model.Note_LitePal;

public interface INoteService {
    void updateNoteById(Integer id);

    Note_LitePal insert2Note(String note_title, String note_content, Integer notebook_id, Integer user_id);

}
