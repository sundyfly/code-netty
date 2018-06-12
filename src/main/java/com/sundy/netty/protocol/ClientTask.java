package com.sundy.netty.protocol;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

/**
 * @author sundy
 * @since 1.8
 * 日期: 2018年05月30日 15:32:15
 * 描述：
 */
public class ClientTask implements Runnable{
    @Override
    public void run() {
        try {
            new ProtocolClient("localhost", 8082).run();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        Executor executor = Executors.newFixedThreadPool(100);
        for (int i = 0; i < 100; i++) {
           executor.execute(new ClientTask());
        }
    }
}
