package com.gatesma.aop;

import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

/**
 * Copyright (C), 2020
 * FileName: LogAspect
 * Author:   Marlon
 * Email: gatesma@foxmail.com
 * Date:     2020/2/5 19:40
 * Description:
 */

@Component
@Aspect
public class LogAspect {


    //抽取公共的切入点表达式
    //1、本类引用
    //2、其他的切面引用
    @Pointcut("execution(public int com.gatesma.aop.MathCalculator.*(..))")
    public void pointCut(){};

    @Before("pointCut()")
    public void logStart() {
        System.out.println("运算开始-Before");
    }

    @After("pointCut()")
    public void logEnd() {
        System.out.println("运算结束-After");
    }




}
