package com.henu.jianyunnote.Dao.impl;

import com.henu.jianyunnote.Model.LitePal.User_LitePal;
import com.henu.jianyunnote.Dao.IUserDao_LitePal;

import java.util.Date;

public  class IUserDaoImpl_LitePal implements IUserDao_LitePal {
    @Override
    public void updateUserByUser(User_LitePal user) {
        if (user != null) {
            user.setUpdateTime(new Date());
            user.save();
        }
    }
}
