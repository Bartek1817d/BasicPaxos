package app;

import java.io.IOException;

import org.json.simple.parser.ParseException;

import app.implementations.LearnerImpl;
import app.implementations.MessengerImpl;
import app.interfaces.Learner;
import app.interfaces.Messenger;
import app.utils.HostsFileParser;
import app.utils.Message;

public class Application {

	private static final int TIMEOUT = 100;
	private static final int PORT = 1234;

	public static void main(String[] args) throws IOException, ClassNotFoundException, ParseException {

		int acceptorsNumber = HostsFileParser.parse("hosts.json", "acceptors").size();
		int quorumSize = (int) Math.floor(acceptorsNumber / 2) + 1;

		Messenger messenger = new MessengerImpl(PORT, TIMEOUT);
		Learner learner = new LearnerImpl(quorumSize);

		while (true) {
			Message message = messenger.receive();
			if (message.getType().equals("accepted")) {
				System.out.println(args[0] + " otrzymał wiadomość typu accepted od " + message.getNodeUID());
				learner.receiveAccepted(message.getNodeUID(), message.getAcceptedID(), message.getAcceptedValue());
			}
		}
	}

}
