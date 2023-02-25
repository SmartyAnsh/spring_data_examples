package com.smartdiscover.mapper;

import com.smartdiscover.entity.Book;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface BookMapper {

    @Select("SELECT * FROM BOOK WHERE id = #{id}")
    Book getBook(@Param("id") Long id);

    @Insert("INSERT INTO Book (name, summary) VALUES (#{name}, #{summary})")
    Long saveBook(@Param("name") String name, @Param("summary") String summary);

}