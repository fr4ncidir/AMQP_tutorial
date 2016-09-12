package amqp_topic;

import java.io.IOException;
import java.util.Scanner;
import java.util.concurrent.TimeoutException;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

public class EmitLogsTopic {
	private static final Scanner scanner = new Scanner(System.in);
	private static final String EXCHANGE_NAME = "topic_logs";

	public static void main(String[] args) throws IOException, TimeoutException {
		ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("localhost");
        Connection connection = factory.newConnection();
        Channel channel = connection.createChannel();

        channel.exchangeDeclare(EXCHANGE_NAME, "topic");

        while (true) {
        	System.out.print("Insert routing key: ");
        	String routing_key = readString();
        	if (routing_key.equals("exit")) break;
        	System.out.print("Insert the message: ");
        	String message = readString();
        	channel.basicPublish(EXCHANGE_NAME, routing_key, null, message.getBytes());
        	System.out.println(" [x] Sent '" + routing_key + "':'" + message + "'");
        }

        connection.close();	
	}

	private static String readString() {
		return scanner.nextLine();
	}
}
