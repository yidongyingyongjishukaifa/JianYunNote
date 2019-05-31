package com.henu.jianyunnote.Dao;

import com.henu.jianyunnote.Model.Bmob.Note_Bmob;


public interface INoteDao_Bmob {
    void deleteNoteById(String objectId);

    Note_Bmob insert2Note(String note_title, String note_content, String notebook_id, String user_id);

}
