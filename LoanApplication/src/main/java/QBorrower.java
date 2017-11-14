import java.util.Scanner;

import javax.jms.JMSException;
import javax.jms.MapMessage;
import javax.jms.Queue;
import javax.jms.QueueConnection;
import javax.jms.QueueConnectionFactory;
import javax.jms.QueueReceiver;
import javax.jms.QueueSender;
import javax.jms.QueueSession;
import javax.jms.Session;
import javax.jms.TextMessage;
import javax.naming.InitialContext;

public class QBorrower {

	private static final String CONNECTION_FACTORY = "LoanQueueCF";
	private static final String REQUEST_QUEUE = "RequestQ";
	private static final String RESPONSE_QUEUE = "ResponseQ";

	private QueueSession session;
	private Queue requestQ;
	private Queue responseQ;

	public QBorrower() throws Exception {

		InitialContext ctx = new InitialContext();

		QueueConnectionFactory conFactory = (QueueConnectionFactory) ctx.lookup(CONNECTION_FACTORY);
		QueueConnection connection = conFactory.createQueueConnection();

		session = connection.createQueueSession(false, Session.AUTO_ACKNOWLEDGE);
		requestQ = (Queue) ctx.lookup(REQUEST_QUEUE);
		responseQ = (Queue) ctx.lookup(RESPONSE_QUEUE);

	}

	public void sendLoanRequest(final double salary, final double loanAmmount) {
		try {
			MapMessage message = session.createMapMessage();
			message.setDouble("salary", salary);
			message.setDouble("loanAmmount", loanAmmount);
			message.setJMSReplyTo(responseQ);

			QueueSender sender = session.createSender(requestQ);
			sender.send(message);

			String filter = "JMSCorrelationID = '" + message.getJMSMessageID() + "'";
			QueueReceiver receiver = session.createReceiver(responseQ, filter);

			TextMessage response = (TextMessage) receiver.receive(30000);
			if (response == null) {
				System.out.println("QLender not responding");
			} else {
				System.out.println(response.getText());
			}

		} catch (JMSException jmse) {
			jmse.printStackTrace();
		}

	}
	
	public void close(){
		try {
			session.close();
		} catch (JMSException e) {
			e.printStackTrace();
		}
	}

	public static void main(String[] args) throws Exception {
		Scanner in = new Scanner(System.in);
		QBorrower borrower = new QBorrower();
		while (true) {
			System.out.println("Enter your salary: ");
			double line = in.nextDouble();
			if (line == -1)
				break;
			double salary = line;
			System.out.println("Enter your desired loan: ");
			double loanAmmount = in.nextDouble();
			borrower.sendLoanRequest(salary, loanAmmount);
		}
		borrower.close();

	}

}
