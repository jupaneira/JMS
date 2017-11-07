import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.TextMessage;

public class ChatMessageListener implements MessageListener {

	public void onMessage(Message message) {
		try {
			TextMessage textMessage = (TextMessage) message;
			System.out.println(textMessage.getText());
		} catch (JMSException e) {
			e.printStackTrace();
		}

	}

}
