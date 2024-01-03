package com.cz.workqueue.model;

import com.cz.workqueue.utils.RabbitmqUtils;
import com.rabbitmq.client.*;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

/**
 * @author zhouchaoyu
 * @time 2023-06-11-15:36
 */
public class Consumer1 {

    public static final String QUEUE_NAME = "workQueue";

    public static void main(String[] args) throws IOException, TimeoutException {

        Channel channel = RabbitmqUtils.getChannel();

        System.out.println("等待接收消息1.........");
//推送的消息如何进行消费的接口回调
        DeliverCallback deliverCallback = (consumerTag, delivery) -> {
            String message = new String(delivery.getBody());
            System.out.println("接收到消息1:" + message);
        };
//取消消费的一个回调接口 如在消费的时候队列被删除掉了
        CancelCallback cancelCallback = (consumerTag) -> {
            System.out.println(consumerTag + "消费者1取消消费接口回调逻辑");
        };

        /**
         * 消费者消费消息
         * 1.消费哪个队列
         * 2.消费成功之后是否要自动应答 true 代表自动应答 false 手动应答
         * 3.消费者未成功消费的回调
         */
        System.out.println("C2 消费者启动等待消费.................. ");
        channel.basicConsume(QUEUE_NAME, true, deliverCallback, cancelCallback);
    }

}
