package com.bjpowernode.crm.settings.web.controller;

import com.bjpowernode.crm.settings.domain.User;
import com.bjpowernode.crm.settings.service.UserService;
import com.bjpowernode.crm.settings.service.impl.UserServiceImpl;
import com.bjpowernode.crm.utils.MD5Util;
import com.bjpowernode.crm.utils.PrintJson;
import com.bjpowernode.crm.utils.ServiceFactory;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class UserController extends HttpServlet {
    @Override
    protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        System.out.println("进入用户控制器");
        //获取web.xml中<servlet-mapping>标签中的<url-pattern>
        String path = request.getServletPath();
        //根据不同的servlet地址处理不同的业务需求
        if("/settings/user/login.do".equals(path)){
            login(request,response);
        }else if ("/settings/user/xxx.do".equals(path)){

        }
    }

    private void login(HttpServletRequest request, HttpServletResponse response) {
        System.out.println("进入验证登录操作");
        //获取浏览器传过来的账号，密码以及ip地址
        String loginAct = request.getParameter("loginAct");
        String loginPwd = request.getParameter("loginPwd");
        //将穿过来的密码转换为MD5密文形式，dao层比较的是密文形式的密码
        loginPwd = MD5Util.getMD5(loginPwd);
        //接收浏览器传过来的ip地址
        String ip = request.getRemoteAddr();
        System.out.println("-------------------ip:"+ip);

        //未来业务层开发，统一使用代理形态的接口对象
        UserService userService = (UserService) ServiceFactory.getService(new UserServiceImpl());

        try{
            User user = userService.login(loginAct,loginPwd,ip);
            //将处理结果放到会话作用域对象中
            request.getSession().setAttribute("user",user);
            //如果程序执行到此处，说明业务层没有为controller抛出任何异常
            //表示登录成功，返回success即可
            PrintJson.printJsonFlag(response,true);
        }catch (Exception e){
            //一旦程序执行了catch块的信息，说明业务层为我们验证登录失败，为controller抛出了异常
            //表示登录失败，此时需要传success和msg错误信息
            String msg = e.getMessage();
            //将success和msg放入map集合中
            Map<String,Object> map = new HashMap<>();
            map.put("success",false);
            map.put("msg",msg);
            //将map转换成json字符串
            PrintJson.printJsonObj(response,map);
        }
    }
}
