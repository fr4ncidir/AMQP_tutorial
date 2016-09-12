package amqp_remote_procedure_call;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

import com.rabbitmq.client.AMQP.BasicProperties;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.ConsumerCancelledException;
import com.rabbitmq.client.QueueingConsumer;
import com.rabbitmq.client.ShutdownSignalException;

public class RPCServer {
	private static final String RPC_QUEUE_NAME = "rpc_queue";
	private final static boolean DURABLE_QUEUE = false;
	private final static boolean AUTOACK = false;

	public static void main(String[] args) throws IOException, TimeoutException, ShutdownSignalException, ConsumerCancelledException, InterruptedException {
		ConnectionFactory factory = new ConnectionFactory();
		factory.setHost("localhost");

		Connection connection = factory.newConnection();
		Channel channel = connection.createChannel();

		channel.queueDeclare(RPC_QUEUE_NAME, DURABLE_QUEUE, false, false, null);

		channel.basicQos(1);

		QueueingConsumer consumer = new QueueingConsumer(channel);
		channel.basicConsume(RPC_QUEUE_NAME, AUTOACK, consumer);

		System.out.println(" [x] Awaiting RPC requests");
		
		while (true) {
			QueueingConsumer.Delivery delivery = consumer.nextDelivery();
			BasicProperties props = delivery.getProperties();
		    BasicProperties replyProps = new BasicProperties
		    		.Builder()
		    		.correlationId(props.getCorrelationId())
		    		.build();

		    String message = new String(delivery.getBody());
		    int n = Integer.parseInt(message);

		    System.out.println(" [.] fib(" + message + ")");
		    String response = "" + fib(n);

		    channel.basicPublish( "", props.getReplyTo(), replyProps, response.getBytes());

		    channel.basicAck(delivery.getEnvelope().getDeliveryTag(), false);
		}
	}

	private static int fib(int n) {
	    if (n == 0) return 0;
	    if (n == 1) return 1;
	    return fib(n-1) + fib(n-2);
	}
}
