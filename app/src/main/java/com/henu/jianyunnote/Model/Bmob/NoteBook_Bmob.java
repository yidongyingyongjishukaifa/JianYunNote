package com.henu.jianyunnote.Model.Bmob;

import cn.bmob.v3.BmobObject;

public class NoteBook_Bmob extends BmobObject {
    private String userId;
    private String noteBookName;
    private int noteNumber;
    private int isDelete;

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

    public int getNoteNumber() {
        return noteNumber;
    }

    public void setNoteNumber(int noteNumber) {
        this.noteNumber = noteNumber;
    }

    public int getIsDelete() {
        return isDelete;
    }

    public void setIsDelete(int isDelete) {
        this.isDelete = isDelete;
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
