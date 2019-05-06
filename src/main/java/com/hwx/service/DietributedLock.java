package com.hwx.service;

import com.hwx.config.CuratorConfiguration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author: Huawei Xie
 * @date: 2019/5/6
 */
@Service
public class DietributedLock {
    @Autowired
    CuratorConfiguration curatorConfiguration;

    public boolean dietributedLock(){
        curatorConfiguration.getLock();
        System.out.println("");
        curatorConfiguration.releaseLock();
        return true;
    }

}
