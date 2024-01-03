package com.cz.workqueue.model4exchange.defaultE;

import com.cz.workqueue.utils.RabbitmqUtils;
import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.MessageProperties;

import java.util.Scanner;


public class Producer {
    private final static String QUEUE_NAME = "ack-work-queue";

    public static void main(String[] args) throws Exception {

        try (Channel channel = RabbitmqUtils.getChannel()) {
            //开启发布确认
            channel.confirmSelect();

            //队列持久化
            boolean durable = true;

            //String queueName = channel.queueDeclare().getQueue(); 临时队列:
            // 一旦我们断开了消费者的连接，队列将被自动删除
            channel.queueDeclare(QUEUE_NAME, durable, false, false, null);
            Scanner scanner = new Scanner(System.in);
            while (scanner.hasNext()) {
                String msg = scanner.next();

                //消息持久化
                AMQP.BasicProperties persistentTextPlain = MessageProperties.PERSISTENT_TEXT_PLAIN;

                //默认交换，我们通过空字符串("")进行标识
                channel.basicPublish("", QUEUE_NAME, persistentTextPlain, msg.getBytes());
                System.out.println("生产者消息:" + msg);
            }


        }
    }
}