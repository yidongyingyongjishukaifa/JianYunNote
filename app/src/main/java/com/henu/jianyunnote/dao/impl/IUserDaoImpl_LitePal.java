package com.henu.jianyunnote.dao.impl;

import com.henu.jianyunnote.model.LitePal.User_LitePal;
import com.henu.jianyunnote.dao.IUserDao_LitePal;

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
