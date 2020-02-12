package com.gatesma.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * Copyright (C), 2020
 * FileName: HelloController
 * Author:   Marlon
 * Email: gatesma@foxmail.com
 * Date:     2020/2/11 21:08
 * Description:
 */
@Controller
public class HelloController {


    @ResponseBody
    @RequestMapping(value = {"hello"})
    public String hello() {
        return "SpringBoot!";
    }

}
