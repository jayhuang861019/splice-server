package com.txzmap.spliceservice.mapper;

import com.txzmap.spliceservice.entity.TbUser;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

/**
 * @author jayhuang
 * @date 2020-07-22 下午2:47
 */

@Repository
public interface TbUserMapper {


    void insert(TbUser user);

    TbUser get(@Param("userName") String userName, @Param("password") String password);


    void changePassword(@Param("userName") String userName, @Param("password") String newPassword);

}
