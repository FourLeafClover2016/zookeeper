package com.hwx.curator;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.recipes.cache.PathChildrenCache;
import org.apache.curator.framework.recipes.cache.PathChildrenCacheEvent;
import org.apache.curator.framework.recipes.cache.PathChildrenCacheListener;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.zookeeper.CreateMode;

/**
 * @author: Huawei Xie
 * @date: 2019/3/30
 */
public class PathChildrenCache_Sample {
    public static void main(String[] args) throws Exception {
        final String path = "/zk-book/child";
        CuratorFramework client = CuratorFrameworkFactory.builder()
                .connectString("192.168.124.61:2181")
                .sessionTimeoutMs(5000)
                .retryPolicy(new ExponentialBackoffRetry(1000, 3))
                .build();
        client.start();

        final PathChildrenCache cache = new PathChildrenCache(client, path, true);
        cache.start(PathChildrenCache.StartMode.POST_INITIALIZED_EVENT);
        cache.getListenable().addListener(new PathChildrenCacheListener() {
            public void childEvent(CuratorFramework curatorFramework, PathChildrenCacheEvent pathChildrenCacheEvent) throws Exception {
                switch (pathChildrenCacheEvent.getType()) {
                    case CHILD_ADDED:
                        System.out.println("CHILD_ADDED：" + pathChildrenCacheEvent.getData());
                        break;
                    case CHILD_UPDATED:
                        System.out.println("CHILD_UPDATED：" + pathChildrenCacheEvent.getData());
                        break;
                    case CHILD_REMOVED:
                        System.out.println("CHILD_REMOVED：" + pathChildrenCacheEvent.getData());
                        break;
                    default:
                        break;
                }
            }
        });
        client.create().withMode(CreateMode.PERSISTENT).forPath(path);
        Thread.sleep(1000);


        client.create().withMode(CreateMode.PERSISTENT).forPath(path + "/c1", "c1".getBytes());
        Thread.sleep(1000);

        client.setData().forPath(path + "/c1", "c1 set data".getBytes());
        Thread.sleep(1000);

        client.delete().forPath(path + "/c1");
        Thread.sleep(1000);

        // Curator 监控子节点变化时，不能监控本节点的变化，也无法监听二级节点的变化
        client.delete().forPath(path);
        Thread.sleep(Integer.MAX_VALUE);
    }
}
