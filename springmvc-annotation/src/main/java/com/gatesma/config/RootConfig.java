package com.gatesma.config;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.stereotype.Controller;

/**
 * Copyright (C), 2020
 * FileName: RootConfig
 * Author:   Marlon
 * Email: gatesma@foxmail.com
 * Date:     2020/2/3 21:49
 * Description:
 */

//Spring的容器不扫描controller;父容器
@ComponentScan(value = {"com.gatesma"}, excludeFilters = {
        @ComponentScan.Filter(type = FilterType.ANNOTATION, classes = {Controller.class})
})
public class RootConfig {
}
