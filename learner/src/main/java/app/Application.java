package app;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;

import org.json.simple.parser.ParseException;

import app.implementations.LearnerImpl;
import app.interfaces.Learner;
import app.utils.HostsFileParser;
import app.utils.Message;

public class Application {

	private static final int TIMEOUT = 100;
	private static final int PORT = 1234;

	public static void main(String[] args) throws IOException, ClassNotFoundException, ParseException {

		int acceptorsNumber = HostsFileParser.parse("hosts.json", "acceptors").size();
		int quorumSize = (int) Math.floor(acceptorsNumber / 2) + 1;

		Learner learner = new LearnerImpl(quorumSize);
		@SuppressWarnings("resource")
		ServerSocket server = new ServerSocket(PORT);
		while (true) {
			try {
				Socket socket = server.accept();
				socket.setSoTimeout(TIMEOUT);
				ObjectInputStream in = new ObjectInputStream(socket.getInputStream());
				Message message = (Message) in.readObject();
				if (!message.getType().equals("accepted")) {
					in.close();
					socket.close();
					continue;
				}
				System.out.println(
						args[0] + " otrzymał wiadomość typu " + message.getType() + " od " + message.getNodeUID());
				learner.receiveAccepted(message.getNodeUID(), message.getAcceptedID(), message.getAcceptedValue());

			} catch (SocketTimeoutException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

}
