package com.txzmap.spliceservice.service;

import com.txzmap.spliceservice.entity.ActiveCode;
import com.txzmap.spliceservice.mapper.ActiveCodeMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ActiveCodeService {

    @Autowired
    ActiveCodeMapper activeCodeMapper;

    public ActiveCode findByCode(String code) {
        return activeCodeMapper.findByCode(code);
    }

    /**
     * 使用该code 将其可用置为false
     * @param code
     */
    public void usingCode(String code)
    {
        activeCodeMapper.usingCode(code);
    }

}
