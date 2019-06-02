package com.henu.jianyunnote.model.Bmob;

import cn.bmob.v3.BmobObject;

public class Users_Bmob extends BmobObject {

    private String Name;
    private String Password;
    private  String Email;
    private String SafePassword;

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public String getEmail() {
        return Email;
    }

    public void setEmail(String email) {
        Email = email;
    }

    public String getPassword() {
        return Password;
    }

    public void setPassword(String password) {
        Password = password;
    }


    public String getSafePassword() {
        return SafePassword;
    }

    public void setSafePassword(String safePassword) {
        SafePassword = safePassword;
    }

}
