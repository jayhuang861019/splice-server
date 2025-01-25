package com.txzmap.spliceservice.mapper;

import com.txzmap.spliceservice.entity.User;
import org.apache.ibatis.annotations.*;

@Mapper
public interface UserMapper {

    // 插入用户信息
    @Insert("insert into tb_user (userName, password, type, expiration,  createTime) " +
            "values (#{user.userName}, #{user.password}, #{user.type}, #{user.expiration},  #{user.createTime})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insertUser(@Param("user") User user);


    // 更新用户信息
    @Update("update tb_user " +
            "set password = #{user.password},lastIn=#{user.lastIn} " +
            "where id = #{user.id}")
    int updateUser(@Param("user") User user);


    @Update("UPDATE tb_user SET password = #{newPassword} WHERE id = #{id} and password=#{oldPassword}")
    int updatePasswordById(@Param("id") Integer id, @Param("oldPassword") String oldPassword, @Param("newPassword") String newPassword);


    // 根据用户ID查询用户信息
    @Select("select * from tb_user where id = #{id}")
    User selectUserById(Integer id);

    // 根据用户名查询是否存在，存在返回1，不存在返回0
    @Select("select count(*) from tb_user where userName = #{userName}")
    int countByUserName(@Param("userName") String userName);

    // 根据用户名和密码查询用户信息，用于登录验证
    @Select("select * from tb_user where userName = #{userName} and password = #{password}")
    User selectUserByUserNameAndPassword(
            @Param("userName") String userName, @Param("password") String password);


    @Update("update tb_user set type=#{user.type},expiration=#{user.expiration} where id=#{user.id}")
    int updateVIPInfo(@Param("user") User user);
}
