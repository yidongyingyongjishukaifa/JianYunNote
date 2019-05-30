package com.henu.jianyunnote.Service.impl;

import com.henu.jianyunnote.Model.User_LitePal;
import com.henu.jianyunnote.Service.IUserService;

import java.util.Date;

public  class IUserServiceImpl implements IUserService {
    @Override
    public void updateUserByUser(User_LitePal user) {
        if (user != null) {
            user.setUpdateTime(new Date());
            user.save();
        }
    }
}
