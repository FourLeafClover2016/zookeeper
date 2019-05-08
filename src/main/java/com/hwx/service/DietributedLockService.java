package com.hwx.service;

import com.hwx.config.CuratorConfiguration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author: Huawei Xie
 * @date: 2019/5/6
 */
@Service
public class DietributedLockService {
    private static int i = 10;
    @Autowired
    CuratorConfiguration curatorConfiguration;

    public boolean dietributedLock() {
        curatorConfiguration.getLock();
        System.out.println("准备执行");
        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        if (i >= 6) {
            i = i - 6;
            System.out.println("执行成功");
        } else {
            System.out.println("直行失败");
        }
        curatorConfiguration.releaseLock();
        return true;
    }

}
