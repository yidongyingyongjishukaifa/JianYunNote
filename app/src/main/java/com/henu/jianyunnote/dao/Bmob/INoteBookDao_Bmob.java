package com.henu.jianyunnote.dao.Bmob;

import com.henu.jianyunnote.model.Bmob.NoteBook_Bmob;

public interface INoteBookDao_Bmob {
    NoteBook_Bmob insert2NoteBook(String notebook_name, String user_id);
}
