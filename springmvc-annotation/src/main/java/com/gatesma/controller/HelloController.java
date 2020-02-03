package com.gatesma.controller;

import com.gatesma.service.HelloService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * Copyright (C), 2020
 * FileName: HelloController
 * Author:   Marlon
 * Email: gatesma@foxmail.com
 * Date:     2020/2/3 21:58
 * Description:
 */
@Controller
public class HelloController {

    @Autowired
    HelloService helloService;


    @ResponseBody
    @RequestMapping("/hello")
    public String hello(){
        String hello = helloService.sayHello("tomcat..");
        return hello;
    }

    //  /WEB-INF/views/success.jsp
    @RequestMapping("/suc")
    public String success(){
        return "success";
    }
}
