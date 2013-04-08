package rabbitmqconsumer;

import com.rabbitmq.client.Connection;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.*;
import java.io.IOException;

/**
 *
 * @author skuarch
 */
public class RabbitMQConsumer {

    private static int numClients = 1;

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws IOException {
        
        ConnectionFactory factory = new ConnectionFactory();
        factory.setUsername("guest");
        factory.setPassword("guest");
        factory.setVirtualHost("/");
        factory.setHost("127.0.0.1");
        factory.setPort(5672);
        Connection conn = factory.newConnection();
        Channel channel = conn.createChannel();
        String exchangeName = "myExchange";
        String queueName = "myQueue";
        String routingKey = "testRoute";
        boolean durable = true;
        channel.exchangeDeclare(exchangeName, "direct", durable);
        channel.queueDeclare(queueName, durable, false, false, null);        
        channel.queueBind(queueName, exchangeName, routingKey);
        boolean noAck = false;
        QueueingConsumer consumer = new QueueingConsumer(channel);
        channel.basicConsume(queueName, noAck, consumer);
        boolean runInfinite = true;
        while (runInfinite) {
            QueueingConsumer.Delivery delivery;
            try {
                delivery = consumer.nextDelivery();
            } catch (InterruptedException ie) {
                continue;
            }

            System.out.println("Message received" + new String(delivery.getBody()) + " client " + numClients++);

            channel.basicAck(delivery.getEnvelope().getDeliveryTag(), false);
        }
        channel.close();
        conn.close();
    }
}
