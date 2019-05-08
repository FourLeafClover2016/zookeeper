package com.hwx.service;

import com.hwx.Application;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = Application.class)
public class dietributesLockTest {
    @Autowired
    private DietributedLockService dietributedLockService;
    @Test
    public void service1(){
        dietributedLockService.dietributedLock();

    }

    @Test
    public void service2(){
        dietributedLockService.dietributedLock();
    }

}
