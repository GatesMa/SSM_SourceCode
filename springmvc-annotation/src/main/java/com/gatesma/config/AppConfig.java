package com.gatesma.config;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.stereotype.Controller;

/**
 * Copyright (C), 2020
 * FileName: AppConfig
 * Author:   Marlon
 * Email: gatesma@foxmail.com
 * Date:     2020/2/3 21:49
 * Description:
 */

//SpringMVC只扫描Controller；子容器
//useDefaultFilters=false 禁用默认的过滤规则；
@ComponentScan(value = {"com.gatesma"}, includeFilters = {
        @ComponentScan.Filter(type = FilterType.ANNOTATION, classes = {Controller.class})
}, useDefaultFilters = false)
public class AppConfig {
    
}
