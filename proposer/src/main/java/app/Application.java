package app;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashSet;
import org.json.simple.parser.ParseException;

import app.implementations.MessengerImpl;
import app.implementations.ProposerImpl;
import app.interfaces.Messenger;
import app.interfaces.Proposer;
import app.utils.HostsFileParser;
import app.utils.Message;

public class Application {

	private static final int TIMEOUT = 500;
	private static final int PORT = 1234;

	public static void main(String[] args)
			throws FileNotFoundException, IOException, ParseException, ClassNotFoundException {
		HashSet<String> acceptorsUIDs = HostsFileParser.parse("hosts.json", "acceptors");
		int quorumSize = (int) Math.floor(acceptorsUIDs.size() / 2) + 1;
		Messenger messenger = new MessengerImpl(PORT, TIMEOUT);
		Proposer proposer = new ProposerImpl(args[0], acceptorsUIDs, quorumSize, messenger);
		Message message = new Message();

		while (true) {
			message = messenger.receive();
			if (!message.getType().equals("write"))
				continue;
			proposer.setProposal(message.getAcceptedValue());
			do {
				while (!proposer.prepare())
					;
				proposer.chooseValue();
			} while (!proposer.acceptRequest());
		}
	}
}
