package amqp_remote_procedure_call;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Scanner;
import java.util.UUID;
import java.util.concurrent.TimeoutException;

import com.rabbitmq.client.AMQP.BasicProperties;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.ConsumerCancelledException;
import com.rabbitmq.client.QueueingConsumer;
import com.rabbitmq.client.ShutdownSignalException;

public class RPCClient {
	private static final Scanner scanner = new Scanner(System.in);
	private static final boolean AUTOACK = true;
	private static final String requestQueueName = "rpc_queue";
	
	private Connection connection;
	private Channel channel;
	private String replyQueueName;
	private QueueingConsumer consumer;
	
	public RPCClient() throws IOException, TimeoutException {
		ConnectionFactory factory = new ConnectionFactory();
	    factory.setHost("localhost");
	    connection = factory.newConnection();
	    channel = connection.createChannel();

	    replyQueueName = channel.queueDeclare().getQueue();
	    consumer = new QueueingConsumer(channel);
	    channel.basicConsume(replyQueueName, AUTOACK, consumer);
	}
	
	public void close() throws IOException {
		connection.close();
	}
	
	public static void main(String[] args) throws ShutdownSignalException, ConsumerCancelledException, InterruptedException {
		String response = null;
		try {
			RPCClient fibonacci = new RPCClient();
			while (true) {
				System.out.print(" [x] Requesting fib(");
				String number = readString();
				if (number.equals("exit")) break;
				System.out.println(")");
				response = fibonacci.call(number);
				System.out.println(" [.] Got '" + response + "'");
			}
			fibonacci.close();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (TimeoutException e) {
			e.printStackTrace();
		}
	}
	
	private String call(String message) throws UnsupportedEncodingException, IOException, ShutdownSignalException, ConsumerCancelledException, InterruptedException {
		String response = null;
		String corrId = UUID.randomUUID().toString();

		BasicProperties props = new BasicProperties
				.Builder()
				.correlationId(corrId)
				.replyTo(replyQueueName)
				.build();

		channel.basicPublish("", requestQueueName, props, message.getBytes("UTF-8"));

		while (true) {
			QueueingConsumer.Delivery delivery = consumer.nextDelivery();
			if (delivery.getProperties().getCorrelationId().equals(corrId)) {
				response = new String(delivery.getBody(),"UTF-8");
				break;
			}
		}

		return response;
	}

	private static String readString() {
		return scanner.nextLine();
	}
	
}
