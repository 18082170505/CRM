package com.bjpowernode.crm.settings.service.impl;

import com.bjpowernode.crm.exception.UserLoginException;
import com.bjpowernode.crm.settings.dao.UserDao;
import com.bjpowernode.crm.settings.domain.User;
import com.bjpowernode.crm.settings.service.UserService;
import com.bjpowernode.crm.utils.DateTimeUtil;
import com.bjpowernode.crm.utils.SqlSessionUtil;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class UserServiceImpl implements UserService {
    private UserDao userDao = SqlSessionUtil.getSqlSession().getMapper(UserDao.class);

    @Override
    public User login(String loginAct, String loginPwd, String ip) throws UserLoginException {
        Map<String,String> map = new HashMap<>();
        map.put("loginAct",loginAct);
        map.put("loginPwd",loginPwd);
        User user = userDao.login(map);
        //若为空说明账号密码不在用户表中
        if (user == null){
            throw new UserLoginException("账号密码错误");
        }
        //如果程序执行到此处则说明账号密码正确
        //继续验证其他三项信息

        //验证失效时间
        String expireTime = user.getExpireTime();
        String currentTime = DateTimeUtil.getSysTime();
        if (expireTime.compareTo(currentTime) < 0){
            throw new UserLoginException("账号已失效");
        }
        //验证锁定状态
        String lockState = user.getLockState();
        if ("0".equals(lockState)){
            throw new UserLoginException("账号已锁定");
        }
        //验证ip地址
        String allowIps = user.getAllowIps();
        if (!allowIps.contains(ip)){
            throw new UserLoginException("ip地址受限");
        }
        return user;
    }

    @Override
    public List<User> getUserList() {
        List<User> userList = userDao.getUserList();
        return userList;
    }
}
