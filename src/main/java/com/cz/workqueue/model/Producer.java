package com.cz.workqueue.model;

import com.cz.workqueue.utils.RabbitmqUtils;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

import java.util.Scanner;

/**
 * @author zhouchaoyu
 * @time 2023-06-11-15:28
 */
public class Producer {
    private final static String QUEUE_NAME = "workQueue";

    public static void main(String[] args) throws Exception {

        try (Channel channel = RabbitmqUtils.getChannel()) {
        /**
         * 生成一个队列
         * 1.队列名称
         * 2.队列里面的消息是否持久化 默认消息存储在内存中
         * 3.该队列是否只供一个消费者进行消费 是否进行共享 true 可以多个消费者消费
         * 4.是否自动删除 最后一个消费者端开连接以后 该队列是否自动删除 true 自动删除
         * 5.其他参数
         */
            channel.queueDeclare(QUEUE_NAME, false, false, false, null);
            Scanner scanner = new Scanner(System.in);
            while (scanner.hasNext()) {
                String msg = scanner.next();
                /**
                 * 发送一个消息
                 * 1.发送到那个交换机
                 * 2.路由的 key 是哪个
                 * 3.其他的参数信息
                 * 4.发送消息的消息体
                 */
                channel.basicPublish("", QUEUE_NAME, null, msg.getBytes());
                System.out.println("消息发送完毕:" + msg);
            }


        }
    }
}