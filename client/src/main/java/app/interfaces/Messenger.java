package app.interfaces;

import java.io.IOException;
import java.net.UnknownHostException;

import app.utils.Message;

public interface Messenger {
	public Message receive() throws IOException, ClassNotFoundException;

	void respond(String destination, Message message) throws UnknownHostException, IOException;
	
	void send(String destination, Message message) throws UnknownHostException, IOException;
	
	public Message sendReceive(String destination, Message message)
			throws UnknownHostException, IOException, ClassNotFoundException;

	public int getPort();

	public void setPort(int port);

	public int getTimeout();

	public void setTimeout(int timeout);

	

}
