package app.implementations;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.HashSet;

import app.interfaces.Acceptor;
import app.utils.Message;
import app.utils.ProposalID;

public class AcceptorImpl implements Acceptor {

	private static final int TIMEOUT = 100;
	private static final int PORT = 1234;

	private ProposalID promisedID;
	private ProposalID acceptedID;
	private Object acceptedValue;
	private String acceptorUID;
	private HashSet<String> learnerUIDs;

	public AcceptorImpl(String acceptorUID, HashSet<String> learnerUIDs) {
		this.acceptorUID = acceptorUID;
		this.learnerUIDs = learnerUIDs;
	}

	@Override
	public void receivePrepare(String fromUID, ProposalID proposalID, Socket socket) throws IOException {

		if (this.promisedID == null || proposalID.isGreaterThan(promisedID)) {
			promisedID = proposalID;
			Message message = new Message();
			message.setType("promise");
			message.setNodeUID(acceptorUID);
			message.setProposalID(proposalID);
			message.setAcceptedID(acceptedID);
			message.setAcceptedValue(acceptedValue);
			ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
			out.writeObject(message);
			out.close();
		}
	}

	@Override
	public void receiveAcceptRequest(String fromUID, ProposalID proposalID, Object value) throws IOException {
		if (promisedID == null || proposalID.isGreaterThan(promisedID) || proposalID.equals(promisedID)) {
			promisedID = proposalID;
			acceptedID = proposalID;
			acceptedValue = value;

			Message message = new Message();
			message.setType("accepted");
			message.setNodeUID(acceptorUID);
			message.setAcceptedID(acceptedID);
			message.setAcceptedValue(acceptedValue);
			Socket socket = new Socket();
			for (String learnerUID : learnerUIDs) {
				socket.connect(new InetSocketAddress(learnerUID, PORT));
				ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
				out.writeObject(message);
				out.close();
				socket.close();
			}
		}
	}


	public ProposalID getPromisedID() {
		return promisedID;
	}

	public ProposalID getAcceptedID() {
		return acceptedID;
	}

	public Object getAcceptedValue() {
		return acceptedValue;
	}

}
