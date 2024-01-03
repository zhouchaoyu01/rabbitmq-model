package com.cz.workqueue.model3confirm;

import com.cz.workqueue.utils.RabbitmqUtils;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.ConfirmCallback;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.UUID;
import java.util.concurrent.ConcurrentNavigableMap;
import java.util.concurrent.ConcurrentSkipListMap;
import java.util.concurrent.TimeoutException;

/**
 * 发布确认：
 * <p>
 * 单个确认
 * 批量确认
 * 异步批量确认
 *
 * @author zhouchaoyu
 * @time 2023-06-12-21:26
 */
public class ConfirmMsg {

    //批量发消息的个数
    public static final int MESSAGE_COUNT = 100000;

    /*
    发布100000单独确认78747ms
    发布100000个批量确认消息,耗时47342ms
    发布100000个异步确认消息,耗时2802ms
     */

    public static void main(String[] args) throws Exception {
        //单个确认
        //publishMessageIndividually();//发布1000单独确认760ms
        //批量确认
         publishMessageBatch();//发布1000个批量确认消息,耗时136ms
        // 异步批量确认
       // publishMessageAsync();//发布1000个异步确认消息,耗时131ms

    }

    public static void publishMessageIndividually() throws Exception {

        long start;
        try (Channel channel = RabbitmqUtils.getChannel()) {

            String queueName = UUID.randomUUID().toString();

            channel.queueDeclare(queueName, false, false, false, null);

            channel.confirmSelect();

            start = System.currentTimeMillis();

            for (int i = 0; i < MESSAGE_COUNT; i++) {
                String s = i + "条";
                channel.basicPublish("", queueName, null, s.getBytes(StandardCharsets.UTF_8));
                //马上确认
                boolean flag = channel.waitForConfirms();
                if (flag) {
                    System.out.println("success " + i);
                }
            }
        }

        long end = System.currentTimeMillis();
        System.out.println("发布" + MESSAGE_COUNT + "单独确认" + (end - start) + "ms");
    }

    public static void publishMessageBatch() throws Exception {
        try (Channel channel = RabbitmqUtils.getChannel()) {
            String queueName = UUID.randomUUID().toString();
            channel.queueDeclare(queueName, false, false, false, null);
            //开启发布确认
            channel.confirmSelect();

            //批量确认消息大小
            int batchSize = 100;
            //未确认消息个数
            int outstandingMessageCount = 0;

            long begin = System.currentTimeMillis();
            for (int i = 0; i < MESSAGE_COUNT; i++) {
                String message = i + "";
                channel.basicPublish("", queueName, null, message.getBytes());
                outstandingMessageCount++;
                if (outstandingMessageCount == batchSize) {
                    channel.waitForConfirms();
                    outstandingMessageCount = 0;
                }
            }
            //为了确保还有剩余没有确认消息 再次确认
            if (outstandingMessageCount > 0) {
                channel.waitForConfirms();
            }
            long end = System.currentTimeMillis();
            System.out.println("发布" + MESSAGE_COUNT + "个批量确认消息,耗时" + (end - begin) + "ms");
        }
    }

    public static void publishMessageAsync() throws Exception {
        Channel channel = RabbitmqUtils.getChannel();
        String queueName = UUID.randomUUID().toString();
        channel.queueDeclare(queueName, false, false, false, null);

        channel.confirmSelect();
        /**
         * 线程安全有序的一个哈希表，适用于高并发的情况
         * 1.轻松的将序号与消息进行关联
         * 2.轻松批量删除条目 只要给到序列号
         * 3.支持并发访问
         */
        ConcurrentSkipListMap<Long, String> skipListMap = new ConcurrentSkipListMap<>();
        /**
         * 确认收到消息的一个回调
         * 1.消息序列号
         * 2.true 可以确认小于等于当前序列号的消息
         * false 确认当前序列号消息
         */
        ConfirmCallback ackCallback = (long deliveryTag, boolean multiple) -> {
            if (multiple) {
                //返回的是小于等于当前序列号的未确认消息 是一个 map
                ConcurrentNavigableMap<Long, String> confirm = skipListMap.headMap(deliveryTag, true);
                //清除该部分未确认消息
                confirm.clear();
            } else {
                //只清除当前序列号的消息
                skipListMap.remove(deliveryTag);
            }
        };


        ConfirmCallback nackCallback = (sequenceNumber, multiple) -> {
            String msg = skipListMap.get(sequenceNumber);
            System.out.println("发布的消息" + msg + "未被确认，序列号" + sequenceNumber);
        };
        /**
         * 添加一个异步确认的监听器
         * 1.确认收到消息的回调
         * 2.未收到消息的回调
         */
        channel.addConfirmListener(ackCallback, nackCallback);
        long start = System.currentTimeMillis();
        for (int i = 0; i < MESSAGE_COUNT; i++) {
            String message = "消息" + i;
            /**
             * channel.getNextPublishSeqNo()获取下一个消息的序列号
             * 通过序列号与消息体进行一个关联
             * 全部都是未确认的消息体
             */
            skipListMap.put(channel.getNextPublishSeqNo(), message);
            channel.basicPublish("", queueName, null, message.getBytes());
        }
        long end = System.currentTimeMillis();
        System.out.println("发布" + MESSAGE_COUNT + "个异步确认消息,耗时" + (end - start) + "ms");
    }
}
