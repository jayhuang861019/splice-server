package com.txzmap.spliceservice.mapper;

import com.txzmap.spliceservice.entity.MapSource;
import org.apache.ibatis.annotations.*;

import java.util.List;

/**
 * @author jayhuang
 * @date 2020-07-22 下午2:47
 */

@Mapper
public interface MapSourceMapper {
    @Insert("INSERT INTO tb_map_source(name, url,owner) " +
            "VALUES(#{name}, #{url},  #{owner})")
    void add(MapSource source);

    @Delete("DELETE FROM tb_map_source WHERE id = #{id} and owner=#{owner}")
    void delete(@Param("id") Integer id, @Param("owner") Integer owner);

    /**
     * 根据用户id获取所属的地图源
     *
     * @param owner
     * @return
     */
    @Select("select * from tb_map_source where owner = #{owner} or owner=-1")
    List<MapSource> getMySource(Integer owner);


    /**
     * 根据用户id获取所属的地图源
     *
     * @param id
     * @return
     */
    @Select("select * from tb_map_source where id = #{id}")
    MapSource getSourceById(Integer id);


}
