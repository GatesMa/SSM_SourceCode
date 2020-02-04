package com.gatesma.test;

import com.gatesma.spring.AppConfig;
import org.junit.Test;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

/**
 * Copyright (C), 2020
 * FileName: IOCTest
 * Author:   Marlon
 * Email: gatesma@foxmail.com
 * Date:     2020/2/4 10:37
 * Description:
 */
public class IOCTest {


    @Test
    public void test01() {

        AnnotationConfigApplicationContext applicationContext = new AnnotationConfigApplicationContext(AppConfig.class);

        Object person = applicationContext.getBean("person");
        System.out.println(person);

    }

}
