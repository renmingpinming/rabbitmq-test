package zhou.yi.T1_helloworld;

import com.rabbitmq.client.*;
import org.junit.Test;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

/**
 * @Author: XiaoLang
 * @Date: 2019/3/25 11:33
 */
public class App {
    @Test
    public void send() throws IOException, TimeoutException {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("localhost");
        Connection connection = factory.newConnection();
        Channel channel = connection.createChannel();
        channel.queueDeclare("hello", false, false, false, null);
        // 将第二个参数设为true，表示声明一个需要持久化的队列。
        // 需要注意的是，若你已经定义了一个非持久的，同名字的队列，要么将其先删除（不然会报错），要么换一个名字。
//        channel.queueDeclare("hello", true, false, false, null);

        channel.basicPublish("", "hello", null, "Hello World3".getBytes());
        // 修改了第三个参数，这是表明消息需要持久化
//        channel.basicPublish("", "hello", MessageProperties.PERSISTENT_TEXT_PLAIN, "Hello World3".getBytes());

        channel.close();
        connection.close();
    }

    @Test
    public void receive() throws Exception {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("localhost");
        Connection connection = factory.newConnection();
        Channel channel = connection.createChannel();
        channel.queueDeclare("hello", false, false, false, null);

        channel.basicConsume("hello", true, new DefaultConsumer(channel) {
            @Override
            public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body) throws IOException {
                System.out.println(new String(body, "UTF-8"));
            }

        });
        synchronized (this){
            // 因为以上接收消息的方法是异步的（非阻塞），当采用单元测试方式执行该方法时，程序会在打印消息前结束，因此使用wait来防止程序提前终止。若使用main方法执行，则不需要担心该问题。
            wait();
        }
    }
}
