package com.hwx.zkclient;

import org.I0Itec.zkclient.IZkDataListener;
import org.I0Itec.zkclient.ZkClient;

/**
 * @author: Huawei Xie
 * @date: 2019/3/22
 */
public class GetSetDate {
    public static void main(String[] args) throws InterruptedException {
        final String path = "/zk-book/book1/b1";
        ZkClient zkClient = new ZkClient("192.168.124.61:2181", 5000);
        zkClient.createPersistent(path, "123");
        zkClient.subscribeDataChanges(path, new IZkDataListener() {
            public void handleDataChange(String dataPath, Object data) throws Exception {
                System.out.println("Node" + dataPath + " changed, new data:" + data);
            }

            public void handleDataDeleted(String dataPath) throws Exception {
                System.out.println("Node" + dataPath + " deleted");
            }
        });
        System.out.println(zkClient.readData(path));
        zkClient.writeData(path, 456);
        Thread.sleep(1000);
        zkClient.delete(path);
        Thread.sleep(Integer.MAX_VALUE);
    }
}
