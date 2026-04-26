package com.algocd.webportal.mappers;

import com.algocd.webportal.entities.Plan;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Result;
import org.apache.ibatis.annotations.Results;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface PlanMapper {

    @Select("SELECT * FROM plans")
    @Results(id = "planResultMap", value = {
        @Result(property = "planId", column = "plan_id", id = true),
        @Result(property = "name", column = "name"),
        @Result(property = "cpuCores", column = "cpu_cores"),
        @Result(property = "ramGb", column = "ram_gb"),
        @Result(property = "monthlyPrice", column = "monthly_price"),
        @Result(property = "hourlyPrice", column = "hourly_price"),
        @Result(property = "expertLimit", column = "expert_limit")
    })
    List<Plan> findAll();
}
