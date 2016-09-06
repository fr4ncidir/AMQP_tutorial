package publish_subscribe;

import java.io.IOException;
import java.util.Scanner;
import java.util.concurrent.TimeoutException;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

public class EmitLog {
	private final static Scanner scanner = new Scanner(System.in);
	private static final String EXCHANGE_NAME = "logs";
	private final static String QUEUE_NAME = "";
	
	public static void main(String[] args) throws IOException, TimeoutException {
		ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("localhost");
        Connection connection = factory.newConnection();
        Channel channel = connection.createChannel();

        channel.exchangeDeclare(EXCHANGE_NAME, "fanout");

        while (true) {
        	System.out.print("What's your message? ");
        	String message = readString();
        	if (message.equals("exit")) break;
        	channel.basicPublish(EXCHANGE_NAME, QUEUE_NAME, null, message.getBytes());
            System.out.println(" [x] Sent '" + message + "'");
        }
        channel.close();
	    connection.close();

	    System.out.println("Bye Bye");
	}

	private static String readString() {
		return scanner.nextLine();
	}
}
