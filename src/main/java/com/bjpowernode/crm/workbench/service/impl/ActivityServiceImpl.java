package com.bjpowernode.crm.workbench.service.impl;

import com.bjpowernode.crm.settings.dao.UserDao;
import com.bjpowernode.crm.settings.domain.User;
import com.bjpowernode.crm.utils.SqlSessionUtil;
import com.bjpowernode.crm.vo.PaginationVO;
import com.bjpowernode.crm.workbench.dao.ActivityDao;
import com.bjpowernode.crm.workbench.dao.ActivityRemarkDao;
import com.bjpowernode.crm.workbench.domain.Activity;
import com.bjpowernode.crm.workbench.domain.ActivityRemark;
import com.bjpowernode.crm.workbench.service.ActivityService;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ActivityServiceImpl implements ActivityService {
    private ActivityDao activityDao = SqlSessionUtil.getSqlSession().getMapper(ActivityDao.class);
    private ActivityRemarkDao activityRemarkDao = SqlSessionUtil.getSqlSession().getMapper(ActivityRemarkDao.class);
    private UserDao userDao = SqlSessionUtil.getSqlSession().getMapper(UserDao.class);

    @Override
    public boolean save(Activity a) {
        boolean flag = true;
        int count = activityDao.save(a);
        if (count != 1){
            flag = false;
        }
        return flag;
    }

    @Override
    public PaginationVO<Activity> pageList(Map<String, Object> map) {
        //取得total
        int total = activityDao.getTotalByCondition(map);
        //取得dataList
        List<Activity> activityList = activityDao.getActivityListByCondition(map);
        //将total和dataList封装到vo中
        PaginationVO<Activity> vo = new PaginationVO<>();
        vo.setTotal(total);
        vo.setDataList(activityList);
        return vo;
    }

    @Override
    public boolean delete(String[] ids) {
        boolean flag = true;
        //先删除市场活动评论再删除市场活动信息，相当于先删除学生信息再删除班级信息
        //获取应该删除的市场活动评论条数
        int count1 = activityRemarkDao.getCountByAids(ids);
        //获取实际删除的市场活动评论条数
        int count2 = activityRemarkDao.deleteByAids(ids);

        if (count1 != count2){
            flag = false;
        }
        int count3 = activityDao.delete(ids);
        if (count3 != ids.length){
            flag = false;
        }
        return flag;
    }

    @Override
    public Map<String, Object> getUserListAndActivity(String id) {
        //取userList
        List<User> userList = userDao.getUserList();
        //取a
        Activity activity = activityDao.getById(id);

        //将userList和a放入map中
        Map<String,Object> map = new HashMap<>();
        map.put("userList",userList);
        map.put("a",activity);
        return map;
    }

    @Override
    public boolean update(Activity a) {
        boolean flag = true;
        int count = activityDao.update(a);
        if (count != 1){
            flag = false;
        }
        return flag;
    }

    @Override
    public Activity detail(String id) {
        //根据id查单条市场活动信息，注意其中的owner为用户id应该改为user中的name
        Activity a = activityDao.detail(id);
        return a;
    }

    @Override
    public List<ActivityRemark> getRemarkListByAid(String activityId) {
        List<ActivityRemark> activityRemarkList = activityRemarkDao.getRemarkListByAid(activityId);
        return activityRemarkList;
    }

    @Override
    public boolean deleteRemark(String id) {
        boolean flag=true;
        int count = activityRemarkDao.deleteById(id);
        if (count != 1){
            flag = false;
        }
        return flag;
    }

    @Override
    public boolean saveRemark(ActivityRemark ar) {
        boolean flag = true;
        int count = activityRemarkDao.saveRemark(ar);
        if (count != 1){
            flag = false;
        }
        return flag;
    }

    @Override
    public boolean updateRemark(ActivityRemark ar) {
        boolean flag = true;
        int count = activityRemarkDao.updateRemark(ar);
        if (count != 1){
            flag = false;
        }
        return flag;
    }

    @Override
    public List<Activity> getActivityListByClueId(String clueId) {
        List<Activity> activityList = activityDao.getActivityListByClueId(clueId);
        return activityList;
    }

    @Override
    public List<Activity> getActivityListByNameAndNotByClueId(Map<String, String> map) {
        List<Activity> aList = activityDao.getActivityListByNameAndNotByClueId(map);
        return aList;
    }

    @Override
    public List<Activity> getActivityListByName(String aname) {
        List<Activity> activityList = activityDao.getActivityListByName(aname);
        return activityList;
    }
}
