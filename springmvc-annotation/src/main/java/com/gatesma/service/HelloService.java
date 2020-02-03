package com.gatesma.service;

import org.springframework.stereotype.Service;

/**
 * Copyright (C), 2020
 * FileName: HelloService
 * Author:   Marlon
 * Email: gatesma@foxmail.com
 * Date:     2020/2/3 21:59
 * Description:
 */
@Service
public class HelloService {

    public String sayHello(String name){

        return "Hello "+name;
    }

}
