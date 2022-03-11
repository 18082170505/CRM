package com.bjpowernode.crm.settings.service;

import com.bjpowernode.crm.exception.UserLoginException;
import com.bjpowernode.crm.settings.domain.User;

import java.util.List;

public interface UserService {
    User login(String loginAct, String loginPwd, String ip) throws UserLoginException;

    List<User> getUserList();
}
