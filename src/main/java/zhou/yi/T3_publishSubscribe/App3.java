package zhou.yi.T3_publishSubscribe;

import com.rabbitmq.client.*;
import org.junit.Test;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

/**
 * @Author: XiaoLang
 * @Date: 2019/3/25 13:34
 */
public class App3 {
    @Test
    public void send() throws IOException, TimeoutException {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("localhost");
        Connection connection = factory.newConnection();
        Channel channel = connection.createChannel();
        channel.exchangeDeclare("notice", "fanout");
        channel.basicPublish( "notice", "", null, "Hello China".getBytes());
        channel.close();
        connection.close();
    }

    @Test
    public void receive() throws Exception {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("localhost");
        Connection connection = factory.newConnection();
        Channel channel = connection.createChannel();
        //先创建一个fanout类型的交换机
        channel.exchangeDeclare("notice", "fanout");
        // 该方法会创建一个名称随机的临时队列
        String queueName = channel.queueDeclare().getQueue();
        // 将队列绑定到指定的交换机（"notice"）上
        channel.queueBind(queueName, "notice", "");

        channel.basicConsume(queueName, true, new DefaultConsumer(channel) {
            @Override
            public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body) throws IOException {
                System.out.println(new String(body, "UTF-8"));
            }

        });
        synchronized (this) {
            wait();
        }
    }
}
