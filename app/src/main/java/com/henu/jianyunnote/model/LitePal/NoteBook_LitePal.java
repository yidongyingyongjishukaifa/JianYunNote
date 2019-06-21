package com.henu.jianyunnote.model.LitePal;

import org.litepal.crud.LitePalSupport;

import java.io.Serializable;
import java.util.Date;

public class NoteBook_LitePal extends LitePalSupport implements Serializable {
    private int id;
    private String bmob_notebook_id;
    private String bmob_user_id;
    private int userId;
    private String noteBookName;
    private int noteNumber;
    private int isDelete;
    private int isChange;
    private int isDownload;
    private Date createTime;
    private Date updateTime;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getIsChange() {
        return isChange;
    }

    public void setIsChange(int isChange) {
        this.isChange = isChange;
    }

    public int getIsDownload() {
        return isDownload;
    }

    public void setIsDownload(int isDownload) {
        this.isDownload = isDownload;
    }

    public String getBmob_user_id() {
        return bmob_user_id;
    }

    public void setBmob_user_id(String bmob_user_id) {
        this.bmob_user_id = bmob_user_id;
    }

    public String getBmob_notebook_id() {
        return bmob_notebook_id;
    }

    public void setBmob_notebook_id(String bmob_notebook_id) {
        this.bmob_notebook_id = bmob_notebook_id;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
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


    @Override
    public String toString() {
        return "NoteBook_LitePal{" +
                "id=" + id +
                ", bmob_notebook_id='" + bmob_notebook_id + '\'' +
                ", bmob_user_id='" + bmob_user_id + '\'' +
                ", userId=" + userId +
                ", noteBookName='" + noteBookName + '\'' +
                ", noteNumber=" + noteNumber +
                ", isDelete=" + isDelete +
                ", isChange=" + isChange +
                ", isDownload=" + isDownload +
                ", createTime=" + createTime +
                ", updateTime=" + updateTime +
                '}';
    }
}
