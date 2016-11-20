package app.implementations;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;

import app.interfaces.Messenger;
import app.utils.Message;

public class MessengerImpl implements Messenger {

	private int port;
	private int timeout;
	private ServerSocket server;
	private Socket socket;

	public MessengerImpl(int port, int timeout) throws IOException {
		this.port = port;
		this.timeout = timeout;
		server = new ServerSocket(port);
	}

	@Override
	public Message receive() throws IOException, ClassNotFoundException {
		socket = server.accept();
		ObjectInputStream in = new ObjectInputStream(socket.getInputStream());
		Message message = (Message) in.readObject();
		return message;
	}

	@Override
	public void respond(String destination, Message message) throws UnknownHostException, IOException {
		ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
		out.writeObject(message);
		socket.close();
	}

	@Override
	public void send(String destination, Message message) throws UnknownHostException, IOException {
		socket = new Socket(destination, port);

		ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
		out.writeObject(message);

		socket.close();
	}
	
	@Override
	public Message sendReceive(String destination, Message message)
			throws UnknownHostException, IOException, ClassNotFoundException {

		socket = new Socket(destination, port);
		socket.setSoTimeout(timeout);

		ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
		out.writeObject(message);

		ObjectInputStream in = new ObjectInputStream(socket.getInputStream());
		Message response = (Message) in.readObject();

		socket.close();

		return response;
	}

	public int getPort() {
		return port;
	}

	public void setPort(int port) {
		this.port = port;
	}

	public int getTimeout() {
		return timeout;
	}

	public void setTimeout(int timeout) {
		this.timeout = timeout;
	}

}
