package com.gatesma.config;

import com.gatesma.bean.Book;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.GenericBeanDefinition;
import org.springframework.beans.factory.support.RootBeanDefinition;
import org.springframework.stereotype.Component;

/**
 * Copyright (C), 2020
 * FileName: MyBeanFactoryPostProcessor
 * Author:   Marlon
 * Email: gatesma@foxmail.com
 * Date:     2020/2/4 13:31
 * Description:
 */
@Component
public class MyBeanFactoryPostProcessor implements BeanFactoryPostProcessor {


    public void postProcessBeanFactory(ConfigurableListableBeanFactory configurableListableBeanFactory) throws BeansException {
        GenericBeanDefinition person = (GenericBeanDefinition) configurableListableBeanFactory.getBeanDefinition("person");
        System.out.println(person.getBeanClassName());
        //修改Bean的定义
//        person.setBeanClass(Book.class);
    }


}
