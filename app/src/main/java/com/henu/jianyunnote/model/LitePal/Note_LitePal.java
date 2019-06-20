package com.henu.jianyunnote.model.LitePal;

import org.litepal.crud.LitePalSupport;

import java.io.Serializable;
import java.util.Date;

public class Note_LitePal extends LitePalSupport implements Serializable {
    private int id;
    private String bmob_note_id;
    private String bmob_notebook_id;
    private String bmob_user_id;
    private int userId;
    private int noteBookId;
    private String title;
    private String content;
    private int isDelete;
    private int isChange;
    private int isSync;
    private int isDownload;
    private Date createTime;
    private Date updateTime;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getBmob_notebook_id() {
        return bmob_notebook_id;
    }

    public void setBmob_notebook_id(String bmob_notebook_id) {
        this.bmob_notebook_id = bmob_notebook_id;
    }

    public int getIsChange() {
        return isChange;
    }

    public void setIsChange(int isChange) {
        this.isChange = isChange;
    }

    public int getIsSync() {
        return isSync;
    }

    public void setIsSync(int isSync) {
        this.isSync = isSync;
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

    public String getBmob_note_id() {
        return bmob_note_id;
    }

    public void setBmob_note_id(String bmob_note_id) {
        this.bmob_note_id = bmob_note_id;
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
        return "Note_LitePal{" +
                "id=" + id +
                ", bmob_note_id='" + bmob_note_id + '\'' +
                ", bmob_notebook_id='" + bmob_notebook_id + '\'' +
                ", bmob_user_id='" + bmob_user_id + '\'' +
                ", userId=" + userId +
                ", noteBookId=" + noteBookId +
                ", title='" + title + '\'' +
                ", content='" + content + '\'' +
                ", isDelete=" + isDelete +
                ", isChange=" + isChange +
                ", isSync=" + isSync +
                ", isDownload=" + isDownload +
                ", createTime=" + createTime +
                ", updateTime=" + updateTime +
                '}';
    }
}
