/*
 * AMQP TUTORIAL #1
 */
package amqp_helloworld;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

import java.io.IOException;
import java.util.Scanner;
import java.util.concurrent.TimeoutException;

public class MyProducer {
	private final static boolean DURABLE_QUEUE = false;
	private final static String QUEUE_NAME = "hello";
	private final static String EXCHANGE_NAME = "";
	private final static Scanner scanner = new Scanner(System.in);

	public static void main(String[] args) throws IOException, TimeoutException {
		ConnectionFactory factory = new ConnectionFactory();
	    factory.setHost("localhost");

	    Connection connection = factory.newConnection();
	    Channel channel = connection.createChannel();
	    channel.queueDeclare(QUEUE_NAME, DURABLE_QUEUE, false, false, null);
	    String message = "";
	    do {
	    	message = readString();
	    	if (!message.equals("exit")) {
	    		channel.basicPublish(EXCHANGE_NAME, QUEUE_NAME, null, message.getBytes());
	    		System.out.println(" [x] Sent '" + message + "'");
	    	}
	    } while (!message.equals("exit"));
	    channel.close();
	    connection.close();

	    System.out.println("Bye Bye");
	}
	
	private static String readString() {
		return scanner.nextLine();
	}

}
