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


    public List<MapSource> getAll() {
        return mapSourceMapper.getAll();
    }


    public MapSource get(Integer mapId) {
        List<MapSource> mapSources = mapSourceMapper.get(mapId);
        if (mapSources != null)
            return mapSources.get(0);
        else
            return null;
    }

    public void add(MapSource source) {
        mapSourceMapper.add(source);
    }

    public void delete(Integer id) {
        mapSourceMapper.delete(id);
    }



}

