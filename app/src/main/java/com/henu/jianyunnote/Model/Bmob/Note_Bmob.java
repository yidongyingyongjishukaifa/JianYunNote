package com.henu.jianyunnote.Model.Bmob;

import cn.bmob.v3.BmobObject;

public class Note_Bmob extends BmobObject {
    private String userId;
    private int noteBookId;
    private String title;
    private String content;
    private int isDelete;

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUserId() {
        return userId;
    }

    public int getNoteBookId() {
        return noteBookId;
    }

    public void setNoteBookId(int noteBookId) {
        this.noteBookId = noteBookId;
    }

    public int getIsDelete() {
        return isDelete;
    }

    public void setIsDelete(int isDelete) {
        this.isDelete = isDelete;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    @Override
    public String toString() {
        return "Note_Bmob{" +
                ", userId=" + userId +
                ", noteBookId=" + noteBookId +
                ", title='" + title + '\'' +
                ", content='" + content + '\'' +
                ", isDelete=" + isDelete +
                '}';
    }
}
