package com.gatesna.servlet;

import javax.servlet.*;
import java.io.IOException;

/**
 * Copyright (C), 2020
 * FileName: UserFilter
 * Author:   Marlon
 * Email: gatesma@foxmail.com
 * Date:     2020/2/3 20:50
 * Description:
 */

public class UserFilter implements Filter {

    @Override
    public void destroy() {
        // TODO Auto-generated method stub

    }

    @Override
    public void doFilter(ServletRequest arg0, ServletResponse arg1, FilterChain arg2)
            throws IOException, ServletException {
        // 过滤请求
        System.out.println("UserFilter...doFilter...");
        //放行
        arg2.doFilter(arg0, arg1);

    }

    @Override
    public void init(FilterConfig arg0) throws ServletException {
        // TODO Auto-generated method stub

    }

}