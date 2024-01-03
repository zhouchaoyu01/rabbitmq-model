package com.cz.workqueue.model4exchange.topic;

import com.cz.workqueue.utils.RabbitmqUtils;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.DeliverCallback;

/**
 * @author zhouchaoyu
 * @time 2023-06-14-17:20
 */
public class ReceiveLogsTopic01 {
    private static final String EXCHANGE_NAME = "topic_logs";

    public static void main(String[] argv) throws Exception {
        Channel channel = RabbitmqUtils.getChannel();
        channel.exchangeDeclare(EXCHANGE_NAME, "topic");
        //声明 Q1 队列与绑定关系
        String queueName = "Q1";
        channel.queueDeclare(queueName, false, false, false, null);
        channel.queueBind(queueName, EXCHANGE_NAME, "*.orange.*");
        System.out.println("等待接收消息........... ");
        DeliverCallback deliverCallback = (consumerTag, delivery) ->
        {
            String message = new String(delivery.getBody(), "UTF-8");
            System.out.println(" 接 收 队 列 :" + queueName + " 绑 定 键:" + delivery.getEnvelope().getRoutingKey() + ",消息:" + message);
        };
        channel.basicConsume(queueName, true, deliverCallback, consumerTag -> {
        });
    }
}
