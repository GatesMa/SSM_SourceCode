package com.gatesna.servlet;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Copyright (C), 2020
 * FileName: UserServlet
 * Author:   Marlon
 * Email: gatesma@foxmail.com
 * Date:     2020/2/3 20:50
 * Description:
 */
public class UserServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        // TODO Auto-generated method stub
        resp.getWriter().write("tomcat...");
    }

}