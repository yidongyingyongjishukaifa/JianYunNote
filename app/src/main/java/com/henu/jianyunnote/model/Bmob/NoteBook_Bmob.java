package com.henu.jianyunnote.model.Bmob;

import cn.bmob.v3.BmobObject;

public class NoteBook_Bmob extends BmobObject {
    private String userId;
    private String noteBookName;
    private Integer noteNumber;
    private Integer isDelete;

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
