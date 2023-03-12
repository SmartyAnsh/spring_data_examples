package com.smartdiscover.mapper;

import com.smartdiscover.entity.Author;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface AuthorMapper {

    @Select("SELECT * FROM AUTHOR WHERE id = #{id}")
    Author getAuthor(@Param("id") Long id);

    @Insert("INSERT INTO AUTHOR (first_name, last_name) VALUES (#{firstName}, #{lastName})")
    Long saveAuthor(@Param("firstName") String firstName, @Param("lastName") String lastName);

}