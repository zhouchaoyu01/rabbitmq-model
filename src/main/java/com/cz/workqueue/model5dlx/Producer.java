package com.cz.workqueue.model5dlx;

import com.cz.workqueue.utils.RabbitmqUtils;
import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.BuiltinExchangeType;
import com.rabbitmq.client.Channel;

/**
 * 死信，顾名思义就是无法被消费的消息，字面意思可以这样理
 * 解，一般来说，producer 将消息投递到 broker 或者直接到queue 里了，consumer 从 queue 取出消息
 * 进行消费，但某些时候由于特定的原因导致 queue 中的某些消息无法被消费，这样的消息如果没有
 * 后续的处理，就变成了死信，有死信自然就有了死信队列。
 *
 * 应用场景:
 * 为了保证订单业务的消息数据不丢失，需要使用到 RabbitMQ 的死信队列机制，当消息消费发生异常时，
 * 将消息投入死信队列中.还有比如说: 用户在商城下单成功并点击去支付后在指定时间未支付时自动失效
 * <p>
 * <p>
 * 消息 TTL 过期
 * 队列达到最大长度(队列满了，无法再添加数据到 mq 中)
 * 消息被拒绝(basic.reject 或 basic.nack)并且 requeue=false
 *
 * @author zhouchaoyu
 * @time 2023-06-15-9:49
 */
public class Producer {
    /*
    消息TTL 过期
     */
    /*
    队列达到最大长度
    消息被拒绝(basic.reject 或 basic.nack)并且 requeue=false

    消息生产者代码去掉 TTL 属性
     */
    private static final String NORMAL_EXCHANGE = "normal_exchange";

    public static void main(String[] argv) throws Exception {
        try (Channel channel = RabbitmqUtils.getChannel()) {
            channel.exchangeDeclare(NORMAL_EXCHANGE,
                    BuiltinExchangeType.DIRECT);
            //设置消息的 TTL 时间
            AMQP.BasicProperties properties = new AMQP.BasicProperties().builder().expiration("10000").build();
            //该信息是用作演示队列个数限制
            for (int i = 1; i < 11; i++) {
                String message = "info" + i;
                channel.basicPublish(NORMAL_EXCHANGE,"zhangsan", properties, message.getBytes());
                System.out.println("生产者发送消息:" + message);
            }
        }
    }
}
