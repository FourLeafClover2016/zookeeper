package com.hwx.curator;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.api.BackgroundCallback;
import org.apache.curator.framework.api.CuratorEvent;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.zookeeper.CreateMode;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @author: Huawei Xie
 * @date: 2019/3/30
 */
public class BackgroundCall {
    public static void main(String[] args) throws Exception {
        String path = "/zk-book/book3";
        CuratorFramework client = CuratorFrameworkFactory.builder()
                .connectString("192.168.124.61:2181")
                .sessionTimeoutMs(5000)
                .retryPolicy(new ExponentialBackoffRetry(1000, 3))
                .build();
        final CountDownLatch semaphone = new CountDownLatch(2);
        ExecutorService executorService = Executors.newFixedThreadPool(2);
        client.start();

        // 传入ExecutorService，curator的异步事件处理逻辑交予该线程处理
        client.create().creatingParentsIfNeeded().withMode(CreateMode.EPHEMERAL).inBackground(new BackgroundCallback() {
            public void processResult(CuratorFramework curatorFramework, CuratorEvent curatorEvent) throws Exception {
                System.out.println("event code:  " + curatorEvent.getResultCode() + ",type:" + curatorEvent.getType());
                System.out.println("Thread of processResult:" + Thread.currentThread().getName());
                semaphone.countDown();
            }
        }, executorService).forPath(path, "init".getBytes());

     //   client.delete().forPath(path);
        // 默认使用zookeeper的默认EventThread来处理
        client.create().creatingParentsIfNeeded().withMode(CreateMode.EPHEMERAL).inBackground(new BackgroundCallback() {
            public void processResult(CuratorFramework curatorFramework, CuratorEvent curatorEvent) throws Exception {
                System.out.println("event code:  " + curatorEvent.getResultCode() + ",type:" + curatorEvent.getType());
                System.out.println("Thread of processResult:" + Thread.currentThread().getName());
                semaphone.countDown();
            }
        }).forPath(path, "init".getBytes());
        semaphone.await();
        executorService.shutdown();
    }
}
