package com.gatesma.dao;

import com.gatesma.entities.Employee;
import org.apache.ibatis.annotations.Param;

/**
 * Copyright (C), 2020
 * FileName: EmployeeMapper
 * Author:   Marlon
 * Email: gatesma@foxmail.com
 * Date:     2020/2/8 11:23
 * Description:
 */
public interface EmployeeMapper {

    public Employee getEmpById(Integer id);

    public void addEmp(Employee employee);

    public Employee getEmpByNameAndGender(@Param("name") String name, @Param("gender") String gender);

}
