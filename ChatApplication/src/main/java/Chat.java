import java.util.Scanner;

import javax.jms.JMSException;
import javax.jms.Session;
import javax.jms.TextMessage;
import javax.jms.Topic;
import javax.jms.TopicConnection;
import javax.jms.TopicConnectionFactory;
import javax.jms.TopicPublisher;
import javax.jms.TopicSession;
import javax.jms.TopicSubscriber;
import javax.naming.InitialContext;

public class Chat {
	private TopicSession pubSession;
	private TopicPublisher publisher;
	private TopicConnection connection;
	private String username;

	public Chat(String topicFactory, String topicName, String username) throws Exception {
		InitialContext ctx = new InitialContext();
		TopicConnectionFactory conFactory = (TopicConnectionFactory) ctx.lookup(topicFactory);

		TopicConnection connection = conFactory.createTopicConnection();

		TopicSession pubSession = connection.createTopicSession(false, Session.AUTO_ACKNOWLEDGE);
		TopicSession subSession = connection.createTopicSession(false, Session.AUTO_ACKNOWLEDGE);

		Topic chatTopic = (Topic) ctx.lookup(topicName);

		TopicPublisher publisher = pubSession.createPublisher(chatTopic);
		TopicSubscriber subscriber = subSession.createSubscriber(chatTopic, null, true);

		subscriber.setMessageListener(new ChatMessageListener());

		this.connection = connection;
		this.pubSession = pubSession;
		this.publisher = publisher;
		this.username = username;

		connection.start();
	}

	public void close() throws JMSException {
		connection.close();
	}

	protected void writeMessage(final String line) throws JMSException {
		TextMessage message = pubSession.createTextMessage();
		message.setText(username + ": " + line);
		publisher.publish(message);

	}

	public static void main(String[] args) {
		final String topicFactory = "TopicCF";
		final String topicName = "topic1";
		String username = "";

		try {
			Scanner in = new Scanner(System.in);
			System.out.println("Please enter your username: ");
			username = in.nextLine();
			Chat chat = new Chat(topicFactory, topicName, username);
			while (true) {
				String line = in.nextLine();
				if (line.equalsIgnoreCase("exit")) {
					chat.close();
					return;
				} else {
					chat.writeMessage(line);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

}
