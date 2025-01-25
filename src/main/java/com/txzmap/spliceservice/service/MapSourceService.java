package com.txzmap.spliceservice.service;

import com.txzmap.spliceservice.entity.MapSource;
import com.txzmap.spliceservice.mapper.MapSourceMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MapSourceService {

    @Autowired
    private MapSourceMapper mapSourceMapper;


    public void add(MapSource source) {
        mapSourceMapper.add(source);
    }

    public void delete(Integer id, Integer owner) {
        mapSourceMapper.delete(id, owner);
    }

    /**
     * 获取所有的地图源
     *
     * @param owner
     * @return
     */
    public List<MapSource> getMySource(Integer owner) {
        return mapSourceMapper.getMySource(owner);
    }

    public MapSource getSourceById(Integer id) {
        return mapSourceMapper.getSourceById(id);
    }


}

