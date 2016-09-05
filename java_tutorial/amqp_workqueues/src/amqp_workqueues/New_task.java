package amqp_workqueues;

import java.io.IOException;
import java.util.Scanner;
import java.util.concurrent.TimeoutException;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.MessageProperties;

public class New_task {
	private final static Scanner scanner = new Scanner(System.in);
	private final static String QUEUE_NAME = "workqueue";
	private final static boolean DURABLE_QUEUE = true;

	public static void main(String[] args) throws IOException, TimeoutException {
		ConnectionFactory factory = new ConnectionFactory();
	    factory.setHost("localhost");

	    Connection connection = factory.newConnection();
	    Channel channel = connection.createChannel();
	    channel.queueDeclare(QUEUE_NAME, DURABLE_QUEUE, false, false, null);
		
		while (true) {
			System.out.println("Seconds for the task to wait: ");
			String message = readString();
			if (message.equals("exit")) break;
			channel.basicPublish("", QUEUE_NAME, MessageProperties.PERSISTENT_TEXT_PLAIN, message.getBytes());
			System.out.println(" [x] Sent request for " + message + " seconds");
		}
	    channel.close();
	    connection.close();

	    System.out.println("Bye Bye");
	}

	private static String readString() {
		return scanner.nextLine();
	}
}
