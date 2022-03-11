package com.bjpowernode.crm.workbench.web.controller;

import com.bjpowernode.crm.settings.domain.User;
import com.bjpowernode.crm.settings.service.UserService;
import com.bjpowernode.crm.settings.service.impl.UserServiceImpl;
import com.bjpowernode.crm.utils.DateTimeUtil;
import com.bjpowernode.crm.utils.PrintJson;
import com.bjpowernode.crm.utils.ServiceFactory;
import com.bjpowernode.crm.utils.UUIDUtil;
import com.bjpowernode.crm.vo.PaginationVO;
import com.bjpowernode.crm.workbench.domain.Activity;
import com.bjpowernode.crm.workbench.domain.ActivityRemark;
import com.bjpowernode.crm.workbench.domain.Clue;
import com.bjpowernode.crm.workbench.domain.Tran;
import com.bjpowernode.crm.workbench.service.ActivityService;
import com.bjpowernode.crm.workbench.service.ClueService;
import com.bjpowernode.crm.workbench.service.impl.ActivityServiceImpl;
import com.bjpowernode.crm.workbench.service.impl.ClueServiceImpl;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ClueController extends HttpServlet {
    @Override
    protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        System.out.println("进入线索控制器");
        //获取web.xml中<servlet-mapping>标签中的<url-pattern>
        String path = request.getServletPath();
        //根据不同的servlet地址处理不同的业务需求
        if("/workbench/clue/getUserList.do".equals(path)){
           getUserList(request,response);
        }else if ("/workbench/clue/save.do".equals(path)){
            save(request,response);
        }else if ("/workbench/clue/detail.do".equals(path)){
            detail(request,response);
        }else if ("/workbench/clue/getActivityListByClueId.do".equals(path)){
            getActivityListByClueId(request,response);
        }else if ("/workbench/clue/unbund.do".equals(path)){
            unbund(request,response);
        }else if ("/workbench/clue/getActivityListByNameAndNotByClueId.do".equals(path)){
            getActivityListByNameAndNotByClueId(request,response);
        }else if ("/workbench/clue/bund.do".equals(path)){
            bund(request,response);
        }else if ("/workbench/clue/getActivityListByName.do".equals(path)){
            getActivityListByName(request,response);
        }else if ("/workbench/clue/convert.do".equals(path)){
            convert(request,response);
        }
    }

    private void  convert(HttpServletRequest request, HttpServletResponse response)throws ServletException, IOException{
        System.out.println("进入线索转换的操作");
        String clueId = request.getParameter("clueId");
        String flag = request.getParameter("flag");
        //System.out.println(clueId);
        //System.out.println(flag);
        Tran t = null;
        String createBy = ((User)request.getSession().getAttribute("user")).getName();
        if ("a".equals(flag)){
            t = new Tran();
            String money = request.getParameter("money");
            String name = request.getParameter("name");
            String expectedDate = request.getParameter("expectedDate");
            String stage = request.getParameter("stage");
            String activityId = request.getParameter("activityId");
            String id = UUIDUtil.getUUID();
            String createTime = DateTimeUtil.getSysTime();
            t.setMoney(money);
            t.setName(name);
            t.setExpectedDate(expectedDate);
            t.setStage(stage);
            t.setActivityId(activityId);
            t.setId(id);
            t.setCreateBy(createBy);
            t.setCreateTime(createTime);
        }
        //在业务层根据t是否为空判断是否需要添加交易信息
        ClueService cs = (ClueService) ServiceFactory.getService(new ClueServiceImpl());
        boolean flag1 = cs.convert(clueId,t,createBy);
        if (flag1){
            response.sendRedirect(request.getContextPath()+"/workbench/clue/index.jsp");
        }
    }
    private void getActivityListByName(HttpServletRequest request, HttpServletResponse response) {
        System.out.println("进入展现市场活动信息列表的操作（根据名称模糊查）");
        String aname = request.getParameter("aname");
        ActivityService as = (ActivityService) ServiceFactory.getService(new ActivityServiceImpl());
        List<Activity> aList = as.getActivityListByName(aname);
        PrintJson.printJsonObj(response,aList);
    }

    private void bund(HttpServletRequest request, HttpServletResponse response) {
        System.out.println("进入关联线索和选中的市场活动的操作");
        String cId = request.getParameter("cId");
        String[] aIds = request.getParameterValues("aId");
        ClueService cs = (ClueService) ServiceFactory.getService(new ClueServiceImpl());
        boolean flag = cs.bund(cId,aIds);
        PrintJson.printJsonFlag(response,flag);
    }

    private void getActivityListByNameAndNotByClueId(HttpServletRequest request, HttpServletResponse response) {
        System.out.println("进入查询市场活动信息的操作（根据市场活动名称模糊查询并去除已经关联的市场活动）");
        String aname = request.getParameter("aname");
        String clueId = request.getParameter("clueId");
        ActivityService as = (ActivityService) ServiceFactory.getService(new ActivityServiceImpl());
        Map<String,String> map = new HashMap<>();
        map.put("aname",aname);
        map.put("clueId",clueId);
        List<Activity> aList = as.getActivityListByNameAndNotByClueId(map);
        PrintJson.printJsonObj(response,aList);
    }

    private void unbund(HttpServletRequest request, HttpServletResponse response) {
        System.out.println("进入解除线索和市场活动关联的操作");
        String id = request.getParameter("id");
        ClueService cs = (ClueService) ServiceFactory.getService(new ClueServiceImpl());
        boolean flag = cs.unbund(id);
        PrintJson.printJsonFlag(response,flag);
    }

    private void getActivityListByClueId(HttpServletRequest request, HttpServletResponse response) {
        System.out.println("进入展现线索关联的市场活动信息列表的操作");
        String clueId = request.getParameter("clueId");
        ActivityService as = (ActivityService) ServiceFactory.getService(new ActivityServiceImpl());
        List<Activity> aList = as.getActivityListByClueId(clueId);
        PrintJson.printJsonObj(response,aList);
    }

    private void detail(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException{
        System.out.println("进入跳转到线索详细信息页的操作");
        String id = request.getParameter("id");
        ClueService cs = (ClueService) ServiceFactory.getService(new ClueServiceImpl());
        Clue clue = cs.detail(id);
        request.setAttribute("c",clue);
        request.getRequestDispatcher("/workbench/clue/detail.jsp").forward(request,response);
    }

    private void save(HttpServletRequest request, HttpServletResponse response) {
        String id = UUIDUtil.getUUID();
        String fullname = request.getParameter("fullname");
        String appellation = request.getParameter("appellation");
        String owner = request.getParameter("owner");
        String company = request.getParameter("company");
        String job = request.getParameter("job");
        String email = request.getParameter("email");
        String phone = request.getParameter("phone");
        String website = request.getParameter("website");
        String mphone = request.getParameter("mphone");
        String state = request.getParameter("state");
        String source = request.getParameter("source");
        String createBy = ((User)request.getSession().getAttribute("user")).getName();
        String createTime = DateTimeUtil.getSysTime();
        String description = request.getParameter("description");
        String contactSummary = request.getParameter("contactSummary");
        String nextContactTime = request.getParameter("nextContactTime");
        String address = request.getParameter("address");

        Clue c = new Clue();
        c.setId(id);
        c.setFullname(fullname);
        c.setAppellation(appellation);
        c.setOwner(owner);
        c.setCompany(company);
        c.setJob(job);
        c.setEmail(email);
        c.setPhone(phone);
        c.setWebsite(website);
        c.setMphone(mphone);
        c.setState(state);
        c.setSource(source);
        c.setCreateBy(createBy);
        c.setCreateTime(createTime);
        c.setDescription(description);
        c.setContactSummary(contactSummary);
        c.setNextContactTime(nextContactTime);
        c.setAddress(address);

        ClueService cs = (ClueService) ServiceFactory.getService(new ClueServiceImpl());
        boolean flag = cs.save(c);
        PrintJson.printJsonFlag(response,flag);
    }

    private void getUserList(HttpServletRequest request, HttpServletResponse response) {
        System.out.println("进入获取用户信息列表的操作");
        UserService us = (UserService) ServiceFactory.getService(new UserServiceImpl());
        List<User> userList = us.getUserList();
        PrintJson.printJsonObj(response,userList);
    }

}
