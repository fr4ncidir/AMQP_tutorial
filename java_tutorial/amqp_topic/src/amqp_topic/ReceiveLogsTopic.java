package amqp_topic;

import java.io.IOException;
import java.util.Scanner;
import java.util.concurrent.TimeoutException;

import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.Consumer;
import com.rabbitmq.client.DefaultConsumer;
import com.rabbitmq.client.Envelope;

public class ReceiveLogsTopic {
	private static final Scanner scanner = new Scanner(System.in);
	private static final String EXCHANGE_NAME = "topic_logs";
	private final static boolean AUTOACK = true;

	public static void main(String[] args) throws IOException, TimeoutException {
		ConnectionFactory factory = new ConnectionFactory();
	    factory.setHost("localhost");
	    Connection connection = factory.newConnection();
	    Channel channel = connection.createChannel();

	    channel.exchangeDeclare(EXCHANGE_NAME, "topic");
	    String queueName = channel.queueDeclare().getQueue();
	    
	    System.out.print("Write routing keys: ");
	    String[] routing_keys = readString().split(" ");
	    
	    for (String s: routing_keys) {
	    	channel.queueBind(queueName, EXCHANGE_NAME, s);
	    }
	    System.out.println(" [*] Waiting for messages. To exit press CTRL+C");
	    
	    Consumer consumer = new DefaultConsumer(channel) {
	    	@Override
	    	public void handleDelivery(String consumerTag, Envelope envelope,AMQP.BasicProperties properties, byte[] body) throws IOException {
	    		String message = new String(body, "UTF-8");
	    		System.out.println(" [x] Received '" + envelope.getRoutingKey() + "':'" + message + "'");
	    	}
	    };
	    channel.basicConsume(queueName, AUTOACK, consumer);
	}

	private static String readString() {
		return scanner.nextLine();
	}
}
