package com.henu.jianyunnote.Dao;

import com.henu.jianyunnote.Model.Bmob.NoteBook_Bmob;

public interface INoteBookDao_Bmob {
    NoteBook_Bmob insert2NoteBook(String notebook_name, String user_id);
}
