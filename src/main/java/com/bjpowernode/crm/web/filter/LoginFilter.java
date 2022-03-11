package com.bjpowernode.crm.web.filter;

import com.bjpowernode.crm.settings.domain.User;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

public class LoginFilter implements Filter {
    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        System.out.println("进入到验证是否登录过的过滤器");

        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpServletResponse response = (HttpServletResponse) servletResponse;
        //默认登录的页面和验证的代码应该放行
        String path = request.getServletPath();
        if ("/login.jsp".equals(path)||"/settings/user/login.do".equals(path)){
            filterChain.doFilter(servletRequest,servletResponse);
        }else {
            //获取当前浏览器会话作用域对象中存放的User对象
            HttpSession session = request.getSession();
            User user = (User) session.getAttribute("user");
            //如果user不为空，说明登录过，放行
            if (user != null){
                filterChain.doFilter(servletRequest,servletResponse);
            }else {
                //没有登录过，重定向到登录页
                //${pageContext.request.contextPath} /项目名
                response.sendRedirect(request.getContextPath()+"/login.jsp");
            }
        }
    }
}
