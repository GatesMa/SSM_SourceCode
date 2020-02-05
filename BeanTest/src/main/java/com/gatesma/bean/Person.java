package com.gatesma.bean;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Copyright (C), 2020
 * FileName: Person
 * Author:   Marlon
 * Email: gatesma@foxmail.com
 * Date:     2020/2/4 13:32
 * Description:
 */
@Component
public class Person {

    @Autowired
    private Book book;

}
