package com.txzmap.spliceservice.mapper;

import com.txzmap.spliceservice.entity.TbTile;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

/**
 * @author jayhuang
 * @date 2020-07-22 下午2:47
 */

@Repository
public interface TbTileMapper {


    TbTile getTile(@Param("mapId") String mapId, @Param("x") Integer x, @Param("y") Integer y, @Param("z") Integer z);


    void insert(TbTile tile);


}
