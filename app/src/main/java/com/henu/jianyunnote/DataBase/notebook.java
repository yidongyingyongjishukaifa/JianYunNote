package com.henu.jianyunnote.DataBase;

import org.litepal.crud.LitePalSupport;

import java.util.Date;

public class notebook extends LitePalSupport {
    private int id;
    private  int userid;
    private String notebookname;
    private int notenumber;

    public int getUserid() {
        return userid;
    }

    public void setUserid(int userid) {
        this.userid = userid;
    }

    public String getNotebookname() {
        return notebookname;
    }

    public void setNotebookname(String notebookname) {
        this.notebookname = notebookname;
    }

    public int getNotenumber() {
        return notenumber;
    }

    public void setNotenumber(int notenumber) {
        this.notenumber = notenumber;
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

    private boolean isdelete;
    private Date createtime;
    private Date updatetime;
}
