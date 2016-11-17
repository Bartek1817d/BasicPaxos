package app;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashSet;

import app.implementations.AcceptorImpl;
import app.interfaces.Acceptor;
import app.utils.Message;

public class Application {

	private static final int TIMEOUT = 100;
	private static final int PORT = 1234;

	public static void main(String[] args) throws IOException, ClassNotFoundException {
		
		HashSet<String> nodes = new HashSet<String>();

		Acceptor acceptor = new AcceptorImpl("acceptor", nodes);
		ServerSocket server = new ServerSocket(PORT);

		while (true) {
			Socket socket = server.accept();
			socket.setSoTimeout(TIMEOUT);
			ObjectInputStream in = new ObjectInputStream(socket.getInputStream());
			Message message = (Message) in.readObject();
			if (message.getType() == "prepare")
				acceptor.receivePrepare(message.getNodeUID(), message.getProposalID(), socket);
			else if (message.getType() == "accept request")
				acceptor.receiveAcceptRequest(message.getNodeUID(), message.getProposalID(),
						message.getAcceptedValue());
		}
	}

}
