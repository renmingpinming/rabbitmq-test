package zhou.yi.T4_routing;

import com.rabbitmq.client.*;
import org.junit.Test;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

/**
 * @Author: XiaoLang
 * @Date: 2019/3/25 14:01
 */
public class App4 {
    @Test
    public void send() throws IOException, TimeoutException {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("localhost");
        Connection connection = factory.newConnection();
        Channel channel = connection.createChannel();

        channel.exchangeDeclare("notice2", "direct");
        for (int i = 0; i < 10; i++) {
            String routingKey = "n"; // normal
            if (i % 2 == 0) {
                routingKey = "s"; // secret
            }
            // 绑定交换机，同时带上routing key
            channel.basicPublish("notice2", routingKey, null, String.valueOf(i).getBytes());
        }
        channel.close();
        connection.close();
    }

    @Test
    public void receive() throws Exception {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("localhost");
        Connection connection = factory.newConnection();
        Channel channel = connection.createChannel();
        channel.exchangeDeclare("notice2", "direct");

        String queueName = channel.queueDeclare().getQueue();
        // 绑定交换机，同时带上routing key
//        channel.queueBind(queueName, "notice2", "n");
         channel.queueBind(queueName, "notice2", "s");

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
