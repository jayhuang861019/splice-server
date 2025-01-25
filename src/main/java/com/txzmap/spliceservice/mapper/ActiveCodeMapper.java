package com.txzmap.spliceservice.mapper;


import com.txzmap.spliceservice.entity.ActiveCode;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

@Mapper
public interface ActiveCodeMapper {

    /**
     * 根据激活码字符串查找激活码记录。
     *
     * @param code 激活码字符串
     * @return 匹配的激活码实体，如果不存在则返回null
     */
    @Select("SELECT * FROM tb_active_code WHERE code = #{code} and available=1")
    ActiveCode findByCode(@Param("code") String code);

    @Update("update tb_active_code set available=0 where code=#{code}")
    void usingCode(@Param("code") String code);
}
