package com.algocd.webportal.mappers;

import com.algocd.webportal.entities.Tag;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Result;
import org.apache.ibatis.annotations.Results;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface TagMapper {

    @Select("SELECT * FROM tags")
    @Results(id = "tagResultMap", value = {
        @Result(property = "resourceId", column = "resource_id"),
        @Result(property = "tagKey", column = "tag_key"),
        @Result(property = "tagValue", column = "tag_value")
    })
    List<Tag> findAll();
}
