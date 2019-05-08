package com.hwx.config;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.recipes.cache.PathChildrenCache;
import org.apache.curator.framework.recipes.cache.PathChildrenCacheEvent;
import org.apache.curator.framework.recipes.cache.PathChildrenCacheListener;
import org.apache.curator.retry.RetryNTimes;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.ZooDefs;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.CountDownLatch;

/**
 * @author: Huawei Xie
 * @date: 2019/5/6
 */
@Configuration
public class CuratorConfiguration {
    @Value("${curator.retryCount}")
    private int retryCount;

    @Value("${curator.elapsedTimeMs}")
    private int elapsedTimeMs;

    @Value("${curator.connectString}")
    private String connectString;

    @Value("${curator.sessionTimeoutMs}")
    private int sessionTimeoutMs;

    @Value("${curator.connectionTimeoutMs}")
    private int connectionTimeoutMs;

    /**
     * zk客户端
     */
    private CuratorFramework client;

    private static CountDownLatch countDownLatch = new CountDownLatch(1);

    private static final String ZK_LOCK_PROJECT = "zk-locks";

    private static final String DISTRIBUTED_LOCK = "distributed_lock";

   /* public CuratorConfiguration(CuratorFramework client) {
        this.client = client;
    }*/

    public void init() {
      //  client = client.usingNamespace("ZKLOCKS_Namespace");
        try {
            if(null == client.checkExists().forPath("/" + ZK_LOCK_PROJECT)){
                client.create()
                        .creatingParentsIfNeeded()
                        .withMode(CreateMode.PERSISTENT)
                        //.withACL(ZooDefs.Ids.CREATOR_ALL_ACL)
                        .forPath("/" + ZK_LOCK_PROJECT);
            }

            addWatcherToLock("/" + ZK_LOCK_PROJECT);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void getLock() {
        while (true) {
            try {
                client.create()
                        .creatingParentsIfNeeded()
                        .withMode(CreateMode.EPHEMERAL)
                     //   .withACL(ZooDefs.Ids.CREATOR_ALL_ACL)
                        .forPath("/" + ZK_LOCK_PROJECT + "/" + DISTRIBUTED_LOCK);
                System.out.println("获取锁......");
                break;
            } catch (Exception e) {
                e.printStackTrace();
                System.out.println("获取锁失败......");
                if (countDownLatch.getCount() <= 0) {
                    countDownLatch = new CountDownLatch(1);
                }
                try {
                    countDownLatch.await();
                } catch (InterruptedException e1) {
                    e1.printStackTrace();
                }
                e.printStackTrace();
            }
        }
    }

    public boolean releaseLock() {
        try {
            if (null != client.checkExists().forPath("/" + ZK_LOCK_PROJECT + "/" + DISTRIBUTED_LOCK)) {
                client.delete().forPath("/" + ZK_LOCK_PROJECT + "/" + DISTRIBUTED_LOCK);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        System.out.println("释放锁");
        return true;
    }

    public void addWatcherToLock(String path) throws Exception {
        PathChildrenCache cache = new PathChildrenCache(client, path, true);
        cache.start(PathChildrenCache.StartMode.POST_INITIALIZED_EVENT);
        cache.getListenable().addListener(new PathChildrenCacheListener() {
            @Override
            public void childEvent(CuratorFramework client, PathChildrenCacheEvent event) throws Exception {
                if (event.getType().equals(PathChildrenCacheEvent.Type.CHILD_REMOVED)) {
                    String path = event.getData().getPath();
                    System.out.println("上一个会话释放锁，节点路径：" + path);
                    if (path.contains(DISTRIBUTED_LOCK)) {
                        System.out.println("释放计数器，让当前请求获取锁");
                        countDownLatch.countDown();
                    }
                }
            }
        });
    }

    @Bean
    public CuratorFramework getCuratorFramework() {
        this.client = CuratorFrameworkFactory.builder()
                .connectString(connectString)
                .sessionTimeoutMs(sessionTimeoutMs)
                .connectionTimeoutMs(connectionTimeoutMs)
                .retryPolicy(new RetryNTimes(retryCount, elapsedTimeMs))
                .build();
        client.start();
        init();
        return client;
    }
}
