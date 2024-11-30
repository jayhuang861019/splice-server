package com.txzmap.spliceservice.mapper;

import com.txzmap.spliceservice.entity.MapSource;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author jayhuang
 * @date 2020-07-22 下午2:47
 */

@Repository
public interface MapSourceMapper {


    List<MapSource> getAll();


    void add(MapSource source);


    void delete(Integer id);


    List<MapSource> get(Integer mapId);
}
