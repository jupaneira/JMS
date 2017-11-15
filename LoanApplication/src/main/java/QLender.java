import javax.jms.JMSException;
import javax.jms.MapMessage;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.Queue;
import javax.jms.QueueConnection;
import javax.jms.QueueConnectionFactory;
import javax.jms.QueueReceiver;
import javax.jms.QueueSender;
import javax.jms.QueueSession;
import javax.jms.Session;
import javax.jms.TextMessage;
import javax.naming.InitialContext;

public class QLender implements MessageListener {

	private static final String CONNECTION_FACTORY = "LoanQueueCF";
	private static final String REQUEST_QUEUE = "RequestQ";
	private QueueSession session;

	public QLender() throws Exception {
		InitialContext ctx = new InitialContext();

		QueueConnectionFactory connFac = (QueueConnectionFactory) ctx.lookup(CONNECTION_FACTORY);
		QueueConnection connection = (QueueConnection) connFac.createQueueConnection();
		session = connection.createQueueSession(false, Session.AUTO_ACKNOWLEDGE);

		Queue requestQ = (Queue) ctx.lookup(REQUEST_QUEUE);
		QueueReceiver receiver = session.createReceiver(requestQ);
		receiver.setMessageListener(this);

		System.out.println("Waiting for loan requests...");

		connection.start();

	}

	public void onMessage(Message message) {
		try {
			MapMessage request = (MapMessage) message;
			double salary = request.getDouble("salary");
			double loanAmount = request.getDouble("loanAmount");

			boolean accepted = false;
			if (loanAmount < 200000) {
				accepted = (salary / loanAmount) > .25;
			} else {
				accepted = (salary / loanAmount) > .33;
			}
			System.out.println(
					"salary= " + salary + ", loan= " + loanAmount + " - ratio = " + (double) (salary / loanAmount));
			System.out.println("Loan is " + (accepted ? "Accepted!" : "Declined"));

			TextMessage response = session.createTextMessage();
			response.setText(accepted ? "Accepted!" : "Declined");
			response.setJMSCorrelationID(message.getJMSMessageID());

			QueueSender sender = session.createSender((Queue) message.getJMSReplyTo());
			sender.send(response);
			System.out.println("Waiting for loan requests...");

		} catch (JMSException jmse) {
			jmse.printStackTrace();
		}

	}

	public static void main(String[] args) {
		try {
			QLender lender = new QLender();
			System.out.println("QLender application started");
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

}
