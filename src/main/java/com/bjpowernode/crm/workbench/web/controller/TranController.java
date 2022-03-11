package com.bjpowernode.crm.workbench.web.controller;

import com.bjpowernode.crm.settings.domain.User;
import com.bjpowernode.crm.settings.service.UserService;
import com.bjpowernode.crm.settings.service.impl.UserServiceImpl;
import com.bjpowernode.crm.utils.DateTimeUtil;
import com.bjpowernode.crm.utils.PrintJson;
import com.bjpowernode.crm.utils.ServiceFactory;
import com.bjpowernode.crm.utils.UUIDUtil;
import com.bjpowernode.crm.workbench.domain.Activity;
import com.bjpowernode.crm.workbench.domain.Clue;
import com.bjpowernode.crm.workbench.domain.Tran;
import com.bjpowernode.crm.workbench.domain.TranHistory;
import com.bjpowernode.crm.workbench.service.ActivityService;
import com.bjpowernode.crm.workbench.service.ClueService;
import com.bjpowernode.crm.workbench.service.CustomerService;
import com.bjpowernode.crm.workbench.service.TranService;
import com.bjpowernode.crm.workbench.service.impl.ActivityServiceImpl;
import com.bjpowernode.crm.workbench.service.impl.ClueServiceImpl;
import com.bjpowernode.crm.workbench.service.impl.CustomerServiceImpl;
import com.bjpowernode.crm.workbench.service.impl.TranServiceImpl;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TranController extends HttpServlet {
    @Override
    protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        System.out.println("进入交易控制器");
        //获取web.xml中<servlet-mapping>标签中的<url-pattern>
        String path = request.getServletPath();
        //根据不同的servlet地址处理不同的业务需求
        if ("/workbench/transaction/add.do".equals(path)) {
            add(request,response);
        } else if ("/workbench/transaction/getCustomerName.do".equals(path)) {
            getCustomerName(request,response);
        }else if ("/workbench/transaction/save.do".equals(path)) {
            save(request,response);
        }else if ("/workbench/transaction/detail.do".equals(path)) {
            detail(request,response);
        }else if ("/workbench/transaction/getTranHistoryListByTranId.do".equals(path)) {
            getTranHistoryListByTranId(request,response);
        }else if ("/workbench/transaction/changeStage.do".equals(path)) {
            changeStage(request,response);
        }else if ("/workbench/transaction/getCharts.do".equals(path)) {
            getCharts(request,response);
        }

    }

    private void getCharts(HttpServletRequest request, HttpServletResponse response) {
        System.out.println("进入获取交易统计图表数据的操作");
        TranService ts = (TranService) ServiceFactory.getService(new TranServiceImpl());
        //前端需要 total和dataList
        Map<String,Object> map = ts.getCharts();
        PrintJson.printJsonObj(response,map);
    }

    private void changeStage(HttpServletRequest request, HttpServletResponse response) {
        System.out.println("进入更新交易阶段的操作");
        String id = request.getParameter("id");
        String stage = request.getParameter("stage");
        String money = request.getParameter("money");
        String expectedDate = request.getParameter("expectedDate");
        String editBy = ((User)request.getSession().getAttribute("user")).getName();
        String editTime = DateTimeUtil.getSysTime();

        //根据stage获取possibility
        Map<String,String> pMap = (Map<String, String>) request.getServletContext().getAttribute("pMap");
        String possibility = pMap.get(stage);

        TranService ts = (TranService) ServiceFactory.getService(new TranServiceImpl());
        Tran t = new Tran();
        t.setId(id);
        t.setStage(stage);
        t.setMoney(money);
        t.setExpectedDate(expectedDate);
        t.setEditBy(editBy);
        t.setEditTime(editTime);
        t.setPossobility(possibility);
        boolean flag= ts.changeStage(t);

        Map<String,Object> map = new HashMap<>();
        map.put("success",flag);
        map.put("t",t);

        PrintJson.printJsonObj(response,map);

    }

    private void getTranHistoryListByTranId(HttpServletRequest request, HttpServletResponse response) {
        System.out.println("进入展示交易历史列表的操作");
        String tranId = request.getParameter("tranId");
        TranService ts = (TranService) ServiceFactory.getService(new TranServiceImpl());
        List<TranHistory> thList = ts.getTranHistoryListByTranId(tranId);
        //遍历交易历史列表，取得每条记录的阶段，在服务器缓存的pMap中根据阶段找可能性
        Map<String,String> pMap = (Map<String, String>) request.getServletContext().getAttribute("pMap");
        for (TranHistory th: thList){
            String stage = th.getStage();
            String possibility = pMap.get(stage);
            //将possibility封装到th中
            th.setPossibility(possibility);
        }
        PrintJson.printJsonObj(response, thList);
    }

    private void detail(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException{
        System.out.println("进入跳转交易详细信息页的操作");
        String id = request.getParameter("id");
        TranService ts = (TranService) ServiceFactory.getService(new TranServiceImpl());
        Tran t = ts.detail(id);
        //获取服务器缓存中的pMap，拿到可能性
        Map<String,String> pMap = (Map<String, String>) request.getServletContext().getAttribute("pMap");
        String stage = t.getStage();
        String possibility = pMap.get(stage);
        t.setPossobility(possibility);

        //放入request域中传递给detail.jsp
        request.setAttribute("t",t);
        request.getRequestDispatcher("/workbench/transaction/detail.jsp").forward(request,response);
    }

    private void save(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException{
        System.out.println("进入添加交易的操作");
        String id = UUIDUtil.getUUID();
        String owner = request.getParameter("owner");
        String money = request.getParameter("money");
        String name = request.getParameter("name");
        String expectedDate = request.getParameter("expectedDate");
        String customerName = request.getParameter("customerName");
        String stage = request.getParameter("stage");
        String type = request.getParameter("type");
        String source = request.getParameter("source");
        String activityId = request.getParameter("activityId");
        String contactsId = request.getParameter("contactsId");
        String createBy =((User)request.getSession().getAttribute("user")).getName();
        String createTime = DateTimeUtil.getSysTime();
        String description = request.getParameter("description");
        String contactSummary = request.getParameter("contactSummary");
        String nextContactTime = request.getParameter("nextContactTime");

        Tran t = new Tran();
        t.setId(id);
        t.setOwner(owner);
        t.setMoney(money);
        t.setName(name);
        t.setExpectedDate(expectedDate);
        t.setStage(stage);
        t.setType(type);
        t.setSource(source);
        t.setActivityId(activityId);
        t.setContactsId(contactsId);
        t.setCreateBy(createBy);
        t.setCreateTime(createTime);
        t.setDescription(description);
        t.setContactSummary(contactSummary);
        t.setNextContactTime(nextContactTime);

        TranService ts = (TranService) ServiceFactory.getService(new TranServiceImpl());
        boolean flag = ts.save(t,customerName);

        if (flag){
            //重定向到列表页
            response.sendRedirect(request.getContextPath()+"/workbench/transaction/index.jsp");
        }
    }

    private void getCustomerName(HttpServletRequest request, HttpServletResponse response) {
        System.out.println("取得 客户名称列表（按照客户名称进行模糊查询）");
        String name = request.getParameter("name");
        CustomerService cs = (CustomerService) ServiceFactory.getService(new CustomerServiceImpl());
        List<String> strList = cs.getCustomerName(name);
        PrintJson.printJsonObj(response,strList);
    }

    private void add(HttpServletRequest request, HttpServletResponse response)throws ServletException, IOException {
        System.out.println("获取所有用户信息");
        UserService us = (UserService) ServiceFactory.getService(new UserServiceImpl());
        List<User> uList = us.getUserList();
        request.setAttribute("uList",uList);
        request.getRequestDispatcher("/workbench/transaction/save.jsp").forward(request,response);
    }
}
