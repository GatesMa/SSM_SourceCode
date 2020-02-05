package com.gatesma.aop;

import org.springframework.stereotype.Component;

/**
 * Copyright (C), 2020
 * FileName: MathCaculator
 * Author:   Marlon
 * Email: gatesma@foxmail.com
 * Date:     2020/2/5 19:40
 * Description:
 */
@Component
public class MathCalculator implements Calcator{

    public int div(int i, int j) {
        return i / j;
    }
}
