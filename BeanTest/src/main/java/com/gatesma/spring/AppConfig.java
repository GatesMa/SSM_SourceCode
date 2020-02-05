package com.gatesma.spring;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

/**
 * Copyright (C), 2020
 * FileName: AppConfig
 * Author:   Marlon
 * Email: gatesma@foxmail.com
 * Date:     2020/2/4 10:36
 * Description:
 */
@Configuration
@ComponentScan(value = {"com.gatesma.aop"})
@EnableAspectJAutoProxy
public class AppConfig {

}
