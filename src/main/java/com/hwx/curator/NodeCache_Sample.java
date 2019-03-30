package com.hwx.curator;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.recipes.cache.NodeCache;
import org.apache.curator.framework.recipes.cache.NodeCacheListener;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.zookeeper.CreateMode;

/**
 * @author: Huawei Xie
 * @date: 2019/3/30
 */
public class NodeCache_Sample {
    public static void main(String[] args) throws Exception {
        String path = "/zk-book/nodecache";
        CuratorFramework client = CuratorFrameworkFactory.builder()
                .sessionTimeoutMs(5000)
                .retryPolicy(new ExponentialBackoffRetry(1000, 3))
                .connectString("192.168.124.61:2181")
                .build();
        client.start();
        client.create()
                .creatingParentsIfNeeded()
                .withMode(CreateMode.EPHEMERAL)
                .forPath(path, "init".getBytes());

        // 默认为false，如果设置为true，NodeCache第一次启动时，就会立刻从zookeeper上读取对应节点的数据保存在Cache中
        final NodeCache cache = new NodeCache(client, path, false);
        cache.start();
        cache.getListenable().addListener(new NodeCacheListener() {
            public void nodeChanged() throws Exception {
                System.out.println("Node data update, new data: " + new String(cache.getCurrentData().getData()));
            }
        });
        client.setData().forPath(path,"init2".getBytes());
        Thread.sleep(1000);
        /**
         *  NodeCache可以监听节点内容变化，也可以监听节点是否存在，如果原本节点并不存在,在节点创建后出发Cache的NodeCacheListener
         *  但如果节点被删除，将无法触发Cache的NodeCacheListener
         */
        client.delete().deletingChildrenIfNeeded().forPath(path);
        Thread.sleep(Integer.MAX_VALUE);
    }
}
