package com.cz.workqueue.model2noautoack;

import com.cz.workqueue.utils.RabbitmqUtils;
import com.cz.workqueue.utils.SleepUtils;
import com.rabbitmq.client.CancelCallback;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.DeliverCallback;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

/**
 *  消息手动应答是不丢失、如果丢失重新放回队列消费
 * @author zhouchaoyu
 * @time 2023-06-11-15:36
 */
public class Consumer4 {

    public static final String QUEUE_NAME = "ack-work-queue";

    public static void main(String[] args) throws IOException, TimeoutException {

        Channel channel = RabbitmqUtils.getChannel();

        System.out.println("4等待接收消息.........处理时间长");

        DeliverCallback deliverCallback = (consumerTag, delivery) -> {
            String message = new String(delivery.getBody());
            SleepUtils.sleep(10);
            System.out.println("接收到消息:" + message);
            /**
             * 1.消息标记 tag
             * 2.是否批量应答未应答消息
             */
            channel.basicAck(delivery.getEnvelope().getDeliveryTag(),false);
        };

        CancelCallback cancelCallback = (consumerTag) -> {
            System.out.println(consumerTag + "消费者取消消费接口回调逻辑");
        };
        //不公平分发  预取值
        channel.basicQos(1);
        //采用手动应答
        boolean autoAck=false;
        channel.basicConsume(QUEUE_NAME, autoAck, deliverCallback, cancelCallback);
    }

}
