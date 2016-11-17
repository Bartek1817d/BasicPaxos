package app;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;

import app.implementations.LearnerImpl;
import app.interfaces.Learner;
import app.utils.Message;

public class Application {
	
	private static final int TIMEOUT = 100;
	private static final int PORT = 1234;

	public static void main(String[] args) throws IOException, ClassNotFoundException {
		Learner learner = new LearnerImpl(3);
		ServerSocket server = new ServerSocket(PORT);
		while(true) {		
			try {
				Socket socket = server.accept();
				socket.setSoTimeout(TIMEOUT);
				ObjectInputStream in = new ObjectInputStream(socket.getInputStream());
				Message message = (Message) in.readObject();
				if(message.getType() != "accepted") {
					in.close();
					socket.close();
					continue;					
				}
				learner.receiveAccepted(message.getNodeUID(), message.getAcceptedID(), message.getAcceptedValue());
				
			} catch (SocketTimeoutException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

}
