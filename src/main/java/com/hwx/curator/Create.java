package com.hwx.curator;

import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.zookeeper.CreateMode;

/**
 * @author: Huawei Xie
 * @date: 2019/3/22
 */
public class Create {
    public static void main(String[] args) throws Exception {
        String path = "/zk-book/book2";
        RetryPolicy retryPolicy = new ExponentialBackoffRetry(1000, 3);
        CuratorFramework client = CuratorFrameworkFactory.builder()
                .connectString("192.168.124.61:2181")
                .sessionTimeoutMs(5000)
                .retryPolicy(retryPolicy)
                .build();
        client.start();

        client.create()
                .creatingParentsIfNeeded()
                .withMode(CreateMode.EPHEMERAL)
                .forPath(path,"book2 created".getBytes());
        Thread.sleep(3000);

        client.delete()
                .deletingChildrenIfNeeded()
                .forPath(path);
        Thread.sleep(Integer.MAX_VALUE);

    }
}
