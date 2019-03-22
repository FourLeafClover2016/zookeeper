package com.hwx.zkclient;

import org.I0Itec.zkclient.IZkChildListener;
import org.I0Itec.zkclient.ZkClient;

import java.util.List;

/**
 * @author: Huawei Xie
 * @date: 2019/3/22
 */
public class Create {
    public static void main(String[] args) throws InterruptedException {
        ZkClient zkClient = new ZkClient("192.168.124.61:2181", 5000);
        String path = "/zk-book/book1";
        //zkClient.createPersistent(path, true);
        //zkClient.delete(path);
        zkClient.subscribeChildChanges(path, new IZkChildListener() {
            public void handleChildChange(String parentPath, List<String> currentChilds) throws Exception {
                System.out.println(parentPath + " child changed, currentChilds:" + currentChilds);
            }
        });
        zkClient.createPersistent(path);
        Thread.sleep(10);
        System.out.println(zkClient.getChildren(path));
        Thread.sleep(10);
        zkClient.createPersistent(path+"/b1");
        Thread.sleep(10);
        zkClient.delete(path+"/b1");
        Thread.sleep(Integer.MAX_VALUE);



        System.out.println("zookeeper session established");
    }
}
