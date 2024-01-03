package com.cz.workqueue.utils;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

/**
 * @author zhouchaoyu
 * @time 2023-06-11-21:20
 */
public class RabbitmqUtils {

    public static Channel getChannel() throws IOException, TimeoutException {
        //创建一个连接工厂
        ConnectionFactory connectionFactory = new ConnectionFactory();

        connectionFactory.setHost("192.168.145.140");
        connectionFactory.setUsername("admin");
        connectionFactory.setPassword("123");


        Connection connection = connectionFactory.newConnection();
        Channel channel = connection.createChannel();
        return channel;
    }


}
