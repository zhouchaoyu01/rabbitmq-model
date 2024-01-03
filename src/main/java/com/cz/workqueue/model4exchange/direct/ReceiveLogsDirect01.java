package com.cz.workqueue.model4exchange.direct;

import com.cz.workqueue.utils.RabbitmqUtils;
import com.rabbitmq.client.BuiltinExchangeType;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.DeliverCallback;
import org.apache.commons.io.FileUtils;

import java.io.File;

/**
 * direct
 * 上一节中的我们的日志系统将所有消息广播给所有消费者，对此我们想做一些改变，例如我们希
 * 望将日志消息写入磁盘的程序仅接收严重错误(errros)，而不存储哪些警告(warning)或信息(info)日志
 * 消息避免浪费磁盘空间。Fanout 这种交换类型并不能给我们带来很大的灵活性-它只能进行无意识的
 * 广播，在这里我们将使用 direct 这种类型来进行替换，这种类型的工作方式是，消息只去到它绑定的
 * routingKey 队列中去。
 *
 * @author zhouchaoyu
 * @time 2023-06-14-17:10
 */
public class ReceiveLogsDirect01 {
    private static final String EXCHANGE_NAME = "direct_logs";
    public static void main(String[] argv) throws Exception {
        Channel channel = RabbitmqUtils.getChannel();
        channel.exchangeDeclare(EXCHANGE_NAME, BuiltinExchangeType.DIRECT);
        String queueName = "disk";
        channel.queueDeclare(queueName, false, false, false, null);
        channel.queueBind(queueName, EXCHANGE_NAME, "error");
        System.out.println("等待接收消息........... ");
        DeliverCallback deliverCallback = (consumerTag, delivery) ->
        {String message = new String(delivery.getBody(), "UTF-8");
            message="接收绑定键:"+delivery.getEnvelope().getRoutingKey()+",消息:"+message;
            File file = new File("D:\\Java_course\\rabbit-mq\\rabbit-mq-demo\\rabbitmq_info.txt");
            FileUtils.writeStringToFile(file,message,"UTF-8");
            System.out.println("错误日志已经接收");
        };
        channel.basicConsume(queueName, true, deliverCallback, consumerTag -> {
        });
    }
}
