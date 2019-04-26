package com.henu.jianyunnote.DataBase;

import org.litepal.crud.LitePalSupport;

import java.util.Date;

public class Note extends LitePalSupport {
    private int id;
    private int userid;
    private int notebookid;

    public int getUserid() {
        return userid;
    }

    public void setUserid(int userid) {
        this.userid = userid;
    }

    public int getNotebookid() {
        return notebookid;
    }

    public void setNotebookid(int notebookid) {
        this.notebookid = notebookid;
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

    public boolean isIsdelete() {
        return isdelete;
    }

    public void setIsdelete(boolean isdelete) {
        this.isdelete = isdelete;
    }

    public Date getCreatetime() {
        return createtime;
    }

    public void setCreatetime(Date createtime) {
        this.createtime = createtime;
    }

    public Date getUpdatetime() {
        return updatetime;
    }

    public void setUpdatetime(Date updatetime) {
        this.updatetime = updatetime;
    }

    private String title;
    private String content;
    private boolean isdelete;
    private Date createtime;
    private Date updatetime;

}
