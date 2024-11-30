package com.txzmap.spliceservice.service;

import com.txzmap.spliceservice.entity.TbTile;
import com.txzmap.spliceservice.mapper.TbTileMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class TbTileService {

    @Autowired
    private TbTileMapper tbTileMapper;


    public Object getTile(String mapId, Integer x, Integer y, Integer z) {
        TbTile tile = tbTileMapper.getTile(mapId, x, y, z);
        System.err.print(mapId + "/" + x + "/" + y + "/" + z);
        if (tile == null)
            return null;
        else
            return tile.getTile();
    }

    public void insert(TbTile tile) {
        tbTileMapper.insert(tile);
    }

    public boolean exist(String mapId, Integer x, Integer y, Integer z) {
        if (tbTileMapper.getTile(mapId, x, y, z) != null)
            return true;
        return false;
    }
}

