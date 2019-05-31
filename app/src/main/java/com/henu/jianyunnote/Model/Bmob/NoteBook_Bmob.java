package com.henu.jianyunnote.Model.Bmob;

import cn.bmob.v3.BmobObject;

public class NoteBook_Bmob extends BmobObject {
    private String userId;
    private String noteBookName;
    private Integer noteNumber;
    private Integer isDelete;
    private String notebook_id;

    public String getNotebook_id() {
        return notebook_id;
    }

    public void setNotebook_id(String notebook_id) {
        this.notebook_id = notebook_id;
    }

    public Integer getNoteNumber() {
        return noteNumber;
    }

    public Integer getIsDelete() {
        return isDelete;
    }

    public void setNoteNumber(Integer noteNumber) {
        this.noteNumber = noteNumber;
    }

    public void setIsDelete(Integer isDelete) {
        this.isDelete = isDelete;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUserId() {
        return userId;
    }

    public String getNoteBookName() {
        return noteBookName;
    }

    public void setNoteBookName(String noteBookName) {
        this.noteBookName = noteBookName;
    }

    @Override
    public String toString() {
        return "NoteBook_Bmob{" +
                ", userId=" + userId +
                ", noteBookName='" + noteBookName + '\'' +
                ", noteNumber=" + noteNumber +
                ", isDelete=" + isDelete +
                '}';
    }
}
