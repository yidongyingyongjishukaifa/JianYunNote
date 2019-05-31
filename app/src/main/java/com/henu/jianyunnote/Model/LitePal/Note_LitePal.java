package com.henu.jianyunnote.Model.LitePal;

import org.litepal.crud.LitePalSupport;

import java.io.Serializable;
import java.util.Date;

public class Note_LitePal extends LitePalSupport implements Serializable {
    private int id;
    private int userId;
    private int noteBookId;
    private String title;
    private String content;
    private int isDelete;
    private Date createTime;
    private Date updateTime;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
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

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
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
                "id=" + id +
                ", userId=" + userId +
                ", noteBookId=" + noteBookId +
                ", title='" + title + '\'' +
                ", content='" + content + '\'' +
                ", isDelete=" + isDelete +
                ", createTime=" + createTime +
                ", updateTime=" + updateTime +
                '}';
    }
}
