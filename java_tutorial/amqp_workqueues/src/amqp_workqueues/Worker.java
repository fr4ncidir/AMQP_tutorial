package amqp_workqueues;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.Consumer;
import com.rabbitmq.client.DefaultConsumer;
import com.rabbitmq.client.Envelope;

public class Worker {
	private final static String QUEUE_NAME = "workqueue";
	private final static boolean DURABLE_QUEUE = true;
	private final static boolean AUTOACK = false;
	private final static int MAX_WORK_NUMBER = 1;
	
	public static void main(String[] args) throws IOException, TimeoutException {
		ConnectionFactory factory = new ConnectionFactory();
	    factory.setHost("localhost");
	    Connection connection = factory.newConnection();
	    Channel channel = connection.createChannel();
	    channel.queueDeclare(QUEUE_NAME, DURABLE_QUEUE, false, false, null);
	    channel.basicQos(MAX_WORK_NUMBER);
	    
	    System.out.println(" [*] Waiting for messages. To exit press CTRL+C");
	    
	    Consumer consumer = new DefaultConsumer(channel) {
	    	@Override
	        public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body) throws IOException {
	    		String message = new String(body, "UTF-8");
	    		System.out.println(" [x] Received request: " + message + " seconds");
	    		try {
					Thread.sleep(Integer.parseInt(message)*1000);
				} catch (NumberFormatException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} finally {
					System.out.println(" [x] Done");
					channel.basicAck(envelope.getDeliveryTag(), false);
				}
	        }
	    };
	    channel.basicConsume(QUEUE_NAME, AUTOACK, consumer);
	}

}
