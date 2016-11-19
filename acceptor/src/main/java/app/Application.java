package app;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashSet;

import org.json.simple.parser.ParseException;

import app.implementations.AcceptorImpl;
import app.interfaces.Acceptor;
import app.utils.HostsFileParser;
import app.utils.Message;

public class Application {

	private static final int TIMEOUT = 100;
	private static final int PORT = 1234;

	public static void main(String[] args) throws IOException, ClassNotFoundException, ParseException {

		HashSet<String> learners = HostsFileParser.parse("hosts.json", "learners");

		Acceptor acceptor = new AcceptorImpl(args[0], learners);

		@SuppressWarnings("resource")
		ServerSocket server = new ServerSocket(PORT);
		while (true) {
			Socket socket = server.accept();
			// socket.setSoTimeout(TIMEOUT);
			ObjectInputStream in = new ObjectInputStream(socket.getInputStream());
			Message message = (Message) in.readObject();
			if (message.getType().equals("prepare")) {
				System.out.println(
						args[0] + " otrzymał wiadomość typu " + message.getType() + " od " + message.getNodeUID());
				acceptor.receivePrepare(message.getNodeUID(), message.getProposalID(), socket);
			} else if (message.getType().equals("accept request")) {
				System.out.println(
						args[0] + " otrzymał wiadomość typu " + message.getType() + " od " + message.getNodeUID());
				acceptor.receiveAcceptRequest(message.getNodeUID(), message.getProposalID(),
						message.getAcceptedValue());
			}
		}
	}

}
