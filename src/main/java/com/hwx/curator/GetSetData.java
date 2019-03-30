package com.hwx.curator;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.data.Stat;


/**
 * @author: Huawei Xie
 * @date: 2019/3/30
 */
public class GetSetData {
    public static void main(String[] args) throws Exception {

        String path = "/zk-book/book2";
        CuratorFramework client = CuratorFrameworkFactory.builder()
                .connectString("192.168.124.61:2181")
                .sessionTimeoutMs(5000)
                .retryPolicy(new ExponentialBackoffRetry(1000, 3))
                .build();
        client.start();
        Stat stat = client.checkExists().forPath(path);
        if (null == stat) {
            client.create()
                    .creatingParentsIfNeeded()
                    .withMode(CreateMode.PERSISTENT)
                    .forPath(path, "init".getBytes());
        }

        Stat stat1 = new Stat();
        System.out.println(new String(client.getData().storingStatIn(stat1).forPath(path)));
        System.out.println(stat1.getVersion());

        client.setData().forPath(path, "set init2".getBytes());
        System.out.println(new String(client.getData().forPath(path)));
    }
}
