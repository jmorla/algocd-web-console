package com.algocd.webportal.mappers;

import com.algocd.webportal.entities.Location;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Result;
import org.apache.ibatis.annotations.Results;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface LocationMapper {

    @Select("SELECT * FROM locations")
    @Results(id = "locationResultMap", value = {
        @Result(property = "locationId", column = "location_id", id = true),
        @Result(property = "name", column = "name"),
        @Result(property = "region", column = "region"),
        @Result(property = "enabled", column = "enabled")
    })
    List<Location> findAll();
}
