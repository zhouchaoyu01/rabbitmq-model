package com.cz.workqueue.model4exchange.topic;

import com.cz.workqueue.utils.RabbitmqUtils;
import com.rabbitmq.client.Channel;

import java.util.HashMap;
import java.util.Map;

/**
 * topic
 * <p>
 * 发送到类型是 topic 交换机的消息的 routing_key 不能随意写，必须满足一定的要求，它必须是一个单
 * 词列表，以点号分隔开。这些单词可以是任意单词，比如说："stock.usd.nyse", "nyse.vmw",
 * "quick.orange.rabbit".这种类型的。当然这个单词列表最多不能超过 255 个字节。
 * 在这个规则列表中，其中有两个替换符是大家需要注意的
 * *(星号)可以代替一个单词
 * #(井号)可以替代零个或多个单词
 *
 * @author zhouchaoyu
 * @time 2023-06-14-17:19
 */
public class EmitLogTopic {
    private static final String EXCHANGE_NAME = "topic_logs";

    public static void main(String[] argv) throws Exception {
        try (Channel channel = RabbitmqUtils.getChannel()) {
            channel.exchangeDeclare(EXCHANGE_NAME, "topic");
            /**
             * Q1-->绑定的是
             * 中间带 orange 带 3 个单词的字符串(*.orange.*)
             * Q2-->绑定的是
             * 最后一个单词是 rabbit 的 3 个单词(*.*.rabbit)
             * 第一个单词是 lazy 的多个单词(lazy.#)
             *
             */
            Map<String, String> bindingKeyMap = new HashMap<>();
            bindingKeyMap.put("quick.orange.rabbit", "被队列 Q1Q2 接收到");
            bindingKeyMap.put("lazy.orange.elephant", "被队列 Q1Q2 接收到");
            bindingKeyMap.put("quick.orange.fox", "被队列 Q1 接收到");
            bindingKeyMap.put("lazy.brown.fox", "被队列 Q2 接收到");
            bindingKeyMap.put("lazy.pink.rabbit", "虽然满足两个绑定但只被队列 Q2 接收一次");
            bindingKeyMap.put("quick.brown.fox", "不匹配任何绑定不会被任何队列接收到会被丢弃");
            bindingKeyMap.put("quick.orange.male.rabbit", "是四个单词不匹配任何绑定会被丢弃");
            bindingKeyMap.put("lazy.orange.male.rabbit", "是四个单词但匹配 Q2");
            for (Map.Entry<String, String> bindingKeyEntry :
                    bindingKeyMap.entrySet()) {
                String bindingKey =
                        bindingKeyEntry.getKey();
                String message = bindingKeyEntry.getValue();
                channel.basicPublish(EXCHANGE_NAME, bindingKey, null,
                        message.getBytes("UTF-8"));
                System.out.println("生产者发出消息" + message);
            }
        }
    }
}
