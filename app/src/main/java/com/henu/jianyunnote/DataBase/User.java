   

package com.henu.jianyunnote.DataBase;



import org.litepal.crud.LitePalSupport;



import java.util.Date;



public class Note extends LitePalSupport {

    private int id;

    private int userid;

    private int notebookname;



    public int getUserid() {

        return userid;

    }



    public void setUserid(int userid) {

        this.userid = userid;

    }



    public int getNotebookid() {

        return notebookid;

    }



    public void setNotebookname(int notebookname) {

        this.notebookname = notebookname;

    }



    public String getBooknumber() {

        return booknumber;

    }



    public void setBooknumber(String booknumber) {

        this.booknumber = booknumber;

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



    private String booknumber;

    private int notenumber;

    private boolean isdelete;

    private Date createtime;

    private Date updatetime;



}
