package com.gatesna.servlet;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

/**
 * Copyright (C), 2020
 * FileName: UserListener
 * Author:   Marlon
 * Email: gatesma@foxmail.com
 * Date:     2020/2/3 20:51
 * Description:监听项目的启动和停止
 */

public class UserListener implements ServletContextListener {


    //监听ServletContext销毁
    @Override
    public void contextDestroyed(ServletContextEvent arg0) {
        // TODO Auto-generated method stub
        System.out.println("UserListener...contextDestroyed...");
    }

    //监听ServletContext启动初始化
    @Override
    public void contextInitialized(ServletContextEvent arg0) {
        // TODO Auto-generated method stub
        ServletContext servletContext = arg0.getServletContext();
        System.out.println("UserListener...contextInitialized...");
    }

}
