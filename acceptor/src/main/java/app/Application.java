package app;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import org.json.simple.parser.ParseException;

import app.implementations.AcceptorImpl;
import app.implementations.MessengerImpl;
import app.interfaces.Acceptor;
import app.interfaces.Messenger;
import app.utils.HostsFileParser;
import app.utils.Message;

public class Application {

	private static final int TIMEOUT = 100;
	private static final int PORT = 1234;

	public static void main(String[] args) throws IOException, ClassNotFoundException, ParseException {

		Set<String> learners = HostsFileParser.parse("hosts.json", "learners");

		Messenger messenger = new MessengerImpl(PORT, TIMEOUT);
		Acceptor acceptor = new AcceptorImpl(args[0], learners, messenger);

		while (true) {
			Message message = messenger.receive();
			if (message.getType().equals("prepare")) {
				System.out.println(
						args[0] + " otrzymał wiadomość typu prepare od " + message.getNodeUID());
				acceptor.receivePrepare(message.getNodeUID(), message.getProposalID());
			} else if (message.getType().equals("accept request")) {
				System.out.println(
						args[0] + " otrzymał wiadomość typu accept request od " + message.getNodeUID());
				acceptor.receiveAcceptRequest(message.getNodeUID(), message.getProposalID(),
						message.getAcceptedValue());
			}
		}
	}

}
