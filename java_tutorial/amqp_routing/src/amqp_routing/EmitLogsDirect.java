package amqp_routing;

import java.io.IOException;
import java.util.Scanner;
import java.util.concurrent.TimeoutException;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

public class EmitLogsDirect {
	private final static Scanner scanner = new Scanner(System.in);
	private final static String EXCHANGE_NAME = "direct_logs";

	public static void main(String[] args) throws IOException, TimeoutException {
		ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("localhost");
        Connection connection = factory.newConnection();
        Channel channel = connection.createChannel();

        channel.exchangeDeclare(EXCHANGE_NAME, "direct");

        while (true) {
        	System.out.print("What's your message? ");
        	String message = readString();
        	if (message.equals("exit")) break;
        	System.out.print("Message severity: ");
        	String severity = readString();
        	channel.basicPublish(EXCHANGE_NAME, severity, null, message.getBytes());
            System.out.println(" [x] Sent "+severity+":'" + message + "'");
        }
        channel.close();
	    connection.close();

	    System.out.println("Bye Bye");
	}

	private static String readString() {
		return scanner.nextLine();
	}
}
