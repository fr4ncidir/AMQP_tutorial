package amqp_routing;

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

public class ReceiveLogsDirect {
	private final static Scanner scanner = new Scanner(System.in);
	private final static String EXCHANGE_NAME = "direct_logs";
	private final static boolean AUTOACK = true;

	public static void main(String[] args) throws IOException, TimeoutException {
		ConnectionFactory factory = new ConnectionFactory();
	    factory.setHost("localhost");
	    Connection connection = factory.newConnection();
	    Channel channel = connection.createChannel();
	    
	    channel.exchangeDeclare(EXCHANGE_NAME, "direct");
	    String queueName = channel.queueDeclare().getQueue();
	    
	    System.out.print("Insert severity string: [info] [warning] [error]: ");
	    String[] severity_array = readString().split(" ");
	    for (String severity: severity_array) {
	    	channel.queueBind(queueName, EXCHANGE_NAME, severity);
	    }
	    
	    System.out.println(" [*] Waiting for messages. To exit press CTRL+C");

	    Consumer consumer = new DefaultConsumer(channel) {
	    	@Override
	    	public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body) throws IOException {
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
