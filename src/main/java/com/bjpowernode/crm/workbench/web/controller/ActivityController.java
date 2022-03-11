package com.bjpowernode.crm.workbench.web.controller;

import com.bjpowernode.crm.settings.domain.User;
import com.bjpowernode.crm.settings.service.UserService;
import com.bjpowernode.crm.settings.service.impl.UserServiceImpl;
import com.bjpowernode.crm.utils.*;
import com.bjpowernode.crm.vo.PaginationVO;
import com.bjpowernode.crm.workbench.domain.Activity;
import com.bjpowernode.crm.workbench.domain.ActivityRemark;
import com.bjpowernode.crm.workbench.service.ActivityService;
import com.bjpowernode.crm.workbench.service.impl.ActivityServiceImpl;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class ActivityController extends HttpServlet {
    @Override
    protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        System.out.println("进入市场活动控制器");
        //获取web.xml中<servlet-mapping>标签中的<url-pattern>
        String path = request.getServletPath();
        //根据不同的servlet地址处理不同的业务需求
        if("/workbench/activity/getUserList.do".equals(path)){
            getUserList(request,response);
        }else if ("/workbench/activity/save.do".equals(path)){
            save(request,response);
        }else if ("/workbench/activity/pageList.do".equals(path)){
            pageList(request,response);
        }else if ("/workbench/activity/delete.do".equals(path)){
            delete(request,response);
        }else if ("/workbench/activity/getUserListAndActivity.do".equals(path)){
            getUserListAndActivity(request,response);
        }else if ("/workbench/activity/update.do".equals(path)){
            update(request,response);
        }else if ("/workbench/activity/detail.do".equals(path)){
            detail(request,response);
        }else if ("/workbench/activity/getRemarkListByAid.do".equals(path)){
            getRemarkListByAid(request,response);
        }else if ("/workbench/activity/deleteRemark.do".equals(path)){
            deleteRemark(request,response);
        }else if ("/workbench/activity/saveRemark.do".equals(path)){
            saveRemark(request,response);
        }else if ("/workbench/activity/updateRemark.do".equals(path)){
            updateRemark(request,response);
        }
    }

    private void updateRemark(HttpServletRequest request, HttpServletResponse response) {
        System.out.println("进入修改备注信息的操作");
        String id = request.getParameter("id");
        String noteContent = request.getParameter("noteContent");
        String editTime = DateTimeUtil.getSysTime();
        String editBy = ((User)request.getSession().getAttribute("user")).getName();
        String editFlag = "1";
        ActivityRemark ar = new ActivityRemark();
        ar.setId(id);
        ar.setNoteContent(noteContent);
        ar.setEditTime(editTime);
        ar.setEditBy(editBy);
        ar.setEditFlag(editFlag);

        ActivityService as = (ActivityService) ServiceFactory.getService(new ActivityServiceImpl());
        boolean flag = as.updateRemark(ar);

        Map<String,Object> map = new HashMap<>();
        map.put("success",flag);
        map.put("ar",ar);

        PrintJson.printJsonObj(response,map);
    }

    private void saveRemark(HttpServletRequest request, HttpServletResponse response) {
        System.out.println("进入添加备注信息的操作");
        String noteContent = request.getParameter("noteContent");
        String activityId = request.getParameter("activityId");
        String id = UUIDUtil.getUUID();
        String createTime = DateTimeUtil.getSysTime();
        String createBy = ((User)request.getSession().getAttribute("user")).getName();
        String editFlag = "0";
        ActivityRemark ar = new ActivityRemark();
        ar.setId(id);
        ar.setNoteContent(noteContent);
        ar.setActivityId(activityId);
        ar.setCreateTime(createTime);
        ar.setCreateBy(createBy);
        ar.setEditFlag(editFlag);

        ActivityService as = (ActivityService) ServiceFactory.getService(new ActivityServiceImpl());

        boolean flag = as.saveRemark(ar);

        Map<String,Object> map = new HashMap<>();
        map.put("success",flag);
        map.put("ar",ar);

        PrintJson.printJsonObj(response,map);
    }

    private void deleteRemark(HttpServletRequest request, HttpServletResponse response) {
        System.out.println("进入删除备注的操作");
        String id = request.getParameter("id");
        ActivityService as = (ActivityService) ServiceFactory.getService(new ActivityServiceImpl());
        boolean flag = as.deleteRemark(id);
        PrintJson.printJsonFlag(response,flag);
    }

    private void getRemarkListByAid(HttpServletRequest request, HttpServletResponse response) {
        System.out.println("进入展示市场活动对应备注信息列表的操作");
        String activityId = request.getParameter("activityId");
        ActivityService as = (ActivityService) ServiceFactory.getService(new ActivityServiceImpl());
        List<ActivityRemark> activityRemarkList = as.getRemarkListByAid(activityId);
        PrintJson.printJsonObj(response,activityRemarkList);
    }

    private void detail(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException{
        System.out.println("进入展示市场活动详细信息的操作");
        String id = request.getParameter("id");
        ActivityService as = (ActivityService) ServiceFactory.getService(new ActivityServiceImpl());
        Activity a = as.detail(id);
        //使用传统请求方式，将查询结果放入request域对象中，在前端jsp中使用el表达式读取
        request.setAttribute("a",a);
        //使用请求转发方式，跳转到detail.jsp
        request.getRequestDispatcher("/workbench/activity/detail.jsp").forward(request,response);

    }

    private void update(HttpServletRequest request, HttpServletResponse response) {
        System.out.println("进入市场活动信息修改操作");
        String id = request.getParameter("id");
        String owner = request.getParameter("owner");
        String name = request.getParameter("name");
        String startDate = request.getParameter("startDate");
        String endDate = request.getParameter("endDate");
        String cost = request.getParameter("cost");
        String description = request.getParameter("description");
        String editTime = DateTimeUtil.getSysTime();
        String editBy = ((User)request.getSession().getAttribute("user")).getName();

        Activity a = new Activity();
        a.setId(id);
        a.setOwner(owner);
        a.setName(name);
        a.setStartDate(startDate);
        a.setEndDate(endDate);
        a.setCost(cost);
        a.setDescription(description);
        a.setEditTime(editTime);
        a.setEditBy(editBy);

        ActivityService as = (ActivityService) ServiceFactory.getService(new ActivityServiceImpl());
        boolean flag = as.update(a);
        PrintJson.printJsonFlag(response,flag);
    }

    private void getUserListAndActivity(HttpServletRequest request, HttpServletResponse response) {
        System.out.println("进入到查询用户信息和根据市场活动id查询单条记录的操作");
        String id = request.getParameter("id");
        ActivityService as = (ActivityService) ServiceFactory.getService(new ActivityServiceImpl());
        /*
         业务层返回用户信息列表 userList和一条市场活动信息a
         由于此信息复用率不高，考虑使用map来接收
         */
        Map<String,Object> map = as.getUserListAndActivity(id);
        PrintJson.printJsonObj(response,map);
    }

    private void delete(HttpServletRequest request, HttpServletResponse response) {
        System.out.println("进入市场活动信息删除操作");
        //获取多个id值
        String[] ids = request.getParameterValues("id");
        ActivityService as = (ActivityService) ServiceFactory.getService(new ActivityServiceImpl());
        boolean flag = as.delete(ids);
        PrintJson.printJsonFlag(response,flag);
    }

    private void pageList(HttpServletRequest request, HttpServletResponse response) {
        System.out.println("进入查询市场信息列表的操作（结合条件查询和分页查询）");
        String name = request.getParameter("name");
        String owner = request.getParameter("owner");
        String startDate = request.getParameter("startDate");
        String endDate = request.getParameter("endDate");
        String pageNostr = request.getParameter("pageNo");
        int pageNo = Integer.valueOf(pageNostr);
        String pageSizestr = request.getParameter("pageSize");
        int pageSize = Integer.valueOf(pageSizestr);
        //计算掠过的记录条数，即sql语句中limit后面的第一个数字
        int skipCount = (pageNo-1)*pageSize;
        //将上述参数放入map中以便于传给dao层
        Map<String,Object> map = new HashMap<>();
        map.put("name",name);
        map.put("owner",owner);
        map.put("startDate",startDate);
        map.put("endDate",endDate);
        map.put("pageSize",pageSize);
        map.put("skipCount",skipCount);
        //获取代理层业务对象
        ActivityService as = (ActivityService) ServiceFactory.getService(new ActivityServiceImpl());
        /*
        前端要：市场活动信息列表以及查询的总记录条数
        业务层拿到以上两项信息后，如何做返回
        map
        map.put("dataList",dataList)
        map.put("total",total)
        PrintJSON map--->json

        vo
        使用自定义范型以便于获取不同的domain对象列表
        PaginationVO<T>
            private int total;
            private List<T> dataList;

         将来分页查询每个模块都有，所以使用一个通用的vo，操作起来比较方便
         */
        PaginationVO<Activity> vo = as.pageList(map);
        PrintJson.printJsonObj(response,vo);


    }

    private void save(HttpServletRequest request, HttpServletResponse response) {
        System.out.println("添加市场活动信息");
        String id = UUIDUtil.getUUID();
        String owner = request.getParameter("owner");
        String name = request.getParameter("name");
        String startDate = request.getParameter("startDate");
        String endDate = request.getParameter("endDate");
        String cost = request.getParameter("cost");
        String description = request.getParameter("description");
        String createTime = DateTimeUtil.getSysTime();
        String createBy = ((User)request.getSession().getAttribute("user")).getName();

        Activity a = new Activity();
        a.setId(id);
        a.setOwner(owner);
        a.setName(name);
        a.setStartDate(startDate);
        a.setEndDate(endDate);
        a.setCost(cost);
        a.setDescription(description);
        a.setCreateTime(createTime);
        a.setCreateBy(createBy);

        ActivityService as = (ActivityService) ServiceFactory.getService(new ActivityServiceImpl());
        boolean flag = as.save(a);
        PrintJson.printJsonFlag(response,flag);

    }

    private void getUserList(HttpServletRequest request, HttpServletResponse response) {
        System.out.println("取得用户信息列表");
        UserService userService = (UserService) ServiceFactory.getService(new UserServiceImpl());
        List<User> ulist = userService.getUserList();
        //将列表转换成json数组
        PrintJson.printJsonObj(response,ulist);
    }


}
