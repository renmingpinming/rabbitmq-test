package zhou.yi.T5_topic;

import com.rabbitmq.client.*;
import org.junit.Test;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

/**
 * @Author: XiaoLang
 * @Date: 2019/3/25 15:22
 */
public class App5 {
    @Test
    public void send() throws IOException, TimeoutException {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("localhost");
        Connection connection = factory.newConnection();
        Channel channel = connection.createChannel();

        channel.exchangeDeclare("movie", "topic");

        channel.basicPublish("movie", "American.action.13", null, "The Bourne Ultimatum".getBytes());
        channel.basicPublish("movie", "American.comedy.R", null, "The Big Lebowski".getBytes());
        channel.basicPublish("movie", "American.romantic.13", null, "Titanic".getBytes());

        channel.basicPublish("movie", "Chinese.action.13", null, "卧虎藏龙".getBytes());
        channel.basicPublish("movie", "Chinese.comedy.13", null, "大话西游".getBytes());
        channel.basicPublish("movie", "Chinese.romantic.13", null, "梁山伯与祝英台".getBytes());

        channel.close();
        connection.close();
    }

    @Test
    public void receive() throws Exception {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("localhost");
        Connection connection = factory.newConnection();
        Channel channel = connection.createChannel();
        channel.exchangeDeclare("movie", "topic");
        // 队列1
//        String queueName = channel.queueDeclare().getQueue();
//        channel.queueBind(queueName, "movie", "American.*.13");
        // 队列2
//        String queueName = channel.queueDeclare().getQueue();
//        channel.queueBind(queueName, "movie", "*.action.*");
        // 队列3
        String queueName = channel.queueDeclare().getQueue();
        channel.queueBind(queueName, "movie", "Chinese.#");


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
